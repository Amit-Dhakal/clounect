package jp.co.fsz.clounect.core.controller;

import jakarta.servlet.http.HttpServletRequest;
import jp.co.fsz.clounect.core.security.annotations.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/app-site")
public class AppSiteInfoController {
  @GetMapping
  @User
  public String getUserApp(
      HttpServletRequest request, Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
    log.info("Inside getUserApp");
    int currentPage = page.orElse(1) - 1;
    Integer pageSize = size.orElse(1);
    Pageable pageable = PageRequest.of(currentPage, pageSize);
    model.addAttribute("requestURI", request.getRequestURI());
    model.addAttribute("isAdmin", true);
    return "core/appLogs/appLogList";
  }
}
