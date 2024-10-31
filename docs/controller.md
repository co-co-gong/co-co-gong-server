## Controller 규약

```java
@ResponseStatus(HttpStatus.OK)                                      // 정상적 응답에 대한 HTTP status 작성
@GetMapping("/${URI}")                                              // API 접근 URI 명시
@Operation(summary = "${SUMMARY}", description = "${DESCRIPTION}")  // Swagger 내 문서화를 위한 설명 작성
// Controller의 모든 응답에 ApiResponseDto class 사용
// Generic을 통해 응답의 data type 명시
public ApiResponseDto<T> methodName(
        HttpServletRequest request,                                 // 직접적인 요청 정보가 필요할 때 사용
        @RequestParam String param1,                                // 쿼리 스트링 또는 폼 데이터의 요청 파라미터
        @PathVariable Long param2,                                  // URL 경로의 변수 매핑
        @RequestBody Dto param3,                                    // 요청 본문(JSON 등)을 객체로 받음
        HttpServletResponse response                                // 직접적인 응답 설정이 필요할 때 사용
) {
    // Request header 사용 예시
    String authHeader = request.getHeader("Authorization");

    // 결과를 위한 business logic 실행
    T data = domainService.businessLogic();

    // Response header 추가 예시
    response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

    // 정상적인 응답 시 사용할 HTTP status 및 응답 data
    return ApiResponseDto.success(HttpStatus.OK.value(), data);
}
```
