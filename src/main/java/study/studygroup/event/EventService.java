package study.studygroup.event;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.studygroup.domain.Account;
import study.studygroup.domain.Enrollment;
import study.studygroup.domain.Event;
import study.studygroup.domain.Study;
import study.studygroup.event.form.EventForm;
import study.studygroup.study.StudyRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final StudyRepository studyRepository;
    private final ModelMapper modelMapper;
    private final EnrollmentRepository enrollmentRepository;

    public Event createEvent(Event event, Account account, Study study) {
        event.setCreatedBy(account);
        event.setStudy(study);
        event.setCreatedDateTime(LocalDateTime.now());
        return eventRepository.save(event);
    }

    public List<Event> findByStudyOrderByStartDateTime(Study study) {
        return eventRepository.findByStudyOrderByStartDateTime(study);
    }

    public void updateEvent(Event event, EventForm eventForm) {
        modelMapper.map(eventForm, event);
        event.acceptWaitingList();
//        eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(),
//                "'" + event.getTitle() + "' 모임 정보를 수정했으니 확인하세요."));
    }

    public void deleteEvent(Event event) {
        eventRepository.delete(event);
//        eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(),
//                "'" + event.getTitle() + "' 모임을 취소했습니다."));
    }

    public void cancelEnrollment(Event event, Account account) {
        Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event, account);
        if (!enrollment.isAttended()) {
            event.removeEnrollment(enrollment);
            enrollmentRepository.delete(enrollment);
            event.acceptNextWaitingEnrollment();
        }
    }
}
