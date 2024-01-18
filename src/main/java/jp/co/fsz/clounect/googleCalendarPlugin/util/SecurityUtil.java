package jp.co.fsz.clounect.googleCalendarPlugin.util;

import jp.co.fsz.clounect.core.dto.GoogleCredentialsDto;
import jp.co.fsz.clounect.core.mapper.CredentialsMapper;
import jp.co.fsz.clounect.core.model.AppSiteInfo;
import jp.co.fsz.clounect.core.repository.AppSiteInfoRepository;
import jp.co.fsz.clounect.core.user.dto.UserDto;
import jp.co.fsz.clounect.core.user.service.UserService;
import jp.co.fsz.clounect.googleCalendarPlugin.dto.OAuthToken;
import jp.co.fsz.clounect.googleCalendarPlugin.dto.UserDetails;
import jp.co.fsz.clounect.googleCalendarPlugin.service.GoogleOAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * <p>[概要] {@link SecurityUtil} サービスクラス。</p>
 * <p>[詳細] セキュリティに関連するユーティリティ機能を提供するサービスクラスです。</p>
 * <p>[備考] このクラスは、ユーザーサービス、アプリケーションサイト情報リポジトリ、
 * 認証情報マッパー、Google OAuthサービスなど、セキュリティに関連する機能を提供します。</p>
 * <p>[環境] JDK17.0</p>
 * <p>著作権 FSZ 2024</p>
 *
 * @author FSZ
 * @since 1.0
 */
@Service
@Slf4j
public class SecurityUtil {

  private final UserService userService;
  private final AppSiteInfoRepository appSiteInfoRepository;
  private final CredentialsMapper credentialsMapper;
  private final GoogleOAuthService googleOAuthService;

  /**
   * <p>[概要] {@link SecurityUtil} クラスのコンストラクタ。</p>
   * <p>[詳細] {@link SecurityUtil} インスタンスを生成するコンストラクタで、
   * 依存性注入により必要なサービスおよびリポジトリを受け取ります。</p>
   * <p>[備考] このクラスは、ユーザーサービス、アプリケーションサイト情報リポジトリ、
   * 認証情報マッパー、Google OAuthサービスなど、Google Calendar関連の機能を提供するための
   * 依存性を解決します。</p>
   *
   * @param userService             ユーザーサービスへの依存性
   * @param appSiteInfoRepository   アプリケーションサイト情報リポジトリへの依存性
   * @param credentialsMapper       認証情報マッパーへの依存性
   * @param googleOAuthService      Google OAuthサービスへの依存性
   * @since 1.0
   */
  public SecurityUtil(UserService userService,
      AppSiteInfoRepository appSiteInfoRepository, CredentialsMapper credentialsMapper,
      GoogleOAuthService googleOAuthService) {
    this.userService = userService;
    this.appSiteInfoRepository = appSiteInfoRepository;
    this.credentialsMapper = credentialsMapper;
    this.googleOAuthService = googleOAuthService;
  }

  /**
   * <p>[概要] アクセストークンを取得するメソッド。</p>
   * <p>[詳細] 現在の認証情報に基づいて、Google Calendar APIへのアクセストークンを取得します。</p>
   * <p>[備考] このメソッドは、認証情報からユーザー情報を取得し、その情報を元にGoogle Calendar APIの
   * アクセストークンを取得します。認証情報やGoogle Calendar APIとの通信中にエラーが発生した場合、
   * 適切な例外がスローされます。</p>
   *
   * @return Google Calendar APIへのアクセストークンを保持したOAuthToken
   * @throws RuntimeException アクセストークンの取得中に発生した実行時例外
   * @since 1.0
   */
  public OAuthToken getAccessToken() {
    Optional<AppSiteInfo> existingRecord = getExistingRecord();
    GoogleCredentialsDto googleCredentialsDto = credentialsMapper.mapToGoogleCredentials(existingRecord);
    String clientId = googleCredentialsDto.getClientId();
    String clientSecret = googleCredentialsDto.getClientSecret();
    String refreshToken = googleCredentialsDto.getRefreshToken();
    return googleOAuthService.getRefreshAccessToken(clientId, clientSecret, refreshToken);
  }

  /**
   * <p>[概要] 存在するレコードに基づいてUserDetailsのリストを取得するメソッド。</p>
   * <p>[詳細] このメソッドは、AppSiteInfoの存在するレコードに基づいてデータを取得し、
   * CredentialsMapperを使用してUserDetailsのリストにマッピングします。</p>
   * <p>[備考] メソッドは、存在するレコードがあればUserDetailsのリストを返し、
   * それ以外の場合は空のリストを返します。 AppSiteInfoの不在は例外をスローしません。</p>
   *
   * @return AppSiteInfoの存在するレコードに基づいて得られたUserDetailsのリスト、または不在の場合は空のリスト
   * @since 1.0
   */
  public List<UserDetails> getUserDetailsList() {
    Optional<AppSiteInfo> existingRecord = getExistingRecord();
    return credentialsMapper.mapToUserDetailsList(existingRecord);
  }

  /**
   * <p>[概要] 現在の認証情報に基づいてAppSiteInfoの存在するレコードを取得するメソッド。</p>
   * <p>[詳細] このメソッドは、現在の認証情報を使用してAppSiteInfoの存在するレコードをデータベースから取得し、
   * Optionalで包んで返します。</p>
   * <p>[備考] AppSiteInfoの存在するレコードがあればOptionalで包まれた状態で返し、
   * それ以外の場合は空のOptionalを返します。 AppSiteInfoの不在は例外をスローしません。</p>
   *
   * @return AppSiteInfoの存在するレコードをOptionalで包んだ結果
   * @since 1.0
   */
  public Optional<AppSiteInfo> getExistingRecord() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    DefaultOidcUser defaultOidcUser = (DefaultOidcUser) authentication.getPrincipal();
    String userEmail = defaultOidcUser.getEmail();
    UserDto user = userService.findUserByEmail(userEmail);
    Long userId = user.getId();
    // TODO: have to add app Id filter
    return appSiteInfoRepository.findByUserId(userId);
  }
}