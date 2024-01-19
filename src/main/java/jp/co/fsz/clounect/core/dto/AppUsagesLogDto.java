package jp.co.fsz.clounect.core.dto;

import jakarta.validation.constraints.Size;
import jp.co.fsz.clounect.core.model.AppSiteInfo;
import jp.co.fsz.clounect.core.model.AppUsagesLog;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link jp.co.fsz.clounect.core.model.AppUsagesLog}
 */
@Data
public class AppUsagesLogDto {
  Long id;

  @Size(max = 255) String appUsageCount;
  @Size(max = 255) String appDescription;
  @Size(max = 255) String appVendor;
  Long userId;
  UUID transactionId;
  Long siteId;

  @Size(max = 500) String webhookUrl;
  Long appId;

  @Size(max = 255) String appName;
  @Size(max = 255) String userName;
  @Size(max = 255) String companyName;
  String receivedPayload;
  String sendPayload;
  String errorLog;
  Boolean status;
  LocalDateTime createdAt;
  LocalDateTime lastUpdatedAt;
  LocalDateTime deletedAt;
  Boolean isActive;

  public static AppUsagesLogDto fromEntity(AppUsagesLog entity) {
    AppUsagesLogDto dto = new AppUsagesLogDto();
    dto.setId(entity.getId());
    dto.setAppUsageCount(entity.getAppUsageCount());
    dto.setAppDescription(entity.getAppDescription());
    dto.setAppVendor(entity.getAppVendor());
    dto.setUserId(entity.getUserId());
    dto.setTransactionId(entity.getTransactionId());
    dto.setSiteId(entity.getSiteId());
    dto.setWebhookUrl(entity.getWebhookUrl());
    dto.setAppId(entity.getAppId());
    dto.setAppName(entity.getAppName());
    dto.setUserName(entity.getUserName());
    dto.setCompanyName(entity.getCompanyName());
    dto.setReceivedPayload(entity.getReceivedPayload());
    dto.setSendPayload(entity.getSendPayload());
    dto.setErrorLog(entity.getErrorLog());
    dto.setStatus(entity.getStatus());
    dto.setCreatedAt(entity.getCreatedAt());
    dto.setLastUpdatedAt(entity.getLastUpdatedAt());
    dto.setDeletedAt(entity.getDeletedAt());
    dto.setIsActive(entity.getIsActive());
    return dto;
  }

  public AppUsagesLog toEntity() {
    AppUsagesLog entity = new AppUsagesLog();
    entity.setId(this.getId());
    entity.setAppUsageCount(this.getAppUsageCount());
    entity.setAppDescription(this.getAppDescription());
    entity.setAppVendor(this.getAppVendor());
    entity.setUserId(this.getUserId());
    entity.setTransactionId(this.getTransactionId());
    entity.setSiteId(this.getSiteId());
    entity.setWebhookUrl(this.getWebhookUrl());
    entity.setAppId(this.getAppId());
    entity.setAppName(this.getAppName());
    entity.setUserName(this.getUserName());
    entity.setCompanyName(this.getCompanyName());
    entity.setReceivedPayload(this.getReceivedPayload());
    entity.setSendPayload(this.getSendPayload());
    entity.setErrorLog(this.getErrorLog());
    entity.setStatus(this.getStatus());
    entity.setCreatedAt(this.getCreatedAt());
    entity.setLastUpdatedAt(this.getLastUpdatedAt());
    entity.setDeletedAt(this.getDeletedAt());
    entity.setIsActive(this.getIsActive());
    return entity;
  }

  public void setAppSiteInfo(AppSiteInfo appSiteInfo) {
    this.setSiteId(appSiteInfo.getId());
    this.setAppId(appSiteInfo.getAppId().getId());
    this.setWebhookUrl(appSiteInfo.getWebhookUrl());
    this.setAppName(appSiteInfo.getAppId().getName());
    this.setAppUsageCount(String.valueOf(appSiteInfo.getUsageCount()));
    this.setAppDescription(appSiteInfo.getAppId().getDescription());
    this.setAppVendor(appSiteInfo.getAppId().getVendorName());

  }
}