package com.smartuis.server.models.schema;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.smartuis.server.models.interfaces.IGenerator;
import com.smartuis.server.models.interfaces.IProtocol;
import com.smartuis.server.models.interfaces.ISampler;
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

    @NotBlank(message = "The name must be defined.")
    String name;

    @NotNull(message = "The sampler must be defined.")
    ISampler sampler;

    @NotNull(message = "The protocol must be defined.")
    @Size(min = 1, message = "At least one protocol must be defined.")
    List<IProtocol> protocols;

    @NotNull(message = "The generator must be defined.")
    @Size(min = 1, message = "At least one generator must be defined.")
    List<IGenerator<?>> generators;

    String template;

    public Map<String, Object> generate() {
        return this.generators
                .stream()
                .collect(Collectors.toMap(IGenerator::name, IGenerator::sample));
    }

    public IProtocol getProtocolByType(String type) {

        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("protocol type cannot be null or empty");
        }

        if (this.protocols == null || this.protocols.isEmpty()) {
            throw new IllegalArgumentException("protocols cannot be null or empty");
        }

        return this.protocols
                .stream()
                .filter(protocol -> protocol.type().equals(type))
                .findFirst()
                .orElse(null);
    }
}
