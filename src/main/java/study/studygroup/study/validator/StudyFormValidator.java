package study.studygroup.study.validator;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import study.studygroup.domain.Study;
import study.studygroup.study.StudyRepository;
import study.studygroup.study.form.StudyForm;

@Component
@RequiredArgsConstructor
public class StudyFormValidator implements Validator {

    private final StudyRepository studyRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(StudyForm.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        StudyForm studyForm = (StudyForm)o;
       if(studyRepository.existsByPath(studyForm.getPath()) ) {
           errors.rejectValue("path", "wrong.path", "스터디 경로를 사용할 수 없습니다.");
       }
    }
}
