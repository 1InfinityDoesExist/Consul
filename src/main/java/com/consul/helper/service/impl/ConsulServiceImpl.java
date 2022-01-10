package com.consul.helper.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.consul.helper.model.ApplicationPropertiesConsul;
import com.consul.helper.model.ConsulPropertyCreateRequest;
import com.consul.helper.model.ConsulPropertyDeleteRequest;
import com.consul.helper.service.ConsulService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ConsulServiceImpl implements ConsulService {
	private static WebClient webClient;
	static {
		webClient = WebClient.create();
	}

	@Override
	public Mono<String> deleteConsulProperties(ConsulPropertyDeleteRequest request) {
		log.info("----Request : {}", request);
		Flux.range(0, request.getPropertiesKey().size()).log()
				.flatMap(iter -> webClient.delete()
						.uri("http://" + request.getConsulUrl() + "/v1/kv/config/" + request.getProjectPath() + "/"
								+ request.getPropertiesKey().get(iter))
						.retrieve()
						.bodyToMono(Boolean.class)
						.log())
				.blockLast();

		return Mono.just("Success");
	}

	/**
	 * Convert Mono To Flux flatMapMany(Flux::fromIterable);
	 * 
	 * http://localhost:8500/v1/kv/config/consul-demo,dev/?keys
	 * 
	 */
	@Override
	public Mono<String[]> getAllKeys(String consulURL, String projectPath) {
		log.info("-----Retrieve all the keys-----");
		return webClient.get().uri("http://" + consulURL + "/v1/kv/config/" + projectPath + "/?keys").retrieve()
				.bodyToMono(String[].class)
				.map(res -> {
					return Stream.of(res)
							.map(prop -> prop.replaceAll("config/" + projectPath + "/", ""))
							.filter(k -> k.length() > 0)
							.toArray(String[]::new);
				}).log();
	}

	/**
	 * via payload string will contain "" as a result the value in consul will also
	 * contain ""
	 * 
	 * properties will be read from application.properties..so no issue.
	 */
	@Override
	public Mono<String> addKeyValuePairInConsulUsingPUT(ConsulPropertyCreateRequest request) {
		log.info("-----About to add /update key value in consul.-----");
		request.getProperties().entrySet().stream().forEach(prop -> {
			System.out.println(prop.getValue());
			webClient.put()
					.uri("http://" + request.getConsulUrl() + "/v1/kv/config/" + request.getProjectPath() + "/"
							+ prop.getKey())
					.body(Mono.just(prop.getValue()), Object.class)
					.accept(MediaType.APPLICATION_JSON)
					.retrieve()
					.bodyToMono(Boolean.class)
					.timeout(Duration.ofMillis(5000))
					.subscribe();
		});
		return Mono.just("Success");
	}

	/**
	 * Adding all the application.properties key-value to consul.
	 * 
	 * @throws IOException
	 */
	@Override
	public Mono<String> appPropertiesToConsul(ApplicationPropertiesConsul request) throws IOException {
		log.info("----Adding all the application.properties to the consul.");
		Map<String, Object> properties = new HashMap<>();
		String projectPath = Paths.get(request.getProjectPath(), "src", "main", "resources", request.getPropFile())
				.toString();
		File file = new File(projectPath);
		if (file.exists()) {
			log.info("---File does Exist");
			BufferedReader bufferReader = null;
			try {
				bufferReader = new BufferedReader(new FileReader(projectPath));
				String line;
				while ((line = bufferReader.readLine()) != null) {
					if (!line.startsWith("#") && !ObjectUtils.isEmpty(line)) {
						String[] split = line.split("=");
						if (split[0].equalsIgnoreCase("server.port")) {
							continue;
						}
						properties.put(split[0], split[1]);
					}
				}
				addKeyValuePairInConsulUsingPUT(ConsulPropertyCreateRequest.builder()
						.consulUrl(request.getConsulURL())
						.projectPath(request.getConsulProjectPath())
						.properties(properties)
						.build())
				.subscribe(res -> log.info(res));
				bufferReader.close();
			} catch (Exception ex) {

			} finally {
				bufferReader.close();
			}
		} else {
			log.info("----Fiel does not exist.");
		}
		return Mono.just("Success");
	}
}
