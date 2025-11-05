package com.greatbuild.clearcost.msvc.organizations.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Nombres de colas y exchange (deben coincidir con msvc-invitations)
    public static final String EXCHANGE_NAME = "invitation.exchange";
    public static final String VALIDATION_REQUEST_QUEUE = "invitation.validation.request.queue";
    public static final String VALIDATION_SUCCESS_QUEUE = "invitation.validation.success.queue";
    public static final String VALIDATION_FAILED_QUEUE = "invitation.validation.failed.queue";
    
    public static final String ROUTING_KEY_REQUEST = "invitation.validation.request";
    public static final String ROUTING_KEY_SUCCESS = "invitation.validation.success";
    public static final String ROUTING_KEY_FAILED = "invitation.validation.failed";

    /**
     * Convertidor de mensajes JSON con soporte para LocalDateTime
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    /**
     * Exchange compartido para eventos de invitación
     */
    @Bean
    public TopicExchange invitationExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    /**
     * Cola que CONSUME: solicitudes de validación
     * Declarada aquí para garantizar que exista aunque msvc-invitations no haya arrancado
     */
    @Bean
    public Queue validationRequestQueue() {
        return new Queue(VALIDATION_REQUEST_QUEUE, true);
    }

    /**
     * Cola que PUBLICA: respuestas de validación exitosas
     * Declarada para asegurar que exista antes de publicar
     */
    @Bean
    public Queue validationSuccessQueue() {
        return new Queue(VALIDATION_SUCCESS_QUEUE, true);
    }

    /**
     * Cola que PUBLICA: respuestas de validación fallidas
     * Declarada para asegurar que exista antes de publicar
     */
    @Bean
    public Queue validationFailedQueue() {
        return new Queue(VALIDATION_FAILED_QUEUE, true);
    }

    /**
     * Binding: Exchange -> Cola de solicitudes
     */
    @Bean
    public Binding validationRequestBinding(Queue validationRequestQueue, TopicExchange invitationExchange) {
        return BindingBuilder.bind(validationRequestQueue)
                .to(invitationExchange)
                .with(ROUTING_KEY_REQUEST);
    }

    /**
     * Binding: Exchange -> Cola de respuestas exitosas
     */
    @Bean
    public Binding validationSuccessBinding(Queue validationSuccessQueue, TopicExchange invitationExchange) {
        return BindingBuilder.bind(validationSuccessQueue)
                .to(invitationExchange)
                .with(ROUTING_KEY_SUCCESS);
    }

    /**
     * Binding: Exchange -> Cola de respuestas fallidas
     */
    @Bean
    public Binding validationFailedBinding(Queue validationFailedQueue, TopicExchange invitationExchange) {
        return BindingBuilder.bind(validationFailedQueue)
                .to(invitationExchange)
                .with(ROUTING_KEY_FAILED);
    }
}
