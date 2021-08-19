package com.example.userservice.security;

import com.example.userservice.service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.context.annotation.Profile;

@Configuration //다른 bean들 보다 우선순위 앞에서 정의된다.
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
    //websecurityconfigureradapter를 상속받는 security configuration 클래스 생성
    private UserService userService;//비즈니스로직
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    //bCryptPasswordEncoder 빈정의
    private Environment env;

    public WebSecurity(Environment env, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.env = env;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //권한 작업을 위해 configure 라는 메소드를 (재)정의한다.
        // 권한에 관련된 부분. 인증(authentication)이 성공을 하면, 어떠한 작업을 할 수 있는지를 명시하는 곳.
        http.csrf().disable();
//        http.authorizeRequests().antMatchers("/users/**").permitAll();
        http.authorizeRequests().antMatchers("/health_check/**").permitAll();
        http.authorizeRequests().antMatchers("/h2-console/**").permitAll();
        http.authorizeRequests().antMatchers("/actuator/**").permitAll();
        http.authorizeRequests().antMatchers("/**")
                .access("hasIpAddress('192.168.219.103') or hasIpAddress('127.0.0.1')")
                .and()
                .addFilter(getAuthenticationFilter()); //5줄 밑 AuthenticationFilter부분과 연동. 필터작업에서 인증처리를 한다 보면 됨.

        http.headers().frameOptions().disable();
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(
                                        authenticationManager(), userService, env);

        return authenticationFilter;
    }

    //인증 관련 작업.
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //configure(AuthenticationManagerBuilder auth) 매서드 재정의
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
        //auth.userDetailService=우리가 정의한 메서드 아님
    }
}
