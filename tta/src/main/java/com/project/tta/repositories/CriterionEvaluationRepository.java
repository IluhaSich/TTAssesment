package com.project.tta.repositories;

import com.project.tta.models.CriterionEvaluation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CriterionEvaluationRepository extends BaseRepository<CriterionEvaluation,Long>{
    @Query("SELECT ce FROM CriterionEvaluation ce WHERE LOWER(ce.criterionName) LIKE %:query%")
    List<CriterionEvaluation> findByFilter(@Param("query") String query);
}
