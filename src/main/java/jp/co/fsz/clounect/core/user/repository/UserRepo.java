package jp.co.fsz.clounect.core.user.repository;

import jp.co.fsz.clounect.core.repository.projections.UserStat;
import jp.co.fsz.clounect.core.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * <p>[概 要] UserRepo ユーザー REST API 処理クラス。</p>
 * <p>[詳細] JPAを使用するためのインターフェース。</p>
 * <p>[備 考] なし。</p>
 * <p>[環 境] JDK17.0</p>
 * <p>Copyright フィーラーシステムZ, 2024</p>
 *
 * @author フィーラーシステムZ
 * @since 1.0
 */
@Repository
public interface UserRepo extends JpaRepository<User, Long> {

  User findByEmail(String email);

  @Query(value = "SELECT COUNT(id) as total,"
      + "SUM(CASE WHEN is_active = false THEN 1 ELSE 0 END) as inactive,"
      + "SUM(CASE WHEN is_active = true THEN 1 ELSE 0 END) as active "
      + "from users", nativeQuery = true)
  UserStat getUserCount();

  Optional<User> findByUuid(UUID uuid);

}
