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

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    JavaMailSender javaMailSender;

    @DisplayName("회원 가입 화면 보이는지 테스트")
    @Test
    public void signupForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"));
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
                .andExpect(view().name("account/sign-up"));
    }
    
    @DisplayName("회원 가입 처리 - 입력값 정상")
    @Test
    public void signUpSubmit_with_correct_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "hoseok")
                .param("email", "baek22h@naver.com")
                .param("password", "12341234")
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        assertThat(accountRepository.existsByEmail("baek22h@naver.com")).isTrue();
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }
}