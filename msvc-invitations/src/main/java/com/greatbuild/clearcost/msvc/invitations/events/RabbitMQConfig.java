package com.greatbuild.clearcost.msvc.invitations.events;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Define el nombre de la cola que vamos a escuchar
    public static final String INVITATION_QUEUE = "invitation.user.queue";

    @Bean
    public Queue invitationQueue() {
        // durable = true (la cola sobrevive a reinicios de RabbitMQ)
        return new Queue(INVITATION_QUEUE, true);
    }

    // (Más adelante, cuando publiquemos eventos, añadiremos Exchanges y Bindings)
}
