package study.studygroup.settings.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import study.studygroup.account.AccountRepository;
import study.studygroup.settings.form.PasswordForm;


public class PasswordFormValidator implements Validator {


    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(PasswordForm.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        PasswordForm form = (PasswordForm)o;

        if (!form.getNewPassword().equals(form.getNewPasswordConfirm())) {
            errors.rejectValue("newPassword", "wrong.password","패스워드가 일치하지 않습니다.");
        }
    }
}
