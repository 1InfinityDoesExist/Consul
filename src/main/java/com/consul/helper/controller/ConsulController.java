package com.consul.helper.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.consul.helper.model.ApplicationPropertiesConsul;
import com.consul.helper.model.ConsulPropertyCreateRequest;
import com.consul.helper.model.ConsulPropertyDeleteRequest;
import com.consul.helper.service.ConsulService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 
 * spring-boot-starter-web "UnComment this dependency in order to make below api
 * work properly"
 * 
 * @author mossad
 *
 */
@Slf4j
@RestController
@RequestMapping(path = "/consul")
public class ConsulController {

	@Autowired
	private ConsulService consulService;

	/**
	 * Api to delete all the requested key-value pair from the consul.
	 * 
	 * @param request
	 * @return
	 */
	@DeleteMapping("/delete-prop")
	public ResponseEntity<Mono<String>> deleteConsulProperties(@RequestBody ConsulPropertyDeleteRequest request) {
		return ResponseEntity.status(HttpStatus.OK).body(consulService.deleteConsulProperties(request));
	}

	/**
	 * Api to get all the keys.
	 * 
	 * @param consulURL
	 * @param projectPath
	 * @return
	 */
	@GetMapping("/keys")
	public ResponseEntity<Mono<String[]>> getAllKeysUsingGET(
			@RequestParam(value = "consulURL", required = true) String consulURL,
			@RequestParam(value = "projectPath", required = true) String projectPath) {

		return ResponseEntity.status(HttpStatus.OK).body(consulService.getAllKeys(consulURL, projectPath));

	}

	/**
	 * Api to update and add key value pair in consul
	 * 
	 * @param request
	 * @return
	 */
	@PutMapping("/add-prop")
	public ResponseEntity<Mono<String>> addPropInConsul(@RequestBody ConsulPropertyCreateRequest request) {

		log.info("--Add key value pair in the consul.----");
		return ResponseEntity.status(HttpStatus.OK).body(consulService.addKeyValuePairInConsulUsingPUT(request));
	}

	@PostMapping("/app-to-consul")
	public ResponseEntity<Mono<String>> appPropToConsul(@RequestBody ApplicationPropertiesConsul request) throws IOException {
		log.info("---Add all application.properties key-value pair to consul");
		return ResponseEntity.status(HttpStatus.OK).body(consulService.appPropertiesToConsul(request));

	}

}
