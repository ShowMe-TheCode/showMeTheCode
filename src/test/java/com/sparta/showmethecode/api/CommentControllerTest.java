package com.sparta.showmethecode.api;

import com.google.common.net.HttpHeaders;
import com.google.gson.Gson;
import com.sparta.showmethecode.answer.domain.Answer;
import com.sparta.showmethecode.comment.domain.Comment;
import com.sparta.showmethecode.comment.dto.request.AddCommentDto;
import com.sparta.showmethecode.comment.dto.request.UpdateCommentDto;
import com.sparta.showmethecode.helper.UserHelper;
import com.sparta.showmethecode.language.domain.Language;
import com.sparta.showmethecode.answer.repository.AnswerRepository;
import com.sparta.showmethecode.comment.repository.CommentRepository;
import com.sparta.showmethecode.question.domain.Question;
import com.sparta.showmethecode.question.repository.QuestionRepository;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest(properties = "spring.config.location=" +
        "classpath:/application-test.yml")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class CommentControllerTest {

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
    Comment comment;
    String token;

    @BeforeAll
    public void init() {
        user = UserHelper.createUser("user", passwordEncoder.encode("password"), "테스트_사용자", UserRole.ROLE_USER);
        reviewer = UserHelper.createUser("reviewer", passwordEncoder.encode("password"), "테스트_리뷰어1", UserRole.ROLE_REVIEWER, "JAVA");

        userRepository.saveAll(Arrays.asList(user, reviewer));

        question = new Question(user, reviewer, "제목", "내용", QuestionStatus.UNSOLVE, "JAVA");
        questionRepository.save(question);

        comment = new Comment("댓글1", user);
        Comment comment2 = new Comment("댓글2", reviewer);
        reviewRequestCommentRepository.saveAll(Arrays.asList(comment, comment2));

        Answer answer = new Answer("답변내용", 4.5, reviewer, question);
        reviewAnswerRepository.save(answer);

        question.addComment(comment);
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
    @DisplayName("1. 댓글추가 API 테스트")
    @Test
    public void 댓글추가() throws Exception {
        String token = createTokenAndSpringSecuritySetting(user);
        AddCommentDto addCommentDto = new AddCommentDto("테스트 댓글입니다.");
        String dtoJson = new Gson().toJson(addCommentDto);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/comments/{questionId}", question.getId())
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + token)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(dtoJson)
                ).andExpect(status().isOk())
                .andDo(document("post-comment",
                                pathParameters(
                                        parameterWithName("questionId").description("리뷰요청_ID")
                                ),
                                requestFields(
                                        fieldWithPath("content").description("댓글내용")
                                )
                        )
                );
    }

    @Order(2)
    @DisplayName("2. 댓글삭제 API 테스트")
    @Test
    public void 댓글삭제() throws Exception {
        String token = createTokenAndSpringSecuritySetting(user);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/comments/{commentId}", comment.getId())
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + token)
                ).andExpect(status().isOk())
                .andDo(document("delete-comment",
                                pathParameters(
                                        parameterWithName("commentId").description("댓글_ID")
                                )
                        )
                );
    }

    @Order(3)
    @DisplayName("3. 댓글수정 API 테스트")
    @Test
    public void 댓글수정() throws Exception {
        String token = createTokenAndSpringSecuritySetting(user);
        UpdateCommentDto updateCommentDto = new UpdateCommentDto("댓글수정 테스트");
        String dtoJson = new Gson().toJson(updateCommentDto);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/comments/{commentId}", comment.getId())
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + token)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(dtoJson)
                ).andExpect(status().isOk())
                .andDo(document("put-comment",
                                pathParameters(
                                        parameterWithName("commentId").description("댓글_ID")
                                ),
                                requestFields(
                                        fieldWithPath("content").description("수정내용")
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
