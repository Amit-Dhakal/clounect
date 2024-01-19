package jp.co.fsz.clounect.core.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.sql.Timestamp;
import java.util.UUID;

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

    @Column(name = "app_site_id")
    private Integer appSiteId;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "received_payload")
    private String receivedPayload;

    @Column(name = "send_payload")
    private String sendPayload;

    @Column(name = "exec_at")
    private Timestamp execAt;

    @Column(name = "cycle")
    private String cycle;

    @Column(name = "detail")
    private String detail;

    @Column(name = "send_flag")
    private Integer sendFlag;

    @Transient
    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @Column(name = "is_active")
    private Boolean isActive;
}
