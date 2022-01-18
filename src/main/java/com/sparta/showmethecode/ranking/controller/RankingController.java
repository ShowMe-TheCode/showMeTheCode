package com.sparta.showmethecode.ranking.controller;

import com.sparta.showmethecode.common.dto.response.PageResponseDto;
import com.sparta.showmethecode.ranking.dto.response.RankingUserResponseDto;
import com.sparta.showmethecode.ranking.service.RankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/ranking")
@RestController
public class RankingController {

    private final RankingService rankingService;

    @GetMapping
    public ResponseEntity getRanking(
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "2") int size,
            @RequestParam(required = false) String type, @RequestParam(required = false) String query) {
        --page;
        log.info("page={}, size={}, query={}, type={}", page, size, query, type);
        if (Objects.isNull(query)) {
            log.info("query가 null임");
        }

        PageResponseDto result = rankingService.getRankings(page, size, query, type);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/top")
    public ResponseEntity getTop5Ranking() {
        List<RankingUserResponseDto> result = rankingService.getTop5Ranking();
        return ResponseEntity.ok(result);
    }
}
