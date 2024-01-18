package jp.co.fsz.clounect.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jp.co.fsz.clounect.core.model.AppMaster;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AppMasterDto {

  private Long id;
  private UUID uuid;

  @NotBlank(message = "Name is required")
  @Size(max = 255, message = "Name cannot exceed 255 characters")
  private String name;

  @NotBlank(message = "Description is required")
  private String description;

  @NotBlank(message = "Description is required")
  private String vendorName;

  @NotBlank(message = "Module Name is required")
  private String moduleName;
  private Boolean isActive;
  private Boolean editMode;
  private Boolean userStatus;

  // Convert Entity to DTO
  public static AppMasterDto fromEntity(AppMaster appMaster) {
    AppMasterDto appMasterDto = new AppMasterDto();
    appMasterDto.setId(appMaster.getId());
    appMasterDto.setUuid(appMaster.getUuid());
    appMasterDto.setName(appMaster.getName());
    appMasterDto.setDescription(appMaster.getDescription());
    appMasterDto.setVendorName(appMaster.getVendorName());
    appMasterDto.setModuleName(appMaster.getModuleName());
    appMasterDto.setIsActive(appMaster.getIsActive());
    return appMasterDto;
  }

  // Convert DTO to Entity
  public AppMaster toEntity() {
    AppMaster appMaster = new AppMaster();
    appMaster.setId(this.id);
    appMaster.setUuid(this.uuid);
    appMaster.setName(this.name);
    appMaster.setDescription(this.description);
    appMaster.setVendorName(this.vendorName);
    appMaster.setModuleName(this.moduleName);
    appMaster.setIsActive(this.isActive);
    return appMaster;
  }

}
