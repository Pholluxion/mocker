package com.smartuis.api.config.amqp;

import com.rabbitmq.client.*;
import com.smartuis.api.models.protocols.AmqpProtocol;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@Slf4j
public class AmqpConnector {

    private AmqpProtocol protocol;
    private Channel channel;
    private Connection connection;
    private final ConnectionFactory connectionFactory;

    public AmqpConnector() {
        this.connectionFactory = new ConnectionFactory();
    }

    public boolean isConnected() {
        return connection != null && connection.isOpen() && channel != null && channel.isOpen();
    }

    public synchronized void connect(AmqpProtocol protocol) {
        try {
            disconnect();
            this.protocol = protocol;
            connectionFactory.setHost(this.protocol.host());
            connectionFactory.setPort(this.protocol.port());
            connectionFactory.setUsername(this.protocol.username());
            connectionFactory.setPassword(this.protocol.password());

            this.connection = connectionFactory.newConnection();
            this.channel = connection.createChannel();

            channel.exchangeDeclare(this.protocol.exchange(), BuiltinExchangeType.DIRECT, true);

            log.info("✅ Successfully connected to RabbitMQ at {}:{}", connectionFactory.getHost(), connectionFactory.getPort());
        } catch (IOException | TimeoutException e) {
            log.error("❌ Failed to connect to RabbitMQ broker: {}", e.getMessage());
            throw new RuntimeException("Failed to connect to RabbitMQ", e);
        }
    }

    @PreDestroy
    public synchronized void disconnect() {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
            log.info("✅ Successfully disconnected from RabbitMQ.");
        } catch (IOException | TimeoutException e) {
            log.error("❌ Failed to disconnect from RabbitMQ: {}", e.getMessage());
        } finally {
            channel = null;
            connection = null;
        }
    }

    public void sendMessage(String message) {
        try {
            if (!isConnected()) {
                log.warn("Channel is not open. Attempting to reconnect...");
                connect(protocol);
            }

            channel.basicPublish(this.protocol.exchange(), this.protocol.routingKey(), null, message.getBytes(StandardCharsets.UTF_8));
            log.info("✅ Message sent to exchange '{}' with routingKey '{}': {}", this.protocol.exchange(), this.protocol.routingKey(), message);
        } catch (IOException e) {
            log.error("❌ Failed to send message: {}", e.getMessage());
        }
    }

    public synchronized void reconnect() {
        try {
            if (protocol == null) {
                throw new IllegalStateException("Protocol configuration is missing. Cannot reconnect.");
            }
            log.info("🔄 Attempting to reconnect to RabbitMQ...");
            connect(protocol);
        } catch (Exception e) {
            log.error("❌ Reconnection attempt failed: {}", e.getMessage());
        }
    }
}
