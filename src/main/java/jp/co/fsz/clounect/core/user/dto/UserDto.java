package jp.co.fsz.clounect.core.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jp.co.fsz.clounect.core.user.role.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * <p>[概 要] UserDto ユーザーDTO REST API 処理クラス。</p>
 * <p>[詳細] データベース内のデータを転送するため。</p>
 * <p>[備 考] なし。</p>
 * <p>[環 境] JDK17.0</p>
 * <p>Copyright フィーラーシステムZ, 2024</p>
 *
 * @author フィーラーシステムZ
 * @since 1.0
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
  private Long id;
  private UUID uuid;

  @NotBlank(message = "Name is required")
  @Size(max = 255, message = "Name cannot exceed 255 characters")
  private String shortName;

  @NotBlank(message = "Email is required")
  private String email;

  @NotBlank(message = "Company Name is required")
  private String companyName;

  @NotBlank(message = "Phone is required")
  @Pattern(regexp = "^\\+[1-9][0-9]{0,24}$", message = "Invalid phone number format")
  private String phone;

  @NotBlank(message = "Address is required")
  private String address;
  private String password;
  private Role role;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;
  private Boolean isActive;
}
