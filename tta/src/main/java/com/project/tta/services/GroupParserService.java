package com.project.tta.services;

import com.project.tta.models.Group;
import jakarta.transaction.Transactional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class GroupParserService {
    static final String HOME_PATH = "https://www.miit.ru";
    private static final Logger log = LoggerFactory.getLogger(GroupParserService.class);

    public List<Group> getGroupsFromWeb() throws IOException {
        Document doc = Jsoup.connect(HOME_PATH + "/timetable/").get();
        Elements groups = doc.getElementsByClass("timetable-url d-inline-block");

        List<Group> allLinks = new ArrayList<>();

        for (Element group : groups) {
            Element aAttrs = group.selectFirst("a");
            if (!aAttrs.attr("href").equals("#")) {
                allLinks.add(extractGroupInfo(aAttrs));
            } else {
                Elements sameNameGroups = group.getElementsByClass("dropdown-menu").select("a");
                for (Element g : sameNameGroups) {
                    allLinks.add(extractGroupInfo(g));
                }
            }
        }
        return allLinks;

    }

//    @Transactional
//    public void saveGroupsFromWeb() throws IOException {
//        var groupList = getGroupsFromWeb();
//        groupList.parallelStream()
//                .filter(group -> !ttaService.existByLink(group.getLink()))
//                .forEach(group -> {
//                    ttaService.save(group);
//                    log.info("Extract and save group info from main page: {}",group);
//                });
//    }

    private String extractLink(Element element) {
        String[] linkArr = element.attr("href").split("/");
        return linkArr[linkArr.length - 1];
    }

    private Group extractGroupInfo(Element element) {
        String name = element.text();
        String groupName = element.attr("title");
        String link = Arrays.asList(element.attr("href").split("/")).getLast();
        Integer course = Integer.parseInt(name.split("-")[1].substring(0,1));

        var group = new Group(name,groupName,link,course,null);
        return group;
    }
}
