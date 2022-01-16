package com.sparta.showmethecode.ranking.repository;

import com.sparta.showmethecode.ranking.domain.Ranking;

import java.util.List;

public interface RankingQueryRepository {

    List<Ranking> findTop5Reviewer();
}
