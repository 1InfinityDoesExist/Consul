package com.consul.helper.model;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ConsulPropertyDeleteRequest {

	@NotEmpty
	@NotNull
	private String consulUrl;
	@NotNull
	@NotEmpty
	private String projectPath;
	private List<String> propertiesKey;
}
