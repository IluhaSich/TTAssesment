package com.project.tta.services;

import com.project.tta.dtos.GroupTotalScore;
import com.project.tta.models.CriterionEvaluation;
import com.project.tta.models.Group;
import com.project.tta.repositories.CriterionEvaluationRepository;
import com.project.tta.repositories.GroupRepository;
import com.project.tta.repositories.TTEvaluationRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class  TTAService {
    private final GroupRepository groupRepository;
    private final TTEvaluationRepository ttEvaluationRepository;
    private final CriterionEvaluationRepository criterionEvaluationRepository;

    public TTAService(GroupRepository groupRepository, TTEvaluationRepository ttEvaluationRepository, CriterionEvaluationRepository criterionEvaluationRepository) {
        this.groupRepository = groupRepository;
        this.ttEvaluationRepository = ttEvaluationRepository;
        this.criterionEvaluationRepository = criterionEvaluationRepository;
    }

    // TODO: добавить еще методов save для сохранения полной цепочки моделей (нужно ли?)
    public Group save(Group group) {
        groupRepository.save(group);
        return group;
    }

    public List<Group> findAllGroups(){
        return groupRepository.findAll();
    }

    public List<CriterionEvaluation> findAllCriteria(){
        return criterionEvaluationRepository.findAll().stream()
                .collect(Collectors.toMap(
                        CriterionEvaluation::getCriterionName,
                        ce -> ce,
                        (existing, replacement) -> existing
                ))
                .values()
                .stream()
                .toList();
//        return criterionEvaluationRepository.findAll();
    }

    public List<Group> findGroupsByFilter(String groupNameFilter) {
        if (groupNameFilter == null || groupNameFilter.trim().isEmpty()) {
            return groupRepository.findAll();
        }
        return groupRepository.findByFilter(groupNameFilter.toLowerCase());
    }

    public List<CriterionEvaluation> findCriteriaByFilter(String criterionNameFilter) {
        if (criterionNameFilter == null || criterionNameFilter.trim().isEmpty()) {
            return criterionEvaluationRepository.findAll();
        }
        return criterionEvaluationRepository.findByFilter(criterionNameFilter.toLowerCase());
    }

    //TODO: прикрутить вывод частичного списка групп и выбранном институте (опционально)
    public List<Group> findAllGroupsFilteredByInstitute(String instituteFilter) {
        if (instituteFilter == null || instituteFilter.isEmpty()) {
            return groupRepository.findAll();
        }
        char instituteInitial = InstituteMapper.getInstituteInitial(instituteFilter);
        return groupRepository.findByInitial(String.valueOf(instituteInitial));
    }

    public List<Group> findGroupsByInstituteAndFilters(String instituteFilter, String groupNameFilter) {
        if (groupNameFilter != null && !groupNameFilter.isEmpty()) {
            return groupRepository.findByNameIgnoreCase(groupNameFilter);
        }
        if (instituteFilter != null && !instituteFilter.isEmpty()) {
            return groupRepository.findAll().stream()
                    .filter(group -> {
                        String groupInstitute = InstituteMapper.getInstituteByGroupName(group.getName());
                        return groupInstitute.equalsIgnoreCase(instituteFilter);
                    })
                    .toList();
        }
        return groupRepository.findAll();
    }

    public List<Group> findGroupsByInstitute(String instituteName) {
        char instituteInitial = InstituteMapper.getInstituteInitial(instituteName);
        if (instituteInitial == '\u0000') {
            return Collections.emptyList();
        }
        return groupRepository.findByInitial(String.valueOf(instituteInitial));
    }

    public List<GroupTotalScore> calculateTotalScores() {
        List<Group> groups = groupRepository.findAll();
        return groups.stream()
                .map(group -> {
                    int total = 0;
                    if (group.getTTEvaluation() != null && group.getTTEvaluation().getCriterionEvaluationList() != null) {
//                        total = (int) group.getTTEvaluation().getCriterionEvaluationList().stream()
//                                .mapToDouble(CriterionEvaluation::getScore)
//                                .sum();
                        total = (int) group.getTTEvaluation().getTotal_grade();
                    }
                    return new GroupTotalScore(
                            group.getName(),
                            group.getLink(),
                            total
                    );
                })
                .toList();
    }

    public boolean existByName(String name){
        return groupRepository.existsByName(name);
    }

    public boolean existByLink(String link){
        return groupRepository.existsByLink(link);
    }

    public Group findByLink(String link) {
        return groupRepository.findByLink(link).orElseThrow();
    }

}

