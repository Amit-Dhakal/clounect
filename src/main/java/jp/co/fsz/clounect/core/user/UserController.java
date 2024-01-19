package jp.co.fsz.clounect.core.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jp.co.fsz.clounect.core.dto.AppMasterDto;
import jp.co.fsz.clounect.core.dto.DashboardDto;
import jp.co.fsz.clounect.core.exception.ResourceNotFoundException;
import jp.co.fsz.clounect.core.security.annotations.Admin;
import jp.co.fsz.clounect.core.security.annotations.User;
import jp.co.fsz.clounect.core.service.DashboardService;
import jp.co.fsz.clounect.core.user.dto.UserDto;
import jp.co.fsz.clounect.core.user.service.UserService;
import jp.co.fsz.clounect.core.util.AppConstants;
import jp.co.fsz.clounect.core.util.PaginationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import java.util.Optional;
import java.util.UUID;

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
@RequestMapping("/user")
public class UserController {

  @Value("${spring.data.web.pageable.default-page-size}")
  private Integer defaultPageSize;

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
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
  @GetMapping("/add")
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
  @PostMapping("/add")
  public String createUser(HttpServletRequest request, @Valid @ModelAttribute("user") UserDto userDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
    userService.createUser(userDto);

    if (bindingResult.hasErrors()) {
      redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.appMasterDto",
          bindingResult);
      redirectAttributes.addFlashAttribute("user", userDto);
      redirectAttributes.addFlashAttribute("requestURI", request.getRequestURI());
      return "core/users/signupForm";
    }

    // Add the success message to the redirectAttributes
    redirectAttributes.addFlashAttribute("successMessage", "Record Updated Successfully!!!");
    return "redirect:/user";
  }

  @GetMapping(value="/{uuid}")
  @Admin
  public String getUserByUuid(HttpServletRequest request, Model model, @NotEmpty @NotNull @PathVariable String uuid) {
    log.info("Inside getUserByUuid");
    UserDto user = userService.getUserByUuid(uuid);
    model.addAttribute("user", user);
    model.addAttribute("requestURI", request.getRequestURI());

    return "core/users/userView";
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
  @GetMapping("/{uuid}/edit")
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
   * @param page    ページ番号,
   * @param sortBy    並べ替え,
   * @param sortOrder 並べ替え順序,
   * @return 管理者ダッシュボード
   * @since 1.0
   */
  @Admin
  @GetMapping
  public String getAllUsers(HttpServletRequest request, Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("sortBy") Optional<String> sortBy, @RequestParam("sortOrder") Optional<String> sortOrder, @RequestParam("query") Optional<String> query) {
    Pageable pageable = PaginationUtil.preparePaginationRequest(page, defaultPageSize);
    String q = null;
    Page<UserDto> users = null;

    if (query.isPresent() && !query.isEmpty()) {
      q = query.get();
      users = userService.searchUser(q, pageable);
    } else {
      q = null;
      users = userService.getAllUsers(pageable);
    }
    mapListDataDetails(request, model, q, users);
    return "core/users/userList";
  }

  private void mapListDataDetails(HttpServletRequest request, Model model, String query, Page<UserDto> users) {
    PaginationUtil.addPaginationInfo(model, users);
    model.addAttribute("users", users);
    model.addAttribute("query", query);
    model.addAttribute("requestURI", request.getRequestURI());
  }

  @Admin
  @GetMapping("/search")
  public String search(HttpServletRequest request, Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("sortBy") Optional<String> sortBy, @RequestParam("sortOrder") Optional<String> sortOrder, @NotNull @RequestParam("query") String query) {
    Pageable pageable = PaginationUtil.preparePaginationRequest(page, defaultPageSize);

    Page<UserDto> users = userService.searchUser(query, pageable);
    mapListDataDetails(request, model, query, users);
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
  @PostMapping("/{uuid}/update")
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
      log.error("Error: {}", e);
      List<String> errorMessages = new ArrayList<>();
      errorMessages.add("Failed to update User.");
      redirectAttributes.addFlashAttribute("messages", errorMessages);
      redirectAttributes.addFlashAttribute("userDto", userDto);
      redirectAttributes.addFlashAttribute("requestURI", request.getRequestURI());
      return "core/users/signupForm";
    }
    redirectAttributes.addFlashAttribute("successMessage",
        "Record Updated Successfully!!!");

    return "redirect:/user/"+ uuid;
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
  @PostMapping("/{uuid}/delete")
  public String deleteUser(@PathVariable String uuid) {
    userService.deleteUser(uuid);
    return "redirect:/admin/dashboard";
  }

  /**
   * <p>[概要] ユーザーを無効にするための PostMapping REST API。 </p>
   * <p>[詳細] ユーザーを無効にする API を提供します </p>
   * <p>【注意事項】特にありません。 </p>
   *
   * @param uuid UUID、
   * @return は成功した場合は無効なメッセージを返し、失敗した場合は例外を返します
   * @since 1.0
   */
  @Admin
  @PostMapping("/{uuid}/disable")
  public ResponseEntity<String> disableUser(@PathVariable String uuid) {
    log.info("User with disabled with id :" + uuid);
    return userService.disableUser(uuid);
  }

  /**
   * <p>[概要] ユーザーを有効にするための PostMapping REST API。 </p>
   * <p>[詳細] ユーザーを有効にするための API を提供します </p>
   * <p>【注意事項】特にありません。 </p>
   *
   * @param uuid UUID、
   * @return は成功した場合は有効なメッセージを返し、失敗した場合は例外を返します
   * @since 1.0
   */
  @Admin
  @PostMapping("/{uuid}/enable")
  public ResponseEntity<String> enable(@PathVariable String uuid) {
    log.info("User with enabled with id :" + uuid);
    return userService.enableUser(uuid);
  }

  /**
   * <p>[概要] 管理ユーザーアプリケーションページに誘導するための GetMapping REST API。 </p>
   * <p>[詳細] 管理ユーザーアプリケーションページを取得するための API を提供します </p>
   * <p>【注意事項】特にありません。 </p>
   *
   * @param uuid    UUID、
   * @param model モデル、
   * @return 管理者ユーザー アプリケーション ページ
   * @since 1.0
   */

  @Admin
  @GetMapping("/{uuid}/appSiteInfo")
  public String getUserAppSiteInfo(@PathVariable UUID uuid, Model model) {
    List<AppMasterDto> appMasterDtos  = userService.findAppMasterByUuid(uuid);
    model.addAttribute("appMasterDtos", appMasterDtos);
    model.addAttribute("userId", uuid);
    model.addAttribute("isAdmin", true);

    return "core/users/adminUserApplication";

  }

  /**
   * <p>[概要] 管理者ユーザーアプリケーションのステータスを変更するための PostMapping REST API。 </p>
   * <p>[詳細] ステータスを有効にした後に無効にしたり、その逆に変更したりするための API を提供します</p>
   *<p>【注意事項】特にありません。 </p>
   *
   * @param userId ユーザーID、
   * @param appId アプリID、
   * @param model モデル、
   * @return 管理者ユーザー アプリケーション ページ
   * @since 1.0
   */
  @Admin
  @PostMapping("change/application/{userId}/{appId}/status")
  public String changeApplicationStatus(@PathVariable UUID userId, @PathVariable UUID appId , Model model) {
    userService.changeApplicationStatus(userId, appId);
    model.addAttribute("isAdmin", true);

    return "core/users/adminUserApplication";
  }

}
