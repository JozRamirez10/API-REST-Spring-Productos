package springboot.app.springbootcrud.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import springboot.app.springbootcrud.services.IUserService;

@Component
public class ExistByUserNameValidation implements ConstraintValidator<ExistByUserName, String>{

    @Autowired
    private IUserService service;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if(service ==  null)
            return true;
        return !service.existsByUsername(username); 
    }

}
