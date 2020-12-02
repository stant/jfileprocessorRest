/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.boot.server;

/**
 *
 * @author stan
 */
import com.towianski.models.JfpRestURIConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
 
@Configuration
//@DependsOn({"customAuthenticationProvider"})
@EnableWebSecurity
@Profile("server")
public class AuthSecurityConfiguration extends WebSecurityConfigurerAdapter {
 
    private static String REALM="MY_TEST_REALM";
     
//    @Autowired
//    private CustomAuthenticationProvider customAuthenticationProvider;
    
//    @Autowired
//    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication().withUser("bill").password("{noop}abc123").roles("ADMIN");
//        auth.inMemoryAuthentication().withUser("stan").password("{noop}test").roles("USER");
//    }
     
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//  
//      http.csrf().disable()
//        .authorizeRequests()
//        .antMatchers("/user/**").hasRole("ADMIN")
//        .and().httpBasic().realmName(REALM).authenticationEntryPoint(getBasicAuthEntryPoint())
//        .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);//We don't need sessions to be created.
//    }
     

    	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider( new CustomAuthenticationProvider() );
	}

        @Override
	// Authorization : Role -> Access
	protected void configure(HttpSecurity http) throws Exception {

//        List<AuthenticationProvider> authenticationProviders = new ArrayList<AuthenticationProvider>(1);
//        //authenticationProviders.add(rememberMeAuthenticationProvider);
//        authenticationProviders.add(customAuthenticationProvider);
//        AuthenticationManager authenticationManager = authenticationManager(authenticationProviders);

            http.httpBasic().and().authorizeRequests()
                .antMatchers( "/jfp/rest/**" ).permitAll()  //.hasRole("WRITE")
                //.antMatchers( "/jfp/rest/**" ).authenticated()
                .antMatchers( "/jfp/sys/**", JfpRestURIConstants.SEARCH).permitAll()
                .antMatchers("/**").hasRole("ADMIN")
            .and().httpBasic().realmName(REALM).authenticationEntryPoint(getBasicAuthEntryPoint())
            .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)  //;//We don't need sessions to be created.
            .and()
                .csrf().disable().headers().frameOptions().disable();
	}

    @Bean
    public CustomBasicAuthenticationEntryPoint getBasicAuthEntryPoint(){
        return new CustomBasicAuthenticationEntryPoint();
    }
     
    /* To allow Pre-flight [OPTIONS] request from browser */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
    }
}