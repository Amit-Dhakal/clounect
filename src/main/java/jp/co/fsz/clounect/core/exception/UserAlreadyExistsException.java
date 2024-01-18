package jp.co.fsz.clounect.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * <p>[概 要] UserAlreadyExists このユーザーは既に存在します。</p>
 * <p>[詳細] 挿入時にユーザーがすでにデータベースに存在する場合にスローされます。</p>
 * <p>[備 考] なし。</p>
 * <p>[環 境] JDK17.0</p>
 * <p>Copyright フィーラーシステムZ, 2024</p>
 *
 * @author NTTコムウェア
 * @since 13.0
 */
@Getter
public class UserAlreadyExistsException extends RuntimeException {
  private final HttpStatus status;
  private final String message;

  /**
   * <p>[概 要] リソースが見つからない例外。</p>
   * <p>[詳 細] 何かが見つからない場合の 404 エラーであるこの例外を処理します。</p>
   * <p>[備 考] なし。</p>
   *
   * @param status  状態,
   * @param message メッセージ,
   * @since 13.0
   */
  public UserAlreadyExistsException(HttpStatus status, String message) {
    super(message);
    this.status = status;
    this.message = message;
  }

}
