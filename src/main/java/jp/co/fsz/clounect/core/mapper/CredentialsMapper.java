package jp.co.fsz.clounect.core.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.fsz.clounect.core.dto.CalendarEventDto;
import jp.co.fsz.clounect.core.dto.GoogleCredentialsDto;
import jp.co.fsz.clounect.core.dto.JustSfaDto;
import jp.co.fsz.clounect.core.model.AppData;
import jp.co.fsz.clounect.core.model.AppSiteInfo;
import jp.co.fsz.clounect.googleCalendarPlugin.dto.CredentialsDto;
import jp.co.fsz.clounect.googleCalendarPlugin.dto.UserDetails;
import jp.co.fsz.clounect.googleCalendarPlugin.exception.CouldNotPerformActionException;
import jp.co.fsz.clounect.googleCalendarPlugin.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * <p>[概要] 認証情報管理モジュールにおけるエンティティとDTO間でのデータ変換を担当するMapperコンポーネント。</p>
 * <p>[詳細]
 * このクラスは、認証情報管理モジュールにおけるエンティティとDTO間でのデータ変換を行います。JacksonのObjectMapperを使用してJSONのシリアライズおよびデシリアライズを提供し、エンティティからDTOへの変換およびその逆を行うメソッドを提供します。</p>
 * <p>[備考]
 * このコンポーネントは@Componentアノテーションを使用してSpringのコンポーネントスキャンを有効にしています。AuditableEntityを拡張してエンティティの変更トラッキングの監査機能を継承しています。</p>
 * <p>[環境] JDK17.0</p>
 * <p>著作権 FSZ 2024</p>
 *
 * @author FSZ
 * @since 1.0
 */
@Component
@Slf4j
public class CredentialsMapper {

  private final ObjectMapper objectMapper;

  public CredentialsMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /**
   * <p>[概要] Optional&lt;AppSiteInfo&gt;からGoogleCredentialsDtoへのデータ変換を行うメソッド。</p>
   * <p>[詳細] Optional&lt;AppSiteInfo&gt;を受け取り、その中のデータをGoogleCredentialsDtoに変換して返します。</p>
   * <p>[備考] アプリケーションサイト情報が存在する場合、その情報からGoogleCredentialsDtoを生成して返します。</p>
   *
   * @param appSiteInfoOptional アプリケーションサイト情報のOptional
   * @return 変換されたGoogleCredentialsDto
   * @throws NotFoundException アプリケーションサイト情報が存在しない場合や、必要な情報が不足している場合に発生
   * @since 1.0
   */
  public GoogleCredentialsDto mapToGoogleCredentials(
      Optional<AppSiteInfo> appSiteInfoOptional) {
    if (appSiteInfoOptional.isPresent()) {
      AppSiteInfo appSiteInfo = appSiteInfoOptional.get();
      String configJson = appSiteInfo.getConfig();
      Map<String, Object> configMap = parseConfigJson(configJson);

      if (configMap.containsKey("googleRecords") && configMap.get(
          "googleRecords") instanceof Map) {
        Map<String, Object> googleRecordsMap = castMap(configMap.get("googleRecords"));

        return GoogleCredentialsDto.builder()
            .clientId((String) googleRecordsMap.get("clientId"))
            .currentUrl((String) googleRecordsMap.get("currentUrl"))
            .redirectUri((String) googleRecordsMap.get("redirectUri"))
            .clientSecret((String) googleRecordsMap.get("clientSecret"))
            .refreshToken((String) configMap.get("refreshToken")).build();
      } else {
        throw new NotFoundException(
            "'googleRecords' key is missing or not a Map in configMap");
      }
    } else {
      throw new NotFoundException("AppSiteInfo not found in Optional");
    }
  }

  /**
   * <p>[概要] JustSfaDtoにデータをマッピングするメソッド。</p>
   * <p>[詳細] 認証情報からユーザーのIDを取得し、そのIDを使用してAppSiteInfoを取得します。
   * 取得したAppSiteInfoからJustSfaDtoに必要な情報を抽出し、新しいJustSfaDtoを構築して返します。</p>
   * <p>[備考] 認証情報、ユーザー情報、およびAppSiteInfoのデータを使用してJustSfaDtoにデータをマッピングします。</p>
   *
   * @return マッピングされたJustSfaDto
   * @throws NotFoundException 認証情報やユーザー情報が不足している場合、またはデータの変換に失敗した場合に発生
   * @since 1.0
   */
  public JustSfaDto mapToJustSfa(Optional<AppSiteInfo> appSiteInfoOptional) {
    if (appSiteInfoOptional.isPresent()) {
      AppSiteInfo appSiteInfo = appSiteInfoOptional.get();
      String configJson = appSiteInfo.getConfig();
      Map<String, Object> configMap = parseConfigJson(configJson);

      if (configMap.containsKey("googleRecords") && configMap.get(
          "googleRecords") instanceof Map) {
        Map<String, Object> googleRecordsMap = castMap(configMap.get("justSfa"));

        return JustSfaDto.builder().tenant((String) configMap.get("tenant"))
            .apiKey((String) googleRecordsMap.get("api-key"))
            .panelId((String) configMap.get("panel-id"))
            .tableId((String) googleRecordsMap.get("teble-id"))
            .detailId((String) googleRecordsMap.get("detail-id"))
            .filterId((String) googleRecordsMap.get("filter-id"))
            .subjectId((String) googleRecordsMap.get("subject-id"))
            .locationId((String) googleRecordsMap.get("location-id"))
            .scheduleId((String) googleRecordsMap.get("schedule-id")).build();
      } else {
        throw new NotFoundException("AppSiteInfo not found in Optional");
      }
    } else {
      throw new NotFoundException("AppSiteInfo not found in Optional");
    }
  }

  /**
   * <p>[概要] クライアントIDとクライアントシークレットを取得するメソッド。</p>
   * <p>[詳細] 認証情報からユーザーのIDを取得し、そのIDを使用してAppSiteInfoを取得します。
   * 取得したAppSiteInfoからGoogle関連のクライアントIDとクライアントシークレットを抽出し、新しいCredentialsDtoに構築して返します。</p>
   * <p>[備考] 認証情報、ユーザー情報、およびAppSiteInfoのデータを使用してクライアントIDとクライアントシークレットを取得します。</p>
   *
   * @return クライアントIDとクライアントシークレットが格納されたCredentialsDto
   * @throws NotFoundException 認証情報やユーザー情報が不足している場合、またはデータの変換に失敗した場合に発生
   * @since 1.0
   */
  public CredentialsDto getClientSecretAndId(Optional<AppSiteInfo> appSiteInfoOptional) {
    if (appSiteInfoOptional.isPresent()) {
      AppSiteInfo appSiteInfo = appSiteInfoOptional.get();
      String configJson = appSiteInfo.getConfig();
      Map<String, Object> configMap = parseConfigJson(configJson);

      if (configMap.containsKey("googleRecords") && configMap.get(
          "googleRecords") instanceof Map) {
        Map<String, Object> googleRecordsMap = castMap(configMap.get("googleRecords"));

        return CredentialsDto.builder()
            .clientId(getStringValue(googleRecordsMap, "clientId"))
            .clientSecret(getStringValue(googleRecordsMap, "clientSecret")).build();
      } else {
        throw new NotFoundException(
            "'googleRecords' key is missing or not a Map in configMap");
      }
    } else {
      throw new NotFoundException("AppSiteInfo not found in Optional");
    }
  }

  /**
   * <p>[概要] 文字列値を取得するメソッド。</p>
   * <p>[詳細] 指定されたキーに対応するマップ内のオブジェクトを取得し、そのオブジェクトが文字列型であれば文字列値を返します。
   * キーが存在しない場合や対応するオブジェクトが文字列型でない場合には、例外をスローします。</p>
   * <p>[備考] Google Calendarの設定情報マップから指定されたキーに対応する文字列値を取得します。</p>
   *
   * @param map 設定情報を格納したマップ
   * @param key 取得したい値のキー
   * @return 指定されたキーに対応する文字列値
   * @throws NotFoundException キーが存在しないか、対応するオブジェクトが文字列型でない場合に発生
   * @since 1.0
   */
  private String getStringValue(Map<String, Object> map, String key) {
    if (map.containsKey(key)) {
      Object value = map.get(key);
      if (value instanceof String) {
        return (String) value;
      } else {
        throw new NotFoundException(
            "'" + key + "' value is not a String in googleRecordsMap");
      }
    } else {
      throw new NotFoundException("'" + key + "' key is missing in googleRecordsMap");
    }
  }

  /**
   * <p>[概要] ユーザー詳細情報リストをマッピングするメソッド。</p>
   * <p>[詳細] アプリケーションサイト情報のオプションからユーザー詳細情報リストをマッピングします。
   * アプリケーションサイト情報が存在する場合、その設定情報からユーザー詳細情報リストを抽出して返します。 オプションが空である場合は、実行時例外をスローします。</p>
   * <p>[備考] ユーザー詳細情報リストは、各ユーザーの詳細情報を格納したDTOのリストです。</p>
   *
   * @param appSiteInfoOptional アプリケーションサイト情報のオプション
   * @return ユーザー詳細情報リスト
   * @throws CouldNotPerformActionException オプションが空である場合に発生
   * @since 1.0
   */
  public List<UserDetails> mapToUserDetailsList(
      Optional<AppSiteInfo> appSiteInfoOptional) {
    ObjectMapper objectMapper = new ObjectMapper();

    try {
      if (appSiteInfoOptional.isPresent()) {
        AppSiteInfo appSiteInfo = appSiteInfoOptional.get();
        String configJson = appSiteInfo.getConfig();
        Map<String, Object> configMap = parseConfigJson(configJson);

        List<Map<String, Object>> userRecords = castList(configMap.get("userRecord"));

        List<UserDetails> userDetailsList = new ArrayList<>();

        for (Map<String, Object> userRecord : userRecords) {
          UserDetails userDetails = objectMapper.convertValue(userRecord,
              UserDetails.class);
          userDetailsList.add(userDetails);
        }

        return userDetailsList;

      }
    } catch (IllegalArgumentException e) {
      throw new CouldNotPerformActionException("Error parsing the configuration" + e);
    }
    return null;
  }

