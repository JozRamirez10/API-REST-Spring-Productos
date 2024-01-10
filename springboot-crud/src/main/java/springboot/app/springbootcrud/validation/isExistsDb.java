package springboot.app.springbootcrud.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = isExistsDbValidation.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface isExistsDb {
    String message() default "ya existe en la bd";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };
}
