package jp.co.fsz.clounect.googleCalendarPlugin.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import jp.co.fsz.clounect.googleCalendarPlugin.dto.EventDto;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>[概要] JSONデータ最適化サービスクラス。</p>
 * <p>[詳細] JSONデータの最適化および操作を提供するサービスクラス。</p>
 * <p>[備考] このサービスクラスは、JSONデータの最適化に関連する操作を提供します。</p>
 * <p>[環境] JDK17.0</p>
 * <p>著作権 FSZ 2024</p>
 *
 * @author 著者FSZ
 * @since 1.0
 */
@Service
@Slf4j
public class JsonDataOptimizationService {
  private final ObjectMapper objectMapper = new ObjectMapper();
  private static final EventDto eventDto = new EventDto();
  private static final String bodyData = eventDto.getBodyData();
  private static final String recordData = eventDto.getRecordData();
  private static final String participantsAndDateData = eventDto.getParticipantsAndDateData();
  private static final String participantsData = eventDto.getParticipantsData();
  private static final String eventTitleData = eventDto.getEventTitleData();
  private static final String recordTitleData = eventDto.getRecordTitleData();
  private static final String dateData = eventDto.getDateData();
  private static final String meetingDateData = eventDto.getMeetingDateData();

  private static final String INPUT_DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mmXXX";
  private static final String OUTPUT_DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX";

