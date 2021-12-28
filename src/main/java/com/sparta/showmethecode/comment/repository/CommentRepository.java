package com.sparta.showmethecode.comment.repository;

import com.sparta.showmethecode.comment.domain.Comment;
import com.sparta.showmethecode.question.domain.Question;
import com.sparta.showmethecode.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentQueryRepository {

    Comment findByIdAndUser(Long id, User user);
    void deleteByQuestionAndUser(Question question, User user);
}
