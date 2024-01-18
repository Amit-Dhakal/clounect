package jp.co.fsz.clounect.core.awscognito;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//TODO: Replace this file
@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class UserDto {

  @Nonnull
  private String name;

  @Nonnull
  private String email;

  @Nonnull
  private String password;

  @Nonnull
  private String userType;
}