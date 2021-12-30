package com.sparta.showmethecode.user.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.showmethecode.user.domain.User;
import com.sparta.showmethecode.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.sparta.showmethecode.language.domain.QLanguage.language;
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

    @Override
    public Page<User> getReviewerRanking(Pageable pageable, boolean isAsc) {

        List<User> content = query.select(user)
                .from(user)
                .where(user.role.eq(UserRole.ROLE_REVIEWER).and(user.answerCount.gt(0)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(isAsc ? user.evalTotal.desc() : user.evalTotal.desc())
                .fetch();

        JPAQuery<User> jpaQuery = query.select(user)
                .from(user)
                .where(user.role.eq(UserRole.ROLE_REVIEWER).and(user.evalTotal.gt(0)));


        return PageableExecutionUtils.getPage(content, pageable, jpaQuery::fetchCount);
    }

    @Override
    public List<User> getReviewerRankingTop5() {

        List<User> users = query.select(user)
                .from(user)
                .join(user.languages, language).fetchJoin()
                .where(user.role.eq(UserRole.ROLE_REVIEWER))
                .limit(5)
                .orderBy(user.evalTotal.desc(), user.answerCount.desc())
                .fetch();

        return users;
    }
}
