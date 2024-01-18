//package jp.co.fsz.clounect.core.model;
//
//import jakarta.persistence.*;
//import jp.co.fsz.clounect.core.user.entity.User;
//import lombok.*;
//import org.hibernate.annotations.GenerationTime;
//import org.hibernate.annotations.JdbcTypeCode;
//import org.hibernate.type.SqlTypes;
//import org.hibernate.annotations.Generated;
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//@Table(name = "app_site_info")
//@Entity
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder(toBuilder = true)
//public class AppSiteInfoEntity {
//
//  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
//  private Long id;
//
//  @Generated(GenerationTime.ALWAYS)
//  @Column(name = "uuid", columnDefinition = "uuid DEFAULT uuid_generate_v4()", insertable = false, updatable = false)
//  private UUID uuid;
//
//  private String webhookUrl;
//
//
//  @ManyToOne(cascade = CascadeType.ALL)
//  @JoinColumn(name = "app_id")
//  private AppMaster appId;
//
//  @ManyToOne(cascade = CascadeType.ALL)
//  @JoinColumn(name = "user_id")
//  private User userId;
//
//  @JdbcTypeCode(SqlTypes.JSON)
//  private String config;
//  private Integer usageCount;
//  private Boolean isEnabled;
//  private LocalDateTime createdAt;
//  private Long createdBy;
//  private LocalDateTime lastUpdatedAt;
//  private Long lastUpdatedBy;
//  private LocalDateTime deletedAt;
//  private Long deletedBy;
//  private Boolean isActive;
//
//  @PrePersist
//  protected void prePersist(){
//    createdAt = LocalDateTime.now();
//  }
//
//  @PreUpdate
//  protected void preUpdate(){
//    lastUpdatedAt = LocalDateTime.now();
//  }
//
//
//}
