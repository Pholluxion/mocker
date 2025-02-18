package com.smartuis.api.models.constraints.protocols;

import com.smartuis.api.models.protocols.AmqpProtocol;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Constraint(validatedBy = AmqpValidator.Validator.class)
public @interface AmqpValidator {

    String message() default "amqp: the protocol must be valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements jakarta.validation.ConstraintValidator<AmqpValidator, AmqpProtocol> {

        @Override
        public boolean isValid(AmqpProtocol amqp, jakarta.validation.ConstraintValidatorContext context) {
            return amqp.host() != null && !amqp.host().isBlank() && amqp.port() != null && amqp.exchange() != null && !amqp.exchange().isBlank();
        }
    }
}
