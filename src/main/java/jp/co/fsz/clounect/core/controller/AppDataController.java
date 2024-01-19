package jp.co.fsz.clounect.core.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jp.co.fsz.clounect.core.dto.AppDataDto;
import jp.co.fsz.clounect.core.security.annotations.Admin;
import jp.co.fsz.clounect.core.service.AppDataService;
import jp.co.fsz.clounect.core.util.PaginationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>[概要] AppDataエンティティに関連するリクエストを処理するControllerクラス。</p>
 * <p>[詳細] このクラスはAppDataエンティティに関連するHTTPリクエストを処理するためのControllerであり、"/app-data" エンドポイントにマップされています。
 * また、SLF4Jが提供するLoggerを使用してログ出力を行います。</p>
 * <p>[環境] JDK 17.0</p>
 * <p>著作権 FSZ 2024</p>
 *
 * @author 著者: FSZ
 * @since バージョン 1.0
 */
@Controller
@RequestMapping("/app-data")
@Slf4j
public class AppDataController {
  private final AppDataService appDataService;
  @Value("${spring.data.web.pageable.default-page-size}")
  private Integer defaultPageSize;

  /**
   * <p>[概要] AppDataControllerのコンストラクタ。</p>
   * <p>[詳細] このコンストラクタは、AppDataControllerクラスのインスタンスを作成する際に、
   * 依存性注入を利用してAppDataServiceを設定します。</p>
   * <p>[備考] コントローラーがアプリケーションサービスを使用するための重要な初期化メソッドです。
   * AppDataServiceは、ビジネスロジックとデータアクセスに関する操作を提供するために使用されます。</p>
   *
   * @param appDataService アプリケーションデータの操作を提供するAppDataServiceのインスタンス,
   * @since 1.0
   */
  public AppDataController(AppDataService appDataService) {
    this.appDataService = appDataService;
  }

  /**
   * <p>[概要] AppDataに関するGETリクエストを処理し、管理者向けのAppDataページへのアクセスを提供します。</p>
   * <p>[詳細] このメソッドは、管理者権限が必要なAppDataに関連するHTTP GETリクエストを処理するためのエンドポイントとして機能します。
   * HTTPリクエストを表すHttpServletRequestオブジェクト、UIデータを管理するためのModelオブジェクト、ページ番号、および検索クエリを受け取ります。</p>
   * <p>[備考] 管理者権限が必要であることを示すために、@Adminアノテーションが使用されています。</p>
   *
   * @param request HTTPリクエストを表すHttpServletRequestオブジェクト,
   * @param model   UIデータを管理するためのModelオブジェクト,
   * @param page    ページ番号のオプションパラメータ,
   * @param query   検索クエリのオプションパラメータ,
   * @return AppDataページの表示名またはページ参照を返します。
   * @since 1.0
   */
  @GetMapping
  @Admin
  public String getAppData(HttpServletRequest request, Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("query") Optional<String> query) {
    Pageable pageable = PaginationUtil.preparePaginationRequest(page, defaultPageSize);
    Page<AppDataDto> appDataDtos = null;
    String q = null;
    if(query.isPresent()) {
      q = query.get();
      appDataDtos = appDataService.searchAppDataByDetail(query.get(), pageable);
    }else{
      appDataDtos = appDataService.getAllAppDatas(pageable);
    }

    mapListDataDetails(request, model, q, appDataDtos);
    return "core/appData/appDataList";
  }

  /**
   * <p>[概要] ページングされたAppDataDTOリストをUIモデルにマッピングするためのユーティリティメソッド。</p>
   * <p>[詳細] このメソッドは、HTTPリクエストを表すHttpServletRequestオブジェクト、UIデータを管理するためのModelオブジェクト、
   * 検索クエリ、およびページングされたAppDataDTOリストを受け取り、それらをUIモデルに適切にマッピングします。</p>
   * <p>[備考] このメソッドは通常、AppDataコントローラー内でデータの一覧表示に使用されます。AppDataDTOはデータ転送オブジェクトであり、
   * ビジネスロジックとUIレイヤーの間でデータを伝達するために使用されます。</p>
   *
   * @param request    HTTPリクエストを表すHttpServletRequestオブジェクト,
   * @param model      UIデータを管理するためのModelオブジェクト,
   * @param query      検索クエリ,
   * @param appDataDtos ページングされたAppDataDTOリスト,
   * @since 1.0
   */
  private void mapListDataDetails(HttpServletRequest request, Model model, String query, Page<AppDataDto> appDataDtos) {
    List<AppDataDto> sortedAppData = appDataDtos.getContent()
        .stream()
        .sorted(Comparator.comparingLong(AppDataDto::getId))
        .collect(Collectors.toList());

    Page<AppDataDto> sortedPage = new PageImpl<>(sortedAppData, appDataDtos.getPageable(), appDataDtos.getTotalElements());
    PaginationUtil.addPaginationInfo(model, sortedPage);
    model.addAttribute("query", query);
    model.addAttribute("appData", sortedPage);
    model.addAttribute("requestURI", request.getRequestURI());
  }

  /**
   * <p>[概要] 指定されたUUIDに基づいてユーザー情報を取得するGETリクエストを処理します。</p>
   * <p>[詳細] このメソッドは、指定されたUUIDに関連するユーザー情報を取得するためのHTTP GETリクエストを処理するためのエンドポイントとして機能します。
   * 管理者権限が必要であることを示すために、@Adminアノテーションが使用されています。HTTPリクエストを表すHttpServletRequestオブジェクト、
   * UIデータを管理するためのModelオブジェクト、およびパス変数として指定されたUUIDを受け取ります。</p>
   * <p>[備考] パス変数として指定されたUUIDは、@NotEmptyと@NotNullのバリデーションが適用されています。</p>
   *
   * @param request HTTPリクエストを表すHttpServletRequestオブジェクト,
   * @param model   UIデータを管理するためのModelオブジェクト,
   * @param uuid    ユーザーを識別するためのUUID（パス変数）,
   * @return 指定されたUUIDに基づくユーザー情報の表示名またはページ参照を返します。
   * @since 1.0
   */
  @GetMapping(value="/{uuid}")
  @Admin
  public String getUserByUuid(HttpServletRequest request, Model model, @NotEmpty @NotNull @PathVariable String uuid) {
    log.info("Inside getUserByUuid");
    AppDataDto appData = appDataService.getAppDataByUuid(uuid);
    model.addAttribute("appData", appData);
    model.addAttribute("requestURI", request.getRequestURI());

    return "core/appData/appDataView";
  }
}
