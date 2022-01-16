package com.sparta.showmethecode.user.service;

import com.sparta.showmethecode.answer.dto.response.AnswerResponseDto;
import com.sparta.showmethecode.common.dto.response.*;
import com.sparta.showmethecode.answer.repository.AnswerRepository;
import com.sparta.showmethecode.question.domain.QuestionStatus;
import com.sparta.showmethecode.question.dto.response.QuestionResponseDto;
import com.sparta.showmethecode.question.repository.QuestionRepository;
import com.sparta.showmethecode.user.repository.UserRepository;
import com.sparta.showmethecode.user.domain.User;
import com.sparta.showmethecode.user.dto.response.ReviewerInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewerService {

    private final AnswerRepository reviewAnswerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    /**
     * 나에게 요청된 리뷰인지 확인
     */
    private boolean isRequestedToMe(Long questionId, User reviewer) {
        return questionRepository.isRequestedToMe(questionId, reviewer);
    }

    /**
     * 내가 답변한 리뷰목록 조회 API
     */
    @Transactional(readOnly = true)
    public PageResponseDto<AnswerResponseDto> getMyAnswerList(User reviewer, int page, int size, boolean isAsc, String sortBy) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AnswerResponseDto> myAnswer = reviewAnswerRepository.findMyAnswer(reviewer.getId(), pageable);

        return new PageResponseDto<AnswerResponseDto>(
                myAnswer.getContent(),
                myAnswer.getTotalPages(),
                myAnswer.getTotalElements(),
                page, size
        );
    }


    /**
     * 나에게 요청된 리뷰 조회
     */
    public PageResponseDtoV2 getMyReceivedRequestList(User user, Long lastId, int limit, List<QuestionStatus> status) {
        List<QuestionResponseDto> result = questionRepository.findReceivedQuestionV2(user.getId(), lastId, limit, status);
        Long currentLastId = result.get(result.size() - 1).getQuestionId();
        boolean lastPage = questionRepository.isLastPage(currentLastId);

        return new PageResponseDtoV2<QuestionResponseDto>(result, currentLastId, lastPage);
    }

    /**
     * 언어이름으로 리뷰어 조회 API
     */
    public List<ReviewerInfoDto> findReviewerByLanguage(String languageName) {
        List<User> reviewers = userRepository.findReviewerByLanguage(languageName.toUpperCase());
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        return reviewers.stream().map(
                r -> new ReviewerInfoDto(
                        r.getId(),
                        r.getUsername(),
                        r.getNickname(),
                        r.getLanguages().stream().map(l -> new String(l.getName())).collect(Collectors.toList()),
                        r.getRanking().getAnswerCount(),
                        r.getRanking().getAverage())
        ).collect(Collectors.toList());
    }


    private Pageable makePageable(int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);

        return PageRequest.of(page, size, sort);
    }
}
