package com.sparta.showmethecode.comment.repository;

import com.sparta.showmethecode.question.domain.Question;
import com.sparta.showmethecode.comment.domain.ReviewRequestComment;
import com.sparta.showmethecode.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRequestCommentRepository extends JpaRepository<ReviewRequestComment, Long>, ReviewRequestCommentDao {

    ReviewRequestComment findByIdAndUser(Long id, User user);
    void deleteByReviewRequestAndUser(Question question, User user);
}
