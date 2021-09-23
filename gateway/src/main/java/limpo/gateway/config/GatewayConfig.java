package limpo.gateway.config;

import limpo.gateway.filter.AuthFilter;
import limpo.gateway.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class GatewayConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AuthFilter filter;


    @Autowired
    private JwtUserDetailsService userDetailService;


    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable().cors().and()
                .authorizeRequests()
                .antMatchers("order-service/api/v1/orders").access("hasRole('ADMIN')")
                .antMatchers("/api/v1/order-service/clients/*","/api/v1/order-service/limpoUnits/*").permitAll()
                .antMatchers(HttpMethod.PUT,"/api/v1/order-service/clients/*").permitAll()
                .antMatchers(HttpMethod.GET,"/api/v1/order-service/limpoUnits/*").permitAll()
                .antMatchers("/auth-service/api/**").permitAll()
                .antMatchers("/os/api/**").permitAll()
                .and()
                .exceptionHandling()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }


}
