package com.consul.helper.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.consul.helper.model.ConsulPropertyDeleteRequest;
import com.consul.helper.service.ConsulService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ConsulServiceImpl implements ConsulService {
	private static WebClient webClient;
	private static ObjectMapper objectMapper;
	private static CollectionType collectionType;
	static {
		webClient = WebClient.create();
		objectMapper = new ObjectMapper();
		collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, String.class);
	}

	@Override
	public Mono<String> deleteConsulProperties(ConsulPropertyDeleteRequest request) {
		log.info("----Request : {}", request);
		Flux.range(0, request.getPropertiesKey().size()).log()
				.flatMap(iter -> webClient.delete()
						.uri("http://" + request.getConsulUrl() + "/v1/kv/config/" + request.getProjectPath() + "/"
								+ request.getPropertiesKey().get(iter))
						.retrieve().bodyToMono(Boolean.class).log())
				.blockLast();

		return Mono.just("Success");
	}

	/**
	 * Convert Mono To Flux flatMapMany(Flux::fromIterable);
	 * 
	 */
	@Override
	public Mono<Object> getAllKeys(String consulURL, String projectPath) {

		return webClient.get().uri("http://" + consulURL + "/v1/kv/config/" + projectPath + "/?keys").retrieve()
				.bodyToMono(String.class).map(res -> {
					try {
						return objectMapper.readValue(res, collectionType);
					} catch (JsonMappingException e) {
						e.printStackTrace();
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
					return null;
				});
	}

}
