package jp.co.fsz.clounect.googleCalendarPlugin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.api.services.calendar.model.Event;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import jp.co.fsz.clounect.core.dto.AppSiteInfoDto;
import jp.co.fsz.clounect.core.model.AppData;
import jp.co.fsz.clounect.core.model.AppSiteInfo;
import jp.co.fsz.clounect.core.repository.AppDataRepository;
import jp.co.fsz.clounect.core.repository.AppMasterRepository;
import jp.co.fsz.clounect.core.repository.AppSiteInfoRepository;
import jp.co.fsz.clounect.core.user.repository.UserRepo;
import jp.co.fsz.clounect.googleCalendarPlugin.dto.OAuthToken;
import jp.co.fsz.clounect.googleCalendarPlugin.exception.CouldNotPerformActionException;
import jp.co.fsz.clounect.googleCalendarPlugin.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * <p>[概要] Google Record サービス クラス。</p>
 * <p>[詳細] Google Calendar API へのアクセスに関するサービスを提供します。</p>
 * <p>[環境] JDK17.0</p>
 * <p>著作権 あなたの会社, 2024</p>
 *
 * @author 著者FSZ
 * @since 1.0
 */
@Service
@Slf4j
public class GoogleRecordService {
  private final AppSiteInfoRepository appSiteInfoRepo;
  private final JsonDataOptimizationService jsonDataOptimizationService;
  private final GoogleOAuthService googleOAuthService;
  private final AppMasterRepository appMasterRepository;
  private final UserRepo userRepo;
  private final AppDataRepository appDataRepository;
  private final SecurityUtil securityUtil;
  private final Gson gson = new Gson();

  /**
   * <p>[概要] Google Record サービス コンストラクタ。</p>
   * <p>[詳細] Google Record サービスのインスタンスを生成するためのコンストラクタです。</p>
   * <p>[環境] JDK17.0</p>
   * <p>著作権 あなたの会社, 2024</p>
   *
   * @param appSiteInfoRepo             {@link AppSiteInfoRepository} インスタンス
   * @param jsonDataOptimizationService {@link JsonDataOptimizationService} インスタンス
   * @param googleOAuthService          {@link GoogleOAuthService} インスタンス
   * @param appMasterRepository
   * @param userRepo
   * @param appDataRepository
   * @param securityUtil
   * @since 1.0
   */
  public GoogleRecordService(
      AppSiteInfoRepository appSiteInfoRepo, JsonDataOptimizationService jsonDataOptimizationService,
      GoogleOAuthService googleOAuthService, AppMasterRepository appMasterRepository,
      UserRepo userRepo, AppDataRepository appDataRepository,
      SecurityUtil securityUtil) {
    this.appSiteInfoRepo = appSiteInfoRepo;
    this.jsonDataOptimizationService = jsonDataOptimizationService;
    this.googleOAuthService = googleOAuthService;
    this.appMasterRepository = appMasterRepository;
    this.userRepo = userRepo;
    this.appDataRepository = appDataRepository;
    this.securityUtil = securityUtil;
  }

  /**
   * <p>[概要] 設定情報を保存するメソッド。</p>
   * <p>[詳細] 指定されたデータとコードを使用して、設定情報を保存します。</p>
   *
   * @param data 設定情報に関連するデータ
   * @param code 設定情報に関連するコード
   * @throws ParseException          日付の解析に失敗した場合にスローされます。
   * @throws JsonProcessingException JSON データの処理に失敗した場合にスローされます。
   * @since 1.0
   */
  public void storeConfiguration(String data, String code)
      throws ParseException, JsonProcessingException {

    Map<String, Object> configMap = new HashMap<>();
    Map<String, String> justSfa = jsonDataOptimizationService.getSfaData(data);

    if (code.isEmpty()) {
      if (justSfa != null && !justSfa.isEmpty()) {
        configMap.put("justSfa", justSfa);
      }
    }

    if (checkNullAndEmpty(code)) {
      Map<String, String> parsedState = jsonDataOptimizationService.parseState(data,
          code);

      if (parsedState != null) {
        Map<String, String> googleRecords = new HashMap<>();
        googleRecords.put("currentUrl", parsedState.get("currentUrl"));
        googleRecords.put("clientSecret", parsedState.get("clientSecret"));
        googleRecords.put("clientId", parsedState.get("clientId"));
        googleRecords.put("redirectUri", parsedState.get("redirectUri"));
        configMap.put("googleRecords", googleRecords);

        OAuthToken token = googleOAuthService.getAccessToken(parsedState.get("clientId"),
            parsedState.get("clientSecret"), parsedState.get("code"),
            parsedState.get("redirectUri"));

        String refreshToken = token.getRefreshToken();

        if (checkNullAndEmpty(refreshToken)) {
          configMap.put("refreshToken", refreshToken);
        }
      }
    }

    Optional<AppSiteInfo> existingRecord = securityUtil.getExistingRecord();


    if (existingRecord.isPresent()) {
      AppSiteInfo existingInfo = existingRecord.get();
      AppSiteInfoDto appSiteInfoDto =  AppSiteInfoDto.fromEntity(existingInfo);
      appSiteInfoDto.setConfig(gson.toJson(configMap));
      String configData = existingInfo.getConfig();
      AppSiteInfo appSiteInfo = appSiteInfoDto.toEntity();

      if (checkNullAndEmpty(configData)) {
        Map<String, Object> checkedData = checkConfigData(configData);

        if (!checkedData.isEmpty()) {
          checkedData.putAll(configMap);
          appSiteInfo.setConfig(gson.toJson(checkedData));
        } else {
          appSiteInfo.setConfig(gson.toJson(configMap));
        }
      } else {
        appSiteInfo.setConfig(gson.toJson(configMap));
      }
      appSiteInfoRepo.save(appSiteInfo);
    }
  }

