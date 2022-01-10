package com.consul.helper.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.consul.helper.model.ApplicationPropertiesConsul;
import com.consul.helper.model.ConsulPropertyCreateRequest;
import com.consul.helper.model.ConsulPropertyDeleteRequest;

import reactor.core.publisher.Mono;

@Service
public interface ConsulService {

	public Mono<String> deleteConsulProperties(ConsulPropertyDeleteRequest request);

	public Mono<String[]> getAllKeys(String consulURL, String projectPath);

	public Mono<String> addKeyValuePairInConsulUsingPUT(ConsulPropertyCreateRequest request);

	public Mono<String> appPropertiesToConsul(ApplicationPropertiesConsul request) throws IOException;

}
