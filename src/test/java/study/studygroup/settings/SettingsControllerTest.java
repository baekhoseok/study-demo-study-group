package study.studygroup.settings;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import study.studygroup.WithAccount;
import study.studygroup.account.AccountRepository;
import study.studygroup.account.AccountService;
import study.studygroup.account.SignUpForm;
import study.studygroup.domain.Account;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;


    @AfterEach
    void afterEach() {
       accountRepository.deleteAll();
    }

    @DisplayName("프로필 수정 성공")
    @Test
    @WithAccount("hoseok")
    public void updateProfile() throws Exception {

        String bio = "짧은 소개를 주정하는 경우";
        mockMvc.perform(post(SettingsController.SETTING_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTING_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account hoseok = accountRepository.findByNickname("hoseok");
        assertEquals(bio, hoseok.getBio());
    }


    @DisplayName("프로필 수정 실패")
    @Test
    @WithAccount("hoseok")
    public void updateProfile_error() throws Exception {

        String bio = "짧은 소개를 주정하는 경우짧은 소개를 주정하는 경우짧은 소개를 주정하는 경우짧은 소개를 주정하는 경우짧은 소개를 주정하는 경우짧은 소개를 주정하는 경우짧은 소개를 주정하는 경우짧은 소개를 주정하는 경우짧은 소개를 주정하는 경우짧은 소개를 주정하는 경우짧은 소개를 주정하는 경우짧은 소개를 주정하는 경우짧은 소개를 주정하는 경우짧은 소개를 주정하는 경우짧은 소개를 주정하는 경우";
        mockMvc.perform(post(SettingsController.SETTING_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTING_PROFILE_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));

        Account hoseok = accountRepository.findByNickname("hoseok");
        assertNull(hoseok.getBio());
    }

    @DisplayName("프로필 수정 폼")
    @Test
    @WithAccount("hoseok")
    public void updateProfileForm() throws Exception {

        mockMvc.perform(get(SettingsController.SETTING_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));

    }
}