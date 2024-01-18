package jp.co.fsz.clounect.core.model;

import jakarta.persistence.*;
import jp.co.fsz.clounect.core.user.entity.User;
import lombok.*;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;


/**
 * <p>[概要] アプリケーションサイト情報エンティティクラス。</p>
 * <p>[詳細] データベースの "app_site_info" テーブルと対応するエンティティクラスです。</p>
 * <p>[環境] JDK17.0</p>
 * <p>著作権 あなたの会社, 2024</p>
 *
 * @author 著者FSZ
 * @since 1.0
 */
@Table(name = "app_site_info")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AppSiteInfo extends AuditableEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "app_site_info_id_seq")
  @SequenceGenerator(name = "app_site_info_id_seq", sequenceName = "app_site_info_id_seq", allocationSize = 1)
  private Long id;

  @org.hibernate.annotations.Generated(GenerationTime.ALWAYS)
  @Column(name = "uuid", columnDefinition = "uuid DEFAULT uuid_generate_v4()", insertable = false, updatable = false)
  private UUID uuid;

  @org.hibernate.annotations.Generated(GenerationTime.ALWAYS)
  @Column(name = "webhook_url", columnDefinition = "uuid DEFAULT uuid_generate_v4()", insertable = false, updatable = false)
  private String webhookUrl;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "app_id")
  private AppMaster appId;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "user_id")
  private User userId;

  @Column(columnDefinition = "jsonb")
  @JdbcTypeCode(SqlTypes.JSON)
  private String config;
  private Integer usageCount;
  private Boolean isEnabled;
  private Boolean isActive;
}

