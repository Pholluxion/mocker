package com.smartuis.server.models.interfaces;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.smartuis.server.models.generators.random.*;
import com.smartuis.server.models.generators.continuous.*;
import com.smartuis.server.models.generators.discrete.*;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = BooleanGenerator.class, name = "boolean"),
        @JsonSubTypes.Type(value = DoubleGenerator.class, name = "random_double"),
        @JsonSubTypes.Type(value = IntegerGenerator.class, name = "random_integer"),
        @JsonSubTypes.Type(value = StringGenerator.class, name = "string"),
        @JsonSubTypes.Type(value = TimestampGenerator.class, name = "timestamp"),

        @JsonSubTypes.Type(value = ExponentialDistribution.class, name = "continuous_exponential"),
        @JsonSubTypes.Type(value = LogNormalDistribution.class, name = "continuous_log_normal"),
        @JsonSubTypes.Type(value = NormalDistribution.class, name = "continuous_normal"),
        @JsonSubTypes.Type(value = TriangularDistribution.class, name = "continuous_triangular"),
        @JsonSubTypes.Type(value = UniformContinuousDistribution.class, name = "continuous_uniform"),

        @JsonSubTypes.Type(value = BernoulliDistribution.class, name = "discrete_bernoulli"),
        @JsonSubTypes.Type(value = BinomialDistribution.class, name = "discrete_binomial"),
        @JsonSubTypes.Type(value = GeometricDistribution.class, name = "discrete_geometric"),
        @JsonSubTypes.Type(value = PoissonDistribution.class, name = "discrete_poisson"),
        @JsonSubTypes.Type(value = UniformDiscreteDistribution.class, name = "discrete_uniform"),
})
public interface IGenerator<T> {
    String name();
    T sample();
}
