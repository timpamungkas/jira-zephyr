package com.jira;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class JiraZephyrApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(JiraZephyrApplication.class, args);
	}

	@Autowired
	private JiraClient jiraClient;

	@Autowired
	private ZephyrClient zephyrClient;

	@Override
	public void run(String... args) throws Exception {
		// TODO : change this project key
		var projectKey = "JIRA";

		// TODO Change this into base64(yourJiraEmail:yourJiraApiToken)
		var jiraCredential = "changeThisToYourJiraCredential";

		// TODO Change this into your zephyr access token
		var zephyrAccessToken = "changeThisToYourZephyrAccessToken";

		int startAt = 0;
		int maxResults = 100;
		int totalPages = 0;
		var allJiraIssues = new HashMap<String, Integer>();
		var allZephyrTestCases = new HashMap<String, Integer>();

		do {
			System.out.println("Fetching jira startAt " + startAt);
			var jiraIssuesResponse = jiraClient.search("Basic " + jiraCredential, "project=" + projectKey, maxResults,
					startAt, "id,key");

			totalPages = (int) Math.ceil(jiraIssuesResponse.getTotal() / maxResults);

			if (totalPages == 0) {
				break;
			}

			startAt += maxResults;
			jiraIssuesResponse.getIssues().forEach(issue -> {
				allJiraIssues.put(issue.getKey().trim(), issue.getId());
			});
		} while (startAt <= (maxResults * totalPages));

		System.out.println("Total issues : " + allJiraIssues.size());

		startAt = 0;
		maxResults = 500;
		totalPages = 0;
		var zephyrAuthHeader = "Bearer " + zephyrAccessToken;

		do {
			System.out.println("Fetching zephyr startAt " + startAt);
			var zephyrTestCaseResponse = zephyrClient.search(zephyrAuthHeader, projectKey, maxResults, startAt);

			totalPages = (int) Math.ceil(zephyrTestCaseResponse.getTotal() / maxResults);

			if (totalPages == 0) {
				break;
			}

			startAt += maxResults;

			for (var testCase : zephyrTestCaseResponse.getValues()) {
				try {
					var projectKeyIndex = testCase.getName().lastIndexOf(projectKey);
					var projectKeyInTestCase = testCase.getName().substring(projectKeyIndex,
							testCase.getName().indexOf(" ", projectKeyIndex));

					var jiraIssueId = allJiraIssues.get(projectKeyInTestCase);
					if (jiraIssueId == null) {
						continue;
					}

					allZephyrTestCases.put(testCase.getKey(), jiraIssueId);
					System.out.println("Fetching zephyr " + testCase.getKey() + " : " + jiraIssueId);
				} catch (Exception e) {
					System.err.println(e.getMessage() + " for " + testCase.getName());
					continue;
				}
			}
		} while (startAt <= (maxResults * totalPages));

		var counter = new AtomicInteger();

		allZephyrTestCases.keySet().parallelStream().forEach(tc -> {
			try {
				var body = new ZephyrLinkToJiraRequest();
				body.setIssueId(allZephyrTestCases.get(tc));

				System.out.println("[" + counter.incrementAndGet() + " of " + allZephyrTestCases.size() + "]"
						+ " Linking " + tc + ":" + allZephyrTestCases.get(tc));
				zephyrClient.linkToJira(zephyrAuthHeader, tc, body);
			} catch (Exception e) {
				// ignore
			}
		});
	}

}
