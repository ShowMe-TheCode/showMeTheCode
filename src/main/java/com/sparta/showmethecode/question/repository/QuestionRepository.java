package com.sparta.showmethecode.question.repository;

import com.sparta.showmethecode.question.domain.Question;
import com.sparta.showmethecode.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long>, QuestionQueryRepository {

    Page<Question> findAll(Pageable pageable);

    Page<Question> findByAnswerUser(User answerUser, Pageable pageable);

    // 테스트
    List<Question> findByTitle(String title);

    List<Question> findByAnswerUser(Long id);
}
