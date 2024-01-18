package jp.co.fsz.clounect.googleCalendarPlugin.service;

import jp.co.fsz.clounect.core.dto.CalendarEventDto;
import jp.co.fsz.clounect.core.dto.GoogleCredentialsDto;
import jp.co.fsz.clounect.core.mapper.CredentialsMapper;
import jp.co.fsz.clounect.core.model.AppData;
import jp.co.fsz.clounect.core.model.AppSiteInfo;
import jp.co.fsz.clounect.core.repository.AppDataRepository;
import jp.co.fsz.clounect.core.repository.AppSiteInfoRepository;
import jp.co.fsz.clounect.googleCalendarPlugin.dto.OAuthToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>[概要] CheckTypeServiceクラス。</p>
 * <p>[詳細] Google Calendarの特定の操作に対するチェック機能を提供するサービスです。</p>
 * <p>[環境] JDK17.0</p>
 * <p>[著作権] FSZ 2024</p>
 *
 * @author Your Name
 * @since 1.0
 */
@Service
@Slf4j
public class CheckTypeService {
  private final GoogleCalendarRecordService googleCalendarRecordService;
  private final JsonDataOptimizationService jsonDataOptimizationService;
  private final AppSiteInfoRepository appSiteInfoRepository;
  private final CredentialsMapper credentialsMapper;
  private final GoogleOAuthService googleOAuthService;
  private final AppDataRepository appDataRepository;

  /**
   * <p>[概要] CheckTypeServiceのコンストラクタ。</p>
   * <p>[詳細] CheckTypeServiceを初期化するために必要な依存関係を受け取ります。</p>
   * <p>[備考] このサービスはGoogle Calendarの操作に関連する機能を提供します。</p>
   *
   * @param googleCalendarRecordService  Google Calendarのレコードに関連するサービス
   * @param jsonDataOptimizationService JSONデータの最適化に関連するサービス
   * @param appSiteInfoRepository        AppSiteInfoのリポジトリ
   * @param credentialsMapper            資格情報のマッピングに使用されるマッパー
   * @param googleOAuthService           Google OAuthに関連するサービス
   * @param appDataRepository            AppDataのリポジトリ
   * @since 1.0
   */
  public CheckTypeService(GoogleCalendarRecordService googleCalendarRecordService,
      JsonDataOptimizationService jsonDataOptimizationService,
      AppSiteInfoRepository appSiteInfoRepository,
      CredentialsMapper credentialsMapper, GoogleOAuthService googleOAuthService,
      AppDataRepository appDataRepository) {
    this.googleCalendarRecordService = googleCalendarRecordService;
    this.jsonDataOptimizationService = jsonDataOptimizationService;
    this.appSiteInfoRepository = appSiteInfoRepository;
    this.credentialsMapper = credentialsMapper;
    this.googleOAuthService = googleOAuthService;
    this.appDataRepository = appDataRepository;
  }

  /**
   * <p>[概要] ペイロード情報を処理してGoogle Calendarへの操作を実行するメソッド。</p>
   * <p>[詳細] ペイロード情報とWebhookのUUIDを受け取り、それに基づいてGoogle Calendarへの
   * 追加、更新、削除などの操作を実行します。</p>
   * <p>[備考] WebhookのUUIDに対応するAppSiteInfoが存在し、かつペイロードが正常に処理できる場合に
   * Google Calendarへの操作を実行します。</p>
   *
   * @param payLoad ペイロード情報のリスト
   * @param uuid    WebhookのUUID
   * @since 1.0
   */
  public void getPlayLoad(List<Map<String, Object>> payLoad, String uuid) {
    Optional<AppSiteInfo> appSiteInfo = appSiteInfoRepository.findByWebhookUrl(uuid);
    if(appSiteInfo.isPresent()){
      Map<String, Object> result = jsonDataOptimizationService.processGoogleCalendarRecords(
          payLoad);

      Long appSiteId = appSiteInfo.get().getId();
      Optional<AppSiteInfo> existingRecord = appSiteInfoRepository.findById(appSiteId);

      GoogleCredentialsDto googleCredentialsDto = credentialsMapper.mapToGoogleCredentials(
          existingRecord);
      String clientId = googleCredentialsDto.getClientId();
      String clientSecret = googleCredentialsDto.getClientSecret();
      String refreshToken = googleCredentialsDto.getRefreshToken();

      OAuthToken token = googleOAuthService.getRefreshAccessToken(clientId, clientSecret,
          refreshToken);

      String accessToken = token.getAccessToken();
      String type = (String) result.get("type");

      if (Objects.equals(type, "ADD_RECORD")) {
        googleCalendarRecordService.addRecord(accessToken, payLoad, appSiteId);
      }

      Optional<AppData> getDetails = appDataRepository.getByAppSiteId(appSiteId);
      if (getDetails.isPresent()) {
        AppData appData = getDetails.get();
        List<CalendarEventDto> calendarEvents = credentialsMapper.getCalendarEvents(Optional.of(appData));
        String calendarId = calendarEvents.get(0).getCalendarId();

        List<String> eventIds = calendarEvents.stream()
            .flatMap(calendarEventDto -> calendarEventDto.getEventIds().stream()).collect(Collectors.toList());
        if (Objects.equals(type, "UPDATE_RECORD")) {
          try {
            googleCalendarRecordService.updateRecord(accessToken, payLoad, calendarId,
                eventIds);

          } catch (Exception e) {
            log.error("error", e);
          }
        }

        if (Objects.equals(type, "DELETE_RECORD")) {
          for (String eventId : eventIds) {
            googleCalendarRecordService.deleteRecord(accessToken, eventId);
          }
        }
      }
    }
  }
}
