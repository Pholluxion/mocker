package com.smartuis.server.config.amqp;

import com.rabbitmq.client.*;
import com.smartuis.server.models.protocols.AmqpProtocol;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * AmqpConnector is responsible for managing the connection to a RabbitMQ broker.
 * It provides methods to connect, disconnect, send messages, and reconnect.
 */
@Slf4j
public class AmqpConnector {

    private AmqpProtocol protocol;
    private Channel channel;
    private Connection connection;
    private final ConnectionFactory connectionFactory;

    /**
     * Constructs an AmqpConnector with a new ConnectionFactory.
     */
    public AmqpConnector() {
        this.connectionFactory = new ConnectionFactory();
    }

    /**
     * Checks if the connector is currently connected to the RabbitMQ broker.
     *
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return connection != null && connection.isOpen() && channel != null && channel.isOpen();
    }

    /**
     * Connects to the RabbitMQ broker using the provided AmqpProtocol configuration.
     * If already connected, it will first disconnect.
     *
     * @param protocol the AmqpProtocol configuration to use for the connection
     */
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

            channel.exchangeDeclare(this.protocol.exchange(), this.protocol.exchangeType(), true);

            log.info("Successfully connected to RabbitMQ at {}:{}", connectionFactory.getHost(), connectionFactory.getPort());
        } catch (IOException | TimeoutException e) {
            log.error("Failed to connect to RabbitMQ broker: {}", e.getMessage());
        }
    }

    /**
     * Disconnects from the RabbitMQ broker. This method is annotated with @PreDestroy
     * to ensure it is called when the application context is destroyed.
     */
    @PreDestroy
    public synchronized void disconnect() {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
            log.info("Successfully disconnected from RabbitMQ.");
        } catch (IOException | TimeoutException e) {
            log.error("Failed to disconnect from RabbitMQ: {}", e.getMessage());
        } finally {
            channel = null;
            connection = null;
        }
    }

    /**
     * Sends a message to the RabbitMQ broker. If the channel is not open, it will attempt to reconnect.
     *
     * @param message the message to send
     */
    public void sendMessage(String message) {
        try {
            if (!isConnected()) {
                log.warn("Channel is not open. Attempting to reconnect...");
                connect(protocol);
            }

            channel.basicPublish(this.protocol.exchange(), this.protocol.routingKey(), null, message.getBytes(StandardCharsets.UTF_8));

        } catch (IOException e) {
            log.error("Failed to send message: {}", e.getMessage());
        }
    }

    /**
     * Attempts to reconnect to the RabbitMQ broker using the existing protocol configuration.
     * If the protocol configuration is missing, it will throw an IllegalStateException.
     */
    public synchronized void reconnect() {
        try {
            if (protocol == null) {
                throw new IllegalStateException("Protocol configuration is missing. Cannot reconnect.");
            }
            log.info("🔄 Attempting to reconnect to RabbitMQ...");
            connect(protocol);
        } catch (Exception e) {
            log.error("Reconnection attempt failed: {}", e.getMessage());
        }
    }
}