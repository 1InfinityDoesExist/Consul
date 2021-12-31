package com.consul.helper.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping

public class HelperController {

	@Value("${test.data.key}")
	private String key;

	@GetMapping("/key")
	public String getKeyValue() {
		System.out.println("key value is : " + key);
		return "Key is : " + key;
	}
}
