package com.sparta.showmethecode.user.service;

import com.sparta.showmethecode.common.dto.response.PageResponseDtoV2;
import com.sparta.showmethecode.language.domain.Language;
import com.sparta.showmethecode.language.repository.LanguageRepository;
import com.sparta.showmethecode.question.domain.QuestionStatus;
import com.sparta.showmethecode.question.dto.response.QuestionResponseDto;
import com.sparta.showmethecode.question.repository.QuestionRepository;
import com.sparta.showmethecode.ranking.domain.Ranking;
import com.sparta.showmethecode.security.JwtUtils;
import com.sparta.showmethecode.security.UserDetailsImpl;
import com.sparta.showmethecode.security.UserDetailsServiceImpl;
import com.sparta.showmethecode.user.domain.User;
import com.sparta.showmethecode.user.domain.UserRole;
import com.sparta.showmethecode.user.dto.request.SigninRequestDto;
import com.sparta.showmethecode.user.dto.request.SignupRequestDto;
import com.sparta.showmethecode.user.dto.response.SigninResponseDto;
import com.sparta.showmethecode.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final LanguageRepository languageRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User saveUser(SignupRequestDto requestDto) {
        UserRole userRole = requestDto.isReviewer() ? UserRole.ROLE_REVIEWER : UserRole.ROLE_USER;

        User user = User.builder()
                .username(requestDto.getUsername())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .nickname(requestDto.getNickname())
                .role(userRole)
                .languages(new ArrayList<>())
                .build();

        User savedUser = userRepository.save(user);

        if (requestDto.getLanguages().size() > 0) {
            Set<String> languages = requestDto.getLanguages();
            for (String l : languages) {
                Language language = new Language(l.toUpperCase());
                language.setUser(savedUser);
                languageRepository.save(language);
            }
        }

        if (user.getRole().equals(UserRole.ROLE_REVIEWER)) {
            Ranking ranking = new Ranking(0, 0.0, 0, 0);
            user.setRanking(ranking);
        }

        return savedUser;
    }

    public SigninResponseDto signin(SigninRequestDto requestDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("로그인에 실패했습니다.");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(requestDto.getUsername());
        String token = jwtUtils.createToken(userDetails.getUsername());

        String authority = userDetails.getAuthorities().stream().findFirst().get().toString();

        return new SigninResponseDto(userDetails.getUser().getId(), token, authority, HttpStatus.CREATED, "로그인에 성공했습니다.");
    }

    public List<String> getMyLanguage(Long userId) {
        return languageRepository.findByUserId(userId);
    }


    /**
     * 내가 등록한 리뷰요청목록 조회 API v2 (더보기 방식)
     */
    public PageResponseDtoV2<QuestionResponseDto> getMyReviewRequestListV2(User user, Long lastId, int limit, List<QuestionStatus> status) {

        List<QuestionResponseDto> result = questionRepository.findMyQuestionV2(user.getId(), lastId, limit, status);

        Long currentLastId = result.get(result.size() - 1).getQuestionId();
        boolean lastPage = questionRepository.isLastPage(currentLastId);

        return new PageResponseDtoV2<QuestionResponseDto>(result, currentLastId, lastPage);
    }

    private Pageable makePageable(int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);

        return PageRequest.of(page, size, sort);
    }
}
