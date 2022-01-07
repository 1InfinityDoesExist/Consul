package com.consul.helper.service;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import com.consul.helper.model.ConsulPropertyDeleteRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface ConsulService {

	public Mono<String> deleteConsulProperties(ConsulPropertyDeleteRequest request);

	public Mono<Object> getAllKeys(String consulURL, String projectPath);

}
