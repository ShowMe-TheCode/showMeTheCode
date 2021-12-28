package com.sparta.showmethecode.api;

import com.google.common.net.HttpHeaders;
import com.sparta.showmethecode.comment.domain.ReviewRequestComment;
import com.sparta.showmethecode.language.domain.Language;
import com.sparta.showmethecode.answer.repository.AnswerRepository;
import com.sparta.showmethecode.comment.repository.ReviewRequestCommentRepository;
import com.sparta.showmethecode.answer.domain.Answer;
import com.sparta.showmethecode.question.repository.QuestionRepository;
import com.sparta.showmethecode.question.domain.Question;
import com.sparta.showmethecode.question.domain.QuestionStatus;
import com.sparta.showmethecode.user.repository.UserRepository;
import com.sparta.showmethecode.security.JwtUtils;
import com.sparta.showmethecode.security.UserDetailsImpl;
import com.sparta.showmethecode.user.domain.User;
import com.sparta.showmethecode.user.domain.UserRole;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Arrays;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest(properties = "spring.config.location=" +
        "classpath:/application-test.yml")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    ReviewRequestCommentRepository reviewRequestCommentRepository;
    @Autowired
    AnswerRepository reviewAnswerRepository;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    PasswordEncoder passwordEncoder;

    final String TOKEN_PREFIX = "Bearer ";

    User user;
    User reviewer;
    User newReviewer;
    Question question;
    Answer answer;
    String token;

    @BeforeAll
    void init() {
        user = new User("user", passwordEncoder.encode("password"), "테스트_사용자", UserRole.ROLE_USER, 0, 0, 0.0);
        reviewer = new User("reviewer", passwordEncoder.encode("password"), "테스트_리뷰어1", UserRole.ROLE_REVIEWER, 0, 0, 0.0, Arrays.asList(new Language("JAVA")));
        newReviewer = new User("newReviewer", passwordEncoder.encode("password"), "테스트_리뷰어2", UserRole.ROLE_REVIEWER, 0, 0, 0.0, Arrays.asList(new Language("JAVA")));
        User reviewer2 = new User("newReviewer2", passwordEncoder.encode("password"), "테스트_리뷰어3", UserRole.ROLE_REVIEWER, 0, 0, 0.0, Arrays.asList(new Language("Python")));

        userRepository.saveAll(Arrays.asList(user, reviewer, newReviewer));

        question = new Question(user, reviewer, "제목", "내용", QuestionStatus.UNSOLVE, "JAVA");
        questionRepository.save(question);

        ReviewRequestComment reviewRequestComment1 = new ReviewRequestComment("댓글1", user);
        ReviewRequestComment reviewRequestComment2 = new ReviewRequestComment("댓글2", reviewer);
        reviewRequestCommentRepository.saveAll(Arrays.asList(reviewRequestComment1, reviewRequestComment2));

        answer = new Answer("답변내용", 4.5, reviewer, question);
        reviewAnswerRepository.save(answer);

        question.addComment(reviewRequestComment1);
        question.addComment(reviewRequestComment2);
        question.setAnswer(answer);

        questionRepository.save(question);

    }

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(MockMvcResultHandlers.print())
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @DisplayName("1. 언어이름으로 리뷰어 조회 API 테스트")
    @Order(1)
    @Test
    public void 회원가입() throws Exception {
        final String testLanguageName = "JAVA";
        mockMvc.perform(get("/user/language")
                        .param("language", testLanguageName)
                ).andExpect(status().isOk())
                .andDo(document("get-user-searchByLanguageName",
                                requestParameters(
                                        parameterWithName("language").description("언어이름")
                                ),
                                responseFields(
                                        fieldWithPath("[].id").description("리뷰어_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("[].username").description("리뷰어_이름").type(JsonFieldType.STRING),
                                        fieldWithPath("[].nickname").description("리뷰어_닉네임").type(JsonFieldType.STRING),
                                        fieldWithPath("[].languages").description("언어이름_목록").type(JsonFieldType.ARRAY),
                                        fieldWithPath("[].answerCount").description("리뷰어_답변수").type(JsonFieldType.NUMBER),
                                        fieldWithPath("[].point").description("리뷰어_답변_포인트").type(JsonFieldType.NUMBER)

                                )
                        )
                );
    }

    @DisplayName("2. 내가 등록한 리뷰요청목록 조회 API 테스트")
    @Order(2)
    @Test
    public void 리뷰요청목록조회() throws Exception {
        String token = createTokenAndSpringSecuritySetting(user);

        mockMvc.perform(get("/user/requests")
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + token)
                        .param("page", "1")
                        .param("size", "10")
                        .param("isAsc", "true")
                        .param("sortBy", "createdAt")
                        .param("status", QuestionStatus.UNSOLVE.toString())
                ).andExpect(status().isOk())
                .andDo(document("get-request-reviewList",
                                requestParameters(
                                        parameterWithName("page").description("요청_페이지_번호").optional(),
                                        parameterWithName("size").description("페이지_당_요소수").optional(),
                                        parameterWithName("sortBy").description("정렬기준_필드_이름").optional(),
                                        parameterWithName("isAsc").description("정렬방향").optional(),
                                        parameterWithName("status").description("리뷰요청_처리상태").optional()
                                ), responseFields(
                                        fieldWithPath("totalPage").description("전체 페이지수").type(JsonFieldType.NUMBER),
                                        fieldWithPath("totalElements").description("전체 요소수").type(JsonFieldType.NUMBER),
                                        fieldWithPath("page").description("현재페이지 번호").type(JsonFieldType.NUMBER),
                                        fieldWithPath("size").description("페이지 당 요소수").type(JsonFieldType.NUMBER),

                                        subsectionWithPath("data").description("리뷰요청_데이터"),
                                        fieldWithPath("data.[].reviewRequestId").description("리뷰요청_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("data.[].username").description("리뷰요청자_이름").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].nickname").description("리뷰요청자_닉네임").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].title").description("리뷰요청_제목").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].content").description("리뷰요청_내용").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].languageName").description("리뷰요청_언어").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].status").description("리뷰요청_상태").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].createdAt").description("리뷰요청_날짜").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].commentCount").description("리뷰요청_댓글수").type(JsonFieldType.NUMBER)
                                )

                        )
                );
    }

    @DisplayName("3.나에게 요청된 리뷰목록 조회 API 테스트")
    @Order(3)
    @Test
    public void 요청된_리뷰목록_조회() throws Exception {
        String token = createTokenAndSpringSecuritySetting(reviewer);

        mockMvc.perform(get("/user/received")
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + token)
                        .param("page", "1")
                        .param("size", "10")
                        .param("isAsc", "true")
                        .param("sortBy", "createdAt")
                        .param("status", QuestionStatus.UNSOLVE.toString())
                ).andExpect(status().isOk())
                .andDo(document("get-received-reviewList",
                                requestParameters(
                                        parameterWithName("page").description("요청_페이지_번호").optional(),
                                        parameterWithName("size").description("페이지_당_요소수").optional(),
                                        parameterWithName("sortBy").description("정렬기준_필드_이름").optional(),
                                        parameterWithName("isAsc").description("정렬방향").optional(),
                                        parameterWithName("status").description("리뷰요청_처리상태").optional()
                                ), responseFields(
                                        fieldWithPath("totalPage").description("전체 페이지수").type(JsonFieldType.NUMBER),
                                        fieldWithPath("totalElements").description("전체 요소수").type(JsonFieldType.NUMBER),
                                        fieldWithPath("page").description("현재페이지 번호").type(JsonFieldType.NUMBER),
                                        fieldWithPath("size").description("페이지 당 요소수").type(JsonFieldType.NUMBER),

                                        subsectionWithPath("data").description("리뷰요청_데이터"),
                                        fieldWithPath("data.[].reviewRequestId").description("리뷰요청_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("data.[].username").description("리뷰요청자_이름").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].nickname").description("리뷰요청자_닉네임").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].title").description("리뷰요청_제목").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].content").description("리뷰요청_내용").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].languageName").description("리뷰요청_언어").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].status").description("리뷰요청_상태").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].createdAt").description("리뷰요청_날짜").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].commentCount").description("리뷰요청_댓글수").type(JsonFieldType.NUMBER)
                                )
                        )
                );
    }

    private String createTokenAndSpringSecuritySetting(User user) {
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        return token = jwtUtils.createToken(userDetails.getUsername());
    }

}
