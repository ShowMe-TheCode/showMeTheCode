package com.sparta.showmethecode.question.repository;

import com.sparta.showmethecode.language.dto.response.ReviewRequestLanguageCount;
import com.sparta.showmethecode.question.domain.QuestionStatus;
import com.sparta.showmethecode.question.dto.response.QuestionResponseDto;
import com.sparta.showmethecode.question.dto.response.RequestAndAnswerResponseDto;
import com.sparta.showmethecode.question.dto.response.QuestionDetailsResponseDto;
import com.sparta.showmethecode.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestionQueryRepository {

    // 코드리뷰 목록 조회
    Page<QuestionResponseDto> findReviewRequestList(Pageable pageable, boolean isAsc, QuestionStatus status);
    // 코드리뷰 목록 조회 v2 더보기 버튼 방식 (no offset)
    List<QuestionResponseDto> findReviewRequestListV2(Long lastId, int limit, String keyword, String language, List<QuestionStatus> status);


    // 질문 검색
    Page<QuestionResponseDto> searchQuestionV1(String keyword, Pageable pageable, boolean isAsc, QuestionStatus status);


    // 코드리뷰요청 상세정보 조회
    QuestionDetailsResponseDto getReviewRequestDetails(Long id);
    // 언어별 코드리뷰요청 카운팅
    List<ReviewRequestLanguageCount> getReviewRequestLanguageCountGroupByLanguage();

    // 자신이 요청한 리뷰 조회 V2
    List<QuestionResponseDto> findMyQuestionV2(Long userId, Long lastId, int limit, List<QuestionStatus> status);
    // 자신에게 요청된 리뷰 조회 V2
    List<QuestionResponseDto> findReceivedQuestionV2(Long userId, Long lastId, int limit, List<QuestionStatus> status);

    // 내가 요청한 리뷰가 맞는지 체크
    boolean isMyReviewRequest(Long reviewId, User user);
    // 나에게 요청된 리뷰가 맞는지 체크
    boolean isRequestedToMe(Long reviewId, User reviewer);
    // 나에게 답변된 리뷰가 맞는지 체크
    boolean isAnswerToMe(Long answerId, User user);
    // 다음 페이지가 있는지 체크, 현재 pk보다 작은 값이 있는지 체크
    boolean isLastPage(Long lastId);

    // 언어이름으로 코드리뷰요청 조회
    Page<QuestionResponseDto> searchRequestByLanguageName(String languageName, Pageable pageable, boolean isAsc);

    // 현재 리뷰요청에 달린 댓글 삭제
    void deleteComment(Long reviewId, Long commentId, Long userId);

    // 리뷰 상세내용 조회 (댓글X)
    RequestAndAnswerResponseDto findReviewRequestAndAnswer(Long id);
}