  /**
   * <p>[概要] カレンダーイベントDTOリストを取得するメソッド。</p>
   * <p>[詳細] アプリケーションデータのオプションからカレンダーイベントDTOリストを取得します。
   * アプリケーションデータが存在する場合、その受信ペイロードからカレンダーイベントDTOリストを生成して返します。
   * オプションが空である場合は、空のリストを返します。</p>
   * <p>[備考] カレンダーイベントDTOリストは、各カレンダーイベントの情報を格納したDTOのリストです。</p>
   *
   * @param appDataOptional アプリケーションデータのオプション
   * @return カレンダーイベントDTOリスト
   * @throws NotFoundException キーが存在しないか、対応するオブジェクトが文字列型でない場合に発生
   * @since 1.0
   */
  public List<CalendarEventDto> getCalendarEvents(Optional<AppData> appDataOptional) {
    List<CalendarEventDto> calendarEvents = new ArrayList<>();

    if (appDataOptional.isPresent()) {
      try {
        AppData appData = appDataOptional.get();
        String receivedPayload = appData.getReceivedPayload();

        JsonNode receivedDataArray = objectMapper.readTree(receivedPayload);

        for (JsonNode eventData : receivedDataArray) {
          String eventId = eventData.path("id").asText();
          String calendarId = eventData.path("organizer").path("email").asText();

          CalendarEventDto calendarEventDto = new CalendarEventDto();
          calendarEventDto.setCalendarId(calendarId);

          List<String> eventIds = new ArrayList<>();
          eventIds.add(eventId);
          calendarEventDto.setEventIds(eventIds);

          calendarEvents.add(calendarEventDto);
        }
      } catch (Exception e) {
        throw new NotFoundException("app optional data was not found" + e);
      }
    }
    return calendarEvents;
  }

  /**
   * <p>[概要] ジェネリックな型のリストに変換を行うメソッド。</p>
   * <p>[詳細] 指定されたオブジェクトをジェネリックな型のリストに安全に変換して返すメソッド。</p>
   * <p>[備考] このメソッドは、オブジェクトがリストにキャスト可能であるかどうかを確認し、変換を行います。</p>
   *
   * @param obj キャスト対象のオブジェクト
   * @param <T> ジェネリックな型
   * @return ジェネリックな型のリストにキャストされたオブジェクト
   * @throws ClassCastException オブジェクトをリストにキャストできない場合に発生
   * @since 1.0
   */
  @SuppressWarnings("unchecked")
  private <T> List<T> castList(Object obj) {
    if (obj instanceof List) {
      return (List<T>) obj;
    } else {
      throw new ClassCastException("Cannot cast object to List");
    }
  }

  /**
   * <p>[概要] JSON形式の設定文字列を解析してマップに変換するメソッド。</p>
   * <p>[詳細] JSON形式の設定文字列を受け取り、Jackson ObjectMapperを使用して
   * マップに変換して返します。</p>
   * <p>[備考] 設定文字列が不正なJSON形式である場合や、変換中にIOExceptionが発生した場合は、
   * RuntimeExceptionがスローされます。</p>
   *
   * @param configJson JSON形式の設定文字列
   * @return 設定文字列を変換したマップ
   * @since 1.0
   */
  private Map<String, Object> parseConfigJson(String configJson) {
    try {
      return objectMapper.readValue(configJson, new TypeReference<>() {
      });
    } catch (IOException e) {
      throw new CouldNotPerformActionException("Error deserializing config", e);
    }
  }

  /**
   * <p>[概要] ジェネリックな型にマップ変換を行うメソッド。</p>
   * <p>[詳細] 指定されたオブジェクトをジェネリックな型にキャストして返すメソッド。</p>
   * <p>[備考] このメソッドは、ジェネリックな型に対して安全なマップ変換を提供します。</p>
   *
   * @param obj キャスト対象のオブジェクト
   * @param <T> ジェネリックな型
   * @return ジェネリックな型にキャストされたオブジェクト
   * @since 1.0
   */
  @SuppressWarnings("unchecked")
  private <T> T castMap(Object obj) {
    return (T) obj;
  }

}

