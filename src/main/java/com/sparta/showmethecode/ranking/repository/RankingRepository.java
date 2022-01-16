package com.sparta.showmethecode.ranking.repository;

import com.sparta.showmethecode.ranking.domain.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RankingRepository extends JpaRepository<Ranking, Long>, RankingQueryRepository {


}
