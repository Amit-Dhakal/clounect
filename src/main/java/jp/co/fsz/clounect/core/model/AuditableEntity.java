package jp.co.fsz.clounect.core.model;

import jakarta.persistence.*;
import jp.co.fsz.clounect.core.user.entity.User;
import lombok.Data;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
public abstract class AuditableEntity {

  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="created_by")
  private User createdBy;

  private LocalDateTime createdAt;

  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="updated_by")
  private User updatedBy;

  private LocalDateTime updatedAt;

  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="deleted_by")
  private User deletedBy;

  private LocalDateTime deletedAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.createdBy = getCurrentUser();
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
    this.updatedBy = getCurrentUser();
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

//  private Long getCurrentUser() {
//    DefaultOidcUser defaultOidcUser = (DefaultOidcUser) SecurityContextHolder.getContext()
//        .getAuthentication().getPrincipal();
//    Long userId = Long.parseLong(
//        (String) defaultOidcUser.getAttributes().get("custom:user_id"));
//   return userId;
//  }
}
