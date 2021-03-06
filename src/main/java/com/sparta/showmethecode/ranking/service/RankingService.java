package com.sparta.showmethecode.ranking.service;

import com.sparta.showmethecode.common.dto.response.PageResponseDto;
import com.sparta.showmethecode.ranking.domain.Ranking;
import com.sparta.showmethecode.ranking.dto.response.RankingUserResponseDto;
import com.sparta.showmethecode.ranking.repository.RankingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RankingService {

    private final RankingRepository rankingRepository;

    public PageResponseDto getRankings(int page, int size, String query, String type) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Ranking> result;
        if (!Objects.isNull(query) && StringUtils.hasText(query)) {
            if (type.equals("language")) {
                log.info("searchType(Language)={}, query={}", type, query);
                result = rankingRepository.searchByLanguage(query, type, pageable);
            }
            else {
                log.info("searchType(Name)={}, query={}", type, query);
                result = rankingRepository.searchByName(query, type, pageable);
            }
        } else {
            result = rankingRepository.findReviewerRanking(pageable);
        }

        List<RankingUserResponseDto> collect = result.stream().map(r -> new RankingUserResponseDto(r)).collect(Collectors.toList());
        return new PageResponseDto(collect, result.getTotalPages(), result.getTotalElements(), result.getNumber(), result.getSize());
    }

    public List<RankingUserResponseDto> getTop5Ranking() {
        List<Ranking> rankings = rankingRepository.findTop5Reviewer();

        return rankings.stream().map(
                ranking -> new RankingUserResponseDto(ranking)
        ).collect(Collectors.toList());
    }
}
