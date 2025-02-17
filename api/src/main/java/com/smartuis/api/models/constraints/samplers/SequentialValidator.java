package com.smartuis.api.models.constraints.samplers;

import com.smartuis.api.models.samplers.SequentialSampler;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Constraint(validatedBy = SequentialValidator.Validator.class)
public @interface SequentialValidator {


    String message() default "sequential: the samplers must be valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<SequentialValidator, SequentialSampler> {

        @Override
        public boolean isValid(SequentialSampler sequential, ConstraintValidatorContext context) {

            return sequential.steps() != null && !sequential.steps().isEmpty();

        }

    }
}
