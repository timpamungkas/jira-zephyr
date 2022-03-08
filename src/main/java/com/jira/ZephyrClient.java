package com.jira;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "zephyrClient", url = "https://api.zephyrscale.smartbear.com")
public interface ZephyrClient {

	@GetMapping(value = "/v2/testcases")
	public ZephyrTestCaseResponse search(@RequestHeader(name = "Authorization") String authHeader,
			@RequestParam(name = "projectKey", required = true) String projectKey,
			@RequestParam(name = "maxResults", required = true, defaultValue = "100") int maxResults,
			@RequestParam(name = "startAt", required = true, defaultValue = "0") int startAt);

	@PostMapping(value = "/v2/testcases/{testCaseKey}/links/issues")
	public String linkToJira(@RequestHeader(name = "Authorization") String authHeader,
			@PathVariable(name = "testCaseKey") String testCaseKey, @RequestBody ZephyrLinkToJiraRequest request);
}
