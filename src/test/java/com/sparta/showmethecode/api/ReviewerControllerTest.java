package com.sparta.showmethecode.api;

import com.google.common.net.HttpHeaders;
import com.google.gson.Gson;
import com.sparta.showmethecode.answer.domain.Answer;
import com.sparta.showmethecode.comment.domain.Comment;
import com.sparta.showmethecode.helper.UserHelper;
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
        user = UserHelper.createUser("user", passwordEncoder.encode("password"), "?????????_?????????", UserRole.ROLE_USER);
        reviewer = UserHelper.createUser("reviewer", passwordEncoder.encode("password"), "?????????_?????????1", UserRole.ROLE_REVIEWER, "JAVA");
        newReviewer = UserHelper.createUser("newReviewer", passwordEncoder.encode("password"), "?????????_?????????2", UserRole.ROLE_REVIEWER, "JAVA");

        userRepository.saveAll(Arrays.asList(user, reviewer, newReviewer));

        question = new Question(user, reviewer, "??????", "??????", QuestionStatus.UNSOLVE, "JAVA");
        questionRepository.save(question);

        Comment comment1 = new Comment("??????1", user);
        Comment comment2 = new Comment("??????2", reviewer);
        reviewRequestCommentRepository.saveAll(Arrays.asList(comment1, comment2));

        answer = new Answer("????????????", 4.5, reviewer, question);
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
    @DisplayName("1. ???????????? API ?????????")
    @Test
    public void ????????????() throws Exception {
        String token = createTokenAndSpringSecuritySetting(reviewer);

        AddAnswerDto addAnswerDto = new AddAnswerDto("????????????");
        String dtoJson = new Gson().toJson(addAnswerDto);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/answers/{questionId}", question.getId())
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + token)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(dtoJson)
                ).andExpect(status().isOk())
                .andDo(document("post-answer",
                                pathParameters(
                                        parameterWithName("questionId").description("????????????_ID")
                                ),
                                requestFields(
                                        fieldWithPath("content").description("????????????")
                                )
                        )
                );
    }

    @Order(2)
    @DisplayName("2. ?????? ????????? ???????????? ?????? API")
    @Test
    public void ????????????_??????() throws Exception {
        String token = createTokenAndSpringSecuritySetting(reviewer);
        mockMvc.perform(RestDocumentationRequestBuilders.get("/reviewers/answers")
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + token)
                        .param("page", "1")
                        .param("size", "10")
                        .param("isAsc", "true")
                        .param("sortBy", "createdAt")
                )
                .andExpect(status().isOk())
                .andDo(document("get-answers",
                                requestParameters(
                                        parameterWithName("page").description("??????_?????????_??????").optional(),
                                        parameterWithName("size").description("?????????_???_?????????").optional(),
                                        parameterWithName("sortBy").description("????????????_??????_??????").optional(),
                                        parameterWithName("isAsc").description("????????????").optional()
                                )
                                , responseFields(
                                        fieldWithPath("totalPage").description("?????? ????????????").type(JsonFieldType.NUMBER),
                                        fieldWithPath("totalElements").description("?????? ?????????").type(JsonFieldType.NUMBER),
                                        fieldWithPath("page").description("??????????????? ??????").type(JsonFieldType.NUMBER),
                                        fieldWithPath("size").description("????????? ??? ?????????").type(JsonFieldType.NUMBER),

                                        subsectionWithPath("data").description("??????_?????????"),
                                        fieldWithPath("data.[].answerId").description("????????????_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("data.[].questionId").description("????????????_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("data.[].username").description("?????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].nickname").description("?????????_?????????").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].content").description("??????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].point").description("??????_??????").type(JsonFieldType.NUMBER),
                                        fieldWithPath("data.[].createdAt").description("??????_??????").type(JsonFieldType.STRING)
                                )
                        )
                );
    }

    @Order(3)
    @DisplayName("3. ???????????? API ?????????")
    @Test
    public void ????????????() throws Exception {
        String token = createTokenAndSpringSecuritySetting(reviewer);

        UpdateAnswerDto updateAnswerDto = new UpdateAnswerDto("????????????");
        String dtoJson = new Gson().toJson(updateAnswerDto);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/answers/{answerId}", answer.getId())
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + token)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(dtoJson)
                ).andExpect(status().isOk())
                .andDo(document("put-answer",
                                requestFields(
                                        fieldWithPath("content").description("????????????")
                                ),
                                pathParameters(
                                        parameterWithName("answerId").description("????????????_ID")
                                )
                        )
                );
    }

    @Order(4)
    @DisplayName("4. ????????? ?????? API")
    @Test
    public void ?????????_??????() throws Exception {
        String token = createTokenAndSpringSecuritySetting(reviewer);

        UpdateReviewerDto updateReviewerDto = new UpdateReviewerDto(newReviewer.getId());
        String dtoJson = new Gson().toJson(updateReviewerDto);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/questions/{questionId}/reviewer/{reviewerId}", question.getId(), reviewer.getId())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(dtoJson)
                ).andExpect(status().isOk())
                .andDo(document("put-question-reviewer",
                                pathParameters(
                                        parameterWithName("questionId").description("????????????_ID"),
                                        parameterWithName("reviewerId").description("??????_?????????_ID")
                                ),
                                requestFields(
                                        fieldWithPath("newReviewerId").description("?????????_?????????_ID")
                                )
                        )
                );
    }

    @Order(5)
    @DisplayName("5. ???????????? API")
    @Test
    public void ????????????() throws Exception {

        String token = createTokenAndSpringSecuritySetting(user);
        EvaluateAnswerDto evaluateAnswerDto = new EvaluateAnswerDto(4.5);
        String dtoJson = new Gson().toJson(evaluateAnswerDto);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/answers/eval/{questionId}/{answerId}", question.getId(), answer.getId())
                .header(HttpHeaders.AUTHORIZATION, token)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(dtoJson)
        ).andExpect(status().isOk())
                .andDo(document("post-answer-evaluate",
                                pathParameters(
                                        parameterWithName("questionId").description("????????????_ID"),
                                        parameterWithName("answerId").description("????????????_ID")
                                ),
                                requestFields(
                                        fieldWithPath("point").description("????????????")
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