  /**
   * <p>[概要] Google Calendar レコードを処理するメソッド。</p>
   * <p>[詳細] 指定された JSON データから Google Calendar イベントを生成および処理するメソッド。</p>
   * <p>[備考] このメソッドは、JSONデータからGoogle Calendarイベントを生成し、必要な処理を行います。</p>
   *
   * @param jsonData JSONデータのリスト、各要素はMap<String, Object>形式であることが期待されます
   * @return Google Calendar イベントオブジェクト
   * @author フィーラーシステムZ
   * @since 1.0
   */
  public Map<String, Object> processGoogleCalendarRecords(
      Map<String, Object> jsonData) {
    Event constructedEvent = null;
    List<String> attendees = new ArrayList<>();
    String type = null;
    Integer recordId = null;

      try {
        String bodyJson = objectMapper.writeValueAsString(jsonData);
        Map<String, Object> body = objectMapper.readValue(bodyJson,
            new TypeReference<>() {
            });

        
        type = (String) body.get("type");
        recordId =  (Integer) body.get("recordId");
        Map<String, Object> record = castMap(body.get(recordData));

        Map<String, Object> participantsAndDate = castMap(
            record.get(participantsAndDateData));

        //TODO: get user id, search in db and map email
        List<List<String>> participants = castList(
            participantsAndDate.get(participantsData));

        if (!participants.isEmpty()) {
          attendees = new ArrayList<>();

          for (List<String> participantGroup : participants) {
            attendees.addAll(participantGroup);
          }
          String eventTitle = cast(record.get(eventTitleData), String.class);
          String location = cast(record.get(recordTitleData), String.class);

          Map<String, Object> dateMap = castMap(record.get(dateData));
          if (dateMap != null) {
            List<Object> startDateList = castList(dateMap.get(meetingDateData));
            List<Object> endDateList = castList(dateMap.get(meetingDateData));

            if (!startDateList.isEmpty() && !endDateList.isEmpty()) {
              String startDateString = String.valueOf(startDateList.get(0));
              String endDateString = String.valueOf(endDateList.get(1));

              SimpleDateFormat inputDateFormat = new SimpleDateFormat(
                  INPUT_DATE_FORMAT_PATTERN);
              SimpleDateFormat outputDateFormat = new SimpleDateFormat(
                  OUTPUT_DATE_FORMAT_PATTERN);

              Date startDate = inputDateFormat.parse(startDateString);
              Date endDate = inputDateFormat.parse(endDateString);

              String formattedStartDate = outputDateFormat.format(startDate);
              String formattedEndDate = outputDateFormat.format(endDate);

              constructedEvent = new Event().setSummary(eventTitle).setLocation(location)
                  .setStart(
                      new EventDateTime().setDateTime(new DateTime(formattedStartDate)))
                  .setEnd(
                      new EventDateTime().setDateTime(new DateTime(formattedEndDate)));
              printDebugInfo(constructedEvent, participants);
            } else {
              log.error("Invalid date format in record: " + record);
            }
          } else {
            log.error("Date map is null in record: " + record);
          }
        } else {
          log.info("Processing record: " + record);
          log.info("Participants: No participants");
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }


    Map<String, Object> result = new HashMap<>();
    result.put("event", constructedEvent);
    result.put("participants", convertToEventAttendees(attendees));
    result.put("type", type);
    result.put("recordId", recordId);
    return result;
  }

  /**
   * <p>[概要] イベント参加者のメールアドレスリストをGoogle Calendar用の {@code EventAttendee} リストに変換するメソッド。</p>
   * <p>[詳細] 指定された参加者のメールアドレスリストから、それぞれのメールアドレスを持つ {@code EventAttendee}
   * オブジェクトを生成し、リストに追加して返します。</p>
   * <p>[備考] このメソッドは、Google Calendar イベントの参加者リストを構築するために使用されます。</p>
   *
   * @param attendees イベントの参加者のメールアドレスリスト
   * @return Google Calendar用の {@code EventAttendee} リスト
   * @since 1.0
   */
  private List<EventAttendee> convertToEventAttendees(List<String> attendees) {
    List<EventAttendee> eventAttendees = new ArrayList<>();
    for (String attendee : attendees) {
      //TODO: Remove later
      eventAttendees.add(new EventAttendee().setEmail("admin@chocodx.site"));
    }
    return eventAttendees;
  }

  /**
   * <p>[概要] デバッグ情報を出力するメソッド。</p>
   * <p>[詳細] 構築されたイベントと参加者リストのデバッグ情報をコンソールに出力するメソッド。</p>
   * <p>[備考] このメソッドは、デバッグ目的で構築されたイベントと参加者リストの情報を表示します。</p>
   *
   * @param constructedEvent 構築されたイベント
   * @param participants     参加者リスト、各参加者はリスト内の要素として表現されています
   * @since 1.0
   */
  private void printDebugInfo(Event constructedEvent, List<List<String>> participants) {
    log.info("Debug: Participants - " + participants);
    log.info("Google Calendar Format: \n" + constructedEvent);
  }

  /**
   * OAuth2コールバックで受け取ったステート情報を解析します。
   * <p>このメソッドは、JSON形式のステート文字列から必要な情報を抽出し、
   * "currentUrl"や"clientSecret"などのキーで表現されるマップに格納して返します。</p>
   *
   * @param state JSON形式のステート文字列で、必要な情報を含んでいます。
   * @return 解析されたステート情報を含むマップ。"currentUrl"や"clientSecret"などのキーがあります。
   * @throws ParseException JSON形式のステート文字列を解析する際にエラーが発生した場合。
   * @since 1.0
   */
  public Map<String, String> parseState(String state, String code) throws ParseException {
    JSONParser parser = new JSONParser();
    JSONObject json = (JSONObject) parser.parse(state);

    Map<String, String> parsedState = new HashMap<>();
    parsedState.put("currentUrl", (String) json.get("currentUrl"));
    parsedState.put("clientSecret", (String) json.get("clientSecret"));
    parsedState.put("clientId", (String) json.get("clientId"));
    parsedState.put("redirectUri", (String) json.get("redirectUri"));
    parsedState.put("code", code);
    return parsedState;
  }

  /**
   * OAuth2コールバックで受け取ったステート情報を解析します。
   * <p>このメソッドは、JSON形式のステート文字列から必要な情報を抽出し、
   * "currentUrl"や"clientSecret"などのキーで表現されるマップに格納して返します。</p>
   *
   * @param data JSON形式のステート文字列で、必要な情報を含んでいます。
   * @return 解析されたステート情報を含むマップ。"currentUrl"や"clientSecret"などのキーがあります。
   * @throws ParseException JSON形式のステート文字列を解析する際にエラーが発生した場合。
   * @since 1.0
   */
  public Map<String, String> getSfaData(String data) throws ParseException {
    JSONParser parser = new JSONParser();
    JSONObject json = (JSONObject) parser.parse(data);

    Map<String, String> getData = new HashMap<>();
    getData.put("tenant", (String) json.get("tenant"));
    getData.put("api-key", (String) json.get("api-key"));
    getData.put("teble-id", (String) json.get("teble-id"));
    getData.put("panel-id", (String) json.get("panel-id"));
    getData.put("filter-id", (String) json.get("filter-id"));
    getData.put("schedule-id", (String) json.get("schedule-id"));
    getData.put("subject-id", (String) json.get("subject-id"));
    getData.put("location-id", (String) json.get("location-id"));
    getData.put("detail-id", (String) json.get("detail-id"));

    return getData;
  }

  /**
   * 入力された文字列に基づいてユーザーデータを取得します。
   *
   * @param userData ユーザーデータを含む入力文字列。
   * @return ユーザーデータを含むマップのリスト。
   * @throws IOException 取得プロセス中にエラーが発生した場合。
   * @since 1.0
   */
  public List<Map<String, Object>> getUserData(String userData) {
    List<Map<String, Object>> resultList = new ArrayList<>();

    try {
      JsonNode jsonNode = objectMapper.readTree(userData);

      if (jsonNode.isArray()) {
        for (JsonNode userNode : jsonNode) {
          Map<String, Object> userMap = new HashMap<>();
          userMap.put("username", userNode.get("username").asText());
          userMap.put("justSfaUserId", userNode.get("justSfaUserId").asText());
          userMap.put("googleEmail", userNode.get("googleEmail").asText());
          userMap.put("linkable", userNode.get("linkable").asBoolean());
          userMap.put("validateEmailFlag", userNode.get("validateEmailFlag").asBoolean());
          resultList.add(userMap);
        }
      }
    } catch (IOException e) {
      log.error("Error :" + e.getMessage());
    }

    return resultList;
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
   * <p>[概要] ジェネリックな型に指定されたクラスに安全に変換を行うメソッド。</p>
   * <p>[詳細] 指定されたオブジェクトをジェネリックな型に、指定されたクラスに安全に変換して返すメソッド。</p>
   * <p>[備考] このメソッドは、オブジェクトが指定されたクラスにキャスト可能であるかどうかを確認し、変換を行います。</p>
   *
   * @param obj   キャスト対象のオブジェクト
   * @param clazz 変換後のクラス
   * @param <T>   ジェネリックな型
   * @return 指定されたクラスにキャストされたオブジェクト
   * @throws ClassCastException オブジェクトを指定されたクラスにキャストできない場合に発生
   * @since 1.0
   */
  private <T> T cast(Object obj, Class<T> clazz) {
    if (clazz.isInstance(obj)) {
      return clazz.cast(obj);
    } else {
      throw new ClassCastException("Cannot cast object to specified class");
    }
  }
}