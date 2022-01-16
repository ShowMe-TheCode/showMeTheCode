package com.sparta.showmethecode.user.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.showmethecode.user.domain.User;
import com.sparta.showmethecode.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Objects;

import static com.sparta.showmethecode.language.domain.QLanguage.language;
import static com.sparta.showmethecode.question.domain.QQuestion.question;
import static com.sparta.showmethecode.user.domain.QUser.user;

@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    private final JPAQueryFactory query;

    /**
     * 언어이름으로 리뷰어 조회
     */
    @Override
    public List<User> findReviewerByLanguage(String languageName) {

        List<User> result = query.select(user)
                .from(user)
                .join(user.languages, language).fetchJoin()
                .where(user.role.eq(UserRole.ROLE_REVIEWER).and(language.name.eq(languageName)))
                .fetch();

        return result;
    }

    private BooleanExpression IdLessThen(Long id) {
        return !Objects.isNull(id) ? question.id.lt(id) : null;
    }
}
