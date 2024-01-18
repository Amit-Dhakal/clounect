package jp.co.fsz.clounect.core.dto;

import lombok.*;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Component
public class GoogleCredentialsDto {
  private String clientId;
  private String currentUrl;
  private String redirectUri;
  private String clientSecret;
  private String refreshToken;
}
