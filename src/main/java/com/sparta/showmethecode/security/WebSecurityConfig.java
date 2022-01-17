package com.sparta.showmethecode.security;

import com.sparta.showmethecode.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

@RequiredArgsConstructor
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final String USER = "USER";
    private final String REVIEWER = "REVIEWER";

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .cors()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .httpBasic().disable()
                .formLogin().disable()

                .authorizeRequests()
                .antMatchers(
                        "/",
                        "/static/**",
                        "/favicon.ico"
                ).permitAll()
                .antMatchers(HttpMethod.POST, "/questions/**").hasAnyRole(USER, REVIEWER)
                .antMatchers(HttpMethod.PUT, "/questions/**").hasAnyRole(USER, REVIEWER)
                .antMatchers(HttpMethod.DELETE, "/questions/**").hasAnyRole(USER, REVIEWER)

                .antMatchers(HttpMethod.GET, "/users/requests").hasAnyRole(USER, REVIEWER)

                .antMatchers("/reviewers/answers", "/reviewers/questions").hasRole(REVIEWER)
                .antMatchers("/reviewers/language").hasAnyRole(USER, REVIEWER)

                .antMatchers(HttpMethod.GET, "/subscribe/**").hasAnyRole(USER, REVIEWER)
                .antMatchers(HttpMethod.GET, "/notifications/**").hasAnyRole(USER, REVIEWER)

                .antMatchers(HttpMethod.POST, "/comments/**").hasAnyRole(USER, REVIEWER)
                .antMatchers(HttpMethod.DELETE, "/comments/**").hasAnyRole(USER, REVIEWER)
                .antMatchers(HttpMethod.PUT, "/comments/**").hasAnyRole(USER, REVIEWER)


                .antMatchers(HttpMethod.POST, "/answers/eval/**").hasAnyRole(USER, REVIEWER)
                .antMatchers(HttpMethod.POST, "/answers/**").hasRole(REVIEWER)
                .antMatchers(HttpMethod.POST, "/answers/**").hasRole(REVIEWER)
                .antMatchers(HttpMethod.PUT, "/answers/**").hasRole(REVIEWER)

                .anyRequest().permitAll();

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    //더블슬래쉬 허용?
    @Bean public HttpFirewall defaultHttpFirewall() { return new DefaultHttpFirewall(); }

}
