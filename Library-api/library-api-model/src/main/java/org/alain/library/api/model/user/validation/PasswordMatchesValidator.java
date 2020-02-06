package org.alain.library.api.model.user.validation;

import org.alain.library.api.model.user.User;
import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        User user = (User) o;
        if (user.getId()==null) {
            if (user.getPassword() == null) {
                return false;
            }
            return BCrypt.checkpw(user.getPasswordConfirmation(), user.getPassword());
        }
        return true;
    }
}
