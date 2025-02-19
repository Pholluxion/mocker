package com.smartuis.api.config.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DefaultHeaderChannelRegistry;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.MessageChannel;

@Configuration
public class IntegrationConfig {

    @Bean(name = "integrationHeaderChannelRegistry")
    public DefaultHeaderChannelRegistry integrationHeaderChannelRegistry() {
        return new DefaultHeaderChannelRegistry();
    }

    @Bean(name = "errorChannel")
    public MessageChannel errorChannel() {
        return new PublishSubscribeChannel();
    }
}
