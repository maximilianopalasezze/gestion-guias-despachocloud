package cl.duoc.gestionguias.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.guias.exchange}")
    private String guiasExchange;

    @Value("${app.rabbitmq.guias.error-exchange}")
    private String guiasErrorExchange;

    @Value("${app.rabbitmq.guias.queue}")
    private String guiasQueue;

    @Value("${app.rabbitmq.guias.dlq}")
    private String guiasDlq;

    @Value("${app.rabbitmq.guias.routing-key}")
    private String guiasRoutingKey;

    @Value("${app.rabbitmq.guias.error-routing-key}")
    private String guiasErrorRoutingKey;

    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public DirectExchange guiasExchange() {
        return ExchangeBuilder
                .directExchange(guiasExchange)
                .durable(true)
                .build();
    }

    @Bean
    public DirectExchange guiasErrorExchange() {
        return ExchangeBuilder
                .directExchange(guiasErrorExchange)
                .durable(true)
                .build();
    }

    @Bean
    public Queue guiasQueue() {
        Map<String, Object> argumentos = new HashMap<>();
        argumentos.put("x-dead-letter-exchange", guiasErrorExchange);
        argumentos.put("x-dead-letter-routing-key", guiasErrorRoutingKey);

        return QueueBuilder
                .durable(guiasQueue)
                .withArguments(argumentos)
                .build();
    }

    @Bean
    public Queue guiasDlq() {
        return QueueBuilder
                .durable(guiasDlq)
                .build();
    }

    @Bean
    public Binding guiasBinding(
            Queue guiasQueue,
            DirectExchange guiasExchange
    ) {
        return BindingBuilder
                .bind(guiasQueue)
                .to(guiasExchange)
                .with(guiasRoutingKey);
    }

    @Bean
    public Binding guiasErrorBinding(
            Queue guiasDlq,
            DirectExchange guiasErrorExchange
    ) {
        return BindingBuilder
                .bind(guiasDlq)
                .to(guiasErrorExchange)
                .with(guiasErrorRoutingKey);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}