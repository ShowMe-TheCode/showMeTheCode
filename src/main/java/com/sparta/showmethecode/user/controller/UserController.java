package com.sparta.showmethecode.user.controller;

import com.sparta.showmethecode.question.domain.QuestionStatus;
import com.sparta.showmethecode.security.UserDetailsImpl;
import com.sparta.showmethecode.user.domain.User;
import com.sparta.showmethecode.user.dto.request.SigninRequestDto;
import com.sparta.showmethecode.user.dto.request.SignupRequestDto;
import com.sparta.showmethecode.common.dto.response.*;
import com.sparta.showmethecode.user.service.UserService;
import com.sparta.showmethecode.user.dto.response.ReviewerInfoDto;
import com.sparta.showmethecode.user.dto.response.SigninResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<BasicResponseDto> signup(@Valid @RequestBody SignupRequestDto requestDto, Errors error) {

        if (error.hasErrors()) {

            String message = "";

            Map<String, String> errors = new HashMap<>();
            for (FieldError value : error.getFieldErrors()) {
                errors.put(value.getField(), value.getDefaultMessage());
                message = value.getDefaultMessage();
                System.out.println(value.getDefaultMessage());
            }


            BasicResponseDto responseDto = BasicResponseDto.builder()
                    .result("fail").httpStatus(HttpStatus.FORBIDDEN).message(message).build();

            return new ResponseEntity<>(responseDto, HttpStatus.FORBIDDEN);

        }

        userService.saveUser(requestDto);
        BasicResponseDto responseDto = BasicResponseDto.builder()
                .result("success").httpStatus(HttpStatus.CREATED).message("??????????????? ??????????????????.").build();


        return ResponseEntity.ok(responseDto);
    }


    @PostMapping("/signin")
    public SigninResponseDto signin(@RequestBody SigninRequestDto requestDto) {

        return userService.signin(requestDto);
    }


    /**
     * ???????????? API
     */
    @PostMapping("/logout")
    public BasicResponseDto logout() {
        SecurityContextHolder.clearContext();

        return new BasicResponseDto(null, "success", "???????????? ??????", HttpStatus.OK);
    }

    /**
     * ?????? ????????? ?????????????????? ?????? API
     */
    @GetMapping("/requests")
    public ResponseEntity<PageResponseDtoV2> getMyRequestList(
            @RequestParam List<QuestionStatus> status, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) Long lastId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        User user = userDetails.getUser();
        PageResponseDtoV2 response = userService.getMyReviewRequestListV2(user, lastId, size, status);

        return ResponseEntity.ok(response);
    }
}
