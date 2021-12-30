package com.sparta.showmethecode.user.controller;

import com.sparta.showmethecode.answer.dto.response.AnswerResponseDto;
import com.sparta.showmethecode.common.dto.response.PageResponseDto;
import com.sparta.showmethecode.common.dto.response.PageResponseDtoV2;
import com.sparta.showmethecode.question.domain.QuestionStatus;
import com.sparta.showmethecode.security.UserDetailsImpl;
import com.sparta.showmethecode.user.domain.User;
import com.sparta.showmethecode.user.dto.response.ReviewerInfoDto;
import com.sparta.showmethecode.user.service.ReviewerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/reviewers")
@RequiredArgsConstructor
@Slf4j
@RestController
public class ReviewerController {

    private final ReviewerService reviewerService;

    /**
     * 리뷰어 랭킹 조회 API (전체랭킹 조회)
     */
    @GetMapping("/rank")
    public ResponseEntity getReviewerRanking(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "true") boolean isAsc
    ) {
        --page;

        PageResponseDto<ReviewerInfoDto> reviewerRanking = reviewerService.getReviewerRanking(page, size, isAsc);

        return ResponseEntity.ok(reviewerRanking);
    }

    /**
     * 리뷰어 랭킹 조회 API (상위 5위)
     */
    @GetMapping("/top")
    public ResponseEntity getReviewerTop5Ranking() {
        List<ReviewerInfoDto> reviewers = reviewerService.getReviewerTop5Ranking();

        log.info("리뷰어 랭킹 5위 = {}", reviewers);

        return ResponseEntity.ok(reviewers);
    }


    /**
     * 내가 답변한 리뷰목록 조회 API
     */
    @GetMapping("/answers")
    public ResponseEntity getMyAnswerList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "true") boolean isAsc,
            @RequestParam(defaultValue = "createdAt") String sortBy
    ) {
        --page;

        User user = userDetails.getUser();
        PageResponseDto<AnswerResponseDto> result = reviewerService.getMyAnswerList(user, page, size, isAsc, sortBy);

        return ResponseEntity.ok(result);
    }

    /**
     * 나에게 요청된 리뷰목록 조회
     */
    @GetMapping("/questions")
    public ResponseEntity<PageResponseDtoV2> getMyReceivedList(
            @RequestParam QuestionStatus status,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) Long lastId, @RequestParam(defaultValue = "10") int size
    ) {
        User user = userDetails.getUser();
        PageResponseDtoV2 response = reviewerService.getMyReceivedRequestList(user, lastId, size, status);

        return ResponseEntity.ok(response);
    }

    /**
     * 언어 이름으로 리뷰어 조회 API
     */
    @GetMapping("/language")
    public ResponseEntity findReviewerByLanguage(@RequestParam String language) {
        List<ReviewerInfoDto> reviewerInfoList = reviewerService.findReviewerByLanguage(language);
        return ResponseEntity.ok().body(reviewerInfoList);
    }
}
