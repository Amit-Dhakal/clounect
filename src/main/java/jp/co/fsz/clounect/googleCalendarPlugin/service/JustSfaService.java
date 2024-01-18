package jp.co.fsz.clounect.googleCalendarPlugin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.net.http.HttpClient;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;


/**
 * <p>[概要] Google Calendar サービスクラス。</p>
 * <p>[備考] HTTP トランスポート、JSON ファクトリ、アプリケーション名の設定が必要です。</p>
 * <p>[環境] JDK17.0</p>
 * <p>著作権 FSZ 2024</p>
 *
 * @author 著者FSZ
 * @since 1.0
 */
@Service
@Slf4j
public class JustSfaService {

  private final ObjectMapper objectMapper;
  private final HttpClient httpClient = HttpClient.newHttpClient();
  private final JsonDataOptimizationService jsonDataOptimizationService;

  public JustSfaService(ObjectMapper objectMapper, JsonDataOptimizationService jsonDataOptimizationService) {
    this.objectMapper = objectMapper;
    this.jsonDataOptimizationService = jsonDataOptimizationService;
  }

  /**
   * <p>[概要] 指定された状態に基づいてデータを取得するメソッド。</p>
   * <p>[詳細] データの取得には指定された状態情報を使用し、結果を真偽値で返します。</p>
   * <p>[備考] このメソッドは、指定された状態に基づいてデータを取得し、成功した場合は true を、
   * 失敗した場合は例外をスローします。</p>
   *
   * @param state 状態情報
   * @return データの取得が成功した場合は true、それ以外は false
   * @throws ParseException データの取得中に例外が発生した場合
   * @since 1.0
   */
  public Boolean getData(String state) throws ParseException {
    Map<String, String> getData = jsonDataOptimizationService.getSfaData(state);
    String tenant = getData.get("tenant");
    String apiKey = getData.get("api-key");
    String tableId = getData.get("teble-id");
    String panelId = getData.get("panel-id");
    String filterId = getData.get("filter-id");
    String scheduleId = getData.get("schedule-id");
    String subjectId = getData.get("subject-id");
    String locationId = getData.get("location-id");
    String detailId = getData.get("detail-id");

    String apiUrl = String.format("https://%s.justsfa.com/sites/api/services/v1/tables/%s/records/?panelName=%s&filterName=%s&limit=30",
        tenant, tableId, panelId, filterId);

    if (tenant.endsWith(".just-db.com")) {
      apiUrl = String.format("https://%s/sites/api/services/v1/tables/%s/records/?panelName=%s&filterName=%s&limit=30",
          tenant, tableId, panelId, filterId);
    }

    HttpRequest httpRequest = HttpRequest.newBuilder()
        .uri(URI.create(apiUrl))
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer " + apiKey)
        .build();

    try {
      HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        String responseBody = response.body();

        JsonNode jsonNode = parseJson(responseBody);
        assert jsonNode != null;

        if (jsonNode.isArray()) {
          for (JsonNode recordNode : jsonNode) {
            if (recordNode.has("record")) {
              JsonNode record = recordNode.get("record");

              if (record != null) {
                if (record.has("test01") && record.has(locationId)
                    && record.has(scheduleId) && record.has(subjectId)
                    && record.has(detailId)) {
                  log.info("Success! Required fields are present in all records.");
                  return true;
                } else {
                  log.error("Required fields are missing in a record.");
                  return false;
                }
              } else {
                log.error("Record is null in a record node.");
                return false;
              }
            } else {
              log.error("Record node is missing in a record node.");
              return false;
            }
          }
        } else {
          log.error("JSON response is empty or not an array.");
          return false;
        }

        log.info(responseBody);
      } else {
        log.error("HTTP error! Status: " + response.statusCode());
        return false;
      }
    } catch (Exception e) {
      log.error("Error: " + e.getMessage());
      return false;
    }
    return false;
  }

  /**
   * <p>[概要] JSON データを解析して JsonNode オブジェクトに変換するメソッド。</p>
   * <p>[詳細] 提供された JSON データを解析し、対応する JsonNode オブジェクトに変換します。</p>
   * <p>[備考] このメソッドは、JSON データを解析して JsonNode オブジェクトに変換し、
   * 変換に成功した場合は JsonNode オブジェクトを、失敗した場合は null を返します。</p>
   *
   * @param jsonData 解析対象の JSON データ
   * @return 解析された JsonNode オブジェクト、変換に失敗した場合は null
   * @since 1.0
   */
  private JsonNode parseJson(String jsonData) {
    try {
      return objectMapper.readTree(jsonData);
    } catch (JsonProcessingException e) {
      log.error("JSON DataError: " + e.getMessage());
      return null;
    }
  }
}
