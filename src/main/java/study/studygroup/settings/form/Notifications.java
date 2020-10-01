package study.studygroup.settings.form;

import lombok.Data;
import lombok.NoArgsConstructor;
import study.studygroup.domain.Account;

@Data
@NoArgsConstructor
public class Notifications {

    private boolean studyCreatedByEmail;
    private boolean studyCreatedByWeb;
    private boolean studyEnrollmentResultByEmail;
    private boolean studyEnrollmentResultByWeb;
    private boolean studyUpdatedByEmail;
    private boolean studyUpdatedByWeb;


}
