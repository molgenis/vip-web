package org.molgenis.vipweb.security;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipweb.VipWebProperties;
import org.molgenis.vipweb.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableJdbcAuditing
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final ApiAuthenticationSuccessHandler apiAuthenticationSuccessHandler;
    private final ApiAuthenticationFailureHandler apiAuthenticationFailureHandler;
    private final VipWebProperties vipWebProperties;
    private final AuthenticationFacade authenticationFacade;

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        String pattern = "/api/**";
        return http.securityMatcher(pattern)
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers(AntPathRequestMatcher.antMatcher(pattern)).permitAll())
                .addFilterAfter(usernamePasswordAuthenticationFilter(), LogoutFilter.class)
                .sessionManagement(
                        sessionManagementConfigurer ->
                                sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .anonymous(
                        anonymousConfigurer ->
                                anonymousConfigurer.principal(RepositoryUserDetailsService.createAnonymousUser()))
                .logout(
                        logoutConfigurer -> {
                            logoutConfigurer.logoutUrl("/api/auth/logout");
                            logoutConfigurer.addLogoutHandler(rememberMeServices());
                            logoutConfigurer.invalidateHttpSession(true);
                            logoutConfigurer.logoutSuccessHandler(logoutSuccessHandler());
                        })
                .authenticationProvider(authenticationProvider())
                .rememberMe(
                        rememberMeConfigurer -> rememberMeConfigurer.rememberMeServices(rememberMeServices()))
                .build();
    }

    @Bean
    public Filter usernamePasswordAuthenticationFilter() {
        UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter =
                new UsernamePasswordAuthenticationFilter(authenticationManager());
        usernamePasswordAuthenticationFilter.setRequiresAuthenticationRequestMatcher(
                AntPathRequestMatcher.antMatcher("/api/auth/login"));
        usernamePasswordAuthenticationFilter.setAllowSessionCreation(false);
        usernamePasswordAuthenticationFilter.setAuthenticationSuccessHandler(
                apiAuthenticationSuccessHandler);
        usernamePasswordAuthenticationFilter.setAuthenticationFailureHandler(
                apiAuthenticationFailureHandler);
        usernamePasswordAuthenticationFilter.setRememberMeServices(rememberMeServices());
        return usernamePasswordAuthenticationFilter;
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new HttpStatusReturningLogoutSuccessHandler();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider =
                new DaoAuthenticationProvider(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }

    @Bean
    public AbstractRememberMeServices rememberMeServices() {
        TokenBasedRememberMeServices tokenBasedRememberMeServices =
                new TokenBasedRememberMeServices(vipWebProperties.rememberme().key(), userDetailsService);
        tokenBasedRememberMeServices.setAlwaysRemember(true);
        tokenBasedRememberMeServices.setUseSecureCookie(true);
        tokenBasedRememberMeServices.setTokenValiditySeconds(Integer.MAX_VALUE);
        return tokenBasedRememberMeServices;
    }

    @Bean
    AuditorAware<AggregateReference<User, Integer>> auditorProvider() {
        return new AuditorAwareImpl(authenticationFacade);
    }
}
