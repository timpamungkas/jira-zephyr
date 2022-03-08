package com.jira;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ZephyrTestCaseResponse {

	private int startAt;
	private int maxResults;
	private int total;
	private List<ZephyrTestCase> values;

}
