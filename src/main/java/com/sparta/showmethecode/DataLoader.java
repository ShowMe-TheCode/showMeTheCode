package com.sparta.showmethecode;

import com.sparta.showmethecode.answer.domain.Answer;
import com.sparta.showmethecode.answer.dto.request.AddAnswerDto;
import com.sparta.showmethecode.answer.dto.request.EvaluateAnswerDto;
import com.sparta.showmethecode.answer.repository.AnswerRepository;
import com.sparta.showmethecode.answer.service.AnswerService;
import com.sparta.showmethecode.comment.domain.Comment;
import com.sparta.showmethecode.language.domain.Language;
import com.sparta.showmethecode.question.domain.Question;
import com.sparta.showmethecode.question.domain.QuestionStatus;
import com.sparta.showmethecode.question.repository.QuestionRepository;
import com.sparta.showmethecode.ranking.domain.Ranking;
import com.sparta.showmethecode.user.domain.User;
import com.sparta.showmethecode.user.domain.UserRole;
import com.sparta.showmethecode.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final AnswerService answerService;

    private final EntityManager em;

    @Override
    public void run(String... args) throws Exception {
        User user1 = createNormalUser("test1", "코린이1", "1234");
        User user2 = createNormalUser("test2", "코린이2", "1234");

        User reviewerJava = createReviewer("reviewer-java", "JavaGod", "1234", "Java");
        User reviewerJava2 = createReviewer("reviewer-java2", "GoodCoder", "1234", "Java");
        User reviewerJava3 = createReviewer("reviewer-java3", "zzzHOHO", "1234", "Java");
        User reviewerSpring = createReviewer("reviewer-spring", "SpringGenius", "1234", "Spring");
        User reviewerSpring2 = createReviewer("reviewer-spring2", "SpringGod", "1234", "Spring");
        User reviewerSpring3 = createReviewer("reviewer-spring3", "likeCode", "1234", "Spring");
        User reviewerFlask = createReviewer("reviewer-flask", "EasyFlask", "1234", "Flask");
        User reviewerFlask2 = createReviewer("reviewer-flask2", "VVeryEasyFlask", "1234", "Flask");
        User reviewerFlask3 = createReviewer("reviewer-flask3", "zzzHi", "1234", "Flask");

        Question questionJava1 = createQuestion(user1, reviewerJava, "Java가 너무 어려워요111", "Java 안할래요 ..111", "JAVA");
        Question questionJava2 = createQuestion(user1, reviewerJava, "Java가 너무 어려워요222", "Java 안할래요 ..222", "JAVA");
        Question questionJava3 = createQuestion(user1, reviewerJava2, "Java가 너무 어려워요333", "Java 안할래요 ..222", "JAVA");
        Question questionJava4 = createQuestion(user1, reviewerJava3, "Java가 너무 어려워요333", "Java 안할래요 ..222", "JAVA");

        Question questionFlask1 = createQuestion(user2, reviewerFlask, "Flask에서 라우팅은 어떻게 하는건가요 ?ㅠ111", "후 ㅠㅠ111", "FLASK");
        Question questionFlask2 = createQuestion(user2, reviewerFlask, "Flask에서 라우팅은 어떻게 하는건가요 ?ㅠ222", "후 123", "FLASK");
        Question questionFlask3 = createQuestion(user2, reviewerFlask2, "Flask에서 라우팅은 어떻게 하는건가요 ?ㅠ444", "후 rf", "FLASK");
        Question questionFlask4 = createQuestion(user2, reviewerFlask3, "Flask에서 라우팅은 어떻게 하는건가요 ?ㅠ555", "후 asdsd", "FLASK");

        Question questionSpring1 = createQuestion(user1, reviewerSpring, "Spring 왜 써요 ?111", "왜 ??111", "SPRING");
        Question questionSpring2 = createQuestion(user1, reviewerSpring, "Spring 왜 써요 ?222", "왜 ??222", "SPRING");
        Question questionSpring3 = createQuestion(user1, reviewerSpring2, "Spring 왜 써요 ?555", "왜 ?asdasd", "SPRING");
        Question questionSpring4 = createQuestion(user1, reviewerSpring3, "Spring 왜 써요 ?666", "왜 ?agsa", "SPRING");
        for (int i=1;i<15;i++) {
            createQuestion(user1, reviewerSpring, "spring" + i, "spring xxx" + i, "SPRING");
        }
        for (int i=1;i<15;i++) {
            createQuestion(user2, reviewerFlask, "flask" + i, "python xxx" + i, "FLASK");
        }
        for (int i=1;i<15;i++) {
            createQuestion(user1, reviewerJava, "java" + i, "java xxx" + i, "JAVA");
        }

        addComment("댓글1", user1, questionJava1);
        addComment("댓글2", user2, questionJava1);
        addComment("댓글3", user1, questionJava1);

        questionJava1 = addAnswer("답변11", questionJava1, reviewerJava);
        questionJava2 = addAnswer("답변11", questionJava2, reviewerJava);
        questionJava3 = addAnswer("답변222", questionJava3, reviewerJava2);
        questionJava4 = addAnswer("답변333", questionJava4, reviewerJava3);

        questionFlask1  = addAnswer("답변11", questionFlask1, reviewerFlask);
        questionFlask2  = addAnswer("답변22", questionFlask2, reviewerFlask);
        questionFlask3  = addAnswer("답변33", questionFlask3, reviewerFlask2);
        questionFlask4  = addAnswer("답변44", questionFlask4, reviewerFlask3);

        questionSpring1  = addAnswer("답변11", questionSpring1, reviewerSpring);
        questionSpring2  = addAnswer("답변22", questionSpring2, reviewerSpring);
        questionSpring3  = addAnswer("답변33", questionSpring3, reviewerSpring2);
        questionSpring4  = addAnswer("답변44", questionSpring4, reviewerSpring3);

        evaluate(user1, questionJava1.getId(), questionJava1.getAnswer().getId(),4.5);
        evaluate(user1, questionJava2.getId(), questionJava2.getAnswer().getId(),2.0);
        evaluate(user1, questionJava3.getId(), questionJava3.getAnswer().getId(),3.5);
        evaluate(user1, questionJava4.getId(), questionJava4.getAnswer().getId(),2.5);

        evaluate(user2, questionFlask1.getId(), questionFlask1.getAnswer().getId(),1.5);
        evaluate(user2, questionFlask2.getId(), questionFlask2.getAnswer().getId(),2.0);
        evaluate(user2, questionFlask3.getId(), questionFlask3.getAnswer().getId(),2.5);
        evaluate(user2, questionFlask4.getId(), questionFlask4.getAnswer().getId(),3.5);

        evaluate(user1, questionSpring1.getId(), questionSpring1.getAnswer().getId(),4.5);
        evaluate(user1, questionSpring2.getId(), questionSpring2.getAnswer().getId(),4.5);
        evaluate(user1, questionSpring3.getId(), questionSpring3.getAnswer().getId(),5.0);
        evaluate(user1, questionSpring4.getId(), questionSpring4.getAnswer().getId(),2.5);
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
        user.setRanking(new Ranking(0, 0.0, 0.0, 0));
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

    private Question addAnswer(String content, Question question, User reviewer) {
        Answer answer = new Answer(content, 0.0, reviewer);
        question.addAnswer(answer);
        question.setStatus(QuestionStatus.SOLVE);
        reviewer.increaseAnswerCount();
        userRepository.save(reviewer);

        return questionRepository.save(question);
    }

    private void addComment(String content, User user, Question question) {
        Comment comment = new Comment(content, user);
        question.addComment(comment);

        questionRepository.save(question);
    }

    private void evaluate(User user, Long questionId, Long answerId, double point) {
        answerService.evaluateAnswer(user, questionId, answerId, new EvaluateAnswerDto(point));
    }
}
