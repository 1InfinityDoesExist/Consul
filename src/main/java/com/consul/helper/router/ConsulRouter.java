package com.consul.helper.router;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.consul.helper.handler.ConsulHandler;

import reactor.core.publisher.Mono;

@Configuration
public class ConsulRouter {

	@Value("${test.data.key}")
	private String key;

	@Autowired
	private ConsulHandler handler;

	
	/**
	 * In order to make the below api work. Just comment out the "spring-boot-starter-web" dependency in pom.xml file.
	 * @return
	 */
	@Bean
	public RouterFunction<ServerResponse> routes() {
		return RouterFunctions.route()
				.GET("/consul-properties", handler::getAllConsulProperties)
				.GET("/key", request -> {
					return ServerResponse.ok().body(BodyInserters.fromPublisher(Mono.just(key), String.class));
				})
				.DELETE("/remove-props", handler::deletePropertyFromConsul) // in development phase.
				.build();

	}

}
