package study.studygroup.account;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import study.studygroup.WithAccount;
import study.studygroup.domain.Account;
import study.studygroup.mail.EmailMessage;
import study.studygroup.mail.EmailService;
import study.studygroup.settings.SettingsController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @PersistenceContext
    private EntityManager em;

    @MockBean
    EmailService emailService;

    @DisplayName("회원 가입 화면 보이는지 테스트")
    @Test
    public void signupForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }
    
    @DisplayName("회원 가입 처리 - 입력값 오류")
    @Test
    public void signUpSubmit_with_wrong_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                    .param("nockname", "hoseok")
                    .param("email", "baek22h..")
                    .param("password", "1234")
                    .with(csrf())
                )
//                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }
    
    @DisplayName("회원 가입 처리 - 입력값 정상")
    @Test
    public void signUpSubmit_with_correct_input() throws Exception {

        String nickname = "hoseok";
        String email = "baek22h@naver.com";
        String password = "12341234";

        mockMvc.perform(post("/sign-up")
                .param("nickname", nickname)
                .param("email", email)
                .param("password", password)
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated().withUsername("hoseok"));

        Account account = accountRepository.findByEmail(email);

        assertNotNull(account);
        assertNotEquals(account.getPassword(), password);
        assertThat(accountRepository.existsByEmail(email)).isTrue();
        assertNotNull(account.getEmailCheckToken());
        then(emailService).should().sendEmail(any(EmailMessage.class));
    }

    @DisplayName("인증 메일 확인 - 입력값 오류")
    @Test
    public void checkEmailToken_with_wrong_input() throws Exception {
        mockMvc.perform(get("/check-email-token")
                    .param("token", "asdfasdfasd")
                    .param("email", "baek22h@naver.com")
                    )
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(unauthenticated());
    }

    @DisplayName("인증 메일 확인 - 입력값 정상")
    @Test
    public void checkEmailToken() throws Exception {

        Account account = Account.builder()
                .email("baek22h@naver.com")
                .password("12341234")
                .nickname("hoseok")
                .build();

        Account newAccount = accountRepository.save(account);
        newAccount.generateEmailCheckToken();

        mockMvc.perform(get("/check-email-token")
                    .param("token", newAccount.getEmailCheckToken())
                    .param("email", newAccount.getEmail())
                    )
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(authenticated().withUsername("hoseok"));
    }

    @Test
    @DisplayName("이메일로 로그인 페이지")
    public void emailLogin_Page() throws Exception {
        mockMvc.perform(get("/email-login"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/email-login"));
    }

    @Test
    @DisplayName("이메일로 로그인 성공")
    @WithAccount("hoseok")
    public void updateNickname() throws Exception {
        mockMvc.perform(post("/email-login")
                .param("email", "hoseok@naver.com")
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/email-login"))
                .andExpect(flash().attributeExists("message"));


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