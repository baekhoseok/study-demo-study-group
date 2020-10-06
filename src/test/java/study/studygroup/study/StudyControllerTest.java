package study.studygroup.study;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import study.studygroup.WithAccount;
import study.studygroup.account.AccountRepository;
import study.studygroup.domain.Account;
import study.studygroup.domain.Study;
import study.studygroup.study.form.StudyForm;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class StudyControllerTest {

    @Autowired StudyController studyController;
    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;
    @Autowired StudyRepository studyRepository;
    @Autowired ModelMapper modelMapper;
    @Autowired ObjectMapper objectMapper;



    @Test
    @DisplayName("스터디 VIEW")
    @WithAccount("hoseok")
    public void viewStudy() throws Exception {

        String path = "test-path";
        Account account = accountRepository.findByNickname("hoseok");
        StudyForm form = StudyForm.builder().path(path).title("ssfdfsdf").shortDescription("1assdafasdf").fullDescription("asdfasdfasdfas").build();
        Study newStudy = modelMapper.map(form, Study.class);
        newStudy.addManager(account);
        newStudy.addMember(account);
        studyRepository.save(newStudy);

        mockMvc.perform(get("/study/test-path"))
                .andExpect(status().isOk())
                .andExpect(view().name(StudyController.STUDY_VIEW_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));
    }

    @Test
    @DisplayName("스터디 생성 폼")
    @WithAccount("hoseok")
    public void createStudyForm() throws Exception {
        mockMvc.perform(get(StudyController.STUDY_NEW_URL))
                .andExpect(status().isOk())
                .andExpect(view().name("study/form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("studyForm"));
    }

    @Test
    @DisplayName("스터디 생성 성공")
    @WithAccount("hoseok")
    public void createStudy() throws Exception {
        String path = "스프링공부하자";

        mockMvc.perform(post(StudyController.STUDY_NEW_URL)
                .param("path", path)
                .param("title", "spring 공부")
                .param("shortDescription", "spring 같이 공부해요.")
                .param("fullDescription", "spring 을 심도있게 공부하는 모임입니다.")
                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/"+ URLEncoder.encode(path, StandardCharsets.UTF_8)))
                .andExpect(flash().attributeExists("studyForm"));

        Account account = accountRepository.findByNickname("hoseok");
        Study study = studyRepository.findByTitle("spring 공부");

        assertThat(study.getManagers().size()).isEqualTo(1);
        assertThat(study.getMembers().size()).isEqualTo(1);
        assertTrue(study.getMembers().contains(account));
        assertTrue(study.getManagers().contains(account));
    }

    @Test
    @DisplayName("스터디 생성 실패")
    @WithAccount("hoseok")
    public void createStudy_fail() throws Exception {

        String path = "스프링공부하자";

        StudyForm form = StudyForm.builder().path(path).title("ssfdfsdf").shortDescription("1assdafasdf").fullDescription("asdfasdfasdfas").build();
        Study newStudy = modelMapper.map(form, Study.class);
        studyRepository.save(newStudy);

        mockMvc.perform(post(StudyController.STUDY_NEW_URL)
                .param("path", path)
                .param("title", "spring 공부1spring 공부1spring 공부1spring 공부1spring 공부1spring 공부1spring 공부1")
                .param("shortDescription", "asdfasdf")
                .param("fullDescription", "spring 을 심도있게 공부하는 모임입니다.")
                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name(StudyController.STUDY_NEW_VIEW_NAME))
                .andExpect(model().attributeExists("account"));

    }
}