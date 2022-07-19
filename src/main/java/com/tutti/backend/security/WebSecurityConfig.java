package com.tutti.backend.security;

import com.tutti.backend.security.filter.FormLoginFilter;
import com.tutti.backend.security.filter.JwtAuthFilter;
import com.tutti.backend.security.jwt.HeaderTokenExtractor;
import com.tutti.backend.security.provider.FormLoginAuthProvider;
import com.tutti.backend.security.provider.JWTAuthProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ComponentScan
@EnableWebSecurity // 스프링 Security 지원을 가능하게 함
@EnableGlobalMethodSecurity(securedEnabled = true) // @Secured 어노테이션 활성화
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JWTAuthProvider jwtAuthProvider;
    private final HeaderTokenExtractor headerTokenExtractor;

    private final AuthenticationFailureHandler customFailureHandler;

    public WebSecurityConfig(JWTAuthProvider jwtAuthProvider, HeaderTokenExtractor headerTokenExtractor,
                             AuthenticationFailureHandler customFailureHandler) {
        this.jwtAuthProvider = jwtAuthProvider;
        this.headerTokenExtractor = headerTokenExtractor;
        this.customFailureHandler = customFailureHandler;
    }

    @Bean
    public BCryptPasswordEncoder encodePassword() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) {
        auth
                .authenticationProvider(formLoginAuthProvider())
                .authenticationProvider(jwtAuthProvider);
    }

    @Override
    public void configure(WebSecurity web) {
        // h2-console 사용에 대한 허용 (CSRF, FrameOptions 무시)
        web
                .ignoring()
                .antMatchers("/h2-console/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.headers().frameOptions().disable();

        http.addFilterBefore(formLoginFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);


        http.authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
            .and()
                .cors()
            .and()
                .exceptionHandling()
                // "접근 불가" 페이지 URL 설정
                .accessDeniedPage("/forbidden.html");
    }

    @Bean
    public FormLoginFilter formLoginFilter() throws Exception {
        FormLoginFilter formLoginFilter = new FormLoginFilter(authenticationManager());
        formLoginFilter.setFilterProcessesUrl("/user/login");
        formLoginFilter.setAuthenticationSuccessHandler(formLoginSuccessHandler());
        formLoginFilter.setAuthenticationFailureHandler(formLoginFailHandler());
        formLoginFilter.afterPropertiesSet();
        return formLoginFilter;
    }

    @Bean
    public UserLoginFailHandler formLoginFailHandler(){
        return new UserLoginFailHandler();
    }

    @Bean
    public FormLoginSuccessHandler formLoginSuccessHandler() {
        return new FormLoginSuccessHandler();
    }

    @Bean
    public FormLoginAuthProvider formLoginAuthProvider() {
        return new FormLoginAuthProvider(encodePassword());
    }

    private JwtAuthFilter jwtFilter() throws Exception {
        List<String> skipPathList = new ArrayList<>();

//         h2-console 허용
        skipPathList.add("GET,/h2-console/**");
        skipPathList.add("POST,/h2-console/**");

//         회원 관리 API 허용
        skipPathList.add("GET,/user/profile/**");
        skipPathList.add("POST,/user/signup");
        skipPathList.add("POST,/user/email");
        skipPathList.add("POST,/user/artist");
        skipPathList.add("GET,/user/kakaologin");
        skipPathList.add("GET,/user/googlelogin");
        skipPathList.add("GET,/confirm-email");
        skipPathList.add("GET,/page");
        skipPathList.add("GET,/feeds");
        skipPathList.add("GET,/feeds/**");
        skipPathList.add("GET,/search");
        skipPathList.add("GET,/chat/**");
        skipPathList.add("GET,/user/fetchAllUsers/*");
        skipPathList.add("GET,/user/search/**");
        skipPathList.add("DELETE,/user/leaveChat");
        skipPathList.add("GET,/search/more");
        skipPathList.add("GET,/chatRoom");
        skipPathList.add("GET,/wss/**");
        skipPathList.add("POST,/wss/**");
        skipPathList.add("GET,/ws/**");
        skipPathList.add("POST,/ws/**");
        skipPathList.add("GET,/actuator/**");

        skipPathList.add("POST,/user/login");

        skipPathList.add("GET,/");

        FilterSkipMatcher matcher = new FilterSkipMatcher(skipPathList,"/**");

        JwtAuthFilter filter = new JwtAuthFilter(matcher, headerTokenExtractor);
        filter.setAuthenticationManager(super.authenticationManagerBean());

        return filter;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}