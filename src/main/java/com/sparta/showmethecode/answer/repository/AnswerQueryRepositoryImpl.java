package com.sparta.showmethecode.answer.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.showmethecode.answer.domain.Answer;
import com.sparta.showmethecode.answer.dto.response.AnswerResponseDto;
import com.sparta.showmethecode.answer.dto.response.QAnswerResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;

import javax.persistence.EntityManager;
import java.util.List;

import static com.sparta.showmethecode.answer.domain.QAnswer.answer;

public class AnswerQueryRepositoryImpl extends QuerydslRepositorySupport implements AnswerQueryRepository {

    private final JPAQueryFactory query;
    private final EntityManager em;

    public AnswerQueryRepositoryImpl(JPAQueryFactory jpaQueryFactory, EntityManager entityManager) {
        super(Answer.class);
        this.query = jpaQueryFactory;
        this.em = entityManager;
    }

    @Override
    public boolean isEvaluated(Long answerId) {
        Integer exist = query.selectOne()
                .from(answer)
                .where(answer.point.gt(0))
                .fetchFirst();

        return exist != null;
    }

    @Override
    public Page<AnswerResponseDto> findMyAnswer(Long userId, Pageable pageable) {
        JPAQuery<AnswerResponseDto> jpaQuery = query.select(
                        new QAnswerResponseDto(
                                answer.id,
                                answer.id,
                                answer.answerUser.username,
                                answer.answerUser.nickname,
                                answer.content,
                                answer.point,
                                answer.createdAt
                        )
                ).from(answer)
                .where(answer.answerUser.id.eq(userId));

        List<AnswerResponseDto> result = getQuerydsl().applyPagination(pageable, jpaQuery).fetch();

        return PageableExecutionUtils.getPage(result, pageable, jpaQuery::fetchCount);
    }

    @Override
    public boolean isMyAnswer(Long reviewerId, Long answerId) {

        Integer exist = query.selectOne()
                .from(answer)
                .where(answer.answerUser.id.eq(reviewerId).
                        and(answer.id.eq(answerId))
                )
                .fetchFirst();

        return exist != null;
    }
}
