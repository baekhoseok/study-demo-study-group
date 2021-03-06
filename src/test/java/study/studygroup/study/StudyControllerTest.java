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
public class StudyControllerTest {

    @Autowired protected StudyController studyController;
    @Autowired protected MockMvc mockMvc;
    @Autowired protected AccountRepository accountRepository;
    @Autowired protected StudyRepository studyRepository;
    @Autowired protected ModelMapper modelMapper;
    @Autowired protected ObjectMapper objectMapper;



    @Test
    @DisplayName("스터디 가입")
    @WithAccount("hoseok")
    public void joinStudy() throws Exception {

        Study study = createAnotherStudy();

        mockMvc.perform(get("/study/"+study.getPath()+"/join"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/"+study.getPath()+"/members"));
    }

    @Test
    @DisplayName("스터디 가입")
    @WithAccount("hoseok")
    public void leaveStudy() throws Exception {

        Study study = createAnotherStudy();
        Account account = accountRepository.findByNickname("hoseok");
        study.getMembers().add(account);

        mockMvc.perform(get("/study/"+study.getPath()+"/leave"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/"+study.getPath()+"/members"));
    }

    @Test
    @DisplayName("스터디 VIEW")
    @WithAccount("hoseok")
    public void viewStudy() throws Exception {

        createNewStudy();

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

    protected Study createNewStudy() {
        String path = "test-path";
        Account account = accountRepository.findByNickname("hoseok");
        StudyForm form = StudyForm.builder().path(path).title("ssfdfsdf").shortDescription("1assdafasdf").fullDescription("asdfasdfasdfas").build();
        Study newStudy = modelMapper.map(form, Study.class);
        newStudy.addManager(account);
        studyRepository.save(newStudy);
        return newStudy;
    }

    protected Study createAnotherStudy() {
        String path = "new-path";
        Account account = accountRepository.save(Account.builder().nickname("paul").email("paul@naver.com").build());
        StudyForm form = StudyForm.builder().path(path).title("ssfdfsdf").shortDescription("1assdafasdf").fullDescription("asdfasdfasdfas").build();
        Study newStudy = modelMapper.map(form, Study.class);
        newStudy.addManager(account);
        studyRepository.save(newStudy);
        return newStudy;
    }

    protected Account createNewAccount() {
        return accountRepository.save(Account.builder().nickname("paul").email("paul@naver.com").build());
    }
}