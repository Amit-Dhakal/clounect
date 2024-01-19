package jp.co.fsz.clounect.core.dto;

import jp.co.fsz.clounect.core.model.AppMaster;
import jp.co.fsz.clounect.core.model.AppSiteInfo;
import jp.co.fsz.clounect.core.user.entity.User;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AppSiteInfoDto {

  private Long id;
  private UUID uuid;
  private String webhookUrl;
  private AppMaster appId;
  private User userId;
  private String config;
  private Integer usageCount;
  private Boolean isEnabled;
  private Boolean isActive;

  public AppSiteInfo toEntity() {
    AppSiteInfo appSiteInfo = new AppSiteInfo();
    appSiteInfo.setId(this.id);
    appSiteInfo.setUuid(this.uuid);
    appSiteInfo.setWebhookUrl(this.webhookUrl);
    appSiteInfo.setAppId(this.appId);
    appSiteInfo.setUserId(this.userId);
    appSiteInfo.setConfig(this.config);
    appSiteInfo.setUsageCount(this.usageCount);
    appSiteInfo.setIsEnabled(this.isEnabled);
    appSiteInfo.setIsActive(this.isActive);
    return appSiteInfo;
  }

  public static AppSiteInfoDto fromEntity(AppSiteInfo appSiteInfo) {
    return AppSiteInfoDto.builder()
        .id(appSiteInfo.getId())
        .uuid(appSiteInfo.getUuid())
        .webhookUrl(appSiteInfo.getWebhookUrl())
        .appId(appSiteInfo.getAppId())
        .userId(appSiteInfo.getUserId())
        .config(appSiteInfo.getConfig())
        .usageCount(appSiteInfo.getUsageCount())
        .isEnabled(appSiteInfo.getIsEnabled())
        .isActive(appSiteInfo.getIsActive()).build();
  }

  public Long getAppId(){
    return this.appId.getId();
  }
}
