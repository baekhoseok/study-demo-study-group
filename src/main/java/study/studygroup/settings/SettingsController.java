package study.studygroup.settings;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import study.studygroup.account.AccountService;
import study.studygroup.account.CurrentUser;
import study.studygroup.domain.Account;
import study.studygroup.settings.form.NicknameForm;
import study.studygroup.settings.form.Notifications;
import study.studygroup.settings.form.PasswordForm;
import study.studygroup.settings.form.Profile;
import study.studygroup.settings.validator.NicknameValidator;
import study.studygroup.settings.validator.PasswordFormValidator;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    public static final String SETTING_PROFILE_VIEW_NAME = "settings/profile";
    public static final String SETTING_PROFILE_URL = "/settings/profile";
    public static final String SETTING_PASSWORD_VIEW_NAME = "settings/password";
    public static final String SETTING_PASSWORD_URL = "/settings/password";
    public static final String SETTING_NOTIFICATIONS_VIEW_NAME = "settings/notifications";
    public static final String SETTING_NOTIFICATIONS_URL = "/settings/notifications";
    public static final String SETTING_ACCOUNT_VIEW_NAME = "settings/account";
    public static final String SETTING_ACCOUNT_URL = "/settings/account";

    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final NicknameValidator nicknameValidator;

    @InitBinder("passwordForm")
    public void passwordFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new PasswordFormValidator());
    }

    @InitBinder("nicknameForm")
    public void nicknameFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(nicknameValidator);
    }

    @GetMapping(SETTING_PROFILE_URL)
    public String profileUpdateForm(@CurrentUser Account account, Model model) {

        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, Profile.class));
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

    @GetMapping(SETTING_PASSWORD_URL)
    public String updatePasswordForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        return SETTING_PASSWORD_VIEW_NAME;
    }

    @PostMapping(SETTING_PASSWORD_URL)
    public String updatePassword(@CurrentUser Account account, @Valid PasswordForm passwordForm, Errors errors,
                                 Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTING_PASSWORD_VIEW_NAME;
        }

        accountService.updatePassword(account, passwordForm.getNewPassword());
        attributes.addFlashAttribute("message", "패스워드를 변경했습니다.");

        return "redirect:" + SETTING_PASSWORD_URL;
    }

    @GetMapping(SETTING_NOTIFICATIONS_URL)
    public String updateNotificationForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute("notifications", modelMapper.map(account, Notifications.class));
        return SETTING_NOTIFICATIONS_VIEW_NAME;
    }

    @PostMapping(SETTING_NOTIFICATIONS_URL)
    public String updateNotification(@CurrentUser Account account, @Valid Notifications notifications, Errors errors,
                                     Model model, RedirectAttributes attributes) {
        if(errors.hasErrors()) {
            model.addAttribute(account);
            return SETTING_NOTIFICATIONS_VIEW_NAME;
        }
        accountService.updateNotification(account, notifications);
        attributes.addFlashAttribute("message", "알람설정을 변경 하였습니다.");
        return "redirect:"+SETTING_NOTIFICATIONS_URL;
    }

    @GetMapping(SETTING_ACCOUNT_URL)
    public String updateAccountForm(Model model) {
        model.addAttribute("nicknameForm", new NicknameForm());
        return SETTING_ACCOUNT_VIEW_NAME;
    }

    @PostMapping(SETTING_ACCOUNT_URL)
    public String updateAccount(@CurrentUser Account account, @Valid NicknameForm nicknameForm, Errors errors,
                                Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTING_ACCOUNT_VIEW_NAME;
        }
        accountService.updateNickname(account, nicknameForm.getNickname());
        attributes.addFlashAttribute("message", "닉네임을 변경 하였습니다.");
        return "redirect:" + SETTING_ACCOUNT_URL;
    }
}
