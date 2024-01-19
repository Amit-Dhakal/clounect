package jp.co.fsz.clounect.core.repository;

import jp.co.fsz.clounect.core.model.AppUsagesLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUsagesLogRepo extends JpaRepository<AppUsagesLog, Long> {
  AppUsagesLog findAppUsagesLogById(Long id);

  List<AppUsagesLog> findByIsActiveTrue();

  Optional<AppUsagesLog> findById(Long id);

  void deleteById(Long id);
}
