package springboot.app.springbootcrud.security.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import springboot.app.springbootcrud.entities.User;

import static springboot.app.springbootcrud.security.TokenJwtConfig.*;

// Por defecto la autenticación se lleva a cabo en la ruta: "/login"

// Esta clase sirve para autenticar al usuario, si se válida correctamente genera un 
    // token y lo devuelve al cliente
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
    
    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    // Intento de autenticación
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        User user = null;
        String username = null;
        String password = null;

        try {
            user = new ObjectMapper().readValue(request.getInputStream(), User.class);
                // Obtiene al usuario que este dentro del request
                // Mapea el objeto obtenido del request a tipo User
            username = user.getUsername();
            password = user.getPassword();
        } catch (StreamReadException e) {
            e.printStackTrace();
        } catch (DatabindException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        UsernamePasswordAuthenticationToken authenticationToken = 
            new UsernamePasswordAuthenticationToken(username, password);
            // Transforma al usuario autenticado en token
        return authenticationManager.authenticate(authenticationToken);
    }

    // Cuando la autenticación es éxitosa
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authResult.getPrincipal(); 
            // De la autenticación se recibe el objeto y se convierte en User de spring.security
        Collection<? extends GrantedAuthority> roles = authResult.getAuthorities(); // Obtiene los roles
            // De cualquier tipo que herede de GrantedAuthority
        
        Claims claims = Jwts.claims() // Genera un claim personalizado
            .add("authorities", new ObjectMapper().writeValueAsString(roles)) // Agregamos los roles al claims
                // Convertimos el roles en ObjectMapper, es decir, a json
            .add("username", user.getUsername()) // Agregamos el username al claims
            .build();  
            

        String token = Jwts.builder() // Toda la información que lleva dentro el token se conoce como "claims"
            .subject(user.getUsername()) // Obtiene el username
            .claims(claims) // Le pasamos los roles al token
            .expiration(new Date(System.currentTimeMillis() + 3600000)) // La sesión expira después de 1 hora
            .issuedAt(new Date()) // Fecha en que se creó el token
            .signWith(SECRET_KEY) // Firma el token usando la SECRET_KEY
            .compact();
            // Genera un token con el username y la SECRET_kEY
        
        response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + token);
        Map<String, String> body = new HashMap<>();
        body.put("token", token);
        body.put("username", user.getUsername());
        body.put("message", String.format("%s has iniciado sesión con éxito", user.getUsername()));
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setContentType(CONTENT_TYPE);
        response.setStatus(200);
    }

    // Cuando la autenticación falla
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {
        Map<String, String> body = new HashMap<>();
        body.put("message", "Error en la autenticación: username o password incorrectos");
        body.put("error", failed.getMessage());
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(401);
        response.setContentType(CONTENT_TYPE);
    }

}
