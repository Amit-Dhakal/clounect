package jp.co.fsz.clounect.core.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jp.co.fsz.clounect.ClounectApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Slf4j
public class CustomLogoutHandler extends SimpleUrlLogoutSuccessHandler {

  private final String logoutUrl;
  private final String logoutRedirectUrl;
  private final String clientId;

  private final String RESPONSE_TYPE = "code";

  public CustomLogoutHandler(String logoutUrl, String logoutRedirectUrl,
      String clientId) {
    this.logoutUrl = logoutUrl;
    this.logoutRedirectUrl = logoutRedirectUrl;
    this.clientId = clientId;
  }

  @Override
  protected String determineTargetUrl(HttpServletRequest request,
      HttpServletResponse response, Authentication authentication) {
    log.info("Inside determineTargetUrl");

    return UriComponentsBuilder.fromUri(URI.create(logoutUrl))
        .queryParam("client_id", clientId).queryParam("logout_uri", logoutRedirectUrl)
        .encode(StandardCharsets.UTF_8).build().toUriString();
  }
}
