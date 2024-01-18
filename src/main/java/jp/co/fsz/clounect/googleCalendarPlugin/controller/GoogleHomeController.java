package jp.co.fsz.clounect.googleCalendarPlugin.controller;

import jakarta.servlet.http.HttpServletRequest;
import jp.co.fsz.clounect.core.security.annotations.User;
import jp.co.fsz.clounect.googleCalendarPlugin.service.UserSetting;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <p>[概要] Google関連の操作を処理するコントローラクラス。</p>
 * <p>[詳細] このクラスには変更通知 REST API 関連の操作を処理するメソッドが含まれています。</p>
 * <p>[備考] なし。</p>
 * <p>[環境] JDK17.0</p>
 * <p>著作権 FSZ 2024</p>
 *
 *  @author 著者FSZ
 * @since 1.0
 */
@Controller
@RequestMapping("/google")
@User
public class GoogleHomeController {

  private final UserSetting userSetting;

  public GoogleHomeController(UserSetting userSetting) {
    this.userSetting = userSetting;
  }

  /**
   * <p>[概要] Google設定取得メソッド。</p>
   * <p>[詳細] "setting" エンドポイントに対するGETリクエストに応じて、Googleの設定画面へのリダイレクトを行います。</p>
   * <p>[環境] JDK17.0</p>
   * <p>[メソッド名] googleSetting</p>
   * <p>[概要] "setting" エンドポイントに対するGETリクエストを処理し、Googleの設定画面へのリダイレクトを行います。</p>
   * <p>[詳細] "googleCalendarPlugin/googleSetting" への遷移を返します。</p>
   *
   * @return "googleCalendarPlugin/googleSetting" ページへの遷移
   * @since 1.0
   */
  @GetMapping("/setting")
  public String googleSetting(HttpServletRequest request, Model model) {
    model.addAttribute("isAdmin", false);
    model.addAttribute("setting",userSetting.getConfig());
    return "apps/googleCalendarPlugin/googleSetting";
  }

  /**
   * <p>[概要] SFAデータ取得メソッド。</p>
   * <p>[詳細] "/sfa" エンドポイントへのGETリクエストに対応し、モデルに必要な属性を設定して、
   * <p>[環境] JDK17.0</p>
   * <p>[メソッド名] getSfaData</p>
   * <p>[詳細] モデルに "isAdmin" 属性を false で設定し、"sfa" 属性にユーザーの設定情報を追加します。
   * "apps/googleCalendarPlugin/googleSetting" への遷移を返します。</p>
   *
   * @param model モデル
   * @return "apps/googleCalendarPlugin/googleSetting" ページへの遷移
   * @since 1.0
   */
  @GetMapping("/sfa")
  public String getSfaData(Model model) {
    model.addAttribute("isAdmin", false);
    model.addAttribute("sfa", userSetting.getConfig());
    return "apps/googleCalendarPlugin/googleSetting";
  }

  /**
   * <p>[概要] Googleユーザー設定取得メソッド。</p>
   * <p>[詳細] "users" エンドポイントに対するGETリクエストに応じて、Googleユーザー設定画面へのリダイレクトを行います。</p>
   * <p>[環境] JDK17.0</p>
   * <p>[メソッド名] googleUserSetting</p>
   * <p>[概要] "users" エンドポイントに対するGETリクエストを処理し、Googleユーザー設定画面へのリダイレクトを行います。</p>
   * <p>[詳細] "googleCalendarPlugin/googleUserSetting" への遷移を返します。</p>
   *
   * @return "googleCalendarPlugin/googleUserSetting" ページへの遷移
   * @since 1.0
   */
  @GetMapping("/user/setting")
  public String googleUserSetting(HttpServletRequest request, Model model) {
    model.addAttribute("isAdmin", false);
    model.addAttribute("requestURI", request.getRequestURI());
    return "apps/googleCalendarPlugin/googleUserSetting";
  }

}