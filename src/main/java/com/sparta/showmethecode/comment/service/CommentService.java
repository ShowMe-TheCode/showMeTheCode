package com.sparta.showmethecode.comment.service;

import com.sparta.showmethecode.comment.domain.ReviewRequestComment;
import com.sparta.showmethecode.comment.dto.request.AddCommentDto;
import com.sparta.showmethecode.comment.dto.request.UpdateCommentDto;
import com.sparta.showmethecode.answer.repository.AnswerRepository;
import com.sparta.showmethecode.comment.repository.ReviewRequestCommentRepository;
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
    private final ReviewRequestCommentRepository reviewRequestCommentRepository;

    private final AnswerRepository reviewAnswerRepository;

    /**
     * 코드리뷰요청 - 댓글추가 API
     */
    @Transactional
    public void addComment_Question(User user, Long questionId, AddCommentDto addCommentDto) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰요청입니다."));
        ReviewRequestComment reviewRequestComment = new ReviewRequestComment(addCommentDto.getContent(), user);
        question.addComment(reviewRequestComment);
    }

    /**
     * 코드리뷰요청 - 댓글삭제 API
     */
    @Transactional
    public long removeComment_Question(User user, Long commentId) {
        return reviewRequestCommentRepository.deleteComment(user.getId(), commentId);
    }

    /**
     * 코드리뷰요청 - 댓글수정 API
     */
    @Transactional
    public void updateComment_Question(User user, Long commentId, UpdateCommentDto updateCommentDto) {
        ReviewRequestComment reviewRequestComment = reviewRequestCommentRepository.findByIdAndUser(commentId, user);
        reviewRequestComment.update(updateCommentDto);
    }

//    /**
//     * 리뷰답변 - 댓글추가 API
//     */
//    public void addComment_Answer(User user, Long answerId, AddCommentDto addCommentDto) {
//        ReviewAnswer reviewAnswer = reviewAnswerRepository.findById(answerId).orElseThrow(
//                () -> new IllegalArgumentException("존재하지 않는 답변입니다.")
//        );
//        ReviewAnswerComment reviewAnswerComment = new ReviewAnswerComment(addCommentDto.getContent(), user, reviewAnswer);
//        reviewAnswer.addComment(reviewAnswerComment);
//    }
//
//    /**
//     * 리뷰답변 - 댓글수정 API
//     */
//    public void updateComment_Answer(User user, Long commentId, UpdateCommentDto updateCommentDto) {
//        ReviewAnswerComment reviewAnswerComment = reviewAnswerCommentRepository.findByIdAndUser(commentId, user);
//        reviewAnswerComment.update(updateCommentDto);
//    }
//
//    /**
//     * 리뷰답변 - 댓글삭제 API
//     */
//    public void removeComment_Answer(User user, Long commentId) {
//        reviewAnswerCommentRepository.deleteByUserAndId(user, commentId);
//    }
}
