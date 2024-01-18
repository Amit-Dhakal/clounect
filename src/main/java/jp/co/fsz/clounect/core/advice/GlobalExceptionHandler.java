package jp.co.fsz.clounect.core.advice;

import jakarta.servlet.http.HttpServletResponse;
import jp.co.fsz.clounect.core.dto.ErrorDetails;
import jp.co.fsz.clounect.core.exception.ResourceNotFoundException;
import jp.co.fsz.clounect.core.exception.UserAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotFoundException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UsernameExistsException;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public String handleAccessDeniedException(AccessDeniedException e, Model model,
      HttpServletResponse response) {
    log.error("Access Denied Exception", e);
    model.addAttribute("status", HttpStatus.FORBIDDEN.value());
    model.addAttribute("errorMessage", "ACCESS NOT GRANTED");
    return "core/error";
  }

  @ExceptionHandler({ NoHandlerFoundException.class, NoResourceFoundException.class })
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public String handleNotFoundException(Exception e, Model model,
      HttpServletResponse response) {
    log.error("Not Found Exception", e);
    model.addAttribute("status", HttpStatus.NOT_FOUND.value());
    model.addAttribute("errorMessage", "PAGE NOT FOUND");
    return "core/error";
  }

  @ExceptionHandler({ UsernameExistsException.class, UserNotFoundException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public String handleException(Exception e, Model model, HttpServletResponse response) {
    log.error("Exception occurred", e);
    model.addAttribute("status", HttpStatus.BAD_REQUEST.value());
    model.addAttribute("errorMessage", e.getMessage());
    return "core/error";
  }


  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String handleGeneralException(Exception e, Model model,
      HttpServletResponse response) {
    log.error("Internal Server Error", e);
    model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    model.addAttribute("errorMessage", "INTERNAL SERVER ERROR");
    return "core/error";
  }

  /**
   * <p>[概 要] リソースが見つからない例外。</p>
   * <p>[詳 細] 何かが見つからない場合の 404 エラーであるこの例外を処理します。</p>
   * <p>[備 考] なし。</p>
   *
   * @param exception  リソースが見つかりません,
   * @param webRequest webリクエスト,
   * @return それぞれの HttpStatus を含む新しい ErrorDetails
   * @since 17.0
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorDetails> handleResourceNotFoundException(
      ResourceNotFoundException exception, WebRequest webRequest) {
    ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), exception.getMessage(),
        webRequest.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
  }

  /**
   * <p>[概 要] ユーザーはすでに存在します例外。</p>
   * <p>[詳 細] ユーザーがすでにデータベースに存在する場合にこの例外を処理します。</p>
   * <p>[備 考] なし。</p>
   *
   * @param exception  このユーザーは既に存在します,
   * @param webRequest webリクエスト,
   * @return それぞれの HttpStatus を含む新しい ErrorDetails
   * @since 17.0
   */
  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<ErrorDetails> handleUserAlreadyExistsException(
      UserAlreadyExistsException exception, WebRequest webRequest) {
    ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), exception.getMessage(),
        webRequest.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }
}

