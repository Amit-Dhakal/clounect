package jp.co.fsz.clounect.googleCalendarPlugin.dto;

import lombok.*;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Component
public class UserDetails {
  private String username;
  private String justSfaUserId;
  private String googleEmail;
  private boolean linkable;
  private boolean validateEmailFlag;
}
