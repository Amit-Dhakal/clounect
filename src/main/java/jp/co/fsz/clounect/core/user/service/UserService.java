package jp.co.fsz.clounect.core.user.service;

import jp.co.fsz.clounect.core.dto.AppMasterDto;
import jp.co.fsz.clounect.core.dto.UserResponse;
import jp.co.fsz.clounect.core.user.dto.UserDto;
import jp.co.fsz.clounect.core.user.entity.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

import java.util.HashMap;

/**
 * <p>[概 要] ユーザーサービスREST API 処理インターフェース。</p>
 * <p>[詳細] UserService の操作。</p>
 * <p>[備 考] なし。</p>
 * <p>[環 境] JDK17.0</p>
 * <p>Copyright フィーラーシステムZ, 2024</p>
 *
 * @author フィーラーシステムZ
 * @since 1.0
 */
public interface UserService {

  ResponseEntity<String> createUser(UserDto userDto);

  UserDto updateUser(UserDto userDto, String id);

  void deleteUser(String id);

  UserResponse getAllUsers(int pageNo, int pageSize, String sortBy, String sortOrder);


  UserDto findUserByEmail(String email);

  ResponseEntity<String> disableUser(String id);

  HashMap<String, Long> getUserCount();

  List<AppMasterDto> findAppSiteInfoById(long id);

  boolean changeApplicationStatus(long userId, long appId);

  ResponseEntity<String> enableUser(String id);

  UserDto getUserByUuid(String uuid);
}
