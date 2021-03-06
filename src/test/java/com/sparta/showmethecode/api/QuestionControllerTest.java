package com.sparta.showmethecode.api;

import com.google.common.net.HttpHeaders;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sparta.showmethecode.answer.domain.Answer;
import com.sparta.showmethecode.comment.domain.Comment;
import com.sparta.showmethecode.helper.UserHelper;
import com.sparta.showmethecode.language.domain.Language;
import com.sparta.showmethecode.question.dto.request.AddQuestionDto;
import com.sparta.showmethecode.answer.repository.AnswerRepository;
import com.sparta.showmethecode.comment.repository.CommentRepository;
import com.sparta.showmethecode.question.dto.request.UpdateQuestionDto;
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

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest(properties = "spring.config.location=" +
        "classpath:/application-test.yml")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class QuestionControllerTest {

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
    Question question;
    String token;

    @BeforeAll
    void init() {
        user = UserHelper.createUser("user", passwordEncoder.encode("password"), "?????????_?????????", UserRole.ROLE_USER);
        reviewer = UserHelper.createUser("reviewer", passwordEncoder.encode("password"), "?????????_?????????1", UserRole.ROLE_REVIEWER, "JAVA");
        userRepository.saveAll(Arrays.asList(user, reviewer));

        question = new Question(user, reviewer, "??????", "??????", QuestionStatus.UNSOLVE, "JAVA");
        questionRepository.save(question);

        Comment comment1 = new Comment("??????1", user);
        Comment comment2 = new Comment("??????2", reviewer);
        reviewRequestCommentRepository.saveAll(Arrays.asList(comment1, comment2));

        Answer answer = new Answer("????????????", 4.5, reviewer, question);
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
    @DisplayName("1. ???????????? ??????")
    @Test
    public void ????????????_??????() throws Exception {
        AddQuestionDto addQuestionDto = new AddQuestionDto("?????????_??????", "?????????_??????", "JAVA", reviewer.getId());
        String dto = new GsonBuilder().create().toJson(addQuestionDto);

        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        String token = jwtUtils.createToken(userDetails.getUsername());

        mockMvc.perform(post("/questions")
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(dto))
                .andExpect(status().isOk())
                .andDo(document("post-question",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT token")
                                ),
                                requestFields(
                                        fieldWithPath("title").description("?????????????????? ??????"),
                                        fieldWithPath("content").description("?????????????????? ??????"),
                                        fieldWithPath("language").description("?????????????????? ????????????"),
                                        fieldWithPath("reviewerId").description("?????????????????? ????????? ID")
                                )
                        )
                );
    }

    @Order(2)
    @DisplayName("2. ???????????? ???????????? ")
    @Test
    public void ????????????_????????????() throws Exception {

        mockMvc.perform(get("/questions")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andDo(document("get-questions",
                                requestParameters(
                                        parameterWithName("lastId").description("??????_?????????_?????????_ID").optional(),
                                        parameterWithName("size").description("?????????_???_?????????").optional(),
                                        parameterWithName("status").description("????????????_????????????").optional(),
                                        parameterWithName("sortBy").description("???????????? ?????? ??????").optional(),
                                        parameterWithName("query").description("?????????????????? ?????? ??????").optional()
                                )
                                , responseFields(
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

    @Order(3)
    @DisplayName("3. ???????????? ?????? ???????????? (????????????) ")
    @Test
    public void ????????????_??????() throws Exception {

        mockMvc.perform(get("/questions/{questionId}", question.getId().toString())
                ).andExpect(status().isOk())
                .andDo(document("get-question",
                        pathParameters(
                                parameterWithName("questionId").description("????????????_ID")
                        ),
                                responseFields(
                                        fieldWithPath("questionId").description("????????????_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("questionUserId").description("???????????????_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("answerUserId").description("???????????????_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("username").description("???????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("nickname").description("???????????????_?????????").type(JsonFieldType.STRING),
                                        fieldWithPath("title").description("????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("content").description("????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("languageName").description("????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("status").description("????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("createdAt").description("????????????_????????????").type(JsonFieldType.STRING),

                                        subsectionWithPath("answer").description("????????????"),
                                        fieldWithPath("answer.answerId").description("???????????????_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("answer.questionId").description("???????????????_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("answer.username").description("???????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("answer.nickname").description("???????????????_?????????").type(JsonFieldType.STRING),
                                        fieldWithPath("answer.content").description("????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("answer.point").description("????????????_??????").type(JsonFieldType.NUMBER),
                                        fieldWithPath("answer.createdAt").description("????????????_????????????").type(JsonFieldType.STRING),


                                        subsectionWithPath("comments").description("??????"),
                                        fieldWithPath("comments.[].commentId").description("??????_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("comments.[].userId").description("??????_?????????_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("comments.[].username").description("??????_?????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("comments.[].nickname").description("??????_?????????_?????????").type(JsonFieldType.STRING),
                                        fieldWithPath("comments.[].content").description("??????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("comments.[].createdAt").description("??????_????????????").type(JsonFieldType.STRING)
                                )
                        )
                );
    }

    @Order(4)
    @DisplayName("4. ???????????? ?????? ??????")
    @Test
    public void ????????????_??????() throws Exception {
        UpdateQuestionDto updateQuestionDto = new UpdateQuestionDto("????????????", "????????????");
        String dtoJson = new Gson().toJson(updateQuestionDto);

        String token = createTokenAndSpringSecuritySetting();

        mockMvc.perform(RestDocumentationRequestBuilders.put("/questions/{id}", question.getId())
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + token)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(dtoJson)
                ).andExpect(status().isOk())
                .andDo(document("put-question",
                                pathParameters(
                                        parameterWithName("id").description("????????????_ID")
                                ),
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT token")
                                ),
                                requestFields(
                                        fieldWithPath("title").description("???????????????_??????_??????"),
                                        fieldWithPath("content").description("???????????????_??????_??????")
                                )
                        )
                );
    }

    @Order(5)
    @DisplayName("5. ???????????? ?????? ??????")
    @Test
    public void ????????????_??????() throws Exception {
        String token = createTokenAndSpringSecuritySetting();

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/questions/{id}", question.getId())
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + token)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                ).andExpect(status().isOk())
                .andDo(document("delete-question",
                                pathParameters(
                                        parameterWithName("id").description("????????????_ID")
                                ),
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT token")
                                )
                        )
                );
    }

    @Order(6)
    @DisplayName("6. ???????????? ?????? ????????? ????????? API ?????????")
    @Test
    public void ?????????_?????????() throws Exception {
        mockMvc.perform(get("/questions/language")
                        .param("language", "JAVA")
                        .param("page", "1")
                        .param("size", "10")
                        .param("isAsc", "true")
                ).andExpect(status().isOk())
                .andDo(document("get-question-language-count",
                                requestParameters(
                                        parameterWithName("language").description("????????????"),
                                        parameterWithName("page").description("??????_?????????_??????").optional(),
                                        parameterWithName("size").description("?????????_???_?????????").optional(),
                                        parameterWithName("isAsc").description("????????????").optional()
                                )
                        )
                );
    }

    private String createTokenAndSpringSecuritySetting() {
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        return token = jwtUtils.createToken(userDetails.getUsername());
    }

}



