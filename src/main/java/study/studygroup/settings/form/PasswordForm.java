package study.studygroup.settings.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class PasswordForm {

    @Length(min=8, max = 50)
    private String newPassword;

    @Length(min = 8, max = 15)
    private String newPasswordConfirm;
}
