package com.sparta.showmethecode.ranking.controller;

import com.sparta.showmethecode.common.dto.response.PageResponseDto;
import com.sparta.showmethecode.ranking.dto.response.RankingUserResponseDto;
import com.sparta.showmethecode.ranking.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/ranking")
@RestController
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/top")
    public ResponseEntity getTop5Ranking() {
        List<RankingUserResponseDto> result = rankingService.getTop5Ranking();
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity getRanking(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        PageResponseDto result = rankingService.getRankings(page, size);
        return ResponseEntity.ok(result);
    }
}
