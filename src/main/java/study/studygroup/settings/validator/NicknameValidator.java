package study.studygroup.settings.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import study.studygroup.account.AccountRepository;
import study.studygroup.settings.form.NicknameForm;

@Component
@RequiredArgsConstructor
public class NicknameValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(NicknameForm.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        NicknameForm nickNameForm = (NicknameForm)o;
        if (accountRepository.existsByNickname(nickNameForm.getNickname())) {
            errors.rejectValue("nickname", "invalid.nickname", "이미 사용중인 닉네임 입니다.");
        }
    }
}
