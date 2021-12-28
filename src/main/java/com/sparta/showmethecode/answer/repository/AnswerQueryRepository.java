package com.sparta.showmethecode.answer.repository;

import com.sparta.showmethecode.answer.dto.response.ReviewAnswerResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AnswerQueryRepository {

    // 현재 답변에 평가가 됐는지 조회
    boolean isEvaluated(Long answerId);
    // 현재 사용자의 답변인지 확인
    boolean isMyAnswer(Long reviewerId, Long answerId);
    // 내가 답변한 리뷰목록 조회
    Page<ReviewAnswerResponseDto> findMyAnswer(Long userId, Pageable pageable);
}
