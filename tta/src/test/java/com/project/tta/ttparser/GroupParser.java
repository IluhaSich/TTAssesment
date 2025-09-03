package com.project.tta.ttparser;

import com.project.tta.models.Group;
import com.project.tta.models.Setting;
import com.project.tta.services.GroupService;
import com.project.tta.services.parser.GroupParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GroupParserTest {

    @Mock
    private GroupService groupService;

    @InjectMocks
    private GroupParser groupParser;

    @Test
    void shouldParseAndSaveGroups() throws Exception {
        // given — HTML с двумя группами
        String html = """
            <div class="timetable-url d-inline-block">
                <a href="/timetable/189053" title="Водные пути">
                    ВВП-111
                </a>
            </div>
            <div class="timetable-url d-inline-block">
                <a href="/timetable/189054" title="Управление воздушным транспортом">
                    УВП-312
                </a>
            </div>
        """;

        Document doc = Jsoup.parse(html, "", Parser.htmlParser());

        // when — вызываем saveGroup напрямую
        for (Element group : doc.getElementsByClass("timetable-url d-inline-block")) {
            groupParser.saveGroup(group.selectFirst("a"));
        }

        // then — проверяем, что объекты переданы в сервис
        ArgumentCaptor<Group> captor = ArgumentCaptor.forClass(Group.class);
        verify(groupService, times(2)).addGroup(captor.capture());

        List<Group> saved = captor.getAllValues();

        assertThat(saved).hasSize(2);
        assertThat(saved.get(0).getName()).isEqualTo("ВВП-111");
        assertThat(saved.get(0).getCourse()).isEqualTo(1);
        assertThat(saved.get(0).getSetting()).isEqualTo(Setting.BACHELOR);

        assertThat(saved.get(1).getName()).isEqualTo("УВП-312");
        assertThat(saved.get(1).getCourse()).isEqualTo(3);
        assertThat(saved.get(1).getSetting()).isEqualTo(Setting.BACHELOR_SENIOR);

//        System.out.println(saved);
    }
}
