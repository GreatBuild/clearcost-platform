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

    // Routing keys
    public static final String INVITATION_CREATED_KEY = "invitation.created";
    public static final String INVITATION_ACCEPTED_KEY = "invitation.accepted";
    public static final String INVITATION_REJECTED_KEY = "invitation.rejected";

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
