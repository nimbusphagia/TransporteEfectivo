package pe.cibertec.transportes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private static final String[] RECURSOS = { "/css/**", "/js/**", "/img/**", "/favicon.ico" };

  private static final String[] PUBLICAS = {
      "/", "/login", "/registro",
      "/lineas/**", "/paraderos/**",
      "/alertas", "/buscar", "/ubicacion/**",
      "/error", "/error/**"
  };

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(RECURSOS).permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/perfil", "/perfil/**").authenticated()
            .requestMatchers("/reportes/**").hasRole("PASAJERO")
            .requestMatchers(PUBLICAS).permitAll()
            .anyRequest().authenticated())
        .formLogin(form -> form
            .loginPage("/login")
            .usernameParameter("correo")
            .passwordParameter("password")
            .defaultSuccessUrl("/", true)
            .failureUrl("/login?error")
            .permitAll())
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout")
            .permitAll());
    return http.build();
  }
}
