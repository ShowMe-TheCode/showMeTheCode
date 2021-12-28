package com.sparta.showmethecode.question.service;

import com.sparta.showmethecode.language.dto.response.ReviewRequestLanguageCount;
import com.sparta.showmethecode.notification.domain.MoveUriType;
import com.sparta.showmethecode.question.domain.Question;
import com.sparta.showmethecode.question.domain.QuestionStatus;
import com.sparta.showmethecode.question.dto.response.RequestAndAnswerResponseDto;
import com.sparta.showmethecode.question.dto.response.ReviewRequestDetailResponseDto;
import com.sparta.showmethecode.question.dto.response.ReviewRequestResponseDto;
import com.sparta.showmethecode.notification.service.NotificationService;
import com.sparta.showmethecode.user.domain.User;
import com.sparta.showmethecode.question.dto.request.ReviewRequestDto;
import com.sparta.showmethecode.question.dto.request.ReviewRequestUpdateDto;
import com.sparta.showmethecode.common.dto.response.*;
import com.sparta.showmethecode.comment.repository.CommentRepository;
import com.sparta.showmethecode.question.repository.QuestionRepository;
import com.sparta.showmethecode.user.dto.request.UpdateReviewerDto;
import com.sparta.showmethecode.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final CommentRepository reviewRequestCommentRepository;

    /**
     * 코드리뷰 요청목록 API
     */
    @Transactional(readOnly = true)
    public PageResponseDto getReviewRequestList(int page, int size, String sortBy, boolean isAsc, QuestionStatus status) {
        Pageable pageable = makePageable(page, size, sortBy, isAsc);

        Page<ReviewRequestResponseDto> reviewRequestList = questionRepository.findReviewRequestList(pageable, isAsc, status);

        return new PageResponseDto<ReviewRequestResponseDto>(
                reviewRequestList.getContent(),
                reviewRequestList.getTotalPages(),
                reviewRequestList.getTotalElements(),
                page, size
        );
    }

    /**
     * 코드리뷰 요청목록 API V2 (더보기 방식)
     */
    @Transactional(readOnly = true)
    public PageResponseDtoV2<ReviewRequestResponseDto> getReviewRequestListV2(Long lastId, int size, QuestionStatus status) {#127

        List<ReviewRequestResponseDto> reviewRequestList = questionRepository.findReviewRequestListV2(lastId, size, status);

        return new PageResponseDtoV2<ReviewRequestResponseDto>(reviewRequestList, reviewRequestList.get(reviewRequestList.size()-1).getReviewRequestId());
    }

    /**
     * 코드리뷰 검색 API
     */
    @Transactional(readOnly = true)
    public PageResponseDto<ReviewRequestResponseDto> searchByTitleOrComment(
            String keyword,
            int page, int size, String sortBy, boolean isAsc,
            QuestionStatus status
    ) {
        Pageable pageable = makePageable(page, size, sortBy, isAsc);
        Page<ReviewRequestResponseDto> results = questionRepository.findSearchByTitleOrCommentAdvanced(keyword, pageable, isAsc, status);

        return new PageResponseDto<ReviewRequestResponseDto>(
                results.getContent(),
                results.getTotalPages(), results.getTotalElements(), page, size
        );
    }

    /**
     * 코드리뷰 요청 API
     * SSE 이벤트 포함
     */
    @Transactional
    public void addReviewRequest(ReviewRequestDto requestDto, User user) {
        User reviewer = userRepository.findById(requestDto.getReviewerId()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 리뷰어입니다.")
        );

        Question question
                = new Question(user, reviewer, requestDto.getTitle(), requestDto.getContent(), QuestionStatus.UNSOLVE, requestDto.getLanguage().toUpperCase());

        questionRepository.save(question);

        notificationService
                .send(question.getAnswerUser(), question, "새로운 리뷰 요청이 도착했습니다!", MoveUriType.ANSWER);
    }

    /**
     * 코드리뷰 수정 API
     */
    @Transactional
    public void updateReviewRequest(ReviewRequestUpdateDto updateDto, Long reviewId, User user) {
        boolean isMyRequest = questionRepository.isMyReviewRequest(reviewId, user);
        if (isMyRequest) {
            Question question = questionRepository.findById(reviewId).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않는 요청입니다.")
            );

            question.update(updateDto);
        }
    }

    /**
     * 코드리뷰 삭제 API
     */
    @Transactional
    public void deleteReviewRequest(Long reviewId, User user){
        boolean isMyRequest = questionRepository.isMyReviewRequest(reviewId, user);
        if (isMyRequest) {
            questionRepository.deleteById(reviewId);
        }
    }

    /**
     * 코드리뷰 단건조회 API (코드리뷰 요청 상세정보)
     */
    @Transactional(readOnly = true)
    public ReviewRequestDetailResponseDto getReviewRequest(Long id) {
        ReviewRequestDetailResponseDto result = questionRepository.getReviewRequestDetails(id);
        return result;
    }

    /**
     * 코드리뷰 요청 언어별 카운팅 API
     */
    @Transactional(readOnly = true)
    public List<ReviewRequestLanguageCount> getCountGroupByLanguageName() {
        return questionRepository.getReviewRequestLanguageCountGroupByLanguage();
    }

    /**
     * 코드리뷰 요청 언어이름 검색 API
     */
    public PageResponseDto<ReviewRequestResponseDto> searchRequestByLanguageName(String language, int page, int size, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        language = language.toUpperCase();

        Page<ReviewRequestResponseDto> reviewRequests = questionRepository.searchRequestByLanguageName(language, pageable, isAsc);

        return new PageResponseDto<ReviewRequestResponseDto>(
                reviewRequests.getContent(),
                reviewRequests.getTotalPages(),
                reviewRequests.getTotalElements(),
                page, size
        );
    }

    /**
     * 답변을 위한 상세정보 조회 (댓글 X, 답변 O)
     */
    public RequestAndAnswerResponseDto getReviewRequestWithAnswer(Long id) {
        RequestAndAnswerResponseDto reviewRequestAndAnswer = questionRepository.findReviewRequestAndAnswer(id);
        return reviewRequestAndAnswer;
    }

    /**
     * 리뷰어 변경하기 API
     */
    @Transactional
    public void changeReviewer(UpdateReviewerDto changeReviewerDto, Long questionId, Long reviewerId) {
        Question question = questionRepository.findById(questionId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 리뷰요청입니다.")
        );

        Long newReviewerId = changeReviewerDto.getNewReviewerId();
        User newReviewer = userRepository.findById(newReviewerId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 리뷰어입니다.")
        );

        question.updateReviewer(newReviewer);

    }

    private Pageable makePageable(int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);

        return PageRequest.of(page, size, sort);
    }


}
