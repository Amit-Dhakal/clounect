package jp.co.fsz.clounect.core.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p> [概要] Configures security settings, OAuth2 login, and logout for the Spring Boot application.</p>
 *
 * <p>[詳細] This class provides security configuration, OAuth2 login, and logout settings. It specifies
 * a custom success handler and authorities mapper for user role mapping based on Cognito groups.
 * </p>
 * <p>[環境] JDK17.0</p>
 * <p>著作権 FSZ 2024</p>
 *
 * @author FSZ
 * @since 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Slf4j
public class WebSecurityConfig {

  @Value("${aws.cognito.logoutUrl}")
  private String logoutUrl;

  @Value("${aws.cognito.logout.success.redirectUrl}")
  private String logoutRedirectUrl;

  @Value("${spring.security.oauth2.client.registration.cognito.clientId}")
  private String clientId;

  private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

  /**
   * Constructs the WebSecurityConfig with a custom authentication success handler.
   *
   * @param customAuthenticationSuccessHandler The custom success handler.
   */
  @Autowired
  public WebSecurityConfig(CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
    this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
  }

  /**
   * Configures security settings for the application.
   *
   * @param http The {@link HttpSecurity} to configure.
   * @return The configured {@link SecurityFilterChain}.
   * @throws Exception If an error occurs during configuration.
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {


    http.authorizeHttpRequests(
            request -> request
                .requestMatchers("/google/**").permitAll()
                .anyRequest().authenticated())
        .csrf((csrf) -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        )
        .oauth2Login(
            oauth -> oauth
//                .redirectionEndpoint(endPoint -> endPoint.baseUri("http://localhost:8090/oauth2"))
                .userInfoEndpoint(
                    userInfoEndpointConfig -> userInfoEndpointConfig.userAuthoritiesMapper(
                        userAuthoritiesMapper()))
                .successHandler(customAuthenticationSuccessHandler))
        .logout(httpSecurityLogoutConfigurer -> {
          httpSecurityLogoutConfigurer.permitAll().logoutSuccessHandler(
                  new CustomLogoutHandler(logoutUrl, logoutRedirectUrl, clientId))
              .invalidateHttpSession(true);
        });

    return http.build();
  }

  /**
   * Maps Cognito groups to Spring Security roles.
   *
   * @return The {@link GrantedAuthoritiesMapper} for user role mapping.
   */
  @Bean
  public GrantedAuthoritiesMapper userAuthoritiesMapper() {
    log.info("Inside userAuthoritiesMapper");
    return (authorities) -> {
      Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

      try {
        OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) new ArrayList<>(
            authorities).get(0);

        mappedAuthorities = ((ArrayList<?>) oidcUserAuthority.getAttributes()
            .get("cognito:groups")).stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
            .collect(Collectors.toSet());

        log.info("Mapped Authorities: {}", mappedAuthorities);
      } catch (Exception exception) {
        log.error("Exception Occurred: {}", exception.getMessage());
      }
      return mappedAuthorities;
    };
  }
}

