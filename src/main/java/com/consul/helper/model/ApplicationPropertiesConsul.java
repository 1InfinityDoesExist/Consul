package com.consul.helper.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ApplicationPropertiesConsul {

	private String consulURL;
	private String projectPath;
	private String propFile;
	private String consulProjectPath;
}
