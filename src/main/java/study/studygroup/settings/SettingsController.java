package study.studygroup.settings;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import study.studygroup.account.AccountRepository;
import study.studygroup.account.AccountService;
import study.studygroup.account.CurrentUser;
import study.studygroup.domain.Account;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    private final AccountService accountService;
    public static final String SETTING_PROFILE_VIEW_NAME = "settings/profile";
    public static final String SETTING_PROFILE_URL = "/settings/profile";

    @GetMapping(SETTING_PROFILE_URL)
    public String profileUpdateForm(@CurrentUser Account account, Model model) {

        model.addAttribute(account);
        model.addAttribute(new Profile(account));
        return SETTING_PROFILE_VIEW_NAME;

    }

    @PostMapping(SETTING_PROFILE_URL)
    public String profileUpdate(@CurrentUser Account account, @Valid Profile profile, Errors errors
            , Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            //form 데이터와 errors 데이터는 자동으로 model 에 저장된다.
            model.addAttribute(account);
            return SETTING_PROFILE_VIEW_NAME;
        }
        accountService.updateProfile(account, profile);

        attributes.addFlashAttribute("message", "프로필을 수정했습니다.");
        return "redirect:"+SETTING_PROFILE_URL;
    }
}
