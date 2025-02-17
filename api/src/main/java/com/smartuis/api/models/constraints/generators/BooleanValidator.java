package com.smartuis.api.models.constraints.generators;

import com.smartuis.api.models.generators.BooleanGenerator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Constraint(validatedBy = BooleanValidator.Validator.class)
public @interface BooleanValidator {

    String message() default "boolean: the generator must be valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements jakarta.validation.ConstraintValidator<BooleanValidator, BooleanGenerator> {

        public static final Double MIN_PROBABILITY = 0.0;
        public static final Double MAX_PROBABILITY = 1.0;

        public static final String PROBABILITY_RANGE_ERROR_MESSAGE = "The probability must be between 0.0 and 1.0.";


        @Override
        public boolean isValid(BooleanGenerator aBoolean, jakarta.validation.ConstraintValidatorContext context) {
            if (aBoolean.probability() == null || aBoolean.name() == null || aBoolean.name().isBlank() || aBoolean.probability().isNaN()) {
                return false;
            }

            if (aBoolean.probability() < MIN_PROBABILITY || aBoolean.probability() > MAX_PROBABILITY) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(PROBABILITY_RANGE_ERROR_MESSAGE).addConstraintViolation();
                return false;
            }

            return true;
        }
    }
}
