//package jp.co.fsz.clounect.core.repository;
//
//import jp.co.fsz.clounect.core.model.AppMaster;
//import jp.co.fsz.clounect.core.model.AppSiteInfoEntity;
//import jp.co.fsz.clounect.core.user.entity.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface AppSiteInfoRepo extends JpaRepository<AppSiteInfoEntity, Long> {
//
//  List<AppSiteInfoEntity> findByUserIdAndAndIsActiveTrue(User user);
//  AppSiteInfoEntity findAppSiteInfoEntitiesByUserIdAndAppId(User userId, AppMaster appId);
//
//}
