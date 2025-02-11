package com.smartuis.server.utils.constraints;

import com.smartuis.server.domain.samplers.StepSampler;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Constraint(validatedBy = ValidDurationInterval.Validator.class)
public @interface ValidDurationInterval {

    String message() default "step: the duration must be greater than or equal to the interval";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


    class Validator implements ConstraintValidator<ValidDurationInterval, StepSampler> {

        @Override
        public boolean isValid(StepSampler step, ConstraintValidatorContext context) {
            return step.getDuration() >= step.getInterval() || step.getDuration() == 0;
        }
    }
}