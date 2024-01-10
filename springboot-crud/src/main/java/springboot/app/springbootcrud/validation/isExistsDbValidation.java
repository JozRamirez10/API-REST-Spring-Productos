package springboot.app.springbootcrud.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import springboot.app.springbootcrud.services.IProductService;

@Component
public class isExistsDbValidation implements ConstraintValidator<isExistsDb, String>{

    @Autowired
    private IProductService service;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(service ==  null)
            return true;
        return !service.existsBySku(value);
    }

}
