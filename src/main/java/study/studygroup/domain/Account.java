package study.studygroup.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Setter @Getter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime joinedAt;

    private String bio;

    private String url;

    private String occupation;

    private String location;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private boolean studyCreatedByEmail;

    private boolean studyCreatedByWeb = true;

    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb = true;

    private boolean studyUpdatedByEmail;

    private boolean studyUpdatedByWeb = true;

    private LocalDateTime emailCheckTokenGeneratedAt;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    public Account(String email, String nickname, String password) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
    }

    public void generateEmailCheckToken() {
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
        this.emailCheckToken = UUID.randomUUID().toString();
    }

    public void completeSignUp() {
        emailVerified = true;
        joinedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail() {
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1));
    }

//    public void updateProfile(Profile profile) {
//        this.bio = profile.getBio();
//        this.location = profile.getLocation();
//        this.occupation = profile.getOccupation();
//        this.url = profile.getUrl();
//        this.profileImage = profile.getProfileImage();
//    }

//    public void updateNotification(Notifications notifications) {
//        this.studyCreatedByEmail = notifications.isStudyCreatedByEmail();
//        this.studyCreatedByWeb = notifications.isStudyCreatedByWeb();
//        this.studyEnrollmentResultByEmail = notifications.isStudyEnrollmentResultByEmail();
//        this.studyEnrollmentResultByWeb = notifications.isStudyEnrollmentResultByWeb();
//        this.studyUpdatedByEmail = notifications.isStudyUpdatedByEmail();
//        this.studyUpdatedByWeb = notifications.isStudyUpdatedByWeb();
//    }
}
