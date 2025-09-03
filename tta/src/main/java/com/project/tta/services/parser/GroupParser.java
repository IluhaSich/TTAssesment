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

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class GroupParser {
    static final String HOME_PATH = "https://www.miit.ru";
    private static final Logger log = LoggerFactory.getLogger(TimeTableParser.class);

    GroupService groupService;

    public GroupParser(GroupService groupService) {
        this.groupService = groupService;
    }

    public void getLinks() throws IOException, ExecutionException, InterruptedException {
        Document doc = Jsoup.connect(HOME_PATH + "/timetable/").get();
        Elements groups = doc.getElementsByClass("timetable-url d-inline-block");

        for (Element group : groups) {
            Element aAttrs = group.selectFirst("a");

            if (!aAttrs.attr("href").equals("#")) {
                saveGroup(aAttrs);
            } else {
                Elements sameNameGroups = group.getElementsByClass("dropdown-menu").select("a");
                for (Element g : sameNameGroups) {
                    saveGroup(g);
                }
            }
        }
    }

    public void saveGroup(Element a) {
        String name = a.text().trim();
        String link = extractLink(a);
        Setting setting = defineGroup(name);
        Integer course = Character.getNumericValue(name.split("-")[1].charAt(0));
        Group group = new Group(
            name,
            link,
            course,
            setting,
            null
        );
        groupService.addGroup(group);
    }

    /**
     * Возвращает к какой категории относиться группа (Магистратура, старший или младший курс бакалавриата)
     * @param groupName название группы
     * @return Setting.MASTER, Setting.BACHELOR_SENIOR или Setting.BACHELOR
     */
    private Setting defineGroup(String groupName){
        String numbers = groupName.split("-")[1];
        if (numbers.charAt(1) == '7'){
            return Setting.MASTER;
        } else if (Character.getNumericValue(numbers.charAt(0)) > 2){
            return Setting.BACHELOR_SENIOR;
        }else {
            return Setting.BACHELOR;
        }
    }

    private String extractLink(Element element) {
        String[] linkArr = element.attr("href").split("/");
        return linkArr[linkArr.length - 1];
    }
}
