package com.odissey.country_service.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {

    // FANOUT (Multi-consumer)
    // Il messaggio prodotto viene inviato indistintamente a tutte le code. Infatti manca il routing key
    public static final String FANOUT_EXCHANGE = "country_fanout_exchange";
    public static final String FANAOUT_QUEUE_AGENCY = "country_agency_queue";
    public static final String FANAOUT_QUEUE_TOUR = "country_tour_queue";

    // Definire l'exchange (in questo caso di tipo Fanout)
    @Bean
    public FanoutExchange exchange(){
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    // Definire la coda per l'agency-service
    @Bean(name = "fanoutQueueAgency")
    public Queue fanoutQueueAgency(){
        return new Queue(FANAOUT_QUEUE_AGENCY, true);
    }

    // Definire la coda per il tour-service
    @Bean(name = "fanoutQueueTour")
    public Queue fanoutQueueTour(){
        return new Queue(FANAOUT_QUEUE_TOUR, true);
    }

    // Binding -> regola di instradamento (routing) del messaggio verso uan determinata coda
    @Bean
    public Binding bindingAgency(@Qualifier("fanoutQueueAg" +
            "ency") Queue queue, FanoutExchange exchange){
        return BindingBuilder.bind(queue).to(exchange);
    }

    @Bean
    public Binding bindingTour(@Qualifier("fanoutQueueTour") Queue queue, FanoutExchange exchange){
        return BindingBuilder.bind(queue).to(exchange);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

}
