package study.studygroup.study;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.studygroup.account.AccountRepository;
import study.studygroup.account.AccountService;
import study.studygroup.domain.Account;
import study.studygroup.domain.Study;
import study.studygroup.domain.Tag;
import study.studygroup.domain.Zone;
import study.studygroup.study.form.StudyDescriptionForm;
import study.studygroup.study.form.StudyForm;
import study.studygroup.tag.TagRepository;
import study.studygroup.zone.ZoneRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyService {
    
    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final TagRepository tagRepository;
    private final ZoneRepository zoneRepository;
    private final ModelMapper modelMapper;

    public Study createStudy(Long accountId, StudyForm studyForm) {
        Account account = accountService.getAccount(accountId);

        Study study = modelMapper.map(studyForm, Study.class);
        study.addManager(account);
        study.addMember(account);

        studyRepository.save(study);

        return study;
    }

    public Study getStudy(String path) {
        Study study = this.studyRepository.findByPath(path);
        checkIfExistingStudy(path, study);
        return study;
    }

    private void checkIfExistingStudy(String path, Study study) {
        if (study == null) {
            throw new IllegalArgumentException(path + "에 해당하는 스터디가 없습니다.");
        }
    }

    public Study getStudyToUpdate(Account account, String path) {
        Study study = this.getStudy(path);
        checkIfManager(account, study);
        return study;
    }

    private void checkIfManager(Account account, Study study) {
        if (!study.isManagedBy(account)) {
            throw new AccessDeniedException("해당 기능을 사용 할 수 없습니다.");
        }

    }

    public void updateDescription(Study study, StudyDescriptionForm form) {
        modelMapper.map(form, study);
    }

    public void updateBanner(Study study, String studyImage) {
        study.setImage(studyImage);
    }

    public void enableBanner(Study study) {
        study.setUseBanner(true);
    }

    public void disableBanner(Study study) {
        study.setUseBanner(false);
    }

    public void addTag(Study study, Tag tag) {
        study.getTags().add(tag);
    }

    public void removeTag(Study study, Tag tag) {
        study.getTags().remove(tag);
    }

    public void addZone(Study study, Zone zone) {
        study.getZones().add(zone);
    }

    public void removeZone(Study study, Zone zone) {
        study.getZones().remove(zone);
    }

    public Study getStudyToUpdateTag(Account account, String path) {
        Study study = studyRepository.findStudyWithTagByPath(path);
        checkIfExistingStudy(path, study);
        checkIfManager(account, study);
        return study;
    }

    public Study getStudyToUpdateZone(Account account, String path) {
        Study study = studyRepository.findStudyWithZoneByPath(path);
        checkIfExistingStudy(path, study);
        checkIfManager(account, study);
        return study;
    }
}
