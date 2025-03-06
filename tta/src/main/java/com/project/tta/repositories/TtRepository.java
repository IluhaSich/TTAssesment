package com.project.tta.repositories;

import com.project.tta.models.TtGrade;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TtRepository extends MongoRepository<TtGrade, String> {
}
