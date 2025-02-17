package com.smartuis.api.models.constraints.protocols;

import com.smartuis.api.models.protocols.MqttProtocol;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Constraint(validatedBy = MqttValidator.Validator.class)
public @interface MqttValidator {

    String message() default "mqtt: the protocol must be valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements jakarta.validation.ConstraintValidator<MqttValidator, MqttProtocol> {


        @Override
        public boolean isValid(MqttProtocol mqtt, jakarta.validation.ConstraintValidatorContext context) {

            return mqtt.host() != null && !mqtt.host().isBlank() && mqtt.port() != null && mqtt.topic() != null && !mqtt.topic().isBlank();
        }
    }
}
