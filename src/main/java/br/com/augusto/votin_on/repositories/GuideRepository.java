package br.com.augusto.votin_on.repositories;

import br.com.augusto.votin_on.entity.Guide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuideRepository extends JpaRepository<Guide, Long> {
    Optional<Guide> findByIdAndClosedAtIsNull(Long id);
    Optional<Guide> findByIdAndClosedAtIsNotNull(Long id);

    @Query(value = "SELECT * FROM Guide  WHERE closed_at IS NOT NULL AND closed_at < CURRENT_TIMESTAMP AND shared is false" ,nativeQuery = true)
    Optional<List<Guide>> findAllClosedGuideLastOneMinuteAndSharedIsFalse();

    @Query(value = "SELECT * FROM Guide WHERE id =:id AND (closed_at IS NOT NULL AND closed_at > CURRENT_TIMESTAMP)",nativeQuery = true)
    Optional<Guide> findByIdAndClosedAtIsNotNullAndStillOpen(Long id);

}
