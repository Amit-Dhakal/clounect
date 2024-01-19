package jp.co.fsz.clounect.googleCalendarPlugin.service;

import com.google.gson.Gson;
import jp.co.fsz.clounect.core.dto.AppDataDto;
import jp.co.fsz.clounect.core.dto.AppSiteInfoDto;
import jp.co.fsz.clounect.core.dto.CalendarEventDto;
import jp.co.fsz.clounect.core.dto.GoogleCredentialsDto;
import jp.co.fsz.clounect.core.mapper.CredentialsMapper;
import jp.co.fsz.clounect.core.model.AppData;
import jp.co.fsz.clounect.core.model.AppSiteInfo;
import jp.co.fsz.clounect.core.model.AppUsagesLog;
import jp.co.fsz.clounect.core.repository.AppDataRepository;
import jp.co.fsz.clounect.core.repository.AppSiteInfoRepository;
import jp.co.fsz.clounect.core.service.AppDataService;
import jp.co.fsz.clounect.core.service.AppUsagesLogService;
import jp.co.fsz.clounect.googleCalendarPlugin.dto.OAuthToken;
import jp.co.fsz.clounect.googleCalendarPlugin.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.stereotype.Service;

import java.util.*;
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
  private final AppDataService appDataService;
  private final AppUsagesLogService appUsagesLogService;

  /**
   * <p>[概要] CheckTypeServiceのコンストラクタ。</p>
   * <p>[詳細] CheckTypeServiceを初期化するために必要な依存関係を受け取ります。</p>
   * <p>[備考] このサービスはGoogle Calendarの操作に関連する機能を提供します。</p>
   *
   * @param googleCalendarRecordService Google Calendarのレコードに関連するサービス
   * @param jsonDataOptimizationService JSONデータの最適化に関連するサービス
   * @param appSiteInfoRepository       AppSiteInfoのリポジトリ
   * @param credentialsMapper           資格情報のマッピングに使用されるマッパー
   * @param googleOAuthService          Google OAuthに関連するサービス
   * @param appDataRepository           AppDataのリポジトリ
   * @param appDataService
   * @param appUsagesLogService
   * @since 1.0
   */
  public CheckTypeService(GoogleCalendarRecordService googleCalendarRecordService,
      JsonDataOptimizationService jsonDataOptimizationService,
      AppSiteInfoRepository appSiteInfoRepository, CredentialsMapper credentialsMapper,
      GoogleOAuthService googleOAuthService, AppDataRepository appDataRepository,
      AppDataService appDataService, AppUsagesLogService appUsagesLogService) {
    this.googleCalendarRecordService = googleCalendarRecordService;
    this.jsonDataOptimizationService = jsonDataOptimizationService;
    this.appSiteInfoRepository = appSiteInfoRepository;
    this.credentialsMapper = credentialsMapper;
    this.googleOAuthService = googleOAuthService;
    this.appDataService = appDataService;
    this.appUsagesLogService = appUsagesLogService;
  }

  /**
   * <p>[概要] ペイロード情報を処理してGoogle Calendarへの操作を実行するメソッド。</p>
   * <p>[詳細] ペイロード情報とWebhookのUUIDを受け取り、それに基づいてGoogle Calendarへの
   * 追加、更新、削除などの操作を実行します。</p>
   * <p>[備考] WebhookのUUIDに対応するAppSiteInfoが存在し、かつペイロードが正常に処理できる場合に
   * Google Calendarへの操作を実行します。</p>
   *
   * @param payLoad       ペイロード情報のリスト
   * @param uuid          WebhookのUUID
   * @param transactionId Log TransactionId
   * @since 1.0
   */
  public void getPlayLoad(Map<String, Object> payLoad, String uuid, UUID transactionId) {
    Optional<AppSiteInfo> appSiteInfo = appSiteInfoRepository.findByWebhookUrl(uuid);
    AppUsagesLog appUsagesLog = new AppUsagesLog();

    if (appSiteInfo.isPresent()) {
      appUsagesLog.setAppId(appSiteInfo.get().getAppId().getId());
      Map<String, Object> result = jsonDataOptimizationService.processGoogleCalendarRecords(
          payLoad);
      Long appSiteId = appSiteInfo.get().getId();

      GoogleCredentialsDto googleCredentialsDto = credentialsMapper.mapToGoogleCredentials(
          appSiteInfo);
      String clientId = googleCredentialsDto.getClientId();
      String clientSecret = googleCredentialsDto.getClientSecret();
      String refreshToken = googleCredentialsDto.getRefreshToken();

      OAuthToken token = googleOAuthService.getRefreshAccessToken(clientId, clientSecret,
          refreshToken);

      String accessToken = token.getAccessToken();
      String type = (String) result.get("type");
      Integer recordId = (Integer) result.get("recordId");

      //create received payload
      AppDataDto.Payload receivedPayload = new AppDataDto.Payload(
          List.of(new AppDataDto.Data(String.valueOf(recordId), type, payLoad)),
          new ArrayList<>());

      Gson gson = new Gson();
      Optional<AppDataDto> getDetails= Optional.empty();
      AppDataDto appDataDto = new AppDataDto();
      String calendarId = "";
      List<String> eventIds = new ArrayList<>();
      try {
        getDetails = appDataService.getByRecIdAndAppSiteId(
            (Integer) result.get("recordId"), appSiteId);
        if (getDetails.isPresent()) {
          appDataDto = getDetails.get();
          List<CalendarEventDto> calendarEvents = credentialsMapper.getCalendarEvents(
              Optional.of(appDataDto.toEntity()));
          calendarId = calendarEvents.get(0).getCalendarId();

          eventIds = calendarEvents.stream()
              .flatMap(calendarEventDto -> calendarEventDto.getEventIds().stream())
              .toList();
        }
      } catch (InvalidDataAccessResourceUsageException e) {
        log.warn("{}", e.getMessage());
      }

      if (Objects.equals(type, "ADD_RECORD")) {
        Optional<AppSiteInfoDto> appSiteInfoDto = appSiteInfo.map(AppSiteInfoDto:: fromEntity);
        appDataDto.setAppSiteId(appSiteInfoDto.get());
        appDataDto.setReceivedPayload(gson.toJson(receivedPayload));

        appDataDto = appDataService.saveAppData(appDataDto);
        googleCalendarRecordService.addRecord(accessToken, payLoad, appDataDto,
            transactionId);

      } else if (Objects.equals(type, "UPDATE_RECORD")) {
        try {
          if (getDetails.isPresent()) {
            googleCalendarRecordService.updateRecord(accessToken, payLoad, calendarId,
                eventIds, appDataDto, transactionId);
          }
        } catch (Exception e) {
          log.error("error", e);
        }
      } else if (Objects.equals(type, "DELETE_RECORD")) {
        if (getDetails.isPresent()) {
          for (String eventId : eventIds) {
            googleCalendarRecordService.deleteRecord(accessToken, eventId, transactionId);
          }
          AppDataDto.Payload sendPayload = new AppDataDto.Payload(
              List.of(new AppDataDto.Data(String.valueOf(recordId), type, result)), new ArrayList<>());
          appDataDto.setSendPayload(gson.toJson(sendPayload));
          appDataService.saveAppData(appDataDto);
        }
      }

    } else {
      throw new NotFoundException("AppSiteInfo not found in Optional");
    }
  }
}
