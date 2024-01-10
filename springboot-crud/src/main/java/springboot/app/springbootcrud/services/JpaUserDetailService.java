package springboot.app.springbootcrud.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import springboot.app.springbootcrud.entities.User;
import springboot.app.springbootcrud.repositories.IUserRepository;

// Al ser un componente, automáticamente se configura
@Service
public class JpaUserDetailService implements UserDetailsService{

    @Autowired
    private IUserRepository repository;

    // Genera un User en el contexto Security con los parámetros del User que inicie sesión
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = repository.findByUsername(username);
        if(userOptional.isEmpty()) // Si no esta presente en la bd
            throw new UsernameNotFoundException(String.format("Username %s no existe en la base de datos", username));
        
        User user = userOptional.orElseThrow();

        // Convertimos los roles en una lista de GrantedAuthority
        List<GrantedAuthority> authorities = user.getRoles().stream()
            .map(rol -> new SimpleGrantedAuthority(rol.getName()))
            .collect(Collectors.toList());
        
        return new org.springframework.security.core.userdetails.User(user.getUsername(), 
            user.getPassword(), // La contraseña automáticamente la desencripta y la compara contra la original
            user.isEnabled(), 
            true,
            true,
            true,
            authorities);
    }

}
