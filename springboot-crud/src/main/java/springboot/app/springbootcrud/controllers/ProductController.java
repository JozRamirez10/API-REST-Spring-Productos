package springboot.app.springbootcrud.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
//import springboot.app.springbootcrud.ProductValidation;
import springboot.app.springbootcrud.entities.Product;
import springboot.app.springbootcrud.services.IProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private IProductService service;

    //@Autowired // Clase que valida los errores
    //private ProductValidation validation;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<Product> list(){
        return service.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> view(@PathVariable Long id){
        Optional<Product> productOptional = service.findById(id);
        if(productOptional.isPresent())
            return ResponseEntity.ok(productOptional.orElseThrow());
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody Product product, BindingResult result){
        //validation.validate(product, result);
        if(result.hasFieldErrors()) // Si ocurrió algun error
            return validation(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(product));
    }

    // El @Valid tiene que ir en el RequestBody y siempre debe ir al comienzo
    // Binding result -> Muestra mensajes de error personalizados -> Debe ir después del @Valid
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@Valid @RequestBody Product product, BindingResult result, @PathVariable Long id){
        //validation.validate(product, result);
        if(result.hasFieldErrors()) // Si ocurrió algun error
            return validation(result);
        Optional<Product> productOptional = service.update(id, product);
        if(productOptional.isPresent())
            return ResponseEntity.status(HttpStatus.CREATED).body(productOptional.orElseThrow());
        return ResponseEntity.notFound().build();
    }   

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id){
        Optional<Product> productOptional = service.delete(id);
        if(productOptional.isPresent())
            return ResponseEntity.ok(productOptional.orElseThrow());
        return ResponseEntity.notFound().build();
    }

    private ResponseEntity<?> validation(BindingResult result){
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(error -> {
            errors.put(error.getField(), "El campo " + error.getField() + " " + error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors); // badRequest == status.BAD_REQUEST
    }
    
}
