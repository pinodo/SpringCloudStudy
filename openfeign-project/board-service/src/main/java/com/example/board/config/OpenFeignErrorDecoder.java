package com.example.board.config;

import com.example.board.exception.UserNotFoundException;

import feign.Response;
import feign.codec.ErrorDecoder;

public class OpenFeignErrorDecoder implements ErrorDecoder {

  /**
   * Feign Client 요청 중 에러(200번대가 아닌 응답)가 발생했을 때 자동으로 호출되는 메서드입니다.
   * 
   * 상대방 서비스(User Service)가 보내온 HTTP 상태 코드(Status Code)를 분석하여,
   * 현재 서비스(Board Service)의 비즈니스 로직에 맞는 Java 예외(Exception)로 변환하는 역할을 합니다.
   * 
   * 예: 404 상태 코드를 받으면 {@link UserNotFoundException}을 생성하여 반환합니다.
   * 여기서 반환된 예외 객체는 Feign 프레임워크가 받아서, 최종적으로 메서드를 호출한 곳(Service)에 던져(throw) 줍니다.
   *
   * @param methodKey 호출된 Feign Client 인터페이스의 메서드 이름 (예: UserClient#getUser(Long))
   * - 어떤 메서드를 실행하다가 에러가 났는지 식별할 때 사용합니다.
   * @param response  서버로부터 받은 원본 HTTP 응답 객체
   * - 응답 상태 코드(status), 헤더, 본문(body) 등의 상세 정보가 포함되어 있습니다.
   * @return 호출자에게 던져질 구체적인 예외 객체 (Exception)
   */
  @Override
  public Exception decode(String methodKey, Response response) {
    // 원격 서버 응답 코드 가로채기 -> 404의 경우 커스텀 예외 전환 비즈니스 전이
    if (response.status() == 404) {
      return new UserNotFoundException("통신 실패: 해당 사용자를 찾을 수 없습니다.");
    }
    return new Exception("기타 Feign 네트워크 연동 일반 오류 발생");
  }
}