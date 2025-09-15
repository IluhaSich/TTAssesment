package com.project.tta.services;

import com.project.tta.models.Group;
import com.project.tta.models.Setting;
import com.project.tta.repositories.GroupRepository;
import com.project.tta.services.parser.GroupParserService;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class GroupService {
    GroupRepository groupRepository;
    GroupParserService groupParserService;
    private static final Logger log = LoggerFactory.getLogger(GroupService.class);

    public GroupService(GroupRepository groupRepository, GroupParserService groupParserService) {
        this.groupRepository = groupRepository;
        this.groupParserService = groupParserService;
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

    public void saveGroupsFromWeb() throws IOException {
        List<Group> groupList = groupParserService.getGroupsFromWeb();
        groupList.parallelStream()
                .filter(group -> !existsByLink(group.getLink()))
                .forEach(group -> {
                    addGroup(group);
                    log.info("Extract and save group info from main page: {}",group);
                });
    }
}
