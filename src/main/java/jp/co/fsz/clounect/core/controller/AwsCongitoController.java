package jp.co.fsz.clounect.core.controller;

import jp.co.fsz.clounect.core.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@Slf4j
public class AwsCongitoController {

  @GetMapping("/dummy")
  public ModelAndView dummy(ModelAndView mv) {
    log.info("Inside dummy");

    // Retrieve user details from the Principal object
    Authentication authentication = SecurityContextHolder.getContext()
        .getAuthentication();

    // Display user details in the console
    if (authentication != null && authentication.getPrincipal() != null) {
      log.info("User Details: " + authentication.getPrincipal());
      mv.addObject("userDetails", authentication.getPrincipal());
    }
    mv.setViewName("core/hello");
    return mv;
  }

  @GetMapping("/oauth2")
  public ModelAndView handleOAuth2Callback(ModelAndView mv) {
    log.info("Inside handleOAuth2Callback");

    // Retrieve user details from the Principal object
    Authentication authentication = SecurityContextHolder.getContext()
        .getAuthentication();

    // Display user details in the console
    if (authentication != null && authentication.getPrincipal() != null) {
     log.info("User Details: " + authentication.getPrincipal());
      mv.addObject("userDetails", authentication.getPrincipal());
    }
    mv.setViewName("core/hello");
    return mv;
  }

}
