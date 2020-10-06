package study.studygroup.study;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import study.studygroup.account.CurrentUser;
import study.studygroup.domain.Account;
import study.studygroup.domain.Study;
import study.studygroup.study.form.StudyDescriptionForm;
import study.studygroup.study.form.StudyForm;
import study.studygroup.study.validator.StudyFormValidator;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class StudyController {

    public static final String ROOT = "/";
    public static final String STUDY = "study";
    public static final String FORM = "form";
    public static final String STUDY_NEW_URL = "/new-study";
    public static final String STUDY_NEW_VIEW_NAME = "study/form";
    public static final String STUDY_VIEW_VIEW_NAME = "study/view";

    private final StudyService studyService;
    private final StudyFormValidator studyFormValidator;
    private final StudyRepository studyRepository;

    @InitBinder("studyForm")
    public void studyFormInitBinder(WebDataBinder webDataBinder) {
         webDataBinder.addValidators(studyFormValidator);
    }


    @GetMapping("/study/{path}/join")
    public String joinStudy(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyRepository.findStudyWithManagersByPath(path);

        studyService.addMember(study, account);
        return "redirect:/study/"+study.getEncodedPath()+"/members";
    }

    @GetMapping("/study/{path}/leave")
    public String leaveStudy(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyRepository.findStudyWithManagersByPath(path);

        studyService.removeMember(study, account);
        return "redirect:/study/"+study.getEncodedPath()+"/members";
    }

    @GetMapping("/study/{path}/members")
    public String getMembers(@CurrentUser Account account, @PathVariable String path, Model model) {
        model.addAttribute(account);
        model.addAttribute(studyService.getStudy(path));
        return "study/members";
    }

    @GetMapping("/study/{path}")
    public String viewStudy(@CurrentUser Account account, @PathVariable("path") String path, Model model) {
        Study study = studyService.getStudy(path);

        model.addAttribute(account);
        model.addAttribute(study);
        return STUDY_VIEW_VIEW_NAME;
    }

    @GetMapping(STUDY_NEW_URL)
    public String newStudyForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new StudyForm());
        return STUDY_NEW_VIEW_NAME;
    }

    @PostMapping(STUDY_NEW_URL)
    public String createStudy(@CurrentUser Account account, @Valid StudyForm studyForm, Errors errors,
                              Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return STUDY_NEW_VIEW_NAME;
        }

        Study study = studyService.createStudy(account.getId(), studyForm);
        attributes.addFlashAttribute(studyForm);
        return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8);
    }


}
