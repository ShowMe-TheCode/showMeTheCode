package com.sparta.showmethecode.comment.domain;

import com.sparta.showmethecode.language.domain.Timestamped;
import com.sparta.showmethecode.comment.dto.request.UpdateCommentDto;
import com.sparta.showmethecode.reviewRequest.domain.ReviewRequest;
import com.sparta.showmethecode.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 리뷰 요청서에 달리는 댓글
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class ReviewRequestComment extends Timestamped {

    @Id
    @GeneratedValue
    private Long id;

    @Lob
    @Column(nullable = false)
    private String content;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "review_request_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ReviewRequest reviewRequest;

    public void setReviewRequest(ReviewRequest reviewRequest) {
        this.reviewRequest = reviewRequest;
    }

    public ReviewRequestComment(String content, User user) {
        this.content = content;
        this.user = user;
    }

    public void update(UpdateCommentDto dto) {
        this.content = dto.getContent();
    }
}
