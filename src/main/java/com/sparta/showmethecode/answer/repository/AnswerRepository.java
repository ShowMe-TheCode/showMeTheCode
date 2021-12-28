package com.sparta.showmethecode.answer.repository;

import com.sparta.showmethecode.answer.domain.Answer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long>, AnswerQueryRepository {

}
