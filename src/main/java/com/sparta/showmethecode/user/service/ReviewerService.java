package com.sparta.showmethecode.user.service;

import com.sparta.showmethecode.common.dto.response.*;
import com.sparta.showmethecode.answer.repository.AnswerRepository;
import com.sparta.showmethecode.answer.dto.response.ReviewAnswerResponseDto;
import com.sparta.showmethecode.question.domain.QuestionStatus;
import com.sparta.showmethecode.question.repository.QuestionRepository;
import com.sparta.showmethecode.question.dto.response.ReviewRequestResponseDto;
import com.sparta.showmethecode.notification.service.NotificationService;
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
     * 리뷰어 랭킹 조회 API (전체랭킹 조회)
     */
    @Transactional(readOnly = true)
    public PageResponseDto<ReviewerInfoDto> getReviewerRanking(
            int page, int size, boolean isAsc
    ) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "evalTotal");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> result = userRepository.getReviewerRanking(pageable, isAsc);

        List<ReviewerInfoDto> reviewerInfo = result.getContent().stream().map(
                u -> new ReviewerInfoDto(
                        u.getId(),
                        u.getUsername(),
                        u.getNickname(),
                        u.getLanguages().stream().map(l -> new String(l.getName())).collect(Collectors.toList()),
                        u.getAnswerCount(),
                        u.getEvalTotal() / u.getEvalCount()
                )
        ).collect(Collectors.toList());

        return new PageResponseDto<ReviewerInfoDto>(
                reviewerInfo,
                result.getTotalPages(),
                result.getTotalElements(),
                page, size
        );
    }

    /**
     * 리뷰어 랭킹 조회 API (상위 5명)
     */
    @Transactional(readOnly = true)
    public List<ReviewerInfoDto> getReviewerTop5Ranking(boolean isDesc) {
        return userRepository.getReviewerRankingTop5(isDesc).stream().map(
                u -> new ReviewerInfoDto(
                        u.getId(),
                        u.getUsername(),
                        u.getNickname(),
                        u.getLanguages().stream().map(
                                l -> new String(l.getName())
                        ).collect(Collectors.toList()),
                        u.getAnswerCount(),
                        u.getEvalCount() > 0 ? u.getEvalTotal() / u.getEvalCount() : 0
                )
        ).collect(Collectors.toList());
    }

    /**
     * 내가 답변한 리뷰목록 조회 API
     */
    @Transactional(readOnly = true)
    public PageResponseDto<ReviewAnswerResponseDto> getMyAnswerList(User reviewer, int page, int size, boolean isAsc, String sortBy) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ReviewAnswerResponseDto> myAnswer = reviewAnswerRepository.findMyAnswer(reviewer.getId(), pageable);

        return new PageResponseDto<ReviewAnswerResponseDto>(
                myAnswer.getContent(),
                myAnswer.getTotalPages(),
                myAnswer.getTotalElements(),
                page, size
        );
    }


    /**
     * 나에게 요청된 리뷰 조회
     */
    public PageResponseDto getMyReceivedRequestList(User user, int page, int size, String sortBy, boolean isAsc, QuestionStatus status) {
        Pageable pageable = makePageable(page, size, sortBy, isAsc);
        Page<ReviewRequestResponseDto> reviewRequests = questionRepository.findMyReceivedRequestList(user.getId(), pageable, status);

        return new PageResponseDto<ReviewRequestResponseDto>(
                reviewRequests.getContent(),
                reviewRequests.getTotalPages(),
                reviewRequests.getTotalElements(),
                page, size
        );
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
                        r.getAnswerCount(),
                        r.getEvalCount() == 0 ? 0 : Double.valueOf(decimalFormat.format(r.getEvalTotal() / r.getEvalCount())))
        ).collect(Collectors.toList());
    }


    private Pageable makePageable(int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);

        return PageRequest.of(page, size, sort);
    }
}
