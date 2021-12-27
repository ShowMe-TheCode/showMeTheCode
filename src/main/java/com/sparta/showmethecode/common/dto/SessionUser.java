package com.sparta.showmethecode.common.dto;

import com.sparta.showmethecode.user.domain.User;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {
    private String name;
    private String email;

    public SessionUser(User user){
        this.name = user.getNickname();
        this.email = user.getUsername();
    }
}
