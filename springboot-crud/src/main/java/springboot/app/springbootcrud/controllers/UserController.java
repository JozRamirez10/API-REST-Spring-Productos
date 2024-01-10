package springboot.app.springbootcrud.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import springboot.app.springbootcrud.entities.User;
import springboot.app.springbootcrud.services.IUserService;

// La seguridad que se incluyó evita que aplicaciones externas puedan acceder a nuestra API
// como Angular, movil, etc. por lo que se incluye la siguiente anotación
    // Postman es un cliente que no representa ningun error ya que esta en el mismo dominio
// @CrossOrigin(originPatterns = "*") // Permite que una aplicación externa consuma nuestra API 
    // originPatterns = "*" // Permite que cualquier dominio pueda acceder a nuestra API
// @CrossOrigin(origins = "http://localhost:4200") //Ejemplo al usar Angular -> Permite que se conecte a la API
    // origins -> Especifica el dominio de la página o servicio que se desea conectar a la API
@CrossOrigin(origins = "http://localhost:4200", originPatterns = "*")
    // Permite ambos casos
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private IUserService service;

    // PermitAll -> No se anota nada
    @GetMapping
    public List<User> list(){
        return service.findAll();
    }
    
    // Agregamos seguridad a las rutas usando anotaciones
    @PreAuthorize("hasRole('ADMIN')") // Solo los admin pueden acceder a esta ruta
    @PostMapping // Método privado (se pueden crear admins)
    public ResponseEntity<?> create(@Valid @RequestBody User user, BindingResult result){
        if(result.hasFieldErrors()) // Si ocurrió algun error
            return validation(result); // Validación
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(user));
    }

    @PostMapping("/register") // Método público (No se pueden crear admins)
    public ResponseEntity<?> register(@Valid @RequestBody User user, BindingResult result){
        user.setAdmin(false); // El usuario no puede ser admin
        return create(user, result);
    }

    // Valida si un campo no se cumple de acuerdo a las anotaciones
    private ResponseEntity<?> validation(BindingResult result){
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(error -> {
            errors.put(error.getField(), "El campo " + error.getField() + " " + error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors); // badRequest == status.BAD_REQUEST
    }

}
