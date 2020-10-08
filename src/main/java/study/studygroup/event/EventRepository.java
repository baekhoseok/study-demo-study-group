package study.studygroup.event;


import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import study.studygroup.domain.Event;
import study.studygroup.domain.Study;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @EntityGraph(value = "Event.withEnrollments", type = EntityGraph.EntityGraphType.LOAD)
    List<Event> findByStudyOrderByStartDateTime(Study study);
    Event findByTitle(String title);

    List<Event> findByStudy(Study study);
}
