package jp.co.fsz.clounect.core.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class AppDataDto {
  private Long id;

  private UUID uuid;

  private Integer appSiteId;

  private Boolean status;

  private String receivedPayload;

  private String sendPayload;

  private Timestamp execAt;

  private String cycle;

  private String detail;

  private Integer sendFlag;

  private Timestamp createdAt;

  private Timestamp updatedAt;

  private Timestamp deletedAt;

  private Boolean isActive;
}
