package com.sparta.showmethecode.ranking.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.showmethecode.language.domain.QLanguage;
import com.sparta.showmethecode.ranking.domain.Ranking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

import static com.sparta.showmethecode.language.domain.QLanguage.language;
import static com.sparta.showmethecode.ranking.domain.QRanking.ranking;
import static com.sparta.showmethecode.user.domain.QUser.user;

public class RankingQueryRepositoryImpl implements RankingQueryRepository {

    private final JPAQueryFactory query;

    public RankingQueryRepositoryImpl(JPAQueryFactory query) {
        this.query = query;
    }

    @Override
    public Page<Ranking> findReviewerRanking(Pageable pageable) {
        List<Ranking> content = query.selectFrom(ranking).distinct()
                .join(ranking.user, user).fetchJoin()
                .orderBy(ranking.average.desc(), ranking.answerCount.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Ranking> jpaQuery = query.selectFrom(ranking).distinct()
                .join(ranking.user, user).fetchJoin();

        return PageableExecutionUtils.getPage(content, pageable, jpaQuery::fetchCount);
    }

    @Override
    public List<Ranking> findTop5Reviewer() {
        return query.selectFrom(ranking).distinct()
                .join(ranking.user, user).fetchJoin()
                .orderBy(ranking.average.desc(), ranking.answerCount.desc())
                .limit(5)
                .fetch();
    }

    @Override
    public Page<Ranking> searchByLanguage(String query, String type, Pageable pageable) {
        List<Ranking> content = this.query.selectFrom(ranking).distinct()
                .join(ranking.user, user).fetchJoin()
                .join(user.languages, language).fetchJoin()
                .where(languageEquals(query))
                .orderBy(ranking.average.desc(), ranking.answerCount.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Ranking> jpaQuery = this.query.selectFrom(ranking).distinct()
                .join(ranking.user, user).fetchJoin()
                .join(user.languages, language).fetchJoin()
                .where(languageEquals(query));

        return PageableExecutionUtils.getPage(content, pageable, jpaQuery::fetchCount);
    }

    @Override
    public Page<Ranking> searchByName(String query, String type, Pageable pageable) {
        List<Ranking> content = this.query.selectFrom(ranking).distinct()
                .join(ranking.user, user).fetchJoin()
                .join(user.languages, language).fetchJoin()
                .where(nicknameContains(query))
                .orderBy(ranking.average.desc(), ranking.answerCount.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Ranking> jpaQuery = this.query.selectFrom(ranking).distinct()
                .join(ranking.user, user).fetchJoin()
                .join(user.languages, language).fetchJoin()
                .where(nicknameContains(query));

        return PageableExecutionUtils.getPage(content, pageable, jpaQuery::fetchCount);
    }

    private BooleanExpression languageEquals(String language) {
        return StringUtils.hasText(language) ? QLanguage.language.name.equalsIgnoreCase(language) : null;
    }

    private BooleanExpression nicknameContains(String nickname) {
        return StringUtils.hasText(nickname) ? user.nickname.containsIgnoreCase(nickname) : null;
    }
}
