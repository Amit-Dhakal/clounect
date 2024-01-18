package jp.co.fsz.clounect.googleCalendarPlugin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * <p>[概要] イベント情報を保持するデータ転送オブジェクト（DTO）クラス。</p>
 * <p>[詳細] イベントに関連する情報をまとめて保持します。</p>
 * <p>[備考] このクラスは、イベントの本文、レコード、参加者と日付データ、参加者データ、イベントタイトル、レコードタイトル、日付データ、および会議日時データを格納します。デフォルト値が設定されています。</p>
 * <p>[環境] JDK17.0</p>
 * <p>著作権 FSZ 2024</p>
 *
 *  @author 著者FSZ
 * @since 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {
  private String bodyData = "body";
  private String recordData = "record";
  private String participantsAndDateData = "field_1638871621";
  private String participantsData = "field_1638871621_participants";
  private String eventTitleData = "test01";
  private String recordTitleData = "field_1638871584";
  private String dateData = "field_1638871621";
  private String meetingDateData = "field_1638871621_period";
  private String type = "type";
}
