package springboot.app.springbootcrud.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import springboot.app.springbootcrud.entities.Rol;
import springboot.app.springbootcrud.entities.User;
import springboot.app.springbootcrud.repositories.IRolRepository;
import springboot.app.springbootcrud.repositories.IUserRepository;

@Service
public class UserServiceImpl implements IUserService{

    @Autowired
    private IUserRepository repository;

    @Autowired
    private IRolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return (List<User>) repository.findAll();
    }

    @Override
    @Transactional
    public User save(User user) {
        List<Rol> roles = new ArrayList<>();
        
        // Todos los usuarios son USER
        rolRepository.findByName("ROLE_USER").ifPresent(roles::add);;
            // Guardamos el rol en la lista de roles

        // Verificamos si es admin
        if(user.isAdmin())
            rolRepository.findByName("ROLE_ADMIN").ifPresent(roles::add);
            // Guardamos el rol de admin en la lista de roles

        user.setRoles(roles);
        
        // Password codificado    
        user.setPassword(passwordEncoder.encode(user.getPassword())); 
            // Codificamos el password del user usando PasswordEnconder
            // Le pasamos al User el password codificado

        return repository.save(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

}
