package jp.co.fsz.clounect.core.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import jp.co.fsz.clounect.core.user.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "app_usages_log")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AppUsagesLog {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", columnDefinition = "BIGINT")
  private Long id;

  @Column(name = "transaction_id")
  private UUID transactionId;

  @Column(name = "site_id")
  private Long siteId;

  @Size(max = 500)
  @Column(name = "webhook_url", length = 500)
  private String webhookUrl;

  @Column(name = "app_id")
  private Long appId;

  @Size(max = 255)
  @Column(name = "app_name")
  private String appName;

  @Size(max = 255)
  @Column(name = "app_usage_count")
  private String appUsageCount;

  @Size(max = 255)
  @Column(name = "app_description")
  private String appDescription;

  @Size(max = 255)
  @Column(name = "app_vendor")
  private String appVendor;

  @Column(name = "user_id")
  private Long userId;

  @Size(max = 255)
  @Column(name = "user_name")
  private String userName;

  @Size(max = 255)
  @Column(name = "company_name")
  private String companyName;

  @Column(name = "received_payload", length = Integer.MAX_VALUE)
  private String receivedPayload;

  @Column(name = "send_payload", length = Integer.MAX_VALUE)
  private String sendPayload;

  @Column(name = "error_log", length = Integer.MAX_VALUE)
  private String errorLog;

  @Column(name = "status")
  private Boolean status;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime lastUpdatedAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @Column(name = "is_active")
  private Boolean isActive;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.userId = getCurrentUser().getId();
    this.userName = getCurrentUser().getShortName();
    this.companyName = getCurrentUser().getCompanyName();
  }

  @PreUpdate
  protected void onUpdate() {
    this.lastUpdatedAt = LocalDateTime.now();
    this.userId = getCurrentUser().getId();
    this.userName = getCurrentUser().getShortName();
    this.companyName = getCurrentUser().getCompanyName();
  }

  private User getCurrentUser() {
    DefaultOidcUser defaultOidcUser = (DefaultOidcUser) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    Long userId = Long.parseLong(
        (String) defaultOidcUser.getAttributes().get("custom:user_id"));
    User user = new User();
    user.setId(userId);
    return user;
  }

}