package jp.co.fsz.clounect.core.controller;

import jakarta.servlet.http.HttpServletRequest;
import jp.co.fsz.clounect.core.dto.DashboardDto;
import jp.co.fsz.clounect.core.service.DashboardService;
import jp.co.fsz.clounect.core.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * <p>[概要] ダッシュボード関連の操作を処理するコントローラークラス。</p>
 * <p>[詳細] このクラスは、Thymeleafに提供されるAPIを処理するためのコントローラー/エンドポイントクラスです。</p>
 * <p>[備考] 特記事項はありません。</p>
 * <p>[環境] JDK17.0</p>
 * <p>Copyright フィーラーシステムZ, 2024</p>
 *
 * @author フィーラーシステムZ
 * @since 1.0
 */
@Slf4j
@Controller
public class DashboardController {

  private final DashboardService dashboardService;
  private final UserService userService;

  public DashboardController(DashboardService dashboardService, UserService userService) {
    this.dashboardService = dashboardService;
    this.userService = userService;
  }

  /**
   * <p>[概要] ダッシュボードに関するGETリクエストを処理し、ダッシュボードページへのアクセスを提供します。</p>
   * <p>[詳細] このメソッドは、ダッシュボードに関連するHTTP
   * GETリクエストを処理するためのエンドポイントとして機能します。HTTPリクエストを表すHttpServletRequestオブジェクトと、UIデータを管理するためのModelオブジェクトを受け取ります。</p>
   * <p>[備考] 特記事項はありません。</p>
   *
   * @param request HTTPリクエストを表すHttpServletRequestオブジェクト,
   * @param model   UIデータを管理するためのModelオブジェクト,
   * @return ダッシュボードの表示名またはページ参照を返します。
   * @since 1.0
   */
  @GetMapping(value = { "/", "/dashboard" })
  public String getDashboard(HttpServletRequest request, Model model) {
    log.info("Inside getDashboard");
    DashboardDto dashboardDto = dashboardService.prepareDashboard();

    Authentication authentication = SecurityContextHolder.getContext()
        .getAuthentication();

    DefaultOidcUser defaultOidcUser = (DefaultOidcUser) authentication.getPrincipal();
    log.info("email:: {}", defaultOidcUser.getEmail());
    log.info("Role:: {}", defaultOidcUser.getAttributes());
    model.addAttribute("dashboardData", dashboardDto);
    model.addAttribute("requestURI", request.getRequestURI());
    return "core/dashboard";
  }
}

