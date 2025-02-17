package com.smartuis.api.models.schema;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.smartuis.api.models.interfaces.IGenerator;
import com.smartuis.api.models.interfaces.IProtocol;
import com.smartuis.api.models.interfaces.ISampler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class Schema {


    @Id
    String id;

    @NotNull(message = "The name must be defined.")
    @NotBlank(message = "The name must be defined.")
    String name;

    @Valid
    @NotNull(message = "The sampler must be defined.")
    ISampler sampler;

    @Valid
    @NotNull(message = "The protocol must be defined.")
    @Size(min = 1, message = "At least one protocol must be defined.")
    List<IProtocol> protocols;

    @Valid
    @NotNull(message = "The generator must be defined.")
    @Size(min = 1, message = "At least one generator must be defined.")
    List<IGenerator<?>> generators;

    Object template;

    public Map<String, Object> generate() {
        return this.generators
                .stream()
                .collect(Collectors.toMap(IGenerator::name, IGenerator::generate));
    }

    public IProtocol getProtocolByType(String type) {
        return this.protocols
                .stream()
                .filter(protocol -> protocol.type().equals(type))
                .findFirst()
                .orElse(null);
    }


}
