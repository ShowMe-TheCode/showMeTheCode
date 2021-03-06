package com.sparta.showmethecode.ranking.repository;

import com.sparta.showmethecode.ranking.domain.Ranking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RankingQueryRepository {

    List<Ranking> findTop5Reviewer();

    Page<Ranking> findReviewerRanking(Pageable pageable);

    Page<Ranking> searchByLanguage(String query, String type, Pageable pageable);
    Page<Ranking> searchByName(String query, String type, Pageable pageable);
}
