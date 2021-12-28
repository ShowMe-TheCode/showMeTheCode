package com.sparta.showmethecode.api;

import com.google.common.net.HttpHeaders;
import com.google.gson.Gson;
import com.sparta.showmethecode.answer.domain.Answer;
import com.sparta.showmethecode.comment.domain.Comment;
import com.sparta.showmethecode.language.domain.Language;
import com.sparta.showmethecode.answer.dto.request.AddAnswerDto;
import com.sparta.showmethecode.answer.dto.request.EvaluateAnswerDto;
import com.sparta.showmethecode.answer.dto.request.UpdateAnswerDto;
import com.sparta.showmethecode.answer.repository.AnswerRepository;
import com.sparta.showmethecode.comment.repository.CommentRepository;
import com.sparta.showmethecode.question.domain.Question;
import com.sparta.showmethecode.question.domain.QuestionStatus;
import com.sparta.showmethecode.question.repository.QuestionRepository;
import com.sparta.showmethecode.user.dto.request.UpdateReviewerDto;
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
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
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

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.config.location=" +
        "classpath:/application-test.yml")
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class ReviewerControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    CommentRepository reviewRequestCommentRepository;
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
        reviewer = new User("reviewer", passwordEncoder.encode("password"), "테스트_리뷰어", UserRole.ROLE_REVIEWER, 0, 0, 0.0, Arrays.asList(new Language("JAVA")));
        newReviewer = new User("newReviewer", passwordEncoder.encode("password"), "테스트_리뷰어", UserRole.ROLE_REVIEWER, 0, 0, 0.0, Arrays.asList(new Language("JAVA")));

        userRepository.saveAll(Arrays.asList(user, reviewer, newReviewer));

        question = new Question(user, reviewer, "제목", "내용", QuestionStatus.UNSOLVE, "JAVA");
        questionRepository.save(question);

        Comment comment1 = new Comment("댓글1", user);
        Comment comment2 = new Comment("댓글2", reviewer);
        reviewRequestCommentRepository.saveAll(Arrays.asList(comment1, comment2));

        answer = new Answer("답변내용", 4.5, reviewer, question);
        reviewAnswerRepository.save(answer);

        question.addComment(comment1);
        question.addComment(comment2);
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

    @Order(1)
    @DisplayName("1. 답변등록 API 테스트")
    @Test
    public void 답변등록() throws Exception {
        String token = createTokenAndSpringSecuritySetting(reviewer);

        AddAnswerDto addAnswerDto = new AddAnswerDto("답변내용");
        String dtoJson = new Gson().toJson(addAnswerDto);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/answer/{questionId}", question.getId())
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + token)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(dtoJson)
                ).andExpect(status().isOk())
                .andDo(document("post-answer",
                                pathParameters(
                                        parameterWithName("questionId").description("리뷰요청_ID")
                                ),
                                requestFields(
                                        fieldWithPath("content").description("답변내용")
                                )
                        )
                );
    }

    @Order(2)
    @DisplayName("2. 답변목록 조회 API")
    @Test
    public void 답변목록_조회() throws Exception {
        String token = createTokenAndSpringSecuritySetting(reviewer);
        mockMvc.perform(RestDocumentationRequestBuilders.get("/answers")
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + token)
                        .param("page", "1")
                        .param("size", "10")
                        .param("isAsc", "true")
                        .param("sortBy", "createdAt")
                )
                .andExpect(status().isOk())
                .andDo(document("get-answers",
                                requestParameters(
                                        parameterWithName("page").description("요청_페이지_번호").optional(),
                                        parameterWithName("size").description("페이지_당_요소수").optional(),
                                        parameterWithName("sortBy").description("정렬기준_필드_이름").optional(),
                                        parameterWithName("isAsc").description("정렬방향").optional()
                                )
                                , responseFields(
                                        fieldWithPath("totalPage").description("전체 페이지수").type(JsonFieldType.NUMBER),
                                        fieldWithPath("totalElements").description("전체 요소수").type(JsonFieldType.NUMBER),
                                        fieldWithPath("page").description("현재페이지 번호").type(JsonFieldType.NUMBER),
                                        fieldWithPath("size").description("페이지 당 요소수").type(JsonFieldType.NUMBER),

                                        subsectionWithPath("data").description("답변_데이터"),
                                        fieldWithPath("data.[].reviewAnswerId").description("리뷰답변_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("data.[].reviewRequestId").description("리뷰요청_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("data.[].username").description("답변자_이름").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].nickname").description("답변자_닉네임").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].answerContent").description("답변_내용").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].point").description("답변_점수").type(JsonFieldType.NUMBER),
                                        fieldWithPath("data.[].createdAt").description("답변_날짜").type(JsonFieldType.STRING)
                                )
                        )
                );
    }

    @Order(3)
    @DisplayName("3. 답변수정 API 테스트")
    @Test
    public void 답변수정() throws Exception {
        String token = createTokenAndSpringSecuritySetting(reviewer);

        UpdateAnswerDto updateAnswerDto = new UpdateAnswerDto("답변수정");
        String dtoJson = new Gson().toJson(updateAnswerDto);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/answer/{answerId}", answer.getId())
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + token)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(dtoJson)
                ).andExpect(status().isOk())
                .andDo(document("put-answer",
                                requestFields(
                                        fieldWithPath("content").description("수정내용")
                                ),
                                pathParameters(
                                        parameterWithName("answerId").description("리뷰답변_ID")
                                )
                        )
                );
    }

    @Order(4)
    @DisplayName("4. 리뷰어 변경 API")
    @Test
    public void 리뷰어_변경() throws Exception {
        String token = createTokenAndSpringSecuritySetting(reviewer);

        UpdateReviewerDto updateReviewerDto = new UpdateReviewerDto(newReviewer.getId());
        String dtoJson = new Gson().toJson(updateReviewerDto);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/question/{questionId}/reviewer/{reviewerId}", question.getId(), reviewer.getId())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(dtoJson)
                ).andExpect(status().isOk())
                .andDo(document("put-question-reviewer",
                                pathParameters(
                                        parameterWithName("questionId").description("리뷰요청_ID"),
                                        parameterWithName("reviewerId").description("현재_답변자_ID")
                                ),
                                requestFields(
                                        fieldWithPath("newReviewerId").description("변경될_답변자_ID")
                                )
                        )
                );
    }

    @Order(5)
    @DisplayName("5. 답변평가 API")
    @Test
    public void 답변평가() throws Exception {

        String token = createTokenAndSpringSecuritySetting(user);
        EvaluateAnswerDto evaluateAnswerDto = new EvaluateAnswerDto(4.5);
        String dtoJson = new Gson().toJson(evaluateAnswerDto);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/question/{questionId}/eval/{answerId}", question.getId(), answer.getId())
                .header(HttpHeaders.AUTHORIZATION, token)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(dtoJson)
        ).andExpect(status().isOk())
                .andDo(document("post-answer-evaluate",
                                pathParameters(
                                        parameterWithName("questionId").description("리뷰요청_ID"),
                                        parameterWithName("answerId").description("리뷰답변_ID")
                                ),
                                requestFields(
                                        fieldWithPath("point").description("평가점수")
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
