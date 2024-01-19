package jp.co.fsz.clounect.core.user.service;

import jp.co.fsz.clounect.core.dto.AppMasterDto;
import jp.co.fsz.clounect.core.dto.AppSiteInfoDto;
import jp.co.fsz.clounect.core.exception.ResourceNotFoundException;
import jp.co.fsz.clounect.core.exception.UserAlreadyExistsException;
import jp.co.fsz.clounect.core.model.AppMaster;
import jp.co.fsz.clounect.core.model.AppSiteInfo;
import jp.co.fsz.clounect.core.repository.AppMasterRepository;
import jp.co.fsz.clounect.core.repository.AppSiteInfoRepository;
import jp.co.fsz.clounect.core.repository.projections.UserStat;
import jp.co.fsz.clounect.core.service.AppSiteInfoService;
import jp.co.fsz.clounect.core.service.AwsCognitoService;
import jp.co.fsz.clounect.core.user.dto.UserDto;
import jp.co.fsz.clounect.core.user.entity.User;
import jp.co.fsz.clounect.core.user.repository.UserRepo;
import jp.co.fsz.clounect.core.user.role.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotFoundException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>[概 要] ユーザサービス実装用の REST API 処理クラスです。</p>
 * <p>[詳細]  UserServiceの実装メソッドを含むクラス。</p>
 * <p>[備 考] なし。</p>
 * <p>[環 境] JDK17.0</p>
 * <p>Copyright フィーラーシステムZ, 2024</p>
 *
 * @author フィーラーシステムZ
 * @since 1.0
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
  private final UserRepo userRepo;

  private final AwsCognitoService awsCognitoService;
  private final AppSiteInfoRepository appSiteInfoRepo;

  private final UserService userService;

  private final AppMasterRepository appMasterRepo;

  private final AppSiteInfoService appSiteInfoService;

  public UserServiceImpl(UserRepo userRepo, AwsCognitoService awsCognitoService,
      AppSiteInfoRepository appSiteInfoRepo, @Lazy UserService userService,
      AppMasterRepository appMasterRepo, AppSiteInfoService appSiteInfoService) {
    this.userRepo = userRepo;
    this.awsCognitoService = awsCognitoService;
    this.appSiteInfoRepo = appSiteInfoRepo;
    this.userService = userService;
    this.appMasterRepo = appMasterRepo;
    this.appSiteInfoService = appSiteInfoService;
  }

  /**
   * <p>[概 要] ユーザー作成の実装。</p>
   * <p>[詳 細]このメソッドはユーザーをデータベースに保存します。</p>
   * <p>[備 考] なし。</p>
   *
   * @param userDto ユーザーDto
   * @return 保存されたユーザー
   * @since 1.0
   */
  @Override
  @Transactional
  public ResponseEntity<String> createUser(UserDto userDto) {
    try {
      User user = convertToEntity(userDto);
      user.setIsActive(true);
      userRepo.save(user);

      userDto.setId(user.getId());
      userDto.setPassword(generatePassword());
      userDto.setRole(Role.USER);
      awsCognitoService.registerUser(userDto);

      return ResponseEntity.ok("User Added Successfully");
    } catch (UserAlreadyExistsException e) {
      throw new UserAlreadyExistsException(HttpStatus.BAD_REQUEST, "User ALready Exists");
    } catch (CognitoIdentityProviderException e) {
      throw e;
    }
  }

  private String generatePassword() {
    int length = 10;
    String uppercaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String lowercaseChars = "abcdefghijklmnopqrstuvwxyz";
    String digitChars = "0123456789";
    String specialChars = "!@#$%^&*()-_=+";

    SecureRandom random = new SecureRandom();
    StringBuilder sb = new StringBuilder(length);

    // Ensure at least one character from each category
    sb.append(uppercaseChars.charAt(random.nextInt(uppercaseChars.length())));
    sb.append(lowercaseChars.charAt(random.nextInt(lowercaseChars.length())));
    sb.append(digitChars.charAt(random.nextInt(digitChars.length())));
    sb.append(specialChars.charAt(random.nextInt(specialChars.length())));

    // Fill the remaining characters randomly
    for (int i = 4; i < length; i++) {
      String allChars = uppercaseChars + lowercaseChars + digitChars + specialChars;
      int randomIndex = random.nextInt(allChars.length());
      char randomChar = allChars.charAt(randomIndex);
      sb.append(randomChar);
    }

    return sb.toString();
  }

  /**
   * <p>[概 要] ユーザー更新の実装。</p>
   * <p>[詳 細]  このメソッドはデータベース内のユーザーを更新します。</p>
   * <p>[備 考] なし。</p>
   *
   * @param userDto ユーザーDto
   * @param uuid    UUID
   * @since 17.0
   */
  @Override
  public UserDto updateUser(UserDto userDto, String uuid) {
    User user = findUserByUuid(uuid);
    User updateUser = user.toBuilder().companyName(userDto.getCompanyName())
        .shortName(userDto.getShortName()).contactNumber(userDto.getPhone())
        .address(userDto.getAddress()).updatedAt(LocalDateTime.now()).build();
    userRepo.save(updateUser);
    return convertToDto(updateUser);
  }

  /**
   * <p>[概 要] ユーザー削除の実装。</p>
   * <p>[詳 細] このメソッドはデータベースからユーザーを削除します。</p>
   * <p>[備 考] なし。</p>
   *
   * @param uuid UUID
   * @since 17.0
   */
  @Override
  @Transactional
  public void deleteUser(String uuid) {
    try {

      User user = findUserByUuid(uuid);

      if (user != null) {
        awsCognitoService.deleteUser(user.getEmail());

        user.setUpdatedAt(LocalDateTime.now());
        user.setDeletedAt(LocalDateTime.now());
        user.setIsActive(false);
        userRepo.save(user);
      }
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Error while registering user", e);
    }
  }

  /**
   * <p>[概 要] すべてのユーザーの実装を取得します。</p>
   * <p>[詳 細] このメソッドはデータベースからすべてのユーザーを提供します。</p>
   * <p>[備 考] なし。</p>
   *
   * @param pageable
   * @since 17.0
   */
  @Override
  public Page<UserDto> getAllUsers(Pageable pageable) {
    return userRepo.findAll(pageable).map(this:: convertToDto);
  }

  /**
   * <p>[概 要] ユーザーを検索します。</p>
   * <p>[詳 細] 名前または電子メールがクエリ パラメータに一致するシステム内のユーザーを検索します。</p>
   *
   * @param query
   * @param pageable
   * @since 17.0
   */
  @Override
  public Page<UserDto> searchUser(String query, Pageable pageable) {
    return userRepo.findByNameOrEmail(query, pageable).map(this:: convertToDto);
  }

  @Override
  public UserDto findUserByEmail(String email) {
    return convertToDto(userRepo.findByEmail(email));
  }

  /**
   * <p>【概要】IDによるユーザー無効化の実装。 </p>
   * <p>【詳細】 データベースとawsCognitoからIDでユーザーを無効化する方法です。 </p>
   * <p>【注意事項】特にありません。 </p>
   *
   * @param uuid UUID
   * @since 1.0
   */

  @Override
  @Transactional
  public ResponseEntity<String> disableUser(String uuid) {
    log.info("Inside disable user");

    try {
      User user = findUserByUuid(uuid);

      if (user != null) {
        awsCognitoService.disableUser(user.getEmail());
        user.setIsActive(false);
        userRepo.save(user);
      }
    } catch (UserNotFoundException e) {
      return ResponseEntity.badRequest().body("Error Disabling: " + "ユーザーが見つかりません");
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (CognitoIdentityProviderException e) {
      return ResponseEntity.internalServerError()
          .body("Error Disabling" + e.getMessage());
    }
    return ResponseEntity.ok("USER Disabled");
  }

  @Override
  public HashMap<String, Long> getUserCount() {
    HashMap<String, Long> useCountMap = new HashMap<>();

    UserStat userStat = userRepo.getUserCount();

    Long totalUser = userStat.getTotal();
    Long activeUsers = userStat.getActive();
    Long inactiveUsers = userStat.getInactive();

    // Assign values to map
    useCountMap.put("totalUsers", totalUser != null ? totalUser : 0);
    useCountMap.put("inactiveUsers", inactiveUsers != null ? inactiveUsers : 0);
    useCountMap.put("activeUsers", activeUsers != null ? activeUsers : 0);

    return useCountMap;
  }

  /**
   * <p>[概要] ID によるユーザーの有効化の実装。 </p>
   * <p>【詳細】 データベースとawsCognitoからIDでユーザーを有効にする方法です。 </p>
   * <p>【注意事項】特にありません。 </p>
   *
   * @param uuid UUID
   * @since 1.0
   */
  @Override
  public ResponseEntity<String> enableUser(String uuid) {
    try {
      User user = findUserByUuid(uuid);

      if (user != null) {
        awsCognitoService.enableUser(user.getEmail());
        user.setIsActive(true);
        userRepo.save(user);
      }
    } catch (UserNotFoundException e) {
      return ResponseEntity.badRequest().body("エラー有効化" + "ユーザーが見つかりません");
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (CognitoIdentityProviderException e) {
      return ResponseEntity.internalServerError().body("An Error Occured");
    }
    return ResponseEntity.ok("USER Enabled");
  }

  @Override
  public UserDto getUserByUuid(String uuid) {
    User user = findUserByUuid(uuid);
    return convertToDto(user);
  }

  /**
   * <p>[概 要]  ID によるユーザーの取得実装。</p>
   * <p>[詳 細]  データベースからユーザーをIDで取得する方法です。</p>
   * <p>[備 考] なし。</p>
   *
   * @param id ID
   * @since 1.0
   */
  private User findUserById(long id) {
    return userRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("user", "id", id));
  }

  /**
   * <p>[概要] UUIDによるユーザー取得の実装。 </p>
   * <p>【詳細】 データベースからUUIDでユーザーを取得するメソッドです。 </p>
   * <p>[備 考] なし。</p>
   *
   * @param uuid UUID
   * @since 1.0
   */
  private User findUserByUuid(String uuid) {
    return userRepo.findByUuid(UUID.fromString(uuid))
        .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", uuid));
  }

  /**
   * <p>[概 要]  UserEntity を UserDTO に変換します。</p>
   * <p>[詳 細] このメソッドは UserDTO 内のデータを UserEntity に変換します。 </p>
   * <p>[備 考] なし。</p>
   *
   * @param user ユーザー
   * @return ユーザーDTO
   * @since 1.0
   */
  private UserDto convertToDto(User user) {
    return UserDto.builder().id(user.getId()).uuid(user.getUuid()).shortName(user.getShortName())
        .email(user.getEmail()).companyName(user.getCompanyName())
        .phone(user.getContactNumber()).address(user.getAddress())
        .role(user.getRole())
        .createdAt(user.getCreatedAt()).isActive(user.getIsActive())
        .updatedAt(user.getUpdatedAt()).build();
  }

  /**
   * <p>[概 要]  UserDTO を UserEntity に変換します。 </p>
   * <p>[詳 細] このメソッドは UserEntity 内のすべてのデータを UserDTO に変換します</p>
   * <p>[備 考] なし。</p>
   *
   * @param userDto ユーザーDTO
   * @return ユーザー
   * @since 1.0
   */
  private User convertToEntity(UserDto userDto) {
    return User.builder().id(userDto.getId()).shortName(userDto.getShortName())
        .email(userDto.getEmail()).companyName(userDto.getCompanyName())
        .contactNumber(userDto.getPhone()).address(userDto.getAddress())
        .isActive(userDto.getIsActive()).build();

  }

  /**
   * <p>[概要] AppMaster を ID で検索します。 </p>
   * <p>【詳細】このメソッドはappSiteInfoからフィルタリングしてアプリマスターを検索するサービス実装です。</p>
   * *<p>【注意事項】 特になし。 </p>
   *
   * @param uuid UUID
   * @return appMasterのリスト
   * @since 1.0
   */
  @Override
  public List<AppMasterDto> findAppMasterByUuid(UUID uuid) {
    User user = findUserByUuid(String.valueOf(uuid));
    List<AppMasterDto> appMasterDtos = new ArrayList<>();
    if (user != null) {
      List<AppSiteInfo> appSiteInfoEntityList = appSiteInfoRepo.findByUserIdAndAndIsActiveTrue(
          user);
      if (appSiteInfoEntityList != null && !appSiteInfoEntityList.isEmpty()) {
        List<AppMaster> appMasterEntities = appMasterRepo.findAll();

        List<AppSiteInfoDto> appSiteInfoDtos = appSiteInfoEntityList.stream()
            .map(AppSiteInfoDto::fromEntity).toList();

        List<Long> appSiteInfoIds = appSiteInfoDtos.stream().map(AppSiteInfoDto::getAppId)
            .toList();

        appMasterDtos = appMasterEntities.stream().map(AppMasterDto::fromEntity)
            .peek(dto -> dto.setUserStatus(appSiteInfoIds.contains(dto.getId())))
            .collect(Collectors.toList());
      } else {
        List<AppMaster> appMasterEntities = appMasterRepo.findAll();
        return appMasterEntities.stream().map(AppMasterDto::fromEntity)
            .collect(Collectors.toList());
      }
    }
    return appMasterDtos;
  }

  /**
   * <p>[概要]ユーザーのアプリケーションの状態を変更します。 </p>
   * <p>[詳細] このメソッドは、有効から無効、またはその逆の場合にステータスを変更するためのサービス実装です。</p>
   *<p>【注意事項】特にありません。 </p>
   *
   * @param userId ユーザーID
   * @param appId アプリID
   * @return ブール値
   * @since 1.0
   */
  @Override
  public boolean changeApplicationStatus(UUID userId, UUID appId) {
    Optional<AppMaster> appMaster = appMasterRepo.findByUuid(appId);
    Optional<User> user = userRepo.findByUuid(userId);

    if (appMaster.isPresent() && user.isPresent()) {

      AppMaster master = appMaster.get();
      User mainUser = user.get();

      AppSiteInfo appSiteInfoEntity = appSiteInfoRepo.findAppSiteInfoEntitiesByUserIdAndAppId(
          mainUser, master);

      if (appSiteInfoEntity == null) {
        AppSiteInfo newAppSiteInfoEntity = new AppSiteInfo();

        Authentication authentication = SecurityContextHolder.getContext()
            .getAuthentication();
        DefaultOidcUser defaultOidcUser = (DefaultOidcUser) authentication.getPrincipal();
        String userEmail = defaultOidcUser.getEmail();
        UserDto userdto = userService.findUserByEmail(userEmail);
        Long adminId = userdto.getId();

        newAppSiteInfoEntity.setAppId(master);
        newAppSiteInfoEntity.setUserId(mainUser);
        newAppSiteInfoEntity.setIsActive(true);

        appSiteInfoService.save(newAppSiteInfoEntity);

      } else if (appSiteInfoEntity.getIsActive()) {
        appSiteInfoEntity.setIsActive(false);
        appSiteInfoRepo.save(appSiteInfoEntity);
      } else {
        appSiteInfoEntity.setIsActive(true);
        appSiteInfoRepo.save(appSiteInfoEntity);
      }
      return true;
    }
    return false;
  }
}