  /**
   * <p>[概要] 設定データの妥当性を確認するメソッド。</p>
   * <p>[詳細] 指定された設定データが有効かどうかを確認し、結果を {@code Map<String, Object>} で返します。</p>
   *
   * @param configData 確認する設定データ
   * @return 設定データの妥当性を表す {@code Map<String, Object>}。詳細は実装に依存します。
   * @since 1.0
   */
  private Map<String, Object> checkConfigData(String configData) {
    TypeToken<Map<String, Object>> typeToken = new TypeToken<>() {
    };
    Map<String, Object> map = gson.fromJson(configData, typeToken.getType());

    map.put("justSfa", map.remove("justSfa"));
    map.put("refreshToken", map.remove("refreshToken"));
    map.put("googleRecords", map.remove("googleRecords"));

    return map;
  }

  /**
   * <p>[概要] Nullまたは空文字列のチェックを行うメソッド。</p>
   * <p>[詳細] 指定された文字列がNullまたは空文字列であるかを判定し、結果を真偽値で返します。</p>
   * <p>[メソッド名] checkNullAndEmpty</p>
   * <p>[備考] このメソッドは、指定された文字列がNullまたは空文字列の場合にtrueを返し、それ以外の場合にはfalseを返します。</p>
   *
   * @param value チェック対象の文字列
   * @return Nullまたは空文字列であればtrue、それ以外はfalse
   * @since 1.0
   */
  private Boolean checkNullAndEmpty(String value) {
    return value != null && !value.isEmpty();
  }

  /**
   * <p>[概要] ユーザーフォームデータを保存するメソッド。</p>
   * <p>[詳細] ユーザーが提供したフォームデータを受け取り、それをデータストアに保存します。</p>
   * <p>[メソッド名] storeUserFormData</p>
   * <p>[備考] このメソッドは、ユーザーフォームデータを引数として受け取り、それをデータベースやファイルなどの適切なデータストアに永続化します。</p>
   *
   * @param userData 保存するユーザーフォームデータ
   * @since 1.0
   */
  public void storeUserFormData(String userData) {
    Map<String, Object> configMap = new HashMap<>();
    List<Map<String, Object>> newUserFormData = jsonDataOptimizationService.getUserData(
        userData);

    configMap.put("userRecord", newUserFormData);
    Optional<AppSiteInfo> existingRecord = securityUtil.getExistingRecord();

    if (existingRecord.isPresent()) {
      AppSiteInfo existingInfo = existingRecord.get();
      String configData = existingInfo.getConfig();

      if (checkNullAndEmpty(configData)) {
        Map<String, Object> checkedData = checkConfigData(configData);

        if (!checkedData.isEmpty()) {
          checkedData.putAll(configMap);
          existingInfo.setConfig(gson.toJson(checkedData));
        } else {
          existingInfo.setConfig(gson.toJson(configMap));
        }
      } else {
        existingInfo.setConfig(gson.toJson(configMap));
      }

      appSiteInfoRepo.save(existingInfo);
    } else {
      AppSiteInfo appSiteInfo = new AppSiteInfo();
      appSiteInfo.setWebhookUrl(appSiteInfo.getWebhookUrl());
      appSiteInfo.setConfig(gson.toJson(configMap));
      appSiteInfoRepo.save(appSiteInfo);
    }
  }

  /**
   * 指定されたイベントデータを受信したときに、関連するアプリケーションサイトにデータを追加します。
   *
   * @param createdData 受信したイベントデータ。
   * @param appSiteId アプリケーションサイトの識別子。
   * @throws JsonProcessingException データの追加プロセス中にエラーが発生した場合。
   * @since 1.0
   */
  public void appendReceivedPayload(Event createdData, Long appSiteId) {
    String receivedData = String.valueOf(createdData);
    Optional<AppData> appDataOptional = appDataRepository.getByAppSiteId(appSiteId);

    if (appDataOptional.isPresent()) {
      AppData existingAppData = appDataOptional.get();

      String currentReceivedPayload = existingAppData.getReceivedPayload();

      if (currentReceivedPayload == null || currentReceivedPayload.isEmpty()) {
        currentReceivedPayload = "[]";
      }

      try {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = (ArrayNode) objectMapper.readTree(currentReceivedPayload);

        JsonNode newObject = objectMapper.readTree(receivedData);
        arrayNode.add(newObject);

        String updatedReceivedPayload = arrayNode.toPrettyString();

        existingAppData.setSendPayload(updatedReceivedPayload);

        appDataRepository.save(existingAppData);
      } catch (JsonProcessingException e) {
        log.error("Error :", e);
        throw new CouldNotPerformActionException("Error while handling payload");
      }
    }
  }
}
