package jp.co.fsz.clounect.core.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * <p>[概要] Custom Authentication Success Handler for handling successful authentication.</p>
 * <p>[詳細] Implementation of {@link AuthenticationSuccessHandler} for handling authentication success.</p>
 * <p>[備 考] This class redirects users based on their role after successful authentication.</p>
 * <p>[環境] JDK17.0</p>
 * <p>著作権 FSZ 2024</p>
 *
 * @author FSZ
 * @since 1.0
 */
@Component
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  /**
   * Handles successful authentication and redirects users based on their roles.
   *
   * @param request        The HTTP request.
   * @param response       The HTTP response.
   * @param authentication The authentication object containing user information.
   * @throws IOException      If an I/O error occurs during redirection.
   * @throws ServletException If a servlet-related error occurs.
   * @since 1.0
   */
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
      HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {
    log.info("Inside onAuthenticationSuccess");
    log.info(authentication.getAuthorities().toString());

    for (GrantedAuthority auth : authentication.getAuthorities()) {

      DefaultOidcUser defaultOidcUser = (DefaultOidcUser) authentication.getPrincipal();

      Map<String, Object> userAttributes = defaultOidcUser.getAttributes();

      log.info("UserAttributes: {}", userAttributes);
      log.info("Authority: {}", auth.getAuthority());

    }

    response.sendRedirect("/dashboard");

  }
}
