package com.greatbuild.clearcost.msvc.invitations.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Nombres de exchanges y colas
    public static final String INVITATION_EXCHANGE = "invitation.exchange";
    public static final String INVITATION_QUEUE = "invitation.user.queue";
    public static final String INVITATION_CREATED_QUEUE = "invitation.created.queue";
    public static final String INVITATION_ACCEPTED_QUEUE = "invitation.accepted.queue";
    public static final String INVITATION_REJECTED_QUEUE = "invitation.rejected.queue";
    
    // Colas para validación asíncrona
    public static final String VALIDATION_REQUEST_QUEUE = "invitation.validation.request.queue";
    public static final String VALIDATION_SUCCESS_QUEUE = "invitation.validation.success.queue";
    public static final String VALIDATION_FAILED_QUEUE = "invitation.validation.failed.queue";

    // Routing keys
    public static final String INVITATION_CREATED_KEY = "invitation.created";
    public static final String INVITATION_ACCEPTED_KEY = "invitation.accepted";
    public static final String INVITATION_REJECTED_KEY = "invitation.rejected";
    
    // Routing keys para validación
    public static final String VALIDATION_REQUEST_KEY = "invitation.validation.request";
    public static final String VALIDATION_SUCCESS_KEY = "invitation.validation.success";
    public static final String VALIDATION_FAILED_KEY = "invitation.validation.failed";

    /**
     * Exchange principal de tipo topic para eventos de invitaciones
     */
    @Bean
    public TopicExchange invitationExchange() {
        return new TopicExchange(INVITATION_EXCHANGE);
    }

    /**
     * Cola para escuchar eventos de invitaciones (listener)
     */
    @Bean
    public Queue invitationQueue() {
        return new Queue(INVITATION_QUEUE, true);
    }

    /**
     * Cola para eventos de invitación creada
     */
    @Bean
    public Queue invitationCreatedQueue() {
        return new Queue(INVITATION_CREATED_QUEUE, true);
    }

    /**
     * Cola para eventos de invitación aceptada
     */
    @Bean
    public Queue invitationAcceptedQueue() {
        return new Queue(INVITATION_ACCEPTED_QUEUE, true);
    }

    /**
     * Cola para eventos de invitación rechazada
     */
    @Bean
    public Queue invitationRejectedQueue() {
        return new Queue(INVITATION_REJECTED_QUEUE, true);
    }

    /**
     * Binding: invitación creada
     */
    @Bean
    public Binding invitationCreatedBinding() {
        return BindingBuilder
                .bind(invitationCreatedQueue())
                .to(invitationExchange())
                .with(INVITATION_CREATED_KEY);
    }

    /**
     * Binding: invitación aceptada
     */
    @Bean
    public Binding invitationAcceptedBinding() {
        return BindingBuilder
                .bind(invitationAcceptedQueue())
                .to(invitationExchange())
                .with(INVITATION_ACCEPTED_KEY);
    }

    /**
     * Binding: invitación rechazada
     */
    @Bean
    public Binding invitationRejectedBinding() {
        return BindingBuilder
                .bind(invitationRejectedQueue())
                .to(invitationExchange())
                .with(INVITATION_REJECTED_KEY);
    }

    // ========== COLAS Y BINDINGS PARA VALIDACIÓN ASÍNCRONA ==========

    /**
     * Cola para solicitudes de validación
     */
    @Bean
    public Queue validationRequestQueue() {
        return new Queue(VALIDATION_REQUEST_QUEUE, true);
    }

    /**
     * Cola para validaciones exitosas
     */
    @Bean
    public Queue validationSuccessQueue() {
        return new Queue(VALIDATION_SUCCESS_QUEUE, true);
    }

    /**
     * Cola para validaciones fallidas
     */
    @Bean
    public Queue validationFailedQueue() {
        return new Queue(VALIDATION_FAILED_QUEUE, true);
    }

    /**
     * Binding: solicitud de validación
     */
    @Bean
    public Binding validationRequestBinding() {
        return BindingBuilder
                .bind(validationRequestQueue())
                .to(invitationExchange())
                .with(VALIDATION_REQUEST_KEY);
    }

    /**
     * Binding: validación exitosa
     */
    @Bean
    public Binding validationSuccessBinding() {
        return BindingBuilder
                .bind(validationSuccessQueue())
                .to(invitationExchange())
                .with(VALIDATION_SUCCESS_KEY);
    }

    /**
     * Binding: validación fallida
     */
    @Bean
    public Binding validationFailedBinding() {
        return BindingBuilder
                .bind(validationFailedQueue())
                .to(invitationExchange())
                .with(VALIDATION_FAILED_KEY);
    }

    /**
     * Convertidor de mensajes JSON con soporte para LocalDateTime
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
