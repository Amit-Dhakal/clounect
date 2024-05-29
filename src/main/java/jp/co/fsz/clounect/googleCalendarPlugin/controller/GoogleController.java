package jp.co.fsz.clounect.googleCalendarPlugin.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import jp.co.fsz.clounect.googleCalendarPlugin.dto.OAuthToken;
import jp.co.fsz.clounect.googleCalendarPlugin.dto.UserDetails;
import jp.co.fsz.clounect.googleCalendarPlugin.exception.CouldNotPerformActionException;
import jp.co.fsz.clounect.googleCalendarPlugin.exception.CredentialsMissingException;
import jp.co.fsz.clounect.googleCalendarPlugin.service.*;
import jp.co.fsz.clounect.googleCalendarPlugin.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import java.util.*;

/**
 * Google関連の操作を処理するコントローラクラス。
 * <p>このクラスは、変更通知 REST API 処理メソッドを記載したクラスです。</p>
 * <p>[備 考] なし。</p>
 * <p>[環境] JDK17.0</p>
 * <p>著作権 FSZ 2024</p>
 *
 * @author 著者FSZ
 * @since 1.0
 */
@RestController
@RequestMapping("/google")
@Slf4j
public class GoogleController {

  private final GoogleCalendarService googleCalendarService;
  private final JsonDataOptimizationService jsonDataOptimizationService;
  private final JustSfaService justSfaService;
  private final GoogleRecordService googleRecordService;
  private final SecurityUtil securityUtil;
  private final CheckTypeService checkTypeService;

  @Autowired
  public GoogleController(GoogleCalendarService googleCalendarService,
      JsonDataOptimizationService jsonDataOptimizationService,
      JustSfaService justSfaService, GoogleRecordService googleRecordService,
      SecurityUtil securityUtil, CheckTypeService checkTypeService) {

    this.googleCalendarService = googleCalendarService;
    this.jsonDataOptimizationService = jsonDataOptimizationService;
    this.justSfaService = justSfaService;
    this.googleRecordService = googleRecordService;
    this.securityUtil = securityUtil;
    this.checkTypeService = checkTypeService;
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
   * <p>[概要] OAuth 2.0 認証コールバック処理メソッド。</p>
   * <p>[詳細] OAuth 2.0 認証コールバックのリクエストを受け取り、アクセストークンの取得を試みます。</p>
   * <p>[メソッド名] oauth2Callback</p>
   * 認証コールバックにおいて、渡された認証コードと状態情報を使用してアクセストークンを取得し、成功または失敗に応じてリダイレクトを行います。</p>
   *
   * @param code  認証コード
   * @param state 状態情報(JSON形式)
   * @return リダイレクトビュー
   * @throws ParseException                 JSONパースエラーが発生した場合
   * @throws CouldNotPerformActionException アクションを実行できませんでした
   * @since 1.0
   */

  @RequestMapping(value = "/oauth2/callback", method = RequestMethod.GET, params = "code")
  public RedirectView oauth2Callback(@RequestParam(value = "code") String code,@RequestParam(value = "state") String state) {

    try {
      Map<String, String> parsedState = jsonDataOptimizationService.parseState(state,
          code);
      String currentUrl = parsedState.get("currentUrl");

      if (checkNullAndEmpty(currentUrl)) {
        throw new CredentialsMissingException("currentUrl is missing");
      }
      try {
        googleRecordService.storeConfiguration(state, code);
        return new RedirectView(currentUrl + "?success=true");
      } catch (CouldNotPerformActionException e) {
        log.error(e.getMessage(), e);
        return new RedirectView(currentUrl + "?success=false");
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    } catch (ParseException e) {
      log.error("Error parsing state: " + e.getMessage(), e);
      return null;
    }
  }

  /**
   * <p>[概要] メール検証メソッド。</p>
   * <p>[詳細] 提供されたアクセストークンとメールリストを使用して、カレンダーアクセスを検証します。</p>
   * <p>[メソッド名] validateEmail</p>
   * <p>[概要] アクセストークンとメールリストを使用して、カレンダーアクセスを検証するメソッド。</p>
   * <p>[詳細] 提供されたアクセストークンとメールリストを使用して、各メールアドレスに対するカレンダーアクセスを検証します。</p>
   *
   * @param email メールアドレスのリスト
   * @return HTTPステータスおよびメッセージ
   * @throws GoogleJsonResponseException メール検証中にエラーが発生した場合
   * @since 1.0
   */
  @PostMapping("/validate-email")
  public ResponseEntity<Map<String, Object>> validateEmail(@RequestBody Map<String, String> email) {
    Map<String, Object> response = new HashMap<>();
    try {
      OAuthToken token = securityUtil.getAccessToken();
      String accessToken = token.getAccessToken();
      String emailToBeValidated = email.get("email");
      if (checkNullAndEmpty(emailToBeValidated)) {
        throw new CredentialsMissingException("Emails list is missing or empty");
      }

      boolean validate = googleCalendarService.checkCalendarAccess(accessToken,
          emailToBeValidated);

      log.info("validateEmail: " + validate);
      response.put("success", validate);
      response.put("message",
          validate ? "Email '" + email + "' is valid." : "Invalid email address.");

      HttpStatus httpStatus = validate ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
      return new ResponseEntity<>(response, httpStatus);
    } catch (GoogleJsonResponseException e) {
      response.put("success", false);
      response.put("message", "Error validating email: " + e.getMessage());
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * <p>[概要] SFAデータの検証メソッド。</p>
   * <p>[詳細] 提供された状態情報を使用して、SFAデータの検証を行います。</p>
   * <p>[概要] 状態情報を使用して、SFAデータの検証を行うメソッド。</p>
   * <p>[詳細] 提供された状態情報を使用して、SFAデータが正当であるかどうかを検証し、結果を真偽値で返します。</p>
   *
   * @param state SFAデータの状態情報
   * @return 検証が成功した場合は true、それ以外は false
   * @throws ParseException 状態情報の解析中にエラーが発生した場合
   * @since 1.0
   */
  @PostMapping("/sfa/validate")
  public Boolean oauth2Callback(@RequestBody String state) {
    try {
      Boolean value = justSfaService.getData(state);
      if (value) {
        googleRecordService.storeConfiguration(state, "");
        return true;
      }
      return false;
    } catch (ParseException | JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * <p>[概要] ユーザーデータを追加するメソッド。</p>
   * <p>[詳細] 提供されたユーザーデータをシステムに追加します。</p>
   * <p>[備考] ユーザーデータはJSON形式の文字列としてリクエストボディで提供されるものとします。</p>
   *
   * @param userData ユーザーデータのJSON形式の文字列
   * @since 1.0
   */
  @PostMapping("/add-user")
  public void submitFormData(@RequestBody String userData) {
    googleRecordService.storeUserFormData(userData);
  }

  /**
   * <p>[概要] ユーザー情報を取得するメソッド。</p>
   * <p>[詳細] 認証されたユーザーの情報を取得し、Google Calendar プラグインに関連するユーザー情報を返します。</p>
   * <p>[備考] 認証情報はセキュリティコンテキストから取得し、それを元に関連するユーザー情報を取得します。</p>
   *
   * @return Google Calendar プラグインに関連するユーザー情報のリスト
   * @since 1.0
   */
  @GetMapping("/get-users")
  public List<UserDetails> getUser() {
    return securityUtil.getUserDetailsList();
  }

  /**
   * <p>[概要] Google イベントを処理するメソッド。</p>
   * <p>[詳細] Google イベントに関する情報を処理し、指定された UUID を使用して特定の処理を実行します。</p>
   * <p>[備考] Google イベントのデータと処理対象の UUID がパラメータとして渡されます。</p>
   *
   * @param data リクエストボディから取得した Google イベントのデータ
   * @param uuid Google イベントを処理するための UUID
   * @return HTTPステータスおよびメッセージ
   * @since 1.0
   */
  @PostMapping("/webhook/{uuid}")
  public ResponseEntity event(@RequestBody Map<String, Object> data,
      @PathVariable String uuid) {

    checkTypeService.getPlayLoad(data, uuid, UUID.randomUUID());
    return ResponseEntity.ok("Success");
  }
}