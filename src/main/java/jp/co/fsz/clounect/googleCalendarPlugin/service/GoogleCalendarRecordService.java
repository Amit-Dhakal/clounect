package jp.co.fsz.clounect.googleCalendarPlugin.service;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import jp.co.fsz.clounect.googleCalendarPlugin.exception.CouldNotPerformActionException;
import jp.co.fsz.clounect.googleCalendarPlugin.exception.CredentialsMissingException;
import jp.co.fsz.clounect.googleCalendarPlugin.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * <p>[概要] Google Calendar レコードサービスクラス。</p>
 * <p>[詳細] Google Calendar API との連携を行い、カレンダーレコードに関する操作を提供するサービスクラス。</p>
 * <p>[備考] このサービスクラスは、Google Calendar とのデータの受け渡しや処理を担当します。</p>
 * <p>[環境] JDK17.0</p>
 * <p>著作権 FSZ 2024</p>
 *
 * @author 著者FSZ
 * @since 1.0
 */
@Service
@Slf4j
public class GoogleCalendarRecordService {
  private final JsonDataOptimizationService jsonDataOptimizationService;
  private final GoogleCalendarService googleCalendarService;

  public GoogleCalendarRecordService(
      JsonDataOptimizationService jsonDataOptimizationService,
      GoogleCalendarService googleCalendarService) {
    this.jsonDataOptimizationService = jsonDataOptimizationService;
    this.googleCalendarService = googleCalendarService;
  }

  /**
   * <p>[概要] Nullまたは空文字列のチェックを行うメソッド。</p>
   * <p>[詳細] 指定された文字列がNullまたは空文字列であるかを判定し、結果を真偽値で返します。</p>
   * <p>[メソッド名] checkNullAndEmpty</p>
   * <p>[備考] このメソッドは、指定された文字列がNullまたは空文字列の場合にtrueを返し、それ以外の場合にはfalseを返します。</p>
   *
   * @param value チェック対象の文字列
   * @return Nullまたは空文字列であればtrue、それ以外はfalse
   * @since 1.0
   */
  Boolean checkNullAndEmpty(String value) {
    return value == null || value.isEmpty();
  }

  /**
   * <p>[概要] Google Calendar にレコードを追加するメソッド。</p>
   * <p>[詳細] 指定されたアクセストークンとJSONデータを使用して、Google Calendar に新しいレコードを追加します。</p>
   * <p>[備考] このメソッドは、指定されたアクセストークンでGoogle Calendar
   * APIにアクセスし、提供されたJSONデータを新しいレコードとして追加します。</p>
   * <p>データベースが設定された後には、コメントアウトされている部分を有効にしてアクセストークンを取得できるようにしてください。</p>
   *
   * @param accessToken Google Calendar API へのアクセスに使用するアクセストークン
   * @param jsonData    追加するレコードの情報を含むJSONデータのリスト
   * @throws CredentialsMissingException    アクセストークンまたはJSONデータが不足している場合に発生
   * @throws IllegalArgumentException       JSONデータから生成されたイベントが存在しない場合に発生
   * @throws CouldNotPerformActionException カレンダーイベントの作成中に発生したその他の例外
   * @since 1.0
   */
  public void addRecord(String accessToken, List<Map<String, Object>> jsonData, Long appSiteId) {
    try {
      if (checkNullAndEmpty(accessToken) || jsonData == null || jsonData.isEmpty()) {
        throw new CredentialsMissingException("accessToken, JsonData are required");
      }
      Map<String, Object> result = jsonDataOptimizationService.processGoogleCalendarRecords(
          jsonData);
      Event event = (Event) result.get("event");
      List<EventAttendee> participants = castList(result.get("participants"));
      if (event != null) {
        for (EventAttendee participant : participants) {
          googleCalendarService.createCalendarEvent(accessToken, event, participant, appSiteId);
        }
      } else {
        throw new NotFoundException("イベントは存在しません");
      }
    } catch (IOException e) {
      log.error("カレンダーを作成できませんでした");
      throw new CouldNotPerformActionException("カレンダーを作成できませんでした", e);
    }
  }

  /**
   * <p>[概要] Google Calendar のレコードを更新するメソッド。</p>
   * <p>[詳細] 指定されたアクセストークン、JSONデータ、カレンダーID、およびイベントIDを使用して、Google Calendar
   * の既存のレコードを更新します。</p>
   * <p>[備考] このメソッドは、指定されたアクセストークンでGoogle Calendar
   * APIにアクセスし、提供されたJSONデータを使って指定されたイベントを更新します。</p>
   * <p>データベースが設定された後には、コメントアウトされている部分を有効にしてアクセストークンを取得できるようにしてください。</p>
   *
   * @param accessToken Google Calendar API へのアクセスに使用するアクセストークン
   * @param jsonData    更新するレコードの情報を含むJSONデータのリスト
   * @param calendarId  更新対象のカレンダーのID
   * @param eventIds    更新対象のイベントのID
   * @throws CredentialsMissingException    アクセストークン、カレンダーID、イベントID、またはJSONデータが不足している場合に発生
   * @throws IllegalArgumentException       JSONデータから生成されたイベントが存在しない場合に発生
   * @throws CouldNotPerformActionException カレンダーイベントの更新中に発生したその他の例外
   * @since 1.0
   */
  public void updateRecord(String accessToken, List<Map<String, Object>> jsonData,
      String calendarId, List<String> eventIds) {
    try {

      if (checkNullAndEmpty(accessToken) || jsonData == null || jsonData.isEmpty()
          || checkNullAndEmpty(calendarId) || eventIds == null || eventIds.isEmpty()) {
        throw new CredentialsMissingException(
            "accessToken, JsonData, calendarId, eventId are required");
      }
      Map<String, Object> result = jsonDataOptimizationService.processGoogleCalendarRecords(
          jsonData);

      Event event = (Event) result.get("event");

      if (event != null) {
        for (String eventId : eventIds) {

          log.info("event" + event);
          Event updateEventDetails = googleCalendarService.updateCalendarEvent(
              accessToken, calendarId, eventId, event);

          log.info("updateEventDetails" + updateEventDetails);
        }
      } else {
        throw new NotFoundException("イベントは存在しません");
      }
    } catch (Exception e) {
      throw new CouldNotPerformActionException("カレンダーを更新できませんでした", e);
    }

  }

  /**
   * <p>[概要] Google Calendar のレコードを削除するメソッド。</p>
   * <p>[詳細] 指定されたアクセストークンとイベントIDを使用して、Google Calendar から指定されたレコードを削除します。</p>
   * <p>[備考] このメソッドは、指定されたアクセストークンでGoogle Calendar APIにアクセスし、指定されたイベントIDのイベントを削除します。</p>
   * <p>データベースが設定された後には、コメントアウトされている部分を有効にしてアクセストークンを取得できるようにしてください。</p>
   *
   * @param accessToken Google Calendar API へのアクセスに使用するアクセストークン
   * @param eventId     削除対象のイベントのID
   * @throws CredentialsMissingException    アクセストークンまたはイベントIDが不足している場合に発生
   * @throws CouldNotPerformActionException カレンダーイベントの削除中に発生したその他の例外
   * @since 1.0
   */
  public void deleteRecord(String accessToken, String eventId) {
    try {

      if (checkNullAndEmpty(accessToken) || checkNullAndEmpty(eventId)) {
        throw new CredentialsMissingException("Access token and eventId are required");
      }
      googleCalendarService.deleteCalendarEvent(accessToken, eventId);
    } catch (Exception e) {
      throw new CouldNotPerformActionException("カレンダーを削除できませんでした", e);
    }
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
}
