package study.studygroup.settings;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import study.studygroup.WithAccount;
import study.studygroup.account.AccountRepository;
import study.studygroup.account.AccountService;
import study.studygroup.account.SignUpForm;
import study.studygroup.domain.Account;

import javax.swing.text.View;

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

    @Autowired
    PasswordEncoder passwordEncoder;


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

    @DisplayName("패스워드 변경 성공.")
    @Test
    @WithAccount("hoseok")
    public void updatePassword() throws Exception {

        mockMvc.perform(post(SettingsController.SETTING_PASSWORD_URL)
                .param("newPassword", "12341234")
                .param("newPasswordConfirm", "12341234")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTING_PASSWORD_URL))
                .andExpect(flash().attributeExists("message"));

        Account hoseok = accountRepository.findByNickname("hoseok");
        assertTrue(passwordEncoder.matches( "12341234", hoseok.getPassword()));

    }

    @DisplayName("패스워드 변경 실패.")
    @Test
    @WithAccount("hoseok")
    public void updatePassword_fail() throws Exception {

        mockMvc.perform(post(SettingsController.SETTING_PASSWORD_URL)
//                .param("newPassword", "123412")
//                .param("newPasswordConfirm", "123412")
                .param("newPassword", "12341234")
                .param("newPasswordConfirm", "12345678")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTING_PASSWORD_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));
    }

    @Test
    @DisplayName("알림설정 변경 페이지")
    @WithAccount("hoseok")
    public void updateNotifications_Page() throws Exception {
        mockMvc.perform(get(SettingsController.SETTING_NOTIFICATIONS_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTING_NOTIFICATIONS_VIEW_NAME))
                .andExpect(model().attributeExists("notifications"));
    }

    @Test
    @DisplayName("알림설정 변경 성공")
    @WithAccount("hoseok")
    public void updateNotifications() throws Exception {
        mockMvc.perform(post(SettingsController.SETTING_NOTIFICATIONS_URL)
                    .param("studyCreatedByEmail", "true")
                    .param("studyEnrollmentResultByEmail", "true")
                    .param("studyUpdatedByEmail", "true")
                    .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTING_NOTIFICATIONS_URL))
                .andExpect(flash().attributeExists("message"));

        Account hoseok = accountRepository.findByNickname("hoseok");
        assertTrue(hoseok.isStudyCreatedByEmail());
        assertTrue(hoseok.isStudyEnrollmentResultByEmail());
        assertTrue(hoseok.isStudyUpdatedByEmail());
    }

    @Test
    @DisplayName("알림설정 변경 실패")
    @WithAccount("hoseok")
    public void updateNotifications_Fail() throws Exception {
        mockMvc.perform(post(SettingsController.SETTING_NOTIFICATIONS_URL)
                    .param("studyCreatedByEmail", "123")
                    .param("studyEnrollmentResultByEmail", "234")
                    .param("studyUpdatedByEmail", "true")
                    .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTING_NOTIFICATIONS_VIEW_NAME))
                .andExpect(model().attributeExists("account"));

    }

    @Test
    @DisplayName("닉네임변경 변경 페이지")
    @WithAccount("hoseok")
    public void updateNickname_Page() throws Exception {
        mockMvc.perform(get(SettingsController.SETTING_ACCOUNT_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTING_ACCOUNT_VIEW_NAME))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @Test
    @DisplayName("닉네임 변경 성공")
    @WithAccount("hoseok")
    public void updateNickname() throws Exception {
        mockMvc.perform(post(SettingsController.SETTING_ACCOUNT_URL)
                .param("nickname", "paul")
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTING_ACCOUNT_URL))
                .andExpect(flash().attributeExists("message"));

        Account hoseok = accountRepository.findByNickname("paul");
        assertNotNull(hoseok);
    }

    @Test
    @DisplayName("닉네임 변경 실패")
    @WithAccount("hoseok")
    public void updateNickname_Fail() throws Exception {
        mockMvc.perform(post(SettingsController.SETTING_ACCOUNT_URL)
                .param("nickname", "ul")
                .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTING_ACCOUNT_VIEW_NAME))
                .andExpect(model().attributeExists("account"));
    }
}