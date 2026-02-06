package com.odissey.agency_service.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {

    // CONSUMES MESSAGES FROM COUNTRY FANOUT PRODUCER
    public static final String COUNTRY_QUEUE = "country_agency_queue";

    @Bean
    public Queue countryQueue(){
        return new Queue(COUNTRY_QUEUE, true);
    }

    @Bean
    public JacksonJsonMessageConverter  messageConverter(){
        return new JacksonJsonMessageConverter();
    }

    // PRODUCER: DIRECT EXCHANGE VERSO TOUR

    public final static String AGENCY_EXCHANGE = "agency_exchange";
    public final static String AGENCY_QUEUE = "agency_tour_queue";
    public final static String AGENCY_ROUTING_KEY = "agency_routing_key";;

    @Bean(name = "agencyExchange")
    public DirectExchange agencyExchange(){
        return new DirectExchange(AGENCY_EXCHANGE);
    }

    @Bean(name = "agencyQueue")
    public Queue agencyQueue(){
        return new Queue(AGENCY_QUEUE, true);
    }

    @Bean(name = "agencyBinding")
    public Binding agencyBinding(@Qualifier("agencyQueue") Queue queue, @Qualifier("agencyExchange") DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(AGENCY_ROUTING_KEY);
    }


}
