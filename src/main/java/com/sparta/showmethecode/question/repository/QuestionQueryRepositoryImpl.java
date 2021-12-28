package com.sparta.showmethecode.question.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.showmethecode.answer.domain.Answer;
import com.sparta.showmethecode.answer.dto.response.ReviewAnswerResponseDto;
import com.sparta.showmethecode.comment.dto.response.CommentResponseDto;
import com.sparta.showmethecode.common.repository.OrderByNull;
import com.sparta.showmethecode.language.dto.response.ReviewRequestLanguageCount;
import com.sparta.showmethecode.question.domain.Question;
import com.sparta.showmethecode.question.domain.QuestionStatus;
import com.sparta.showmethecode.question.dto.response.*;
import com.sparta.showmethecode.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.querydsl.core.types.ExpressionUtils.count;
import static com.sparta.showmethecode.answer.domain.QAnswer.answer;
import static com.sparta.showmethecode.comment.domain.QComment.comment;
import static com.sparta.showmethecode.question.domain.QQuestion.question;
import static com.sparta.showmethecode.user.domain.QUser.user;


@Slf4j
public class QuestionQueryRepositoryImpl extends QuerydslRepositorySupport implements QuestionQueryRepository {

    private final EntityManager em;
    private final JPAQueryFactory query;

    public QuestionQueryRepositoryImpl(JPAQueryFactory jpaQueryFactory, EntityManager entityManager) {
        super(Question.class);
        this.em = entityManager;
        this.query = jpaQueryFactory;
    }

    @Override
    public Page<ReviewRequestResponseDto> findReviewRequestList(Pageable pageable, boolean isAsc, QuestionStatus status) {

        JPAQuery<ReviewRequestResponseDto> jpaQuery = query.select(new QReviewRequestResponseDto(
                        question.id,
                        question.requestUser.username,
                        question.requestUser.nickname,
                        question.title,
                        question.content,
                        question.languageName,
                        question.status,
                        question.createdAt,
                        ExpressionUtils.as(
                                JPAExpressions.select(comment.id.count())
                                        .from(comment)
                                        .where(comment.question.eq(question)), "commentCount")
                ))
                .where(statusEqual(status))
                .from(question);

        JPQLQuery<ReviewRequestResponseDto> pagination = getQuerydsl().applyPagination(pageable, jpaQuery);

        long totalCount = pagination.fetchCount();
        return new PageImpl<>(pagination.fetch(), pageable, totalCount);
    }

    @Override
    public Page<ReviewRequestResponseDto> findSearchByTitleOrCommentAdvanced(String keyword, Pageable pageable, boolean isAsc, QuestionStatus status) {

        List<ReviewRequestResponseDto> results = query
                .select(new QReviewRequestResponseDto(
                        question.id,
                        user.username,
                        user.nickname,
                        question.title,
                        question.content,
                        question.languageName,
                        question.status,
                        question.createdAt,
                        ExpressionUtils.as(JPAExpressions.select(count(comment.id))
                                .from(comment)
                                .where(comment.question.eq(question)), "commentCount")
                        )
                )
                .from(question)
                .join(question.requestUser, user)
                .where(containingTitleOrComment(keyword))
                .where(statusEqual(status))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(isAsc ? question.createdAt.desc() : question.createdAt.asc())
                .fetch();

        JPAQuery<ReviewRequestResponseDto> jpaQuery = query
                .select(new QReviewRequestResponseDto(
                        question.id,
                        user.username,
                        user.nickname,
                        question.title,
                        question.content,
                        question.languageName,
                        question.status,
                        question.createdAt,
                        ExpressionUtils.as(JPAExpressions.select(count(comment.id))
                                .from(comment)
                                .where(comment.question.eq(question)), "commentCount")
                        )
                )
                .from(question)
                .join(question.requestUser, user).fetchJoin()
                .where(containingTitleOrComment(keyword));

        return PageableExecutionUtils.getPage(results, pageable, jpaQuery::fetchCount);
    }

    @Override
    public Page<ReviewRequestResponseDto> findSearchByTitleOrComment(String keyword, Pageable pageable) {

        QueryResults<ReviewRequestResponseDto> results
                = query.select(new QReviewRequestResponseDto(
                        question.id,
                        user.username,
                        user.nickname,
                        question.title,
                        question.content,
                        question.languageName,
                        question.status,
                        question.createdAt,
                        ExpressionUtils.as(JPAExpressions.select(count(comment.id))
                        .from(comment)
                        .where(comment.question.eq(question)), "commentCount")
                 ))
                .from(question)
                .join(question.requestUser, user).fetchJoin()
                .where(containingTitleOrComment(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<ReviewRequestResponseDto> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public ReviewRequestDetailResponseDto getReviewRequestDetails(Long id) {

        Question result = query.select(question)
                .from(question)
                .join(question.requestUser, user).fetchJoin()
                .leftJoin(question.answer, answer).fetchJoin()
                .leftJoin(question.comments, comment).fetchJoin()
                .where(question.id.eq(id))
                .fetchFirst();

        List<CommentResponseDto> comments = new ArrayList<>();

        if (result.hasComments()) {
            comments = result.getComments().stream().map(
                    c -> new CommentResponseDto(c.getId(), c.getUser().getId(), c.getUser().getUsername(), c.getUser().getNickname(), c.getContent(), c.getCreatedAt())
            ).collect(Collectors.toList());
        }

        for (CommentResponseDto comment : comments) {
            System.out.println(comment);
        }


        Answer answer = result.getAnswer();
        if (!Objects.isNull(answer)) {
            ReviewAnswerResponseDto reviewAnswerResponseDto = new ReviewAnswerResponseDto(
                    answer.getId(),
                    result.getId(),
                    answer.getAnswerUser().getUsername(),
                    answer.getAnswerUser().getNickname(),
                    answer.getContent(),
                    answer.getPoint(),
                    answer.getCreatedAt()
            );
            return new ReviewRequestDetailResponseDto(
                    result.getId(), result.getAnswerUser().getId(),
                    result.getRequestUser().getUsername(), result.getRequestUser().getNickname(),
                    result.getTitle(), result.getContent(),
                    result.getStatus(), result.getLanguageName(), result.getCreatedAt(),
                    comments,
                    reviewAnswerResponseDto
            );
        }
        return new ReviewRequestDetailResponseDto(
                result.getId(), result.getAnswerUser().getId(),
                result.getRequestUser().getUsername(),  result.getRequestUser().getNickname(),
                result.getTitle(), result.getContent(),
                result.getStatus(), result.getLanguageName(), result.getCreatedAt(),
                comments
        );
    }

    @Override
    public List<ReviewRequestLanguageCount> getReviewRequestLanguageCountGroupByLanguage() {
        List<Tuple> result = query.select(question.languageName, question.id.count())
                .from(question)
                .groupBy(question.languageName)
                .orderBy(OrderByNull.DEFAULT)
                .fetch();

        return result.stream().map(
                r -> new ReviewRequestLanguageCount(r.get(0, String.class), r.get(1, Long.class))
        ).collect(Collectors.toList());
    }

    @Override
    public Page<ReviewRequestResponseDto> findMyReviewRequestList(Long id, Pageable pageable, QuestionStatus status) {

        JPAQuery<ReviewRequestResponseDto> jpaQuery = query
                .select(new QReviewRequestResponseDto(
                        question.id,
                                user.username,
                                user.nickname,
                        question.title,
                        question.content,
                        question.languageName,
                        question.status,
                        question.createdAt,
                                ExpressionUtils.as(JPAExpressions.select(count(comment.id))
                                        .from(comment)
                                        .where(comment.question.eq(question)), "commentCount")
                        )
                )
                .from(question)
                .join(question.requestUser, user)
                .where(user.id.eq(id))
                .where(statusEqual(status))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        JPQLQuery<ReviewRequestResponseDto> pagination = getQuerydsl().applyPagination(pageable, jpaQuery);

        long totalCount = pagination.fetchCount();
        return new PageImpl<>(pagination.fetch(), pageable, totalCount);
    }

    @Override
    public Page<ReviewRequestResponseDto> findMyReceivedRequestList(Long id, Pageable pageable, QuestionStatus status) {
        JPAQuery<ReviewRequestResponseDto> jpaQuery = query
                .select(new QReviewRequestResponseDto(
                        question.id,
                                user.username,
                                user.nickname,
                        question.title,
                        question.content,
                        question.languageName,
                        question.status,
                        question.createdAt,
                                ExpressionUtils.as(JPAExpressions.select(count(comment.id))
                                        .from(comment)
                                        .where(comment.question.eq(question)), "commentCount")
                        )
                )
                .from(question)
                .join(question.answerUser, user)
                .where(user.id.eq(id))
                .where(statusEqual(status))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());


        JPQLQuery<ReviewRequestResponseDto> pagination = getQuerydsl().applyPagination(pageable, jpaQuery);
        long totalCount = pagination.fetchCount();

        return new PageImpl<>(pagination.fetch(), pageable, totalCount);
    }

    @Override
    public boolean isMyReviewRequest(Long reviewId, User user) {
        Integer exist = query.selectOne()
                .from(question)
                .where(question.id.eq(reviewId).and(question.requestUser.eq(user)))
                .fetchFirst();

        return exist != null;
    }

    @Override
    public boolean isRequestedToMe(Long reviewId, User reviewer) {
        Integer exist = query.selectOne()
                .from(question)
                .where(question.id.eq(reviewId).and(question.answerUser.eq(reviewer)))
                .fetchFirst();

        return exist != null;
    }

    @Override
    public boolean isAnswerToMe(Long answerId, User user) {
        Integer exist = query.selectOne()
                .from(question)
                .where(question.answer.id.eq(answerId).and(question.requestUser.id.eq(user.getId())))
                .fetchFirst();

        return exist != null;
    }

    @Override
    public Page<ReviewRequestResponseDto> searchRequestByLanguageName(String languageName, Pageable pageable, boolean isAsc) {

        JPAQuery<ReviewRequestResponseDto> jpaQuery = query
                .select(new QReviewRequestResponseDto(
                        question.id,
                                user.username,
                                user.nickname,
                        question.title,
                        question.content,
                        question.languageName,
                        question.status,
                        question.createdAt,
                                ExpressionUtils.as(JPAExpressions.select(count(comment.id))
                                        .from(comment)
                                        .where(comment.question.eq(question)), "commentCount")
                        )
                )
                .from(question)
                .join(question.requestUser, user)
                .where(question.languageName.eq(languageName));

        List<ReviewRequestResponseDto> result = getQuerydsl().applyPagination(pageable, jpaQuery).fetch();

        return PageableExecutionUtils.getPage(result, pageable, jpaQuery::fetchCount);
    }

    @Override
    public void deleteComment(Long reviewId, Long commentId, Long userId) {
        query.delete(question)
                .where(question.id.eq(reviewId));
    }

    @Override
    public RequestAndAnswerResponseDto findReviewRequestAndAnswer(Long id) {

        return query.select(
                        new QRequestAndAnswerResponseDto(
                                question.id, question.requestUser.username,
                                question.title, question.content, question.status, question.createdAt,
                                question.answer.id, question.answer.content
                        )
                ).from(question)
                .join(question.requestUser, user)
                .leftJoin(question.answer, answer)
                .where(question.id.eq(id))
                .fetchOne();
    }

    private BooleanExpression containingTitleOrComment(String keyword) {
        return Objects.isNull(keyword) || keyword.isEmpty() ? null : question.title.contains(keyword).or(question.content.contains(keyword));
    }

    private BooleanExpression statusEqual(QuestionStatus status) {
        return !Objects.isNull(status) && !status.equals(QuestionStatus.ALL) ? question.status.eq(status) : null;
    }
}
