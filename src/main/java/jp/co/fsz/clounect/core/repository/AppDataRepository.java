package jp.co.fsz.clounect.core.repository;

import jp.co.fsz.clounect.core.model.AppData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppDataRepository extends JpaRepository<AppData, Long> {
  Optional <AppData> getByAppSiteId (Long appSiteId);
}
