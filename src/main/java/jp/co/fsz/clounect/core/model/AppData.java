package jp.co.fsz.clounect.core.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * <p>[概要] アプリケーション内でAppDataを表すエンティティクラス。</p>
 * <p>[詳細] このクラスはデータベースの "app_data" テーブルにマッピングされるエンティティであり、AppDataに関連する情報をカプセル化し、対応するデータベーステーブルの表現となります。</p>
 * <p>[コンストラクタ] デフォルトコンストラクタ（引数なし）、全引数コンストラクタ、およびtoBuilderがtrueに設定されたビルダーが提供されています。</p>
 * <p>[アノテーション] クラスはJPAアノテーションで注釈が付けられており、データベースへのマッピングを定義する@Entityおよび@Tableが含まれています。</p>
 * <p>[環境] JDK 17.0</p>
 * <p>著作権 FSZ 2024</p>
 *
 * @author 著者: FSZ
 * @since バージョン 1.0
 */
@Table(name = "app_data")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AppData {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Generated(GenerationTime.ALWAYS)
  @Column(name = "uuid", columnDefinition = "uuid DEFAULT uuid_generate_v4()", insertable = false, updatable = false)
  private UUID uuid;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_site_id")
    private AppSiteInfo appSiteId;

  @Column(name = "status")
  private Boolean status;

  @Column(name = "received_payload")
  private String receivedPayload;

  @Column(name = "send_payload")
  private String sendPayload;

    @Column(name = "exec_at")
    private LocalDateTime execAt;

  @Column(name = "cycle")
  private String cycle;

  @Column(name = "detail")
  private String detail;

  @Column(name = "send_flag")
  private Integer sendFlag;

  @Column(name = "is_active")
  private Boolean isActive;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  private LocalDateTime deletedAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }
}

