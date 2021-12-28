package com.sparta.showmethecode.answer.controller;

import com.sparta.showmethecode.answer.dto.request.AddAnswerDto;
import com.sparta.showmethecode.answer.dto.request.EvaluateAnswerDto;
import com.sparta.showmethecode.answer.dto.request.UpdateAnswerDto;
import com.sparta.showmethecode.answer.service.AnswerService;
import com.sparta.showmethecode.security.UserDetailsImpl;
import com.sparta.showmethecode.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/answers")
@RestController
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    /**
     * 답변 API
     */
    @Secured("ROLE_REVIEWER")
    @PostMapping("/{questionId}")
    public ResponseEntity addReviewAndComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long questionId,
            @RequestBody AddAnswerDto addAnswerDto
    ) {
        User reviewer = userDetails.getUser();
        answerService.addAnswer(reviewer.getId(), questionId, addAnswerDto);
        return ResponseEntity.ok("ok");
    }

    /**
     * 답변수정 API
     */
    @PutMapping("/{answerId}")
    public ResponseEntity updateMyAnswer(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long answerId,
            @RequestBody UpdateAnswerDto updateAnswerDto
    ) {
        User reviewer = userDetails.getUser();

        answerService.updateAnswer(reviewer, answerId, updateAnswerDto);

        return ResponseEntity.ok("ok");
    }

    /**
     * 요청거절
     */
    @PostMapping("/{questionId}/reject")
    public ResponseEntity rejectRequestedReview(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long questionId
    ) {
        User user = userDetails.getUser();
        answerService.rejectRequestedReview(user, questionId);

        return ResponseEntity.ok("ok");
    }

    /**
     * 답변에 대한 평가 API
     */
    @Secured({"ROLE_USER", "ROLE_REVIEWER"})
    @PostMapping("/{questionId}/eval/{answerId}")
    public ResponseEntity evaluateAnswer(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long questionId, @PathVariable Long answerId,
            @RequestBody EvaluateAnswerDto evaluateAnswerDto
    ) {
        User user = userDetails.getUser();
        answerService.evaluateAnswer(user, questionId, answerId, evaluateAnswerDto);

        return ResponseEntity.ok("ok");
    }
}
