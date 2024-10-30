## Exception Handling

> Service layer의 exception handling은 [`GlobalExceptionHandler`](src/main/java/com/server/global/error/handler/GlobalExceptionHandler.java)를 통해 관리

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleFriendException(AuthException e) {
        return ResponseEntity.status(e.getStatus()).body(ApiResponseDto.error(e.getStatus(), e.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleFriendException(BusinessException e) {
        return ResponseEntity.status(e.getStatus()).body(ApiResponseDto.error(e.getStatus(), e.getMessage()));
    }

}
```

> [`ErrorCode`](src/main/java/com/server/global/error/code) 및 [`Exception`](src/main/java/com/server/global/error/exception) 정의 후 아래 예시와 같이 service layer에서 예외 생성

```java
throw new AuthException(AuthErrorCode.OAUTH_PROCESS_ERROR);
throw new BusinessException(FriendErrorCode.RECEIPT_ALREADY_EXISTS);
```
