package com.smartuis.server.config.jackson;

import com.smartuis.server.dtos.SchemaDTO;
import com.smartuis.server.models.generators.random.RandomDouble;
import com.smartuis.server.models.generators.random.RandomInteger;
import com.smartuis.server.models.generators.random.BooleanGenerator;
import com.smartuis.server.models.generators.continuous.*;
import com.smartuis.server.models.generators.discrete.*;
import com.smartuis.server.models.interfaces.IGenerator;
import com.smartuis.server.models.interfaces.IProtocol;
import com.smartuis.server.models.interfaces.ISampler;
import com.smartuis.server.models.protocols.AmqpProtocol;
import com.smartuis.server.models.protocols.MqttProtocol;
import com.smartuis.server.models.samplers.SequentialSampler;
import com.smartuis.server.models.samplers.StepSampler;
import com.smartuis.server.models.schema.Schema;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeHint;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

@Configuration
@ImportRuntimeHints(JacksonHints.class)
public class JacksonHints implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {

        /// Interfaces
        hints.reflection().registerForInterfaces(ISampler.class, this::registerMembers);
        hints.reflection().registerForInterfaces(IProtocol.class, this::registerMembers);
        hints.reflection().registerForInterfaces(IGenerator.class, this::registerMembers);

        /// Classes
        hints.reflection().registerType(Schema.class, this::registerMembers);
        hints.reflection().registerType(SchemaDTO.class, this::registerMembers);
        hints.reflection().registerType(Schema[].class, this::registerMembers);
        hints.reflection().registerType(SchemaDTO[].class, this::registerMembers);

        /// Protocols
        hints.reflection().registerType(AmqpProtocol.class, this::registerMembers);
        hints.reflection().registerType(MqttProtocol.class, this::registerMembers);

        /// Samplers
        hints.reflection().registerType(StepSampler.class, this::registerMembers);
        hints.reflection().registerType(SequentialSampler.class, this::registerMembers);

        /// Generators
        hints.reflection().registerType(BooleanGenerator.class, this::registerMembers);
        hints.reflection().registerType(RandomDouble.class, this::registerMembers);
        hints.reflection().registerType(RandomInteger.class, this::registerMembers);

        hints.reflection().registerType(ExponentialDistribution.class, this::registerMembers);
        hints.reflection().registerType(LogNormalDistribution.class, this::registerMembers);
        hints.reflection().registerType(NormalDistribution.class, this::registerMembers);
        hints.reflection().registerType(TriangularDistribution.class, this::registerMembers);
        hints.reflection().registerType(UniformContinuousDistribution.class, this::registerMembers);

        hints.reflection().registerType(BernoulliDistribution.class, this::registerMembers);
        hints.reflection().registerType(BinomialDistribution.class, this::registerMembers);
        hints.reflection().registerType(GeometricDistribution.class, this::registerMembers);
        hints.reflection().registerType(PoissonDistribution.class, this::registerMembers);
        hints.reflection().registerType(UniformDiscreteDistribution.class, this::registerMembers);

    }

    private void registerMembers(TypeHint.Builder builder) {
        builder.withMembers(
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.DECLARED_FIELDS,
                MemberCategory.UNSAFE_ALLOCATED
        );
    }


}