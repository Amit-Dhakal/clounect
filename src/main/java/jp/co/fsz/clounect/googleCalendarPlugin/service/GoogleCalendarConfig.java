package jp.co.fsz.clounect.googleCalendarPlugin.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * /** Google Calendar APIのセットアップのための構成クラスです。 Google Calendar
 * APIとのやり取りに必要なコンポーネントを初期化および提供します
 * <p>[備 考] なし。</p>
 * <p>[環境] JDK17.0</p>
 * <p>著作権 FSZ 2024</p>
 *
 *  @author 著者FSZ
 * @since 1.0
 */
@Configuration
public class GoogleCalendarConfig {

  /**
   * <p>[概要] HTTP トランスポートの Bean 定義メソッド。</p>
   * <p>[詳細] Google Calendar API へのアクセスに使用する HTTP トランスポートを提供する Bean 定義メソッド。</p>
   * <p>[備考] 信頼できるトランスポートを作成するために、一般的なセキュリティ例外と入出力例外が発生する可能性があります。</p>
   *
   * @return 信頼できる HTTP トランスポート
   * @throws GeneralSecurityException セキュリティ例外が発生した場合
   * @throws IOException              入出力例外が発生した場合
   * @since 1.0
   */
  @Bean
  public HttpTransport httpTransport() throws GeneralSecurityException, IOException {
    return GoogleNetHttpTransport.newTrustedTransport();
  }

  /**
   * <p>[概要] JSON ファクトリの Bean 定義メソッド。</p>
   * <p>[詳細] Google Calendar API へのアクセス時に使用する JSON ファクトリを提供する Bean 定義メソッド。</p>
   * <p>[備考] デフォルトの Gson インスタンスを返します。</p>
   *
   * @return デフォルトの JSON ファクトリ
   * @since 1.0
   */
  @Bean
  public JsonFactory jsonFactory() {
    return GsonFactory.getDefaultInstance();
  }

  /**
   * <p>[概要] アプリケーション名の Bean 定義メソッド。</p>
   * <p>[詳細] Google Calendar API へのアクセス時に使用するアプリケーション名を提供する Bean 定義メソッド。</p>
   * <p>[備考] "clounect" を返します。</p>
   *
   * @since 1.0
   */
  @Value("${application.name}")
  private String applicationName;

  @Bean
  public String applicationName() {
    return applicationName;
  }
}
