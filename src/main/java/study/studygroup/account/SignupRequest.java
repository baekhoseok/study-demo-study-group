package study.studygroup.account;

import lombok.*;

@Setter @Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {

    private String nickname;
    private String email;
    private String password;
}
