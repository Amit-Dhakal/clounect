package jp.co.fsz.clounect.googleCalendarPlugin.dto;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;
/**
 * <p>[概要] イベントに関連する情報を保持するデータ転送オブジェクト（DTO）クラス。</p>
 * <p>[詳細] このクラスは、イベントに関連する情報をまとめて保持します。</p>
 * <p>[備考] イベントの本文、レコード、参加者および日付データ、参加者データ、イベントタイトル、
 * レコードタイトル、日付データ、および会議日時データを格納します。デフォルト値が設定されています。</p>
 * <p>[環境] JDK 17.0</p>
 * <p>著作権 FSZ 2024</p>
 *
 * @author 著者: FSZ
 * @since バージョン 1.0
 */
@Data
@Builder(toBuilder = true)
@Component
public class CredentialsDto {
  private String clientId;
  private String clientSecret;
}
