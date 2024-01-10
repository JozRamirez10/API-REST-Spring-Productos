package springboot.app.springbootcrud.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import springboot.app.springbootcrud.security.filter.JwtAuthenticationFilter;
import springboot.app.springbootcrud.security.filter.JwtValidationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true) // Anotación necesaria para filtrar 
                // por roles usando anotaciones en los controllers
public class SpringSecurityConfig {
    
    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    AuthenticationManager authenticationManager() throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Ayuda a codificar las contraseñas de los usuarios
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // Administra la configuración de acceso a las rutas
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http.authorizeHttpRequests((authz) -> authz.requestMatchers(HttpMethod.GET, "/api/users").permitAll()
            // Autoriza el acceso a todas las páginas dentro de la ruta '/users' que sean GET
        .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()
            // Autoriza el acceso a '/register' del método POST
        /* // EL CODIGO COMENTADO SE REMPLAZO USANDO ANOTACIONES EN LOS CONTROLLERS
        .requestMatchers(HttpMethod.POST, "/api/users").hasRole("ADMIN")
            // Crear usuarios admins
            // Solo los usuarios "admin" pueden crear más usuarios admin
            // En la bd el rol es -> "ROLE_ADMIN", pero aquí solo se ingresa el "ADMIN"
        .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/{id}").hasAnyRole("ADMIN", "USER")
            // Los users y admin pueden listar los productos en general o por id
        .requestMatchers(HttpMethod.POST, "/api/products").hasRole("ADMIN")
            // Solo admins pueden crear productos
        .requestMatchers(HttpMethod.PUT, "/api/products/{id}").hasRole("ADMIN")
            // Solo admins pueden modificar productos
        .requestMatchers(HttpMethod.DELETE, "/api/products/{id}").hasRole("ADMIN")
            // Solo admins pueden eliminar productos
        */
        .anyRequest().authenticated())
            // Todas las demás rutas neceistan autenticación
        .addFilter(new JwtAuthenticationFilter(authenticationManager()))
            // Usando la clase que configuramos para los filtros, la agregamos al filtro de security
            // de esta forma va autenticar al usuario al iniciar sesion
        .addFilter(new JwtValidationFilter(authenticationManager()))
            // Este filtro válida que el token del usuario 
        .csrf(config -> config.disable())
            // Genera un token de seguridad para evitar vulnerabilidades
            // No se está trabajando con vistas ni con formualarios, por lo tanto se tiene que deshabilitar
        .cors(cors-> cors.configurationSource(corsConfigurationSource()))
            // Le damos la configuración al corns del método de configuración
        .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Cada vez que se hace un request genera un token con una sesión "sin estado"
            // porque no es necesarios guardar la información de session para este caso que solo 
            // se van a crear y listar usuarios
        .build();
    }
    
    // Configuración del corns para permitir acceso a apps de diferente dominio
    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList("*")); // Cualquier dominio
        config.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT"));
            // Metodos permitidos
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
            // Handlers permitidos
        config.setAllowCredentials(true);
            // Permitir credenciales
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
            // Registramos la configuracion del corns en una fuente
        return source;
    }  
    
    @Bean
    FilterRegistrationBean<CorsFilter> corsFilter(){
        FilterRegistrationBean<CorsFilter> corsBean = new FilterRegistrationBean<>(
                new CorsFilter(corsConfigurationSource()));
            // Filtro para el corns
        corsBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
            // Le damos alta prioridad
        return corsBean;
    }
}
