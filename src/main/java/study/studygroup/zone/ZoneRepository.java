package study.studygroup.zone;

import org.springframework.data.jpa.repository.JpaRepository;
import study.studygroup.domain.Zone;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
    Zone findByCityAndProvince(String city, String province);
}
