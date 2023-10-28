package com.kakaoseventeen.dogwalking._core.security;

import com.kakaoseventeen.dogwalking._core.security.handler.CustomAccessDeniedHandler;
import com.kakaoseventeen.dogwalking._core.security.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

	private final JwtProvider jwtProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    public class CustomSecurityFilterManager extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) throws Exception {
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
            builder.addFilter(new JwtAuthenticationFilter(authenticationManager, jwtProvider));
            super.configure(builder);
        }
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        //csrf disable
        http.csrf(AbstractHttpConfigurer::disable);
        //form login disable
        http.formLogin(AbstractHttpConfigurer::disable);
        //iframe disable
        http.headers((headers) -> headers.frameOptions((frameOptions) -> frameOptions.disable()));
        // Session 기반의 인증기반을 사용하지 않고 jwt 인증방식 사용
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );
        //cors 재설정
        http.cors(corsCustomizer -> corsCustomizer.configurationSource(configurationSource()));
        //로그인 인증창 뜨지 않게 비활성화
        http.httpBasic(AbstractHttpConfigurer::disable);
        //커스텀 필터 적용
        http.apply(new CustomSecurityFilterManager());
        //토큰을 활용하는 경우 아래 요청에 대해 '인가'에 대해서 사용.

        http.authorizeHttpRequests(authorize ->
                authorize
                        .requestMatchers(new AntPathRequestMatcher("/api/member/**"), new AntPathRequestMatcher("/api/home"), new AntPathRequestMatcher("/init"))
                        .permitAll()
                        .anyRequest().authenticated()
        );

        http.exceptionHandling(exceptionHandling->
                exceptionHandling.authenticationEntryPoint(new CustomAuthenticationEntryPoint()).accessDeniedHandler(new CustomAccessDeniedHandler())
        );



        return http.build();
    }

    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*"); // GET, POST, PUT, DELETE
        configuration.addAllowedOriginPattern("*"); // 모든 IP 주소 허용
        configuration.setAllowCredentials(true); // 클라이언트에서 쿠키 요청 허용
        configuration.addExposedHeader("Authorization");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}
