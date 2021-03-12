package de.idealo.sso.codingchallenge.mvc.security;

import static de.idealo.sso.codingchallenge.mvc.Consts.CONVERTER_URL;
import static de.idealo.sso.codingchallenge.mvc.Consts.LOGIN_URL;
import static de.idealo.sso.codingchallenge.mvc.Consts.REGISTER_URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.inject.Inject;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Inject
    ConverterUserDetailsServices converterUserDetailsServices;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers("/", "/" + LOGIN_URL, "/" + REGISTER_URL, "/rest/*").permitAll()
                .antMatchers(HttpMethod.POST, "/rest/*").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/" + LOGIN_URL)
                .successForwardUrl("/" + CONVERTER_URL)
                .permitAll()
                .and()
            .logout()
                .permitAll();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(converterUserDetailsServices).passwordEncoder(passwordEncoder());
    }
}
