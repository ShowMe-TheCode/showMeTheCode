package com.sparta.showmethecode.reviewAnswer.repository;

import com.sparta.showmethecode.reviewAnswer.domain.ReviewAnswer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewAnswerRepository extends JpaRepository<ReviewAnswer, Long>, ReviewAnswerDao {

}
