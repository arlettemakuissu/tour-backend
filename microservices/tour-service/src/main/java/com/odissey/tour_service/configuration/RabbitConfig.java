package com.odissey.tour_service.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String COUNTRY_QUEUE = "country_tour_queue";
    public static final String AGENCY_QUEUE = "agency_tour_queue";

    @Bean(name = "countryQueue")
    public Queue countryQueue(){
        return new Queue(COUNTRY_QUEUE, true);
    }

    @Bean(name = "agencyQueue")
    public Queue agencyQueue(){
        return new Queue(AGENCY_QUEUE, true);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
