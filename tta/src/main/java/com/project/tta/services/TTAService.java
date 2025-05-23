package com.project.tta.services;

import com.project.tta.models.CriterionEvaluation;
import com.project.tta.models.Group;
import com.project.tta.repositories.CriterionEvaluationRepository;
import com.project.tta.repositories.GroupRepository;
import com.project.tta.repositories.TTEvaluationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TTAService {
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

    public List<Group> findAll(){
        return groupRepository.findAll();
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

