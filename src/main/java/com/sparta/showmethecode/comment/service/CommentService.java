package com.sparta.showmethecode.comment.service;

import com.sparta.showmethecode.comment.domain.Comment;
import com.sparta.showmethecode.comment.dto.request.AddCommentDto;
import com.sparta.showmethecode.comment.dto.request.UpdateCommentDto;
import com.sparta.showmethecode.answer.repository.AnswerRepository;
import com.sparta.showmethecode.comment.repository.CommentRepository;
import com.sparta.showmethecode.question.repository.QuestionRepository;
import com.sparta.showmethecode.question.domain.Question;
import com.sparta.showmethecode.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class CommentService {

    private final QuestionRepository questionRepository;
    private final CommentRepository reviewRequestCommentRepository;

    private final AnswerRepository reviewAnswerRepository;

    /**
     * 댓글추가 API
     */
    @Transactional
    public void addComment(User user, Long questionId, AddCommentDto addCommentDto) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰요청입니다."));
        Comment comment = new Comment(addCommentDto.getContent(), user);
        question.addComment(comment);
    }

    /**
     * 댓글삭제 API
     */
    @Transactional
    public long removeComment(User user, Long commentId) {
        return reviewRequestCommentRepository.deleteComment(user.getId(), commentId);
    }

    /**
     * 댓글수정 API
     */
    @Transactional
    public void updateComment(User user, Long commentId, UpdateCommentDto updateCommentDto) {
        Comment comment = reviewRequestCommentRepository.findByIdAndUser(commentId, user);
        comment.update(updateCommentDto);
    }


}
