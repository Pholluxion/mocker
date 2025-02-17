package com.smartuis.api.models.constraints.samplers;

import com.smartuis.api.models.samplers.StepSampler;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Constraint(validatedBy = StepValidator.Validator.class)
public @interface StepValidator {


    String message() default "step: the duration and interval must be valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<StepValidator, StepSampler> {

        public static final Integer MIN_INTERVAL = 100;
        public static final Integer MIN_DURATION = 0;

        public static final String INTERVAL_ERROR_MESSAGE = "The interval must be at least 100.";
        public static final String DURATION_ERROR_MESSAGE = "The duration must be greater than or equal to 0.";
        public static final String DURATION_INTERVAL_ERROR_MESSAGE = "The duration must be greater than or equal to the interval.";

        @Override
        public boolean isValid(StepSampler step, ConstraintValidatorContext context) {

            if (step.interval() == null || step.duration() == null) {
                return false;
            }

            if (step.interval() < MIN_INTERVAL) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(INTERVAL_ERROR_MESSAGE).addConstraintViolation();
                return false;
            }

            if (step.duration() < MIN_DURATION) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(DURATION_ERROR_MESSAGE).addConstraintViolation();
                return false;
            }

            if (step.duration() < step.interval() && step.duration() != 0) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(DURATION_INTERVAL_ERROR_MESSAGE).addConstraintViolation();
                return false;
            }

            return true;
        }
    }
}