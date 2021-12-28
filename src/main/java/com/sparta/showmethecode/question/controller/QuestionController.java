package com.sparta.showmethecode.question.controller;

import com.sparta.showmethecode.common.dto.response.PageResponseDto;
import com.sparta.showmethecode.language.dto.response.ReviewRequestLanguageCount;
import com.sparta.showmethecode.question.domain.QuestionStatus;
import com.sparta.showmethecode.question.dto.request.ReviewRequestDto;
import com.sparta.showmethecode.question.dto.request.ReviewRequestUpdateDto;
import com.sparta.showmethecode.question.dto.response.RequestAndAnswerResponseDto;
import com.sparta.showmethecode.question.dto.response.ReviewRequestDetailResponseDto;
import com.sparta.showmethecode.question.dto.response.ReviewRequestResponseDto;
import com.sparta.showmethecode.question.service.QuestionService;
import com.sparta.showmethecode.security.UserDetailsImpl;
import com.sparta.showmethecode.user.domain.User;
import com.sparta.showmethecode.user.dto.request.UpdateReviewerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RequestMapping("/questions")
@Slf4j
@RequiredArgsConstructor
@RestController
public class QuestionController {

    private final QuestionService reviewRequestService;

    /**
     * 요청목록
     */
    @GetMapping
    public ResponseEntity<PageResponseDto> getReviewRequestList(
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "true") Boolean isAsc,
            @RequestParam(required = false) String query, @RequestParam(required = false, defaultValue = "ALL") QuestionStatus status
    ) {
        --page;

        if (!Objects.isNull(query)) {
            return ResponseEntity.ok(reviewRequestService.searchByTitleOrComment(query, page, size, sortBy, isAsc, status));
        }

        PageResponseDto result = reviewRequestService.getReviewRequestList(page, size, sortBy, isAsc, status);

        return ResponseEntity.ok(result);
    }

    /**
     * 질문 요청 API
     * SSE 이벤트 포함
     */
    @PostMapping
    public ResponseEntity<String> addReviewRequest(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody ReviewRequestDto requestDto) {

        User user = userDetails.getUser();
        reviewRequestService.addReviewRequest(requestDto, user);

        return ResponseEntity.ok("ok");
    }

    /**
     * 질문 상세조회 API (코드리뷰 요청 상세정보)
     */
    @GetMapping("/{questionId}}")
    public ResponseEntity<ReviewRequestDetailResponseDto> getReviewRequest(@PathVariable Long questionId) {
        ReviewRequestDetailResponseDto reviewRequest = reviewRequestService.getReviewRequest(questionId);

        return ResponseEntity.ok(reviewRequest);
    }

    /**
     * 질문 수정 API
     */
    @PutMapping("/{questionId}")
    public ResponseEntity updateReviewRequest(
            @RequestBody ReviewRequestUpdateDto updateDto,
            @PathVariable Long questionId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        User user = userDetails.getUser();
        reviewRequestService.updateReviewRequest(updateDto, questionId, user);

        return ResponseEntity.ok("ok");
    }

    /**
     * 질문 삭제 API
     */
    @DeleteMapping("/{questionId}")
    public ResponseEntity deleteReviewRequest(
            @PathVariable Long questionId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        reviewRequestService.deleteReviewRequest(questionId, user);
        return ResponseEntity.ok("ok");
    }

    /**
     * 질문요청 언어별 카운팅 API
     */
    @GetMapping("/languages/count")
    public List<ReviewRequestLanguageCount> getCountGroupByLanguageName() {
        return reviewRequestService.getCountGroupByLanguageName();
    }

    /**
     * 질문 언어이름 검색 API
     */
    @GetMapping("/language")
    public ResponseEntity searchRequestByLanguageName(
            @RequestParam String language,
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "true") boolean isAsc
    ) {
        --page;
        PageResponseDto<ReviewRequestResponseDto> result = reviewRequestService.searchRequestByLanguageName(language, page, size, isAsc);

        return ResponseEntity.ok(result);
    }

    /**
     * 답변을 위한 질문 상세정보 조회 (댓글 X, 답변 O)
     */
    @GetMapping("/{id}/answer")
    public ResponseEntity getReviewRequestDetailsWithoutComments(
            @PathVariable Long id
    ) {
        RequestAndAnswerResponseDto response = reviewRequestService.getReviewRequestWithAnswer(id);

        return ResponseEntity.ok(response);
    }

    /**
     * 리뷰어 변경하기 API
     */
    @PutMapping("/{questionId}/reviewer/{reviewerId}")
    public ResponseEntity changeReviewer(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody UpdateReviewerDto changeReviewerDto,
            @PathVariable Long questionId, @PathVariable Long currentReviewerId
    ) {
        reviewRequestService.changeReviewer(changeReviewerDto, questionId, currentReviewerId);

        return ResponseEntity.ok("success");
    }
}
