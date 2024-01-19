package jp.co.fsz.clounect.core.dto;

import jp.co.fsz.clounect.core.model.AppUsagesLog;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppLogDto {
  private Long id;
  private LocalDateTime createdAt;
  private String name;
  private String destination;
  private String collaborate;
  private String result;
  private Boolean isActive;

  public static AppLogDto fromEntity(AppUsagesLog appUsagesLog) {
    AppLogDto appLogDto = new AppLogDto();
    appLogDto.setId(appUsagesLog.getId());
    appLogDto.setCreatedAt(appUsagesLog.getCreatedAt());
    appLogDto.setName(appUsagesLog.getUserName());
    appLogDto.setDestination(appUsagesLog.getAppName());
    appLogDto.setCollaborate("type");
    appLogDto.setResult(appUsagesLog.getStatus()?"success":"fail");
    return appLogDto;
  }
}