package com.sparta.showmethecode.ranking.service;

import com.sparta.showmethecode.common.dto.response.PageResponseDto;
import com.sparta.showmethecode.ranking.domain.Ranking;
import com.sparta.showmethecode.ranking.dto.response.RankingUserResponseDto;
import com.sparta.showmethecode.ranking.repository.RankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RankingService {

    private final RankingRepository rankingRepository;

    public PageResponseDto getRankings(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Ranking> result = rankingRepository.findReviewerRanking(pageable);

        return new PageResponseDto(result);
    }

    public List<RankingUserResponseDto> getTop5Ranking() {
        List<Ranking> rankings = rankingRepository.findTop5Reviewer();

        return rankings.stream().map(
                ranking -> new RankingUserResponseDto(ranking)
        ).collect(Collectors.toList());
    }
}
