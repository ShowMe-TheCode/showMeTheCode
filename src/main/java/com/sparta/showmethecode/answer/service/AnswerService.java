package com.sparta.showmethecode.answer.service;

import com.sparta.showmethecode.answer.domain.Answer;
import com.sparta.showmethecode.answer.dto.request.AddAnswerDto;
import com.sparta.showmethecode.answer.dto.request.EvaluateAnswerDto;
import com.sparta.showmethecode.answer.dto.request.UpdateAnswerDto;
import com.sparta.showmethecode.answer.repository.AnswerRepository;
import com.sparta.showmethecode.notification.domain.MoveUriType;
import com.sparta.showmethecode.notification.service.NotificationService;
import com.sparta.showmethecode.question.domain.Question;
import com.sparta.showmethecode.question.domain.QuestionStatus;
import com.sparta.showmethecode.question.repository.QuestionRepository;
import com.sparta.showmethecode.user.domain.User;
import com.sparta.showmethecode.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AnswerService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository reviewAnswerRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    /**
     * 리뷰요청에 대한 답변등록 API
     * 자신에게 요청된 리뷰가 아닌 경우에 대한 처리 필요
     */
    @Transactional
    public void addAnswer(Long reviewerId, Long reviewId, AddAnswerDto addAnswerDto) {
        User reviewer = userRepository.findById(reviewerId).get();
        if (isRequestedToMe(reviewId, reviewer)) {
            Answer answer = Answer.builder()
                    .content(addAnswerDto.getContent())
                    .answerUser(reviewer)
                    .build();
            Answer savedAnswer = reviewAnswerRepository.save(answer);

            Question question = questionRepository.findById(reviewId).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않는 리뷰요청입니다.")
            );

            reviewer.increaseAnswerCount();
            question.setStatus(QuestionStatus.SOLVE);
            question.setAnswer(savedAnswer);

            notificationService
                    .send(question.getRequestUser(), question, "리뷰 등록이 완료되었습니다.", MoveUriType.DETAILS);
        }
    }

    /**
     * 리뷰요청 거절 API
     * 자신에게 요청된 리뷰가 아닌 경우에 대한 처리 필요
     */
    @Transactional
    public void rejectRequestedReview(User reviewer, Long questionId) {
        if (isRequestedToMe(questionId, reviewer)) {
            Question question = questionRepository.findById(questionId).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않는 리뷰요청입니다.")
            );

            // SOLVE(해결됨)이 아닌 경우에만 거절이 가능하도록
            QuestionStatus status = question.getStatus();
            if (!status.equals(QuestionStatus.SOLVE) && !status.equals(QuestionStatus.EVALUATED)) {
                question.setStatus(QuestionStatus.REJECTED);
                notificationService.send(question.getRequestUser(), question, "리뷰 요청이 거절되었습니다.", MoveUriType.DETAILS);
            } else {
                throw new IllegalArgumentException("해결되지 않은 리뷰요청에 대해서만 거절이 가능합니다.");
            }
        }
    }

    /**
     * 답변한 리뷰 수정 API
     */
    @Transactional
    public void updateAnswer(User reviewer, Long answerId, UpdateAnswerDto updateAnswerDto) {
        if (isMyAnswer(reviewer.getId(), answerId)) {
            Answer answer = reviewAnswerRepository.findById(answerId).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않는 답변입니다.")
            );

            answer.update(updateAnswerDto);
        }
    }

    /**
     * 답변에 대한 평가 API
     *
     * 평가하고자 하는 답변이 내가 요청한 코드리뷰에 대한 답변인지 확인해야 함
     */
    @Transactional
    public void evaluateAnswer(User user, Long questionId, Long answerId, EvaluateAnswerDto evaluateAnswerDto) {
        Answer answer = reviewAnswerRepository.findById(answerId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 답변입니다.")
        );

        Question question = questionRepository.findById(questionId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 리뷰요청입니다.")
        );

        question.setStatus(QuestionStatus.EVALUATED);

        answer.evaluate(evaluateAnswerDto.getPoint());
        answer.getAnswerUser().evaluate(evaluateAnswerDto.getPoint());
    }

    private boolean isRequestedToMe(Long questionId, User reviewer) {
        return questionRepository.isRequestedToMe(questionId, reviewer);
    }

    private boolean isMyAnswer(Long reviewerId, Long answerId) {
        return reviewAnswerRepository.isMyAnswer(reviewerId, answerId);
    }
}
