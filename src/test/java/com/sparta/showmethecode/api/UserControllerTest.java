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

    @DisplayName("1. ?????????????????? ????????? ?????? API ?????????")
    @Order(1)
    @Test
    public void ????????????() throws Exception {
        final String testLanguageName = "JAVA";
        mockMvc.perform(get("/reviewers/language")
                        .param("language", testLanguageName)
                ).andExpect(status().isOk())
                .andDo(document("get-user-searchByLanguageName",
                                requestParameters(
                                        parameterWithName("language").description("????????????")
                                ),
                                responseFields(
                                        fieldWithPath("[].id").description("?????????_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("[].username").description("?????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("[].nickname").description("?????????_?????????").type(JsonFieldType.STRING),
                                        fieldWithPath("[].languages").description("????????????_??????").type(JsonFieldType.ARRAY),
                                        fieldWithPath("[].answerCount").description("?????????_?????????").type(JsonFieldType.NUMBER),
                                        fieldWithPath("[].point").description("?????????_??????_?????????").type(JsonFieldType.NUMBER)

                                )
                        )
                );
    }

    @DisplayName("2. ?????? ????????? ?????????????????? ?????? API ?????????")
    @Order(2)
    @Test
    public void ????????????????????????() throws Exception {
        String token = createTokenAndSpringSecuritySetting(user);

        mockMvc.perform(get("/users/requests")
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + token)
                        .param("size", "10")
                        .param("status", QuestionStatus.UNSOLVE.toString())
                ).andExpect(status().isOk())
                .andDo(document("get-request-reviewList",
                                requestParameters(
                                        parameterWithName("size").description("?????????_???_?????????").optional(),
                                        parameterWithName("status").description("????????????_????????????").optional()
                                ), responseFields(
                                        fieldWithPath("lastId").description("????????? ?????? ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("lastPage").description("?????? ???????????? ????????? ???????????????").type(JsonFieldType.BOOLEAN),

                                        subsectionWithPath("data").description("????????????_?????????"),
                                        fieldWithPath("data.[].questionId").description("????????????_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("data.[].username").description("???????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].nickname").description("???????????????_?????????").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].title").description("????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].content").description("????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].languageName").description("????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].status").description("????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].createdAt").description("????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].commentCount").description("????????????_?????????").type(JsonFieldType.NUMBER)
                                )

                        )
                );
    }

    @DisplayName("3.????????? ????????? ???????????? ?????? API ?????????")
    @Order(3)
    @Test
    public void ?????????_????????????_??????() throws Exception {
        String token = createTokenAndSpringSecuritySetting(reviewer);

        mockMvc.perform(get("/reviewers/questions")
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + token)
                        .param("size", "10")
                        .param("status", QuestionStatus.UNSOLVE.toString())
                ).andExpect(status().isOk())
                .andDo(document("get-received-reviewList",
                                requestParameters(
                                        parameterWithName("lastId").description("??????_?????????_?????????_ID").optional(),
                                        parameterWithName("size").description("?????????_???_?????????").optional(),
                                        parameterWithName("status").description("????????????_????????????").optional()
                                ), responseFields(
                                        fieldWithPath("lastId").description("????????? ?????? ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("lastPage").description("?????? ???????????? ????????? ???????????????").type(JsonFieldType.BOOLEAN),

                                        subsectionWithPath("data").description("????????????_?????????"),
                                        fieldWithPath("data.[].questionId").description("????????????_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("data.[].username").description("???????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].nickname").description("???????????????_?????????").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].title").description("????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].content").description("????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].languageName").description("????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].status").description("????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].createdAt").description("????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].commentCount").description("????????????_?????????").type(JsonFieldType.NUMBER)
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
