package jp.co.fsz.clounect.core.controller;

import jakarta.servlet.http.HttpServletRequest;
import jp.co.fsz.clounect.core.dto.DashboardDto;
import jp.co.fsz.clounect.core.security.annotations.Admin;
import jp.co.fsz.clounect.core.service.DashboardService;
import jp.co.fsz.clounect.core.util.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/dashboard")
public class DashboardController {

  private DashboardService dashboardService;

  public DashboardController(DashboardService dashboardService) {
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
}
