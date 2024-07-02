package pe.shadow.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import pe.shadow.security.UserDetailsServiceImpl;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http
                //configurar es el login
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                )
                //configurar los acceso o permisos a la rutas/urls
                .authorizeHttpRequests((authz)-> authz
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/capas/**", "/capas").authenticated()
                        .anyRequest().permitAll()
                )
                .exceptionHandling(customizer -> customizer.accessDeniedHandler(accessDeniedHandlerApp()))
                .userDetailsService(userDetailsServiceImpl)
                .logout(logout->logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET")).logoutSuccessUrl("/"))
                .csrf().disable();
        return http.build();
    }

    @Bean
    AccessDeniedHandler accessDeniedHandlerApp()
    {
        return (request, response, accessDeniedException) -> {
            response.sendRedirect(request.getContextPath() + "/403");
        };
    }
}
