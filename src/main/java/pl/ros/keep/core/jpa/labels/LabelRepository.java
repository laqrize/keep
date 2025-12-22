package pl.ros.keep.core.jpa.labels;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.ros.keep.core.jpa.users.AppUser;

import java.util.List;

public interface LabelRepository extends JpaRepository<Label, Long> {
    List<Label> findAllByCreatedByAndStatus(AppUser createdBy, String status);
}
