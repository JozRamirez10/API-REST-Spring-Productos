package springboot.app.springbootcrud.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
// import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import springboot.app.springbootcrud.validation.ExistByUserName;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @ExistByUserName
    @Column(unique = true)
    @NotBlank
    @Size(min = 4, max = 12)
    private String username;
    /*
     * alter table user add constraint name_unique UNIQUE (username);
     */
    
    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        // Únicamente se puede acceder al password cuando se crea el usuario
        // Es decir, que cuando se lista el usuario en algun json, no se va mostrar la contraseña
    // @JsonIgnore // Ignora el campo siempre que se invoque en un json
        // Pero no permite que sea ni de escritura ni de lectura
    private String password;

    // Se provoca un error ciclico ya que al llamar a users->roles este vuelve a llamar a users:
    // users -> roles -> users... un problema ciclico
    @JsonIgnoreProperties({"users", "handler", "hibernateLazyInitializer"})
        // Indicamos que de la clase Rol ignore el atributo "users"
        // De esta forma queda: users -> roles y evitamos el problema ciclico
        // Indicamos que ignores las demás propiedades por bugs del proxy que pudieran aparecer
    @ManyToMany
    @JoinTable( // Configuración de la tabla intermedia
        name = "users_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id"),
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "rol_id"})}

    )
    private List<Rol> roles;

    private boolean enabled;
        // Habilita si el usuario puede iniciar sesión

    @Transient // Indica que no es un campo que este en la tabla
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean admin;
    
    public User() {
        this.roles = new ArrayList<>();
    }

    @PrePersist
    public void prePersist(){
        this.enabled = true; // Por defecto habilitamos al usuario
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Rol> getRoles() {
        return roles;
    }

    public void setRoles(List<Rol> roles) {
        this.roles = roles;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }
    
}
