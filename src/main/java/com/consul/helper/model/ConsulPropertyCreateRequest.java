package com.consul.helper.model;

import java.util.Map;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ConsulPropertyCreateRequest {
	@NotEmpty
	@NotNull
	private String consulUrl;
	@NotNull
	@NotEmpty
	private String projectPath;
	private Map<String, Object> properties;

}
