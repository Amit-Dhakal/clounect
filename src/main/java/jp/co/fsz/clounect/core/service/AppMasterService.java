package jp.co.fsz.clounect.core.service;

import jp.co.fsz.clounect.core.dto.AppMasterDto;
import jp.co.fsz.clounect.core.exception.ResourceNotFoundException;
import jp.co.fsz.clounect.core.model.AppMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;

public interface AppMasterService {
  Page<AppMasterDto> getAllAppMasters(Pageable pageable);

  List<AppMasterDto> getAllActiveAppMasters();

  Page<AppMasterDto> getAllActiveAppMasters(Pageable pageable);

  AppMasterDto getAppMasterById(Long id) throws ResourceNotFoundException;

  AppMasterDto getAppMasterByUuid(String uuid) throws ResourceNotFoundException;

  AppMasterDto saveAppMaster(AppMasterDto appMasterDto);

  AppMasterDto updateAppMaster(String uuid, AppMasterDto appMasterDto) throws
      ResourceNotFoundException;

  boolean deleteAppMaster(String uuid) throws ResourceNotFoundException;

  boolean enableDisableApp(String uuid, boolean value) throws ResourceNotFoundException;

  HashMap<String, Long> getAppCount();
}