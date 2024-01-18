package jp.co.fsz.clounect.googleCalendarPlugin.exception;

/**
 * <p>[概要] 資格情報が不足している場合にスローされる例外クラス。</p>
 * <p>[詳細] アプリケーションで必要な資格情報が不足している場合にこの例外がスローされます。</p>
 * <p>[備考] この例外はランタイム例外であり、資格情報の不足に関する情報を保持します。</p>
 * <p>[環境] JDK17.0</p>
 * <p>著作権 FSZ 2024</p>
 *
 * <p>このクラスは、資格情報が必要であり、それが提供されなかった場合にスローされる例外を表します。</p>
 * <p>例外には、資格情報の不足に関する詳細な情報が含まれています。</p>
 *
 * @author 著者FSZ
 * @since 1.0
 */
public class CouldNotPerformActionException extends RuntimeException {


  /**
   * <p>[概要] アクションの実行ができなかった場合にスローされる例外クラスのコンストラクタ。</p>
   * <p>[詳細] アクションの実行ができなかった場合に発生させる例外を生成するコンストラクタ。</p>
   * <p>[備考] このコンストラクタは、アクションの実行ができない状況に関する詳細な情報を受け取り、例外を生成します。</p>
   *
   * @param message アクションの実行ができない理由を示す説明
   * @since 1.0
   */
  public CouldNotPerformActionException(String message) {
    super(message);
  }

  /**
   * <p>[概要] アクションの実行ができなかった場合にスローされる例外クラスのコンストラクタ。</p>
   * <p>[詳細] アクションの実行ができなかった場合に発生させる例外を生成するコンストラクタ。</p>
   * <p>[備考] このコンストラクタは、アクションの実行ができない状況に関する詳細な情報を受け取り、例外を生成します。</p>
   *
   * @param message アクションの実行ができない理由を示す説明
   * @param cause   アクションの実行ができない状況に関する詳細な情報を保持する例外
   * @since 1.0
   */
  public CouldNotPerformActionException(String message, Throwable cause) {
    super(message, cause);
  }
}
