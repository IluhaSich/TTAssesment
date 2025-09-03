package com.project.tta.services;

import com.project.tta.models.Group;
import com.project.tta.repositories.GroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class GroupService {
    GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public void addGroup(Group group){
        groupRepository.save(group);
    }
    public boolean existsByName(String name){
        return groupRepository.existsByName(name);
    }
    public boolean existsByLink(String link){
        return groupRepository.existsByLink(link);
    }
    public Group findByLink(String link){
        return groupRepository.findByLink(link).orElseThrow(NoSuchElementException::new);
    }
    public List<Group> findByFilter(String query){
        return groupRepository.findByFilter(query);
    }
    public List<Group> findByNameIgnoreCase(String name){
        return groupRepository.findByNameIgnoreCase(name);
    }
    public List<Group> findByInitial(String initial){
        return groupRepository.findByInitial(initial);
    }
}
