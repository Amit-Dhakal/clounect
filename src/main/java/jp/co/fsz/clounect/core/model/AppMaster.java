package jp.co.fsz.clounect.core.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.util.UUID;

@Data
@Entity
@Table(name = "app_master")
public class AppMaster extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Generated(GenerationTime.ALWAYS)
  @Column(name = "uuid", columnDefinition = "uuid DEFAULT uuid_generate_v4()", insertable = false, updatable = false)
  private UUID uuid;

  @Column(name = "name")
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "module_name")
  private String moduleName;

  @Column(name = "vendor_name")
  private String vendorName;

  @Column(name = "is_active")
  private Boolean isActive;

}
