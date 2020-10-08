package study.studygroup.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.studygroup.WithAccount;
import study.studygroup.domain.Account;
import study.studygroup.domain.Event;
import study.studygroup.domain.Study;
import study.studygroup.event.form.EventForm;
import study.studygroup.study.StudyControllerTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EventControllerTest extends StudyControllerTest {

    @Autowired EventService eventService;
    @Autowired EventRepository eventRepository;


    @Test
    @DisplayName("이벤트 생성 폼")
    @WithAccount("hoseok")
    public void createEventForm() throws Exception {
        Study study = createNewStudy();
        mockMvc.perform(get("/study/"+study.getPath() + "/new-event"))
                .andExpect(status().isOk())
                .andExpect(view().name("event/form"))
                .andExpect(model().attributeExists("eventForm"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("account"));
    }

    @Test
    @DisplayName("이벤트 생성")
    @WithAccount("hoseok")
    public void createEvent() throws Exception {

        Study study = createNewStudy();
        EventForm form = getEventForm();

        mockMvc.perform(post("/study/"+study.getPath() + "/new-event")
                        .param("title", form.getTitle())
                        .param("description", form.getDescription())
                        .param("startDateTime", form.getStartDateTime().toString())
                        .param("endDateTime", form.getEndDateTime().toString())
                        .param("endEnrollmentDateTime", form.getEndEnrollmentDateTime().toString())
                        .param("eventType", form.getEventType().toString())
                        .param("limitOfEnrollment", form.getLimitOfEnrollments().toString())
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
//                .andExpect(view().name("event/form"))
                ;

        Event event = eventRepository.findByTitle(form.getTitle());
        assertNotNull(event);
        assertEquals(form.getDescription(), event.getDescription());
        assertEquals(EventType.FCFS, event.getEventType());
        assertEquals("hoseok", event.getCreatedBy().getNickname());

    }

    @Test
    @DisplayName("모임 조회")
    @WithAccount("hoseok")
    public void getEvent() throws Exception {
        Study study = createNewStudy();
        Account account = accountRepository.findByNickname("hoseok");
        EventForm form = getEventForm();

        Event event = eventRepository.save(modelMapper.map(form, Event.class));
        event.setCreatedBy(account);

        mockMvc.perform(get("/study/"+study.getPath()+"/events/"+event.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("event/view"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attributeExists("study"));
    }

    @Test
    @DisplayName("이벤트 리스트")
    @WithAccount("hoseok")
    public void events() throws Exception {
        Study study = createNewStudy();
        mockMvc.perform(get("/study/"+study.getPath() + "/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/events"))
                .andExpect(model().attributeExists("newEvents"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("oldEvents"));
    }

    private EventForm getEventForm() {
        return EventForm.builder()
                .title("new")
                .description("aaaaaaaa")
                .startDateTime(LocalDateTime.of(2020, 10, 10, 13, 0))
                .endDateTime(LocalDateTime.of(2020, 10, 20, 13, 0))
                .endEnrollmentDateTime(LocalDateTime.of(2020, 10, 5, 13, 0))

                .eventType(EventType.FCFS)
                .limitOfEnrollments(5)
                .build();
    }
}