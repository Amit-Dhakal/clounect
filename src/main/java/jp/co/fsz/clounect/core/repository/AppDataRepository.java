package jp.co.fsz.clounect.core.repository;

import jp.co.fsz.clounect.core.model.AppData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppDataRepository extends JpaRepository<AppData, Long> {

  Optional<AppData> findByUuid(UUID uuid);

  boolean existsByUuid(UUID uuid);

  List<AppData> findByIsActiveTrue();

  Page<AppData> findByIsActiveTrue(Pageable pageable);

  @Query(value = "SELECT ad.* from app_data ad where CAST(ad.received_payload as JSON)->'request'->0->>'id' = :recId AND ad.app_site_id = :appSiteId", nativeQuery = true)
  Optional<AppData> getAppData(@Param("recId") String recId, @Param("appSiteId") Long appSiteId);

  @Query("SELECT u FROM AppData u WHERE u.detail = :query")
  Page<AppData> getAppDataByDetail(String query, Pageable pageable);
}
