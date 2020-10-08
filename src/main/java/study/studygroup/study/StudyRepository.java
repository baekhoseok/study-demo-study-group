package study.studygroup.study;


import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import study.studygroup.domain.Study;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long> {
    Study findByTitle(String title);

    @EntityGraph(attributePaths = {"tags", "zones", "managers", "members"}, type = EntityGraph.EntityGraphType.LOAD)
    Study findByPath(String path);

    boolean existsByPath(String path);

    @EntityGraph(attributePaths = {"tags", "managers"})
    Study findStudyWithTagByPath(String path);

    @EntityGraph(attributePaths = {"zones", "managers"})
    Study findStudyWithZoneByPath(String path);

    @EntityGraph(attributePaths = {"managers"})
    Study findStudyWithManagersByPath(String path);

    boolean existsByTitle(String title);

    Study findSimpleStudyByPath(String path);

    Study findStudyOnlyByPath(String path);
}
