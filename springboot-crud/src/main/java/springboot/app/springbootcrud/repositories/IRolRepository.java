package springboot.app.springbootcrud.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import springboot.app.springbootcrud.entities.Rol;

public interface IRolRepository extends CrudRepository<Rol, Long>{

    Optional<Rol> findByName(String name);
}
