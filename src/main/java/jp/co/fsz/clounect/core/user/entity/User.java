package jp.co.fsz.clounect.core.user.entity;

import jakarta.persistence.*;
import jp.co.fsz.clounect.core.user.role.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * <p>[概 要] User ユーザー REST API 処理クラス。</p>
 * <p>[詳細] データベースの操作。</p>
 * <p>[備 考] なし。</p>
 * <p>[環 境] JDK17.0</p>
 * <p>Copyright フィーラーシステムZ, 2024</p>
 *
 * @author フィーラーシステムZ
 * @since 1.0
 */
@Entity
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "users_id_seq")
  @SequenceGenerator(name = "users_id_seq", sequenceName = "users_id_seq", allocationSize = 1)
  private Long id;

  @Generated(GenerationTime.ALWAYS)
  @Column(name = "uuid", columnDefinition = "uuid DEFAULT uuid_generate_v4()", insertable = false, updatable = false)
  private UUID uuid;
  private String shortName;
  private String email;
  private String companyName;
  private String contactNumber;
  private String address;

  @Enumerated(EnumType.STRING)
  @Column(name = "role")
  private Role role;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;
  private Boolean isActive;

  @PrePersist
  protected void prePersist(){
    createdAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void preUpdate(){
    updatedAt = LocalDateTime.now();
  }

  @PreRemove
  public void preRemove() {
    this.isActive = false;
    this.deletedAt = LocalDateTime.now();
  }
}
