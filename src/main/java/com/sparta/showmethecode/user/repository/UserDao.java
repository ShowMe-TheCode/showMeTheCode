package com.sparta.showmethecode.user.repository;

import com.sparta.showmethecode.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserDao {

    // 언어별 리뷰어 조회
    List<User> findReviewerByLanguage(String language);
}
