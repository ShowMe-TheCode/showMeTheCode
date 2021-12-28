package com.sparta.showmethecode.comment.controller;

import com.sparta.showmethecode.user.domain.User;
import com.sparta.showmethecode.comment.dto.request.AddCommentDto;
import com.sparta.showmethecode.comment.dto.request.UpdateCommentDto;
import com.sparta.showmethecode.security.UserDetailsImpl;
import com.sparta.showmethecode.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/comments")
@RequiredArgsConstructor
@RestController
@Slf4j
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글추가 API
     */
    @PostMapping("/{questionId}")
    public ResponseEntity addComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long questionId,
            @RequestBody AddCommentDto addCommentDto
    ) {
        User user = userDetails.getUser();
        commentService.addComment(user, questionId, addCommentDto);

        return ResponseEntity.ok().body("댓글작성 완료");
    }

    /**
     * 댓글삭제 API
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity removeComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long commentId
    ) {
        User user = userDetails.getUser();
        long row = commentService.removeComment(user, commentId);

        return ResponseEntity.ok().body("댓글삭제 완료");
    }

    /**
     * 댓글수정 API
     */
    @PutMapping("/{commentId}")
    public ResponseEntity updateComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long commentId,
            @RequestBody UpdateCommentDto updateCommentDto
    ) {
        User user = userDetails.getUser();
        commentService.updateComment(user, commentId, updateCommentDto);

        return ResponseEntity.ok().body("댓글수정 완료");
    }
}
