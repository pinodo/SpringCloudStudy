package com.example.board.service;

import org.springframework.stereotype.Service;

import com.example.board.client.UserClient;
import com.example.board.dto.BoardRequest;
import com.example.board.dto.BoardResponse;
import com.example.common.dto.UserResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

  private final UserClient userClient;

  public BoardResponse createBoard(BoardRequest request) {
    // 1. FeignClient를 통해 User Service 호출 (동기 방식)
    // 사용자가 없으면 ErrorDecoder에 의해 UserNotFoundException 발생
    UserResponse user = userClient.getUser(request.userId());
    
    // 2. 사용자 검증 완료 후 게시글 저장 로직 (여기서는 생략하고 바로 응답 생성)
    // 실제로는 DB에 save(board) 하는 로직이 들어감
    Long generatedBoardId = 100L; 

    // 3. 작성자 이름(user.name())을 포함하여 응답 반환
    return new BoardResponse(
      generatedBoardId,
      request.title(),
      request.content(),
      user.name() // user-service에서 받아온 데이터 활용
    );
  }
}