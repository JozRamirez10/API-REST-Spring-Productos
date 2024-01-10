package springboot.app.springbootcrud.security.filter;

import static springboot.app.springbootcrud.security.TokenJwtConfig.CONTENT_TYPE;
import static springboot.app.springbootcrud.security.TokenJwtConfig.HEADER_AUTHORIZATION;
import static springboot.app.springbootcrud.security.TokenJwtConfig.PREFIX_TOKEN;
import static springboot.app.springbootcrud.security.TokenJwtConfig.SECRET_KEY;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import springboot.app.springbootcrud.security.SimpleGrantedAuthorityJsonCreator;

public class JwtValidationFilter extends BasicAuthenticationFilter{

    // Es necesario generar este constructor
    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        String header = request.getHeader(HEADER_AUTHORIZATION); // Obtenemos el header del request
        
        if(header == null || !header.startsWith(PREFIX_TOKEN)){ // Si es nulo o no contiene el prefix
            chain.doFilter(request, response);
            return;
        }
        
        String token = header.replace(PREFIX_TOKEN, ""); // Quitamos el prefix del token
        
        try {
            Claims claims = Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();    
            // Verificando con la SECRET_KEY obtiene los claims del token (username, roles, expiración, etc)
            String username = claims.getSubject(); // Obtiene el username
            // String username = claims.get("username"); // Otra forma de obtener el username
            Object authoritiesClaims = claims.get("authorities"); // Obtiene los roles
            Collection<? extends GrantedAuthority> authorities = // Obtiene un Collecion que hereda de GrantedAuthority
                Arrays.asList(new ObjectMapper()
                    .addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class)
                        // El constructor de SimpleGrantedAuthorityJsonCreator se convierte al de SimpleGrantedAuthority
                        // SimpleGrantedAuthority recibe un el rol desde el Json, pero lo busca por ->  "role": ""
                        // Nosotros especificamos en reglas anteriores que el rol en el json tenga el nombre de -> "authority": ""
                        // Por lo que es necesario hacer el mixIn para que dentro del json el "authority" lo valide como "role"
                        // Y pueda ser leído por el constructor de SimpleGrantedAuthority
                    .readValue(authoritiesClaims.toString().getBytes(),  
                        // Mapea el autorithiesClaims a ObjectMapper
                SimpleGrantedAuthority[].class)); // De tipo SimpleGrantedAuthority.class
                    // En esta parte se instancia la clase SimpleGrantedAuthority, por lo que es necesario aplicar el mixIn
                    // para evitar errores

            UsernamePasswordAuthenticationToken authenticationToken = 
                new UsernamePasswordAuthenticationToken(username, null, authorities);
                // Iniciamos sesión autenticando al usuario con el username y los authorities
            SecurityContextHolder .getContext().setAuthentication(authenticationToken);
                // Autenticamos al usuario
            chain.doFilter(request, response);
                // Continua la cadena de filtros 

        } catch (JwtException e) {
            Map<String, String> body = new HashMap<>();
            body.put("error", e.getMessage());
            body.put("message", "El token JWT es inválido");

            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
            response.setStatus(401);
            response.setContentType(CONTENT_TYPE);
        }
                
    }
}
