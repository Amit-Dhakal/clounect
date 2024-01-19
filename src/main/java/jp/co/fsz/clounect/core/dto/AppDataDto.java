package jp.co.fsz.clounect.core.dto;

import jp.co.fsz.clounect.core.model.AppData;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * <p>[概要] AppDataに関連する情報を保持するデータ転送オブジェクト（DTO）クラス。</p>
 * <p>[詳細] このクラスは、AppDataに関連する情報をまとめて保持し、データの転送を担当します。</p>
 * <p>[備考] 受信および送信ペイロード、作成タイムスタンプ、詳細、および成功または失敗を示すフラグのためのフィールドを含みます。
 * 一部のフィールドにはデフォルト値が設定されています。</p>
 * <p>[環境] JDK 17.0</p>
 * <p>著作権 FSZ 2024</p>
 *
 * @author 著者: FSZ
 * @since バージョン 1.0
 */
@Data
public class AppDataDto {

  private Long id;
  private UUID uuid;
  private AppSiteInfoDto appSiteId;
  private Boolean status;
  private String receivedPayload;
  private String sendPayload;
  private LocalDateTime execAt;
  private String cycle;
  private String detail;
  private Integer sendFlag;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;
  private Boolean isActive;

  public record Payload(List<Data> request, List<Data> response) {};

  public record Data(String id, String type, Map<String,Object> load) {};

  /**
   * <p>[概要] AppDataエンティティからAppDataDtoへの変換メソッド。</p>
   * <p>[詳細] この静的メソッドは、AppDataエンティティから対応するAppDataDtoへの変換を行います。</p>
   * <p>[備考] AppDataDtoはデータ転送オブジェクトであり、ビジネスロジックとUIレイヤーの間でデータを伝達するために使用されます。</p>
   *
   * @param entity 変換元のAppDataエンティティ,
   * @return 変換されたAppDataDtoオブジェクト,
   * @since 1.0
   */
  public static AppDataDto fromEntity(AppData entity) {
    AppDataDto dto = new AppDataDto();
    dto.setId(entity.getId());
    dto.setUuid(entity.getUuid());
    dto.setAppSiteId(AppSiteInfoDto.fromEntity(entity.getAppSiteId()));
    dto.setStatus(entity.getStatus());
    dto.setReceivedPayload(entity.getReceivedPayload());
    dto.setSendPayload(entity.getSendPayload());
    dto.setExecAt(entity.getExecAt());
    dto.setCycle(entity.getCycle());
    dto.setDetail(entity.getDetail());
    dto.setSendFlag(entity.getSendFlag());
    dto.setCreatedAt(entity.getCreatedAt());
    dto.setUpdatedAt(entity.getUpdatedAt());
    dto.setIsActive(entity.getIsActive());
    return dto;
  }

  /**
   * <p>[概要] AppDataDtoからAppDataエンティティへの変換メソッド。</p>
   * <p>[詳細] このメソッドは、AppDataDtoから新しいAppDataエンティティを作成して返します。</p>
   * <p>[備考] AppDataDtoはデータ転送オブジェクトであり、ビジネスロジックとUIレイヤーの間でデータを伝達するために使用されます。</p>
   *
   * @return 新しく作成されたAppDataエンティティオブジェクト,
   * @since 1.0
   */
  // toEntity method to convert AppDataDto to AppData
  public AppData toEntity() {
    AppData entity = new AppData();
    entity.setId(this.getId());
    entity.setUuid(this.getUuid());
    entity.setAppSiteId(this.getAppSiteId().toEntity());
    entity.setStatus(this.getStatus());
    entity.setReceivedPayload(this.getReceivedPayload());
    entity.setSendPayload(this.getSendPayload());
    entity.setExecAt(this.getExecAt());
    entity.setCycle(this.getCycle());
    entity.setDetail(this.getDetail());
    entity.setSendFlag(this.getSendFlag());
    entity.setCreatedAt(this.getCreatedAt());
    entity.setUpdatedAt(this.getUpdatedAt());
    entity.setIsActive(this.getIsActive());
    return entity;
  }
}