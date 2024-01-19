package jp.co.fsz.clounect.googleCalendarPlugin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.fsz.clounect.core.model.AppSiteInfo;
import jp.co.fsz.clounect.googleCalendarPlugin.exception.CouldNotPerformActionException;
import jp.co.fsz.clounect.googleCalendarPlugin.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;


/**
 * <p>[概要] ユーザーの設定情報を管理するサービスクラス。</p>
 * <p>[詳細] このサービスクラスは、ユーザーごとの設定や情報を管理し、アプリケーション内での一貫性を確保します。</p>
 * <p>[環境] JDK17.0</p>
 * <p>著作権 FSZ 2024</p>
 *
 * @author 著者FSZ
 * @since 1.0
 */
//TODO remove this class Later (user mapper to get the desired data) --> created in credentialsMapper class (or use sql)
@Service
@Slf4j
public class UserSetting {
  private final SecurityUtil securityUtil;

  /**
   * <p>[概要] {@link SecurityUtil} を使用してユーザー設定を初期化するコンストラクタ。</p>
   * <p>[詳細] このコンストラクタは、{@link SecurityUtil} を利用してユーザー設定を初期化します。ユーザー設定は、
   * アプリケーション内でユーザー固有の設定や情報を管理するために使用されます。</p>
   * <p>[コンストラクタ名] UserSetting</p>
   * <p>[引数] securityUtil - ユーザー設定の初期化に使用される {@link SecurityUtil} オブジェクト</p>
   * <p>[備考] ユーザー設定の具体的な内容は、アプリケーションの実装によって異なる可能性があります。</p>
   *
   * @param securityUtil ユーザー設定の初期化に使用される {@link SecurityUtil} オブジェクト
   * @since 1.0
   */
  public UserSetting(SecurityUtil securityUtil) {
    this.securityUtil = securityUtil;
  }

  //TODO dont use this method to call data user mapper class (mentioned aove)

  /**
   * <p>[概要] 設定をマップとして取得するメソッド。</p>
   * <p>[詳細] このメソッドは、設定をキーと値のペアのマップとして返します。設定はアプリケーションで使用される
   * パラメータや設定情報を表します。</p>
   * <p>[メソッド名] getConfig</p>
   * <p>[戻り値] キーと値のペアで構成された設定を表すマップ。</p>
   * <p>[備考] 具体的な設定の詳細は、アプリケーションの実装によって異なる可能性があります。</p>
   *
   * @return キーと値のペアで構成された設定を表すマップ。
   * @since 1.0
   */
  public Map<String, Object> getConfig() {
    Optional<AppSiteInfo> existingRecord = securityUtil.getExistingRecord();

    if (existingRecord.isPresent()) {
      AppSiteInfo appSiteInfo = existingRecord.get();
      String configJson = appSiteInfo.getConfig();

      if (configJson != null && !configJson.isEmpty()) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
          return objectMapper.readValue(configJson, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
          throw new CouldNotPerformActionException("Could not parse config" + e.getMessage());
        }
      }
    }

    return Collections.emptyMap();
  }

  /**
   * <p>[概要] Webフックを取得するメソッド。</p>
   * <p>[詳細] このメソッドは、アプリケーションで使用されるWebフックを返します。Webフックは外部サービスとの連携や通知のために利用されます。</p>
   * <p>[メソッド名] getWebHook</p>
   * <p>[戻り値] Webフックを表す文字列。</p>
   * <p>[備考] 具体的なWebフックの詳細は、アプリケーションの実装によって異なる可能性があります。</p>
   *
   * @return Webフックを表す文字列。
   * @since 1.0
   */
  public String getWebHook(){
    Optional<AppSiteInfo> getAppSiteInfo = securityUtil.getExistingRecord();
    return getAppSiteInfo.map(AppSiteInfo::getWebhookUrl).orElse(null);
  }
}
