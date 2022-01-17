package com.sparta.showmethecode.api;

import com.google.common.net.HttpHeaders;
import com.sparta.showmethecode.comment.domain.Comment;
import com.sparta.showmethecode.helper.UserHelper;
import com.sparta.showmethecode.language.domain.Language;
import com.sparta.showmethecode.answer.repository.AnswerRepository;
import com.sparta.showmethecode.comment.repository.CommentRepository;
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
        user = UserHelper.createUser("user", passwordEncoder.encode("password"), "테스트_사용자", UserRole.ROLE_USER);
        reviewer = UserHelper.createUser("reviewer", passwordEncoder.encode("password"), "테스트_리뷰어1", UserRole.ROLE_REVIEWER, "JAVA");
        newReviewer = UserHelper.createUser("newReviewer", passwordEncoder.encode("password"), "테스트_리뷰어2", UserRole.ROLE_REVIEWER, "JAVA");

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

    @DisplayName("1. 언어이름으로 리뷰어 조회 API 테스트")
    @Order(1)
    @Test
    public void 회원가입() throws Exception {
        final String testLanguageName = "JAVA";
        mockMvc.perform(get("/reviewers/language")
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

        mockMvc.perform(get("/users/requests")
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + token)
                        .param("size", "10")
                        .param("status", QuestionStatus.UNSOLVE.toString())
                ).andExpect(status().isOk())
                .andDo(document("get-request-reviewList",
                                requestParameters(
                                        parameterWithName("size").description("페이지_당_요소수").optional(),
                                        parameterWithName("status").description("리뷰요청_처리상태").optional()
                                ), responseFields(
                                        fieldWithPath("lastId").description("마지막 요소 ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("lastPage").description("현재 페이지가 마지막 페이지인지").type(JsonFieldType.BOOLEAN),

                                        subsectionWithPath("data").description("리뷰요청_데이터"),
                                        fieldWithPath("data.[].questionId").description("리뷰요청_ID").type(JsonFieldType.NUMBER),
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

        mockMvc.perform(get("/reviewers/questions")
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + token)
                        .param("size", "10")
                        .param("status", QuestionStatus.UNSOLVE.toString())
                ).andExpect(status().isOk())
                .andDo(document("get-received-reviewList",
                                requestParameters(
                                        parameterWithName("lastId").description("현재_페이지_마지막_ID").optional(),
                                        parameterWithName("size").description("페이지_당_요소수").optional(),
                                        parameterWithName("status").description("리뷰요청_처리상태").optional()
                                ), responseFields(
                                        fieldWithPath("lastId").description("마지막 요소 ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("lastPage").description("현재 페이지가 마지막 페이지인지").type(JsonFieldType.BOOLEAN),

                                        subsectionWithPath("data").description("리뷰요청_데이터"),
                                        fieldWithPath("data.[].questionId").description("리뷰요청_ID").type(JsonFieldType.NUMBER),
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
