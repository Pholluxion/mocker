package com.smartuis.api.config.jackson;

import com.smartuis.api.models.generators.BooleanGenerator;
import com.smartuis.api.models.interfaces.IGenerator;
import com.smartuis.api.models.interfaces.IProtocol;
import com.smartuis.api.models.interfaces.ISampler;
import com.smartuis.api.models.protocols.AmqpProtocol;
import com.smartuis.api.models.protocols.MqttProtocol;
import com.smartuis.api.models.samplers.SequentialSampler;
import com.smartuis.api.models.samplers.StepSampler;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

@Configuration
@ImportRuntimeHints(JacksonHints.class)
public class JacksonHints implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection().registerForInterfaces(IGenerator.class, hint -> hint.withMembers(
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.DECLARED_FIELDS
        ));
        hints.reflection().registerType(BooleanGenerator.class, hint -> hint.withMembers(
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.DECLARED_FIELDS

        ));

        hints.reflection().registerForInterfaces(IProtocol.class, hint -> hint.withMembers(
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.DECLARED_FIELDS

        ));
        hints.reflection().registerType(AmqpProtocol.class, hint -> hint.withMembers(
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.DECLARED_FIELDS

        ));
        hints.reflection().registerType(MqttProtocol.class, hint -> hint.withMembers(
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.DECLARED_FIELDS


        ));

        hints.reflection().registerForInterfaces(ISampler.class, hint -> hint.withMembers(
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.DECLARED_FIELDS

        ));
        hints.reflection().registerType(StepSampler.class, hint -> hint.withMembers(
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.DECLARED_FIELDS

        ));
        hints.reflection().registerType(SequentialSampler.class, hint -> hint.withMembers(
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.DECLARED_FIELDS

        ));

    }
}