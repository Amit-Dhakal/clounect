package jp.co.fsz.clounect.googleCalendarPlugin.service;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.Events;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import jp.co.fsz.clounect.googleCalendarPlugin.exception.CouldNotPerformActionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * <p>[概要] Google Calendar サービスクラス。</p>
 * <p>[詳細] Google Calendar API へのアクセスおよび操作を提供するサービスクラス。</p>
 * <p>[備考] HTTP トランスポート、JSON ファクトリ、アプリケーション名の設定が必要です。</p>
 * <p>[環境] JDK17.0</p>
 * <p>著作権 FSZ 2024</p>
 *
 * @author 著者FSZ
 * @since 1.0
 */
@Service
@Slf4j
public class GoogleCalendarService {
  private final HttpTransport httpTransport;
  private final JsonFactory jsonFactory;
  private final String applicationName;
  private final GoogleRecordService googleRecordService;

  /**
   * <p>[概要] Google Calendar サービスを初期化するためのコンストラクタ。</p>
   * <p>[詳細] 指定された HTTP トランスポート、JSON ファクトリ、およびアプリケーション名を使用して、Google Calendar
   * サービスを初期化します。</p>
   * <p>[備考] なし。</p>
   *
   * @param httpTransport     Google Calendar サービスへの HTTP トランスポート
   * @param jsonFactory       JSON ファクトリ
   * @param applicationName   アプリケーション名
   * @param googleRecordService
   * @since 1.0
   */
  @Autowired
  public GoogleCalendarService(HttpTransport httpTransport, JsonFactory jsonFactory,
      String applicationName, GoogleRecordService googleRecordService) {

    this.httpTransport = httpTransport;
    this.jsonFactory = jsonFactory;
    this.applicationName = applicationName;
    this.googleRecordService = googleRecordService;
  }

  /**
   * <p>[概要] アクセストークンを使用して新しいカレンダーイベントを作成するメソッド。</p>
   * <p>[詳細] アクセストークンを使用して、指定されたイベントの詳細を含む新しいカレンダーイベントを作成します。</p>
   * <p>[備考] 開始日時や場所などのイベントの詳細は、引数として渡された Event オブジェクトに設定されている必要があります。</p>
   *
   * @param accessToken  Google Calendar API にアクセスするためのアクセストークン
   * @param eventDetails 新しいイベントの詳細を含む Event オブジェクト
   * @throws IOException                    カレンダーイベントの作成中にエラーが発生した場合
   * @throws CouldNotPerformActionException イベントの作成中にエラーが発生した場合は
   * @since 1.0
   */
  public Event createCalendarEvent(String accessToken, Event eventDetails,
      EventAttendee participant, UUID transactionId) throws IOException {
    try {
      GoogleCredentials credentials = GoogleCredentials.newBuilder()
          .setAccessToken(new AccessToken(accessToken, null)).build();

      HttpRequestInitializer requestInitializer = request -> request.getHeaders()
          .setAuthorization("Bearer " + credentials.getAccessToken().getTokenValue());

      Calendar calendar = new Calendar.Builder(httpTransport, jsonFactory,
          requestInitializer).setApplicationName(applicationName).build();
      eventDetails.setAttendees(Collections.singletonList(participant));

      Event createdData = calendar.events().insert("primary", eventDetails).execute();
      log.info("Calendar created: " + createdData);
      return createdData;
    } catch (CouldNotPerformActionException e) {
      log.error("Error creating Event: ", e.getMessage());

    }
    return null;
  }

  /**
   * <p>[概要] アクセストークンを使用して指定されたカレンダーイベントを削除するメソッド。</p>
   * <p>[詳細] アクセストークンを使用して、指定されたカレンダーのイベントを削除します。</p>
   * <p>[備考] カレンダーイベントを削除するには、対象のイベントのIDを指定してください。</p>
   *
   * @param accessToken Google Calendar API にアクセスするためのアクセストークン
   * @param eventId     削除するイベントのID
   * @throws IOException                 イベントの削除中にエラーが発生した場合
   * @throws CouldNotPerformActionException イベントの削除中にエラーが発生した場合
   * @since 1.0
   */
  public void deleteCalendarEvent(String accessToken, String eventId) throws IOException {
    try {
      GoogleCredentials credentials = GoogleCredentials.newBuilder()
          .setAccessToken(new AccessToken(accessToken, null)).build();

      HttpRequestInitializer requestInitializer = request -> request.getHeaders()
          .setAuthorization("Bearer " + credentials.getAccessToken().getTokenValue());

      Calendar calendar = new Calendar.Builder(httpTransport, jsonFactory,
          requestInitializer).setApplicationName(applicationName).build();

      calendar.events().delete("primary", eventId).execute();
    } catch (CouldNotPerformActionException e) {
      throw new CouldNotPerformActionException("Failed to delete CalendarEvent", e);
    }
  }

  /**
   * <p>[概要] アクセストークンとカレンダーIDを使用して指定されたイベントを更新するメソッド。</p>
   * <p>[詳細] アクセストークンとカレンダーIDを使用して、指定されたイベントの詳細情報を更新します。</p>
   * <p>[備考] イベントの開始日時、終了日時、場所、サマリーなどが更新されたイベントの情報を渡してください。</p>
   *
   * @param accessToken         Google Calendar API にアクセスするためのアクセストークン
   * @param calendarId          イベントが存在するカレンダーのID
   * @param eventId             更新するイベントのID
   * @param updatedEventDetails 更新後のイベントの詳細情報
   * @return 更新されたイベントの情報
   * @throws IOException イベントの更新中にエラーが発生した場合
   * @throws CouldNotPerformActionException イベント更新時にエラーが発生した場合
   * @since 1.0
   */
  public Event updateCalendarEvent(String accessToken, String calendarId, String eventId,
      Event updatedEventDetails) {
    try {
      AccessToken token = new AccessToken(accessToken, null);
      GoogleCredentials credentials = GoogleCredentials.newBuilder().setAccessToken(token)
          .build();

      HttpRequestInitializer requestInitializer = request -> request.getHeaders()
          .setAuthorization("Bearer " + credentials.getAccessToken().getTokenValue());

      Calendar calendar = new Calendar.Builder(httpTransport, jsonFactory,
          requestInitializer).setApplicationName(applicationName).build();

      Event existingEventData = calendar.events().get(calendarId, eventId).execute();

      existingEventData.setSummary(updatedEventDetails.getSummary());
      existingEventData.setStart(updatedEventDetails.getStart());
      existingEventData.setEnd(updatedEventDetails.getEnd());
      existingEventData.setLocation(updatedEventDetails.getLocation());

      Event updatedData = calendar.events().update(calendarId, eventId, existingEventData)
          .execute();
      log.info("UpdatedData: " + updatedData);

      return updatedData;
    } catch (IOException e) {
      log.error("Error");
      throw new CouldNotPerformActionException("Failed to update CalendarEvent", e);
    }
  }

  /**
   * <p>[概要] アクセストークンとカレンダーIDを使用して指定された日付範囲内のイベントを一覧表示するメソッド。</p>
   * <p>[詳細] アクセストークンとカレンダーIDを使用して、指定された日付範囲内のイベントを一覧表示します。</p>
   * <p>[備考] 開始日と終了日の形式は "yyyy-MM-dd" です。</p>
   *
   * @param accessToken Google Calendar API にアクセスするためのアクセストークン
   * @param calendarId  イベントを取得するカレンダーのID
   * @param startDate   一覧表示するイベントの開始日（形式: "yyyy-MM-dd"）
   * @param endDate     一覧表示するイベントの終了日（形式: "yyyy-MM-dd"）
   * @return 指定された日付範囲内のイベントのリスト
   * @throws IOException                 イベントの取得中にエラーが発生した場合
   * @throws CouldNotPerformActionException イベントの取得中にエラーが発生した場合
   * @since 1.0
   */
  public List<Event> listCalendarEvents(String accessToken, String calendarId,
      String startDate, String endDate) throws IOException {
    try {
      GoogleCredentials credentials = GoogleCredentials.newBuilder()
          .setAccessToken(new AccessToken(accessToken, null)).build();

      HttpRequestInitializer requestInitializer = request -> request.getHeaders()
          .setAuthorization("Bearer " + credentials.getAccessToken().getTokenValue());

      Calendar calendar = new Calendar.Builder(httpTransport, jsonFactory,
          requestInitializer).setApplicationName(applicationName).build();

      DateTime startDateTime = new DateTime(startDate);
      DateTime endDateTime = new DateTime(endDate);

      Events events = calendar.events().list(calendarId).setTimeMin(startDateTime)
          .setTimeMax(endDateTime).execute();

      return events.getItems();
    } catch (CouldNotPerformActionException e) {
      throw new CouldNotPerformActionException("Failed to List CalendarEvent", e);
    }
  }

  /**
   * <p>[概要] アクセストークンとカレンダーIDを使用してカレンダーアクセスを確認するメソッド。</p>
   * <p>[詳細] 提供されたアクセストークンとカレンダーIDリストを使用して、各カレンダーへのアクセスを確認します。</p>
   * <p>[備考] 開始日と終了日の形式は "yyyy-MM-dd" です。</p>
   *
   * @param accessToken Google Calendar API アクセストークン
   * @param email       カレンダーIDを表すメールアドレスのリスト
   * @return 各カレンダーのアクセスステータスを示す真偽値のリスト
   * @throws GoogleJsonResponseException アクセス確認中にエラーが発生した場合
   * @throws IOException                 アクセス確認中にエラーが発生した場合
   * @since 1.0
   */
  public boolean checkCalendarAccess(String accessToken, String email) throws GoogleJsonResponseException {
    GoogleCredentials credentials = GoogleCredentials.newBuilder()
        .setAccessToken(new AccessToken(accessToken, null)).build();
    HttpRequestInitializer requestInitializer = request -> request.getHeaders()
        .setAuthorization("Bearer " + credentials.getAccessToken().getTokenValue());

    Calendar calendar = new Calendar.Builder(httpTransport, jsonFactory, requestInitializer).build();

    boolean valid = false;
    try {
      Events events = calendar.events().list(email).setMaxResults(1).execute();
      if (events != null) {
        valid = true;
      }
    } catch (IOException e) {
      log.error("Error getting calendar User: " + e.getMessage());
    }

    return valid;
  }
}
