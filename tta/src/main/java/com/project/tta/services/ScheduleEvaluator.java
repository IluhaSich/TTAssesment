package com.project.tta.services;

import com.project.tta.models.*;
import com.project.tta.repositories.GroupRepository;
import com.project.tta.repositories.TTEvaluationRepository;
import com.project.tta.services.criteria.interfaces.EvaluationCriterion;
import com.project.tta.services.parser.TimeTableParser;
import jakarta.annotation.PreDestroy;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

//private static final Logger log = LoggerFactory.getLogger(ScheduleEvaluator.class);

@Service
public class ScheduleEvaluator {
    private final List<EvaluationCriterion> criteria;
    private final GroupRepository groupRepository;
    private final TimeTableParser timeTableParser;
    private final TTEvaluationRepository ttEvaluationRepository;
    private static final Logger log = LoggerFactory.getLogger(ScheduleEvaluator.class);

    // общий пул потоков для парсинга и сохранения
    private final ExecutorService executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
    );

    public ScheduleEvaluator(List<EvaluationCriterion> criteria,
                             GroupRepository groupRepository,
                             TimeTableParser timeTableParser,
                             TTEvaluationRepository ttEvaluationRepository) {
        this.criteria = criteria;
        this.groupRepository = groupRepository;
        this.timeTableParser = timeTableParser;
        this.ttEvaluationRepository = ttEvaluationRepository;
    }

    /**
     * Асинхронная оценка одной группы
     */
    @Transactional
    public TTEvaluation evaluateAndSaveGroup(Group group) throws IOException {
        log.info("Запуск оценки для группы: {}", group.getName());

        Optional<TTEvaluation> existingEval =
                ttEvaluationRepository.findTopByGroupOrderByLocalDateTimeDesc(group);

        if (existingEval.isPresent()) {
            log.info("Оценка для группы '{}' уже существует (дата {}). Пропуск.",
                    group.getName(), existingEval.get().getLocalDateTime());
            return existingEval.get();
        }

        int baseScore = 100;
        Map<String, Integer> scores = new HashMap<>();

        String[][] timeTable;
        try {
            // парсим расписание в текущем потоке пула
            timeTable = timeTableParser.getTimeTable(group.getLink());
        } catch (Exception e) {
            log.error("Ошибка при парсинге расписания для группы {}", group.getName(), e);
            throw new IOException("Ошибка при парсинге расписания", e);
        }

        // считаем штрафы по критериям
        for (EvaluationCriterion criterion : criteria) {
            int penalty = criterion.evaluate(timeTable, group.getSetting());
            scores.put(criterion.getName(), penalty);
            baseScore += penalty;
            log.debug("Критерий '{}' дал штраф {}", criterion.getName(), penalty);
        }

        // формируем сущность оценки
        TTEvaluation ttEvaluation = new TTEvaluation();
        ttEvaluation.setGroup(group);
        ttEvaluation.setLocalDateTime(LocalDateTime.now());
        ttEvaluation.setTotalGrade(baseScore);

        // список оценок по критериям
        List<CriterionEvaluation> criterionEvaluations = scores.entrySet().stream()
                .map(entry -> {
                    CriterionEvaluation ce = new CriterionEvaluation();
                    ce.setCriterionName(entry.getKey());
                    ce.setScore(entry.getValue());
                    ce.setTTEvaluation(ttEvaluation);
                    return ce;
                })
                .toList();

        ttEvaluation.setCriterionEvaluationList(criterionEvaluations);

        // синхронизируем обе стороны OneToOne
        group.setTTEvaluation(ttEvaluation);

        // сохраняем
        TTEvaluation saved = ttEvaluationRepository.save(ttEvaluation);

        log.info("Оценка группы '{}' успешно создана. Итоговая оценка: {}", group.getName(), baseScore);

        return saved;
    }

    /**
     * Массовая параллельная оценка всех групп
     */
    public void evaluateAndSaveAll() {
        List<Group> groups = groupRepository.findAll();

        List<CompletableFuture<Void>> futures = groups.stream()
                .map(group -> CompletableFuture.runAsync(() -> {
                    try {
                        evaluateAndSaveGroup(group);
                    } catch (IOException e) {
                        log.error("Ошибка при оценке группы {}", group.getName(), e);
                        throw new RuntimeException(e);
                    }
                }, executor))
                .toList();

        // ждём завершения всех задач
        futures.forEach(CompletableFuture::join);
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
    }
}


//2025-09-15T05:12:20.292+03:00 ERROR 28080 --- [tta] [onPool-worker-2] c.p.tta.services.ScheduleEvaluator       : Ошибка при оценке группы ТПСу-651
//
//java.io.IOException: Ошибка при парсинге расписания
//at com.project.tta.services.ScheduleEvaluator.evaluateAndSaveGroup(ScheduleEvaluator.java:80) ~[classes/:na]
//at com.project.tta.services.ScheduleEvaluator.lambda$evaluateAndSaveAll$2(ScheduleEvaluator.java:121) ~[classes/:na]
//at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:184) ~[na:na]
//at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1708) ~[na:na]
//at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:509) ~[na:na]
//at java.base/java.util.stream.ForEachOps$ForEachTask.compute(ForEachOps.java:291) ~[na:na]
//at java.base/java.util.concurrent.CountedCompleter.exec(CountedCompleter.java:754) ~[na:na]
//at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:387) ~[na:na]
//at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1312) ~[na:na]
//at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1843) ~[na:na]
//at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1808) ~[na:na]
//at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:188) ~[na:na]
//Caused by: java.util.concurrent.ExecutionException: java.net.SocketTimeoutException: Read timed out
//at java.base/java.util.concurrent.FutureTask.report(FutureTask.java:122) ~[na:na]
//at java.base/java.util.concurrent.FutureTask.get(FutureTask.java:191) ~[na:na]
//at com.project.tta.services.ScheduleEvaluator.evaluateAndSaveGroup(ScheduleEvaluator.java:69) ~[classes/:na]
//        ... 11 common frames omitted
//Caused by: java.net.SocketTimeoutException: Read timed out
//at java.base/sun.nio.ch.NioSocketImpl.timedRead(NioSocketImpl.java:278) ~[na:na]
//at java.base/sun.nio.ch.NioSocketImpl.implRead(NioSocketImpl.java:304) ~[na:na]
//at java.base/sun.nio.ch.NioSocketImpl.read(NioSocketImpl.java:346) ~[na:na]
//at java.base/sun.nio.ch.NioSocketImpl$1.read(NioSocketImpl.java:796) ~[na:na]
//at java.base/java.net.Socket$SocketInputStream.read(Socket.java:1099) ~[na:na]
//at java.base/sun.security.ssl.SSLSocketInputRecord.read(SSLSocketInputRecord.java:489) ~[na:na]
//at java.base/sun.security.ssl.SSLSocketInputRecord.readHeader(SSLSocketInputRecord.java:483) ~[na:na]
//at java.base/sun.security.ssl.SSLSocketInputRecord.bytesInCompletePacket(SSLSocketInputRecord.java:70) ~[na:na]


//-09-15T05:14:19.079+03:00  INFO 28080 --- [tta] [nio-8080-exec-3] c.p.tta.services.ScheduleEvaluator       : Запуск оценки для группы: СКУ-321
//Hibernate:
//select
//t1_0.id,
//t1_0.group_id,
//t1_0.local_date_time,
//t1_0.total_grade
//        from
//ttevaluation t1_0
//where
//t1_0.group_id=?
//order by
//t1_0.local_date_time desc
//fetch
//first ? rows only
//2025-09-15T05:14:20.847+03:00 ERROR 28080 --- [tta] [nio-8080-exec-3] o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed: java.lang.NullPointerException] with root cause
//
//java.lang.NullPointerException: Cannot read the array length because "array" is null
//at java.base/java.util.Arrays.stream(Arrays.java:5528) ~[na:na]
//at com.project.tta.services.criteria.BasicCriteria.getLessonQuantity(BasicCriteria.java:20) ~[classes/:na]
//at com.project.tta.services.criteria.DailyLoadCriterion.evaluate(DailyLoadCriterion.java:42) ~[classes/:na]
//at com.project.tta.services.ScheduleEvaluator.evaluateAndSaveGroup(ScheduleEvaluator.java:73) ~[classes/:na]
//at com.project.tta.services.ScheduleEvaluator.lambda$evaluateAndSaveAll$2(ScheduleEvaluator.java:121) ~[classes/:na]
//at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:184) ~[na:na]
//at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1708) ~[na:na]
//at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:509) ~[na:na]