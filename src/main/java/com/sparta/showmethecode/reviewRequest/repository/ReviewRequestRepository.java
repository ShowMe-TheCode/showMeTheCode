package com.sparta.showmethecode.reviewRequest.repository;

import com.sparta.showmethecode.reviewRequest.domain.ReviewRequest;
import com.sparta.showmethecode.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRequestRepository extends JpaRepository<ReviewRequest, Long>, ReviewRequestDao {

    Page<ReviewRequest> findAll(Pageable pageable);

    Page<ReviewRequest> findByAnswerUser(User answerUser, Pageable pageable);

    // 테스트
    List<ReviewRequest> findByTitle(String title);

    List<ReviewRequest> findByAnswerUser(Long id);
}
