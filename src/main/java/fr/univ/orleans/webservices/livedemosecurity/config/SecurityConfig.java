package fr.univ.orleans.webservices.livedemosecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
/*
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("fred").password("{noop}fred").roles("USER")
                .and()
                .withUser("admin").password("{noop}admin").roles("USER","ADMIN");
        {bcrypt} for BCryptPasswordEncoder,
        {noop} for NoOpPasswordEncoder,
        {pbkdf2} for Pbkdf2PasswordEncoder,
        {scrypt} for SCryptPasswordEncoder,
        {sha256} for StandardPasswordEncoder.
    }
*/
    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        UserDetails fred = User.builder()
                .username("fred").password("{noop}fred").roles("USER").build();
        UserDetails admin = User.builder()
                .username("admin").password("{noop}admin").roles("USER","ADMIN").build();
        return new InMemoryUserDetailsManager(fred,admin);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET,"/api/messages").permitAll()
                .antMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                .anyRequest().hasRole("USER")
                .and().httpBasic()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
