package com.consul.helper.controller;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.consul.helper.model.ConsulPropertyDeleteRequest;
import com.consul.helper.service.ConsulService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 
 * spring-boot-starter-web "UnComment this dependency in order to make below api
 * work properly"
 * 
 * @author mossad
 *
 */
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
	public ResponseEntity<Mono<Object>> getAllKeysUsingGET(
			@RequestParam(value = "consulURL", required = true) String consulURL,
			@RequestParam(value = "projectPath", required = true) String projectPath) {

		return ResponseEntity.status(HttpStatus.OK).body(consulService.getAllKeys(consulURL, projectPath));

	}

}
