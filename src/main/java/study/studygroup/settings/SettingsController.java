package study.studygroup.settings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import study.studygroup.account.AccountService;
import study.studygroup.account.CurrentUser;
import study.studygroup.domain.Account;
import study.studygroup.domain.Tag;
import study.studygroup.domain.Zone;
import study.studygroup.settings.form.*;
import study.studygroup.settings.validator.NicknameValidator;
import study.studygroup.settings.validator.PasswordFormValidator;
import study.studygroup.tag.TagRepository;
import study.studygroup.zone.ZoneRepository;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    public static final String SETTING_TAGS_VIEW_NAME = "settings/tags";
    public static final String SETTING_TAGS_URL = "/settings/tags";
    public static final String SETTING_ZONES_VIEW_NAME = "settings/zones";
    public static final String SETTING_ZONES_URL = "/settings/zones";

    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final NicknameValidator nicknameValidator;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;
    private final ZoneRepository zoneRepository;


    @InitBinder("passwordForm")
    public void passwordFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new PasswordFormValidator());
    }

    @InitBinder("nicknameForm")
    public void nicknameFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(nicknameValidator);
    }

    @GetMapping(SETTING_ZONES_URL)
    public String updateZonesFomr(@CurrentUser Account account, Model model) throws JsonProcessingException {

        List<Zone> allZones = zoneRepository.findAll();
        Set<Zone> zones = accountService.getZones(account);
        model.addAttribute(account);
        model.addAttribute("zones", zones.stream().map(Zone::toString).collect(Collectors.toList()));

        List<String> collect = allZones.stream().map(Zone::toString).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(collect));

        return SETTING_ZONES_VIEW_NAME;
    }

    @PostMapping(SETTING_ZONES_URL+"/add")
    private ResponseEntity addZone(@CurrentUser Account account, @RequestBody ZoneForm zoneForm) {
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }

        accountService.addZone(account, zone);
        return ResponseEntity.ok().build();
    }

    @PostMapping(SETTING_ZONES_URL+"/remove")
    public ResponseEntity removeZone(@CurrentUser Account account, @RequestBody ZoneForm zoneForm) {
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }

        accountService.removeZone(account, zone);
        return ResponseEntity.ok().build();
    }

    @GetMapping(SETTING_TAGS_URL)
    public String updateTagsForm(@CurrentUser Account account, Model model) throws JsonProcessingException {

        Set<Tag> tags = accountService.getTags(account);

        model.addAttribute(account);
        model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(Collectors.toList()));

        List<Tag> allTags = tagRepository.findAll();
        List<String> collect = allTags.stream().map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(collect));

        return SETTING_TAGS_VIEW_NAME;
    }

    @PostMapping(SETTING_TAGS_URL+"/add")
    @ResponseBody
    public ResponseEntity addTag(@CurrentUser Account account, @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();

        Tag tag = tagRepository.findByTitle(title);
        if (tag == null) {
            tag = tagRepository.save(Tag.builder().title(title).build());
        }

        accountService.addTag(account, tag);

        return ResponseEntity.ok().build();
    }

    @PostMapping(SETTING_TAGS_URL+"/remove")
    @ResponseBody
    public ResponseEntity removeTag(@CurrentUser Account account, @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();

        Tag tag = tagRepository.findByTitle(title);
        if (tag == null) {
            return ResponseEntity.badRequest().build();
        }

        accountService.removeTag(account, tag);

        return ResponseEntity.ok().build();
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
