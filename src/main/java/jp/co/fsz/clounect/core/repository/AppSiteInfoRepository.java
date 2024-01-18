package jp.co.fsz.clounect.core.repository;

import jp.co.fsz.clounect.core.model.AppMaster;
import jp.co.fsz.clounect.core.model.AppSiteInfo;
import jp.co.fsz.clounect.core.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * <p>[概要] AppSiteInfoRepository アプリ処理クラス。 </p>
 * <p>[詳細] JPAを利用するためのインターフェースです。 </p>
 *<p>【備考】特記事項はございません。 </p>
 * <p>[環境] JDK17.0</p>
 * <p>著作権フィーラー システム Z、2024 年</p>
 *
 * @author 著者 FSZ
 * @1.0以降
 */
@Repository
public interface AppSiteInfoRepository extends JpaRepository<AppSiteInfo,Long> {

  /**
   * <p>[概要] Webhook URL による {@link AppSiteInfo} エンティティの取得実装。</p>
   * <p>[詳細] データベースから指定された Webhook URL に対応する AppSiteInfo エンティティを取得する方法です。</p>
   * <p>[備考] 特記事項はありません。</p>
   *
   * @param webhookUrl Webhook URL
   * @since 1.0
   */
  Optional<AppSiteInfo> findByWebhookUrl(String webhookUrl);
  Optional<AppSiteInfo> findById(Long id);

  AppSiteInfo findAppSiteInfoById(Long id);

  @Query("SELECT asi FROM AppSiteInfo asi LEFT JOIN FETCH asi.userId u WHERE u.id = :userId")
  Optional<AppSiteInfo> findByUserId (@Param("userId") Long userId);

  Optional<AppSiteInfo> findByUserIdAndAppId(User user, AppMaster appMaster);

  @Query("SELECT asi FROM AppSiteInfo asi WHERE asi.userId.id = :userId AND asi.appId.id = :appId")
  AppSiteInfo findByUserIdAndAppId(@Param("userId")Long userId, @Param("appId")Long appId);

  List<AppSiteInfo> findByUserIdAndAndIsActiveTrue(User user);
  AppSiteInfo findAppSiteInfoEntitiesByUserIdAndAppId(User userId, AppMaster appId);



}