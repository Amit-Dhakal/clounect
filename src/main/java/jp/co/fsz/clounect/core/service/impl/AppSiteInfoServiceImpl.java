package jp.co.fsz.clounect.core.service.impl;

import jp.co.fsz.clounect.core.model.AppSiteInfo;
import jp.co.fsz.clounect.core.repository.AppSiteInfoRepository;
import jp.co.fsz.clounect.core.service.AppSiteInfoService;
import org.springframework.stereotype.Service;

@Service
public class AppSiteInfoServiceImpl implements AppSiteInfoService {
  private final AppSiteInfoRepository appSiteInfoRepository;

  public AppSiteInfoServiceImpl(AppSiteInfoRepository appSiteInfoRepository) {
    this.appSiteInfoRepository = appSiteInfoRepository;
  }

  @Override
  public AppSiteInfo getAppSiteInfoById(Long id) {
    return appSiteInfoRepository.findAppSiteInfoById(id);
  }

  @Override
  public AppSiteInfo save(AppSiteInfo appSiteInfo) {
    appSiteInfo.setWebhookUrl("/webhook/");
    appSiteInfo = appSiteInfoRepository.save(appSiteInfo);
    appSiteInfo.setWebhookUrl("/webhook/" + appSiteInfo.getUuid());
    return appSiteInfo;
  }
}
