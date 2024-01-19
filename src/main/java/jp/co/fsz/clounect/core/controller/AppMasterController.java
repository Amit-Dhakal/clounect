package jp.co.fsz.clounect.core.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import jp.co.fsz.clounect.core.dto.AppMasterDto;
import jp.co.fsz.clounect.core.exception.ResourceNotFoundException;
import jp.co.fsz.clounect.core.security.annotations.Admin;
import jp.co.fsz.clounect.core.service.AppMasterService;
import jp.co.fsz.clounect.core.util.PaginationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/app-master")
@Slf4j
public class AppMasterController {

  private final AppMasterService appMasterService;

  @Value("${spring.data.web.pageable.default-page-size}")
  private Integer defaultPageSize;


  @Autowired
  public AppMasterController(AppMasterService appMasterService) {
    this.appMasterService = appMasterService;
  }

  @GetMapping
  @Admin
  public String getAppMasters(HttpServletRequest request, Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("query") Optional<String> query) {
    log.info("Inside getAppMasters");
    Pageable pageable = PaginationUtil.preparePaginationRequest(page, defaultPageSize);
    Page<AppMasterDto> appMasterDtos = null;
    String q = null;

    if (query.isPresent() && !query.isEmpty()) {
      q = query.get();
      appMasterDtos = appMasterService.searchAppMaster(query.get(), pageable);
    } else {
      q = null;
      appMasterDtos = appMasterService.getAllAppMasters(pageable);
    }

    mapListDataDetails(request, model, q, appMasterDtos);
    return "core/appMaster/appMasterList";
  }

  @GetMapping("/search")
  @Admin
  public String searchAppMasters(HttpServletRequest request, Model model, @RequestParam("page") Optional<Integer> page, @NotEmpty @RequestParam("query") String query) {
    log.info("Inside searchAppMasters");
    log.info("page:: {}, query:: {}", page, query);
    Pageable pageable = PaginationUtil.preparePaginationRequest(page, defaultPageSize);
    Page<AppMasterDto> appMasterDtos = appMasterService.searchAppMaster(query, pageable);
    mapListDataDetails(request, model, query, appMasterDtos);
    return "core/appMaster/appMasterList";
  }

  private void mapListDataDetails(HttpServletRequest request, Model model, String query, Page<AppMasterDto> appMasterDtos) {
    PaginationUtil.addPaginationInfo(model, appMasterDtos);
    model.addAttribute("query", query);
    model.addAttribute("appMasters", appMasterDtos);
    model.addAttribute("requestURI", request.getRequestURI());
  }

  @GetMapping(value="/{uuid}")
  @Admin
  public String getAppMasterByUuid(HttpServletRequest request, Model model, @NotEmpty @NotNull @PathVariable String uuid) {
    log.info("Inside getAppMasterByUuid");
    AppMasterDto appMasterDto = appMasterService.getAppMasterByUuid(uuid);
    model.addAttribute("appMaster", appMasterDto);
    model.addAttribute("requestURI", request.getRequestURI());

    return "core/appMaster/appMasterView";
  }

  @GetMapping("/{uuid}/edit")
  @Admin
  public String showEditForm(HttpServletRequest request,@NotEmpty @NotNull @PathVariable String uuid, Model model) {
    log.info("Inside showEditForm");
    AppMasterDto appMasterDto = appMasterService.getAppMasterByUuid(uuid);
    boolean editMode = (appMasterDto != null);
    appMasterDto.setEditMode(editMode);
    log.info("appMasterDto:: ", appMasterDto.getEditMode());
    model.addAttribute("appMasterDto", appMasterDto);
    model.addAttribute("requestURI", request.getRequestURI());
    return "core/appMaster/appMasterForm";
  }

  @PostMapping
  @Admin
  public String saveAppMaster(@ModelAttribute("appMasterDto") AppMasterDto appMasterDto) {
    log.info("Inside saveAppMaster");
    appMasterService.saveAppMaster(appMasterDto);
    return "redirect:/app-master";
  }

  @PostMapping("/{uuid}")
  @Admin
  public String updateAppMaster(HttpServletRequest request, @PathVariable String uuid, @Valid @ModelAttribute("appMasterDto") AppMasterDto appMasterDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
    log.info("Inside updateAppMaster");

    if (bindingResult.hasErrors()) {
      redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.appMasterDto",
          bindingResult);
      redirectAttributes.addFlashAttribute("appMasterDto", appMasterDto);
      redirectAttributes.addFlashAttribute("requestURI", request.getRequestURI());
      return "core/appMaster/appMasterForm";
    }

    try {
      appMasterService.updateAppMaster(uuid, appMasterDto);
    } catch (ResourceNotFoundException e) {
      log.error("Error:: {}",e);
      List<String > errorMessages = new ArrayList<>();
      errorMessages.add("Failed to update application.");
      redirectAttributes.addFlashAttribute("messages", errorMessages);
      redirectAttributes.addFlashAttribute("appMasterDto", appMasterDto);
      redirectAttributes.addFlashAttribute("requestURI", request.getRequestURI());
      return "core/appMaster/appMasterForm";

    }

    // Add the success message to the redirectAttributes
    redirectAttributes.addFlashAttribute("successMessage", "Record Updated Successfully!!!");

    return "redirect:/app-master/"+ uuid;
  }

//  @PostMapping("/{uuid}/delete")
//  @Admin
//  public String deleteAppMaster(@NotEmpty @NotNull @PathVariable String uuid) {
//    log.info("Inside deleteAppMaster");
//    appMasterService.deleteAppMaster(uuid);
//    return "redirect:/app-master";
//  }

  @PostMapping("/{uuid}/enable")
  @Admin
  public ResponseEntity<String> enableAppMaster(@NotEmpty @NotNull @PathVariable String uuid) {
    log.info("Inside enableAppMaster");
    appMasterService.enableDisableApp(uuid, true);
    return ResponseEntity.ok("App Enabled");
  }

  @PostMapping("/{uuid}/disable")
  @Admin
  public ResponseEntity<String> disableAppMaster(@NotEmpty @NotNull @PathVariable String uuid) {
    log.info("Inside disableAppMaster");
    appMasterService.enableDisableApp(uuid, false);
    return ResponseEntity.ok("App Disabled");
  }

}
