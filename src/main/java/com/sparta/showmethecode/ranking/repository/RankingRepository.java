package com.sparta.showmethecode.ranking.repository;

import com.sparta.showmethecode.ranking.domain.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RankingRepository extends JpaRepository<Ranking, Long>, RankingQueryRepository {

}
