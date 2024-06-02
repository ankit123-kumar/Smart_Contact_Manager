package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class MyConfig {
	@Bean
	public UserDetailsService getUserDetailService() {
	
		return  new UserDetailsServiceImple();
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	public DaoAuthenticationProvider authenticationProvider() {
		
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(getUserDetailService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		
		return daoAuthenticationProvider;
	}
	
	
	// Expose AuthenticationManager as a Bean
    @Bean
    public DaoAuthenticationProvider authenticationManagerBean() throws Exception {
        return this.authenticationProvider();
    }
   
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasRole("USER")
                .requestMatchers("/**")
                .permitAll()
                .and()
                .formLogin()
                .loginPage("/signin")
                .loginProcessingUrl("/dologin")
                .defaultSuccessUrl("/user/index")
                //.failureUrl("/login-fail")
                .and()
                .csrf()
                .disable();
        http.authenticationProvider(authenticationProvider());
        
        return http.build();
    }

	
	/*
	 * public SecurityFilterChain securityFilterChain(HttpSecurity http)throws
	 * Exception { http.authorizeRequests()
	 * .requestMatchers("/admin/**").hasRole("ADMIN")
	 * .requestMatchers("/user/**").hasRole("USER")
	 * .requestMatchers("/").permitAll() .and() .formLogin()
	 * .and().csrf().disable();
	 * 
	 * }
	 */
}