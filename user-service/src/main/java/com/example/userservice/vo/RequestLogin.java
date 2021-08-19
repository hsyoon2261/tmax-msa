package com.example.userservice.vo;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestLogin {
    //사용자 로그인 정보를 저장하기 위한 모델 클래스 추가
    @NotNull(message = "Email cannot be null")
    @Size(min = 2, message = "Email not be less than two characters")
    @Email
    private String email;

    @NotNull(message = "Password cannot be null")
    @Size(min = 8, message = "Password must be equals or grater thatn 8 characters")
    private String password;
}


//본 클래스는 사용자가 전달했던 데이터 값을 , 저장하기 위한 클래스임.