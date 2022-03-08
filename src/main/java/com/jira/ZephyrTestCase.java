package com.jira;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ZephyrTestCase {

	private String key;
	private int id;
	private String name;

}
