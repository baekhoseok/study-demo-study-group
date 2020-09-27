package study.studygroup.account;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    @GetMapping("/sign-up")
    public String signup(Model model) {
        model.addAttribute("signUpForm", new SignupRequest());
        return "account/sign-up";
    }
}
