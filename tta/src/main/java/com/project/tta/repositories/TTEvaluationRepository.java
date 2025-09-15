package com.project.tta.repositories;

import com.project.tta.models.Group;
import com.project.tta.models.TTEvaluation;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TTEvaluationRepository extends BaseRepository<TTEvaluation,Long>{
    Optional<TTEvaluation> findTopByGroupOrderByLocalDateTimeDesc(Group group);

}
