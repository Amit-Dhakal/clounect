package jp.co.fsz.clounect.core.repository;

import jp.co.fsz.clounect.core.model.AppMaster;
import jp.co.fsz.clounect.core.repository.projections.AppStat;
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
public interface AppMasterRepository extends JpaRepository<AppMaster, Long> {

  Optional<AppMaster> findByUuid(UUID uuid);

  @Query("SELECT a FROM AppMaster a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(a.vendorName) LIKE LOWER(CONCAT('%', :query, '%'))")
  Page<AppMaster> findByNameOrVendorName(@Param("query") String query, Pageable pageable);

  boolean existsByUuid(UUID uuid);

  List<AppMaster> findByIsActiveTrue();

  Page<AppMaster> findByIsActiveTrue(Pageable pageable);

  @Query(value = "SELECT COUNT(id) as total,"
      + "SUM(CASE WHEN is_active = false THEN 1 ELSE 0 END) as inactive,"
      + "SUM(CASE WHEN is_active = true THEN 1 ELSE 0 END) as active "
      + "from app_master", nativeQuery = true)
  AppStat getAppCounts();

}
