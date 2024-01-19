package jp.co.fsz.clounect.core.service;

import jp.co.fsz.clounect.core.dto.AppDataDto;
import jp.co.fsz.clounect.core.exception.ResourceNotFoundException;
import jp.co.fsz.clounect.core.model.AppData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * <p>[概要] AppData関連のサービスを処理する契約を定義するインターフェース。</p>
 * <p>[詳細] このインターフェースは、AppDataに関連するサービスを提供するクラスによって実装されるメソッドを概説しています。
 * AppDataエンティティを管理するための操作を指定する契約として機能します。</p>
 * <p>[環境] JDK 17.0</p>
 * <p>著作権 FSZ 2024</p>
 *
 * @author 著者: FSZ
 * @since バージョン 1.0
 */
public interface AppDataService {
  Page<AppDataDto> getAllAppDatas(Pageable pageable);

  Page<AppDataDto> searchAppDataByDetail(String query, Pageable pageable);

  List<AppDataDto> getAllActiveAppDatas();

  Page<AppDataDto> getAllActiveAppDatas(Pageable pageable);

  AppDataDto getAppDataById(Long id) throws ResourceNotFoundException;

  AppDataDto getAppDataByUuid(String uuid) throws ResourceNotFoundException;

  AppDataDto saveAppData(AppDataDto appMasterDto);

  AppDataDto updateAppData(String uuid, AppDataDto appMasterDto) throws
      ResourceNotFoundException;

  boolean deleteAppData(String uuid) throws ResourceNotFoundException;

  boolean enableDisableAppData(String uuid, boolean value) throws ResourceNotFoundException;

  Optional<AppDataDto> getByRecIdAndAppSiteId(Integer recId, Long appSiteId);

}