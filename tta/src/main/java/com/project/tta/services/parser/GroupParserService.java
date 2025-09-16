package com.project.tta.services.parser;

import com.project.tta.models.Group;
import com.project.tta.models.Setting;
import com.project.tta.services.GroupService;
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
                Group extracted = extractGroupInfo(aAttrs);
                if (extracted != null) {
                    allLinks.add(extracted);
                }
            } else {
                Element parentToggle = group.selectFirst(".dropdown-toggle");
                String courseName = parentToggle.attr("title");

                Elements sameNameGroups = group.getElementsByClass("dropdown-menu").select("a");
                for (Element g : sameNameGroups) {
                    Group extracted = extractGroupInfo(g);
                    if (extracted != null) {
                        extracted.setCourseName(courseName.trim());
                        allLinks.add(extracted);
                    }
                }
            }
        }
        return allLinks;
    }

    /**
     * Возвращает к какой категории относиться группа (Магистратура, старший или младший курс бакалавриата)
     * @param groupName название группы
     * @return Setting.MASTER, Setting.BACHELOR_SENIOR или Setting.BACHELOR
     */
    private Setting defineGroup(String groupName) {
        String numbers = groupName.split("-")[1];
        if (numbers.charAt(1) == '7') {
            return Setting.MASTER;
        } else if (Character.getNumericValue(numbers.charAt(0)) > 2) {
            return Setting.BACHELOR_SENIOR;
        } else {
            return Setting.BACHELOR;
        }
    }

    private Group extractGroupInfo(Element element) {
        String name = element.text();
        String groupName = element.attr("title");
        String[] hrefParts = element.attr("href").split("/");
        String link = hrefParts[hrefParts.length - 1];

        String numbers = name.split("-")[1];
        char secondDigit = numbers.charAt(1);

        if (secondDigit != '1' && secondDigit != '4' && secondDigit != '7') {
            return null;
        }

        if (name.split("-")[0].endsWith("д")) {
            return null;
        }


        Integer course = Integer.parseInt(numbers.substring(0, 1));
        Setting setting = defineGroup(name);

        return new Group(name, groupName, link, course, setting, null);
    }
}
