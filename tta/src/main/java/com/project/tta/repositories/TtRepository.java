package com.project.tta.repositories;

import com.project.tta.models.TtGrade;
import org.springframework.stereotype.Repository;

@Repository
public interface TtRepository extends BaseRepository<TtGrade, Long> {
    boolean existsById(long gradeId);
}
