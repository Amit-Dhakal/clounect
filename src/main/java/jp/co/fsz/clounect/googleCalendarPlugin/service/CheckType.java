package jp.co.fsz.clounect.googleCalendarPlugin.service;

import jp.co.fsz.clounect.core.dto.CalendarEventDto;
import jp.co.fsz.clounect.core.dto.GoogleCredentialsDto;
import jp.co.fsz.clounect.core.mapper.CredentialsMapper;
import jp.co.fsz.clounect.core.model.AppData;
import jp.co.fsz.clounect.core.model.AppSiteInfo;
import jp.co.fsz.clounect.core.repository.AppDataRepository;
import jp.co.fsz.clounect.core.repository.AppSiteInfoRepository;
import jp.co.fsz.clounect.core.user.dto.UserDto;
import jp.co.fsz.clounect.core.user.service.UserService;
import jp.co.fsz.clounect.googleCalendarPlugin.dto.OAuthToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.ls.LSException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CheckType {
  private final GoogleCalendarRecordService googleCalendarRecordService;
  private final JsonDataOptimizationService jsonDataOptimizationService;
  private final AppSiteInfoRepository appSiteInfoRepository;
  private final UserService userService;
  private final CredentialsMapper credentialsMapper;
  private final GoogleOAuthService googleOAuthService;
  private final AppDataRepository appDataRepository;
  AppData appData = new AppData();

  public CheckType(GoogleCalendarRecordService googleCalendarRecordService,
      JsonDataOptimizationService jsonDataOptimizationService,
      AppSiteInfoRepository appSiteInfoRepository, UserService userService,
      CredentialsMapper credentialsMapper, GoogleOAuthService googleOAuthService,
      AppDataRepository appDataRepository) {
    this.googleCalendarRecordService = googleCalendarRecordService;
    this.jsonDataOptimizationService = jsonDataOptimizationService;
    this.appSiteInfoRepository = appSiteInfoRepository;
    this.userService = userService;
    this.credentialsMapper = credentialsMapper;
    this.googleOAuthService = googleOAuthService;
    this.appDataRepository = appDataRepository;
  }

  public void getPlayLoad(List<Map<String, Object>> payLoad, String uuid) {
    Optional<AppSiteInfo> appSiteInfo = appSiteInfoRepository.findByWebhookUrl(uuid);
    if(appSiteInfo.isPresent()){
      Map<String, Object> result = jsonDataOptimizationService.processGoogleCalendarRecords(
          payLoad);

      Long appSiteId = appSiteInfo.get().getId();
      //    UserDto user = userService.getUserById(1);
      //    Long userId = user.getId();
      //    Optional<AppSiteInfo> existingRecord = appSiteInfoRepository.findByUserId(userId);

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
