package jp.co.fsz.clounect.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * <p>[概 要] ResourceNotFoundException リソースが見つからない例外。</p>
 * <p>[詳細] 何かが存在しない場合に 404 でエラーがスローされる。</p>
 * <p>[備 考] なし。</p>
 * <p>[環 境] JDK17.0</p>
 * <p>Copyright フィーラーシステムZ, 2024</p>
 *
 * @author フィーラーシステムZ
 * @since 17.0
 */
@Getter
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
  private final String resourceName;
  private final String fieldName;
  private final Object fieldValue;

  /**
   * <p>[概 要] リソースが見つからない例外。</p>
   * <p>[詳 細] 何かが見つからない場合の 404 エラーであるこの例外を処理します。</p>
   * <p>[備 考] なし。</p>
   *
   * @param resourceName リソース名,
   * @param fieldName    フィールド名,
   * @param fieldValue   フィールド値
   * @since 17.0
   */
  public <T> ResourceNotFoundException(String resourceName, String fieldName,
      T fieldValue) {
    super(
        String.format("%s not found with %s : %s", resourceName, fieldName, fieldValue));
    this.resourceName = resourceName;
    this.fieldName = fieldName;
    this.fieldValue = fieldValue;
  }
}