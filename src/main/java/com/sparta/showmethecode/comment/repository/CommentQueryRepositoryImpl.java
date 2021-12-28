package com.sparta.showmethecode.comment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.showmethecode.comment.domain.QComment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.sparta.showmethecode.comment.domain.QComment.comment;


@Slf4j
@RequiredArgsConstructor
public class CommentQueryRepositoryImpl implements CommentQueryRepository {

    private final JPAQueryFactory query;


    @Override
    public long deleteComment(Long userId, Long commentId) {
        return query.delete(comment)
                .where(comment.user.id.eq(userId)
                        .and(comment.id.eq(commentId)))
                .execute();
    }

    @Override
    public boolean isMyComment(Long commentId, Long userId) {

        Integer exist = query.selectOne()
                .from(comment)
                .where(comment.id.eq(commentId)
                        .and(comment.user.id.eq(userId)))
                .fetchFirst();

        return exist != null;
    }
}
