package com.project.tta.repositories;

import com.project.tta.models.Group;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends BaseRepository<Group,Long>{
    boolean existsByName(String name);
    boolean existsByLink(String link);
    Optional<Group> findByLink(String link);
}
