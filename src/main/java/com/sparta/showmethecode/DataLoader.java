package com.sparta.showmethecode;

import com.sparta.showmethecode.answer.domain.Answer;
import com.sparta.showmethecode.answer.repository.AnswerRepository;
import com.sparta.showmethecode.comment.domain.Comment;
import com.sparta.showmethecode.language.domain.Language;
import com.sparta.showmethecode.question.domain.Question;
import com.sparta.showmethecode.question.domain.QuestionStatus;
import com.sparta.showmethecode.question.repository.QuestionRepository;
import com.sparta.showmethecode.user.domain.User;
import com.sparta.showmethecode.user.domain.UserRole;
import com.sparta.showmethecode.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @Override
    public void run(String... args) throws Exception {
        User user1 = createNormalUser("test1", "코린이1", "1234");
        User user2 = createNormalUser("test2", "코린이2", "1234");
        User reviewerJava = createReviewer("reviewer-java", "JavaGod", "1234", "Java");
        User reviewerSpring = createReviewer("reviewer-spring", "SpringGenius", "1234", "Spring");
        User reviewerFlask = createReviewer("reviewer-flask", "EasyFlask", "1234", "Flask");

        Question questionJava = createQuestion(user1, reviewerJava, "Java가 너무 어려워요", "Java 안할래요 ..", "JAVA");
        Question questionFlask = createQuestion(user2, reviewerJava, "Flask에서 라우팅은 어떻게 하는건가요 ?ㅠ", "후 ㅠㅠ", "FLASK");
        Question questionSpring = createQuestion(user1, reviewerJava, "Spring 왜 써요 ?", "왜 ??", "SPRING");
        for (int i=1;i<15;i++) {
            createQuestion(user1, reviewerSpring, "spring" + i, "spring xxx" + i, "SPRING");
        }
        for (int i=1;i<15;i++) {
            createQuestion(user2, reviewerFlask, "flask" + i, "python xxx" + i, "FLASK");
        }
        for (int i=1;i<15;i++) {
            createQuestion(user1, reviewerJava, "java" + i, "java xxx" + i, "JAVA");
        }

        addAnswer("answer1", questionJava, reviewerJava);
        addAnswer("answer2", questionFlask, reviewerFlask);
        addAnswer("answer3", questionSpring, reviewerSpring);
        addComment("댓글1", user1, questionJava);
        addComment("댓글2", user2, questionJava);
        addComment("댓글3", user1, questionJava);
    }



    private User createNormalUser(String username, String nickname, String password) {
        User user = User.builder()
                .username(username)
                .nickname(nickname)
                .password(passwordEncoder.encode(password))
                .role(UserRole.ROLE_USER).build();
        userRepository.save(user);

        return user;
    }

    private User createReviewer(String username, String nickname, String password, String language) {
        User user =  User.builder()
                .username(username)
                .nickname(nickname)
                .password(passwordEncoder.encode(password))
                .languages(new ArrayList<>())
                .role(UserRole.ROLE_REVIEWER).build();
        user.addLanguage(new Language(language.toUpperCase()));
        userRepository.save(user);

        return user;
    }

    private Question createQuestion(User user, User reviewer, String title, String content, String language) {
        Question question = new Question(null, title, content, QuestionStatus.UNSOLVE, language,
                user, reviewer, null, new ArrayList<>(), null);

        questionRepository.save(question);

        return question;
    }

    private void addAnswer(String content, Question question, User reviewer) {
        Answer answer = new Answer(content, 0.0, reviewer);
        question.addAnswer(answer);
        question.setStatus(QuestionStatus.SOLVE);

        questionRepository.save(question);
    }

    private void addComment(String content, User user, Question question) {
        Comment comment = new Comment(content, user);
        question.addComment(comment);

        questionRepository.save(question);
    }
}
