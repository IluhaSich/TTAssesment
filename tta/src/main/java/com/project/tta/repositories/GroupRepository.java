package com.project.tta.repositories;

import com.project.tta.models.Group;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends BaseRepository<Group,Long>{
    boolean existsByName(String name);
    boolean existsByLink(String link);
    Optional<Group> findByLink(String link);
    @Query("SELECT g FROM Group g WHERE LOWER(g.name) LIKE %:query%")
    List<Group> findByFilter(@Param("query") String query);
}
