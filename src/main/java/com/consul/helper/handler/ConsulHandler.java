package com.consul.helper.handler;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.consul.helper.model.ConsulPropertyDeleteRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ConsulHandler {

	private static WebClient webClient;
	private static ObjectMapper objectMapper;
	private static CollectionType collectionType;
	private static Decoder decoder;
	private static JSONObject properties;

	static {
		decoder = Base64.getDecoder();
		properties = new JSONObject();
		webClient = WebClient.create();
		objectMapper = new ObjectMapper();

		collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class);

	}

	public Mono<ServerResponse> addPropertiesToConsul(ServerRequest request) {
		log.info("----Add properties to the consul.");

		return null;
	}

	public Mono<ServerResponse> deletePropertyFromConsul(ServerRequest request) {
		log.info("----Delete property from the consul.");

		request.bodyToMono(String.class).map(mp -> {
			System.out.println(mp);
			return mp;
		});
		request.bodyToMono(ConsulPropertyDeleteRequest.class).subscribe(prop -> {
			System.out.println(" consul url : " + prop.getConsulUrl());
			System.out.println(" getProjectPath : " + prop.getProjectPath());
			System.out.println(" consul keys  : " + prop.getPropertiesKey());
			prop.getPropertiesKey().stream().forEach(property -> {
				Mono<Boolean> bodyToMono = webClient.delete().uri(
						"http://" + prop.getConsulUrl() + "/v1/kv/config/" + prop.getProjectPath() + "/" + property)
						.retrieve().bodyToMono(Boolean.class);
			});
		});
		return null;
	}

	/**
	 * http://localhost:8500/v1/kv/config/consul-demo,dev/?recurse
	 * @param request
	 * @return
	 */
	public Mono<ServerResponse> getAllConsulProperties(ServerRequest request) {
		log.info("-----Get all consul properties");

		String consulURL = request.queryParam("consulURL").get();
		String projectPath = request.queryParam("projectPath").get();
		// Null check for above data.
		log.info("----Consul url : {}", consulURL);

		Mono<JSONObject> log2 = webClient.get()
				.uri("http://" + consulURL + "/v1/kv/config/" + projectPath + "/?recurse").retrieve()
				.bodyToMono(String.class)

				.map(res -> {
					List<Map<String, Object>> prop;
					try {
						prop = objectMapper.readValue(res, collectionType);
						for (Map<String, Object> mp : prop) {
							if (mp.containsKey("Value") && mp.get("Value") != null) {
								properties.put((((String) mp.get("Key")).split("/"))[2],
										new String(decoder.decode((String) mp.get("Value"))));
							}
						}

					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
					return properties;
				}).log();

		return ServerResponse.ok().body(BodyInserters.fromPublisher(log2, JSONObject.class));
	}
}
