package jp.co.fsz.clounect.core.dto;

import lombok.*;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Component
public class JustSfaDto {
  private String tenant;
  private String apiKey;
  private String panelId;
  private String tableId;
  private String detailId;
  private String filterId;
  private String subjectId;
  private String locationId;
  private String scheduleId;
}
