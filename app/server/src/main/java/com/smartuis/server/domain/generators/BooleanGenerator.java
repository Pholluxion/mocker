package com.smartuis.server.domain.generators;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.smartuis.server.domain.interfaces.IGenerator;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@JsonTypeName("boolean")
public class BooleanGenerator implements IGenerator<Boolean> {

    @NotBlank(message = "boolean: name must be provided (not blank)")
    @Size(min = 4, message = "boolean: name must be at least 4 characters long")
    private final String name;

    @NotNull(message = "boolean: probability must be provided (not null)")
    @DecimalMin(value = "0.0", message = "boolean: probability must be at least 0.0")
    @DecimalMax(value = "1.0", message = "boolean: probability must be at most 1.0")
    private final Double probability;

    @Override
    public Boolean value() {
        return random.nextDouble() < probability;
    }

    @Override
    public String name() {
        return this.name;
    }
}
