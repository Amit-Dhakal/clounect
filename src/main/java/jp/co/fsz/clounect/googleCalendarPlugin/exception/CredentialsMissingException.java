package jp.co.fsz.clounect.googleCalendarPlugin.exception;

import lombok.Getter;

/**
 * <p>[概要] 資格情報が不足している場合にスローされる例外クラス。</p>
 * <p>[詳細] アプリケーションで必要な資格情報が不足している場合にこの例外がスローされます。</p>
 * <p>[備考] この例外はランタイム例外であり、資格情報の不足に関する情報を保持します。</p>
 * <p>[環境] JDK17.0</p>
 * <p>著作権 FSZ 2024</p>
 *
 *  @author 著者FSZ
 * @since 1.0
 */
@Getter
public class CredentialsMissingException extends RuntimeException {
  private final String reason;

  /**
   * <p>[概要] 資格情報が不足している例外クラスのコンストラクタ。</p>
   * <p>[詳細] 資格情報が不足している例外を生成するコンストラクタ。</p>
   * <p>[備考] このコンストラクタは資格情報の不足に関する情報を受け取り、例外を生成します。</p>
   *
   * @param reason 資格情報が不足している理由を示す説明
   *@since 1.0
   */
  public CredentialsMissingException(String reason) {
    super(reason);
    this.reason = reason;
  }
}
