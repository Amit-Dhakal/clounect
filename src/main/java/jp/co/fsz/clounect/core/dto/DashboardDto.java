package jp.co.fsz.clounect.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDto {

  private String companyName;
  private String name;
  private String address;
  private boolean isAdmin;

  //App Stat
  private long totalApps;
  private long totalActiveApps;
  private long totalInactiveApps;

  //User Stat
  private long totalUsers;
  private long totalActiveUsers;
  private long totalInactiveUsers;
}
