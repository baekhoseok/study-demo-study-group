package study.studygroup.study;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import study.studygroup.WithAccount;
import study.studygroup.account.AccountRepository;
import study.studygroup.domain.Account;
import study.studygroup.domain.Study;
import study.studygroup.domain.Tag;
import study.studygroup.domain.Zone;
import study.studygroup.settings.form.TagForm;
import study.studygroup.settings.form.ZoneForm;
import study.studygroup.study.form.StudyForm;
import study.studygroup.tag.TagRepository;
import study.studygroup.zone.ZoneRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class StudySettingsControllerTest {

    @Autowired StudyController studyController;
    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;
    @Autowired StudyRepository studyRepository;
    @Autowired ModelMapper modelMapper;
    @Autowired ObjectMapper objectMapper;
    @Autowired TagRepository tagRepository;
    @Autowired ZoneRepository zoneRepository;

    public String path = "test-study";

    @BeforeEach
    public void beforeEach() {
        Account account = accountRepository.findByNickname("hoseok");
        StudyForm form = StudyForm.builder().path(path).title("ssfdfsdf").shortDescription("1assdafasdf").fullDescription("asdfasdfasdfas").build();
        Study newStudy = modelMapper.map(form, Study.class);
        newStudy.addManager(account);
        newStudy.addMember(account);
        studyRepository.save(newStudy);
    }



    @Test
    @DisplayName("스터디 지역 폼 보기")
    @WithAccount("hoseok")
    public void viewStudySettingsZonesForm() throws Exception {

        mockMvc.perform(get("/study/"+ path +"/settings/zones"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/zones"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("zones"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("study"));
    }

    @Test
    @DisplayName("스터디 지역 추가하기")
    @WithAccount("hoseok")
    public void enableStudySettingsZoneAdd() throws Exception {

        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName("Seoul(서울특별시)/none");

        mockMvc.perform(post("/study/"+ path +"/settings/zones/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm))
                .with(csrf())
        )
                .andExpect(status().isOk());

        Zone zone = zoneRepository.findByCityAndProvince("Seoul", "none");
        Study study = studyRepository.findByPath(path);
        assertTrue(study.getZones().contains(zone));

    }

    @Test
    @DisplayName("스터디 지역 제거하기")
    @WithAccount("hoseok")
    public void enableStudySettingsZoneRemove() throws Exception {

        Zone zone = zoneRepository.save(Zone.builder().city("Seoul").localNameOfCity("서울특별시").province("none").build());

        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName("Seoul(서울특별시)/none");

        mockMvc.perform(post("/study/"+ path +"/settings/zones/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm))
                .with(csrf())
        )
                .andExpect(status().isOk());

        Study study = studyRepository.findByPath(path);
        assertFalse(study.getZones().contains(zone));

    }

    @Test
    @DisplayName("스터디 테그 폼 보기")
    @WithAccount("hoseok")
    public void viewStudySettingsTagsForm() throws Exception {

        mockMvc.perform(get("/study/"+ path +"/settings/tags"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/tags"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("tags"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("study"));
    }

    @Test
    @DisplayName("스터디 태그 추가하기")
    @WithAccount("hoseok")
    public void enableStudySettingsTagAdd() throws Exception {

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("hibernate");

        mockMvc.perform(post("/study/"+ path +"/settings/tags/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf())
        )
                .andExpect(status().isOk());

        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle());
        assertEquals(tagForm.getTagTitle(), tag.getTitle());

    }

    @Test
    @DisplayName("스터디 태그 제거하기")
    @WithAccount("hoseok")
    public void enableStudySettingsTagRemove() throws Exception {

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("hibernate");

        Tag tag = tagRepository.save(Tag.builder().title(tagForm.getTagTitle()).build());

        mockMvc.perform(post("/study/"+ path +"/settings/tags/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf())
        )
                .andExpect(status().isOk());

        Study study = studyRepository.findByPath(path);

        assertFalse(study.getTags().contains(tag));

    }


    @Test
    @DisplayName("스터디 배너 폼 보기")
    @WithAccount("hoseok")
    public void viewStudySettingsBannerForm() throws Exception {

        mockMvc.perform(get("/study/"+ path +"/settings/banner"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/banner"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));
    }


    @Test
    @DisplayName("스터디 배너 사용하기")
    @WithAccount("hoseok")
    public void enableStudySettingsBanner() throws Exception {

        mockMvc.perform(post("/study/"+ path +"/settings/banner/enable")
                .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/banner"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));

        Study study = studyRepository.findByPath(path);
        assertTrue(study.isUseBanner());

    }

    @Test
    @DisplayName("스터디 배너 사용 안하기")
    @WithAccount("hoseok")
    public void disalbeStudySettingsBanner() throws Exception {

        mockMvc.perform(post("/study/"+ path +"/settings/banner/disable")
                .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/banner"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));

        Study study = studyRepository.findByPath(path);
        assertFalse(study.isUseBanner());

    }

    @Test
    @DisplayName("스터디 배너 업데이트")
    @WithAccount("hoseok")
    public void updateStudySettingsBanner() throws Exception {

        mockMvc.perform(post("/study/"+ path +"/settings/banner")
                .param("image", "aaaaa")
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/"+path+"/settings/banner"))
                .andExpect(flash().attributeExists("message"));

        Study study = studyRepository.findByPath(path);
        assertEquals(study.getImage(), "aaaaa");

    }

    @Test
    @DisplayName("스터디 소 폼 보기")
    @WithAccount("hoseok")
    public void viewStudySettingsDescriptionForm() throws Exception {

        mockMvc.perform(get("/study/"+ path +"/settings/description"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/description"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));
    }


    @Test
    @DisplayName("스터디 소개 업데이트")
    @WithAccount("hoseok")
    public void viewStudySettingsDescription() throws Exception {

        mockMvc.perform(post("/study/"+ path +"/settings/description")
                    .param("shortDescription", "aaaaa")
                    .param("fullDescription", "bbbbb")
                    .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/"+path+"/settings/description"))
                .andExpect(flash().attributeExists("message"));

        Study study = studyRepository.findByPath(path);
        assertEquals(study.getFullDescription(), "bbbbb");
        assertEquals(study.getShortDescription(), "aaaaa");

    }



    @Test
    @DisplayName("스터디 소개 업데이트 실패")
    @WithAccount("hoseok")
    public void viewStudySettingsDescription_fail() throws Exception {

        mockMvc.perform(post("/study/"+ path +"/settings/description")
                    .param("shortDescription", "")
                    .param("fullDescription", "bbbbb")
                    .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/description"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));


    }

}