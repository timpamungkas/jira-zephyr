package com.jira;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "jiraClient", url = "https://bfifinance.atlassian.net")
public interface JiraClient {

	@GetMapping(value = "/rest/api/2/search")
	public JiraIssueResponse search(@RequestHeader(name = "Authorization") String authHeader,
			@RequestParam(name = "jql", required = true) String jql,
			@RequestParam(name = "maxResults", required = true, defaultValue = "100") int maxResults,
			@RequestParam(name = "startAt", required = true, defaultValue = "0") int startAt,
			@RequestParam(name = "fields", required = true, defaultValue = "id,key") String fields);

}
