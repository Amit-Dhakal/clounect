package jp.co.fsz.clounect.core.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jp.co.fsz.clounect.core.dto.AppMasterDto;
import jp.co.fsz.clounect.core.dto.DashboardDto;
import jp.co.fsz.clounect.core.exception.ResourceNotFoundException;
import jp.co.fsz.clounect.core.security.annotations.Admin;
import jp.co.fsz.clounect.core.security.annotations.User;
import jp.co.fsz.clounect.core.service.DashboardService;
import jp.co.fsz.clounect.core.user.dto.UserDto;
import jp.co.fsz.clounect.core.user.service.UserService;
import jp.co.fsz.clounect.core.util.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>[概 要] Controller コントローラー / エンドポイント REST API 処理クラス。</p>
 * <p>[詳細] これはthymeleafに与えられるAPIのクラスです。</p>
 * <p>[備 考] なし。</p>
 * <p>[環 境] JDK17.0</p>
 * <p>Copyright フィーラーシステムZ, 2024</p>
 *
 * @author フィーラーシステムZ
 * @since 1.0
 */
@Slf4j
@Controller
@RequestMapping("/")
public class UserController {

  private final UserService userService;

  private DashboardService dashboardService;

  public UserController(UserService userService, DashboardService dashboardService) {
    this.userService = userService;
    this.dashboardService = dashboardService;
  }

  @GetMapping
  public String getDashboard(HttpServletRequest request, Model model) {
    log.info("Inside getDashboard");
    DashboardDto dashboardDto = dashboardService.prepareDashboard();
    model.addAttribute("isAdmin", dashboardDto.isAdmin());
    model.addAttribute("dashboardData", dashboardDto);
    model.addAttribute("requestURI", request.getRequestURI());
    log.info("uri:: {}", request.getRequestURI().toString());
    return "core/dashboard";
  }

  /**
   * <p>[概 要]サインアップ ユーザーの GetMapping REST API。</p>
   * <p>[詳 細] 取得リクエスト用の API を提供し、サインアップ ページに誘導します。</p>
   * <p>[備 考] なし。</p>
   *
   * @param model モデル,
   * @return ユーザーサインアップページ
   * @since 1.0
   */
  @Admin
  @GetMapping("add-user")
  public String signUpForm(Model model) {
    model.addAttribute("user", new UserDto());
    model.addAttribute("title", "Add User");
    model.addAttribute("button", "ADD");
    model.addAttribute("isAdmin", true);
    return "core/users/signupForm";
  }

  /**
   * <p>[概 要] ユーザーを作成するための PostMapping REST API。</p>
   * <p>[詳 細]  検索リクエストのAPIを提供し、編集ページへ遷移します。</p>
   * <p>[備 考] なし。</p>
   *
   * @param userDto ユーザーDTO,
   * @return すべてのユーザーを示すページを返します。
   * @since 1.0
   */
  @Admin
  @PostMapping("add-user")
  public String createUser(@Valid @ModelAttribute("user") UserDto userDto) {
    userService.createUser(userDto);
    return "redirect:/admin/dashboard";
  }

  /**
   * <p>[概 要] ユーザーを編集するための GetMapping REST API。</p>
   * <p>[詳 細]  検索リクエストのAPIを提供し、編集ページへ遷移します。</p>
   * <p>[備 考] なし。</p>
   *
   * @param model モデル,
   * @param uuid    ID
   * @return フォームを編集する
   * @since 1.0
   */
  @Admin
  @GetMapping("{uuid}/editUser")
  public String editUser(Model model, @PathVariable String uuid) {
    UserDto user = userService.getUserByUuid(uuid);
    model.addAttribute("user", user);
    model.addAttribute("title", "Edit User");
    model.addAttribute("button", "EDIT");
    model.addAttribute("isAdmin", true);

    return "core/users/signupForm";
  }

  /**
   * <p>[概 要] ユーザーを編集するための GetMapping REST API。</p>
   * <p>[詳 細]  検索リクエストのAPIを提供し、編集ページへ遷移します。</p>
   * <p>[備 考] なし。</p>
   *
   * @param model     モデル,
   * @param pageNo    ページ番号,
   * @param pageSize  ページサイズ,
   * @param sortBy    並べ替え,
   * @param sortOrder 並べ替え順序,
   * @return 管理者ダッシュボード
   * @since 1.0
   */
  @Admin
  @GetMapping("admin/dashboard")
  public String getAllUsers(Model model,
      @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFUALT_PAGE_NO, required = false) int pageNo,
      @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFUALT_PAGE_SIZE, required = false) int pageSize,
      @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFUALT_SORT_BY, required = false) String sortBy,
      @RequestParam(value = "sortOrder", defaultValue = AppConstants.DEFUALT_SORT_DIRECTION, required = false) String sortOrder) {

    var userResponse = userService.getAllUsers(pageNo, pageSize, sortBy, sortOrder);
    model.addAttribute("userResponse", userResponse);
    model.addAttribute("isAdmin", true);
    return "core/users/userList";
  }

  /**
   * <p>[概 要] ユーザーを更新するための UpdateMapping REST API。</p>
   * <p>[詳 細]  検索リクエストのAPIを提供し、編集ページへ遷移します。</p>
   * <p>[備 考] なし。</p>
   *
   * @param userDto ユーザーDTO,
   * @param uuid      ID,
   * @return すべてのユーザーを示すページを返します。
   * @since 1.0
   */
  @Admin
  @PostMapping("user/{uuid}/update")
  public String updateUser(HttpServletRequest request,
     @Valid  @ModelAttribute("user") UserDto userDto, @PathVariable String uuid,
      BindingResult bindingResult, RedirectAttributes redirectAttributes) {
    log.info("Inside Update User");

    if (bindingResult.hasErrors()) {
      redirectAttributes.addFlashAttribute(
          "org.springframework.validation.BindingResult.userDto", bindingResult);
      redirectAttributes.addFlashAttribute("userDto", userDto);
      redirectAttributes.addFlashAttribute("requestURI", request.getRequestURI());
      return "core/users/signupForm";
    }
    try {

      userService.updateUser(userDto, uuid);
    } catch (ResourceNotFoundException e) {
      log.error("Error: {}", e.getStackTrace());
      List<String> errorMessages = new ArrayList<>();
      errorMessages.add("Failed to update User.");
      redirectAttributes.addFlashAttribute("messages", errorMessages);
      redirectAttributes.addFlashAttribute("userDto", userDto);
      redirectAttributes.addFlashAttribute("requestURI", request.getRequestURI());
      return "core/users/signupForm";
    }
    redirectAttributes.addFlashAttribute("successMessage",
        "Record Updated Successfully!!!");

    return "redirect:/admin/dashboard";
  }

  /**
   * <p>[概 要] ユーザーを削除するための PostMapping REST API。</p>
   * <p>[詳 細]  検索リクエストのAPIを提供し、編集ページへ遷移します。</p>
   * <p>[備 考] なし。</p>
   *
   * @param uuid ID,
   * @return すべてのユーザーを示すページを返します。
   * @since 1.0
   */
  @Admin
  @PostMapping("user/{uuid}/delete")
  public String deleteUser(@PathVariable String uuid) {
    userService.deleteUser(uuid);
    return "redirect:/admin/dashboard";
  }
  @User
  @GetMapping("/user/dashboard")
  public String homepage(Model model) {
    Authentication authentication = SecurityContextHolder.getContext()
        .getAuthentication();
    DefaultOidcUser defaultOidcUser = (DefaultOidcUser) authentication.getPrincipal();
    String userEmail = defaultOidcUser.getEmail();
    UserDto user = userService.findUserByEmail(userEmail);
    model.addAttribute("user", user);
    model.addAttribute("isAdmin", false);

    return "core/users/index";
  }

  @Admin
  @PostMapping("/user/{uuid}/disable")
  public ResponseEntity<String> disableUser(@PathVariable String uuid) {
    log.info("User with disabled with id :" + uuid);
    return userService.disableUser(uuid);
  }

  @Admin
  @PostMapping("/user/{uuid}/enable")
  public ResponseEntity<String> enable(@PathVariable String uuid) {
    log.info("User with enabled with id :" + uuid);
    return userService.enableUser(uuid);
  }

  @Admin
  @GetMapping("/user/appSiteInfo/{id}")
  public String getUserAppSiteInfo(@PathVariable long id, Model model) {
    List<AppMasterDto> appMasterDtos  = userService.findAppSiteInfoById(id);
    model.addAttribute("appMasterDtos", appMasterDtos);
    model.addAttribute("userId", id);
    model.addAttribute("isAdmin", true);

    return "core/users/adminUserApplication";

  }

  @Admin
  @PostMapping("/change/application/status/{userId}/{appId}")
  public String changeApplicationStatus(@PathVariable long userId, @PathVariable long appId , Model model) {
    userService.changeApplicationStatus(userId, appId);
    model.addAttribute("isAdmin", true);

    return "core/users/adminUserApplication";
  }

}
