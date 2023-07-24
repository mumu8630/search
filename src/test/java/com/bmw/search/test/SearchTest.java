package com.bmw.search.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bmw.search.SearchApplication;
import com.bmw.search.service.SearchService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class SearchTest {
	private static final Logger LOG = LoggerFactory.getLogger(SearchTest.class);
	@Autowired
	SearchService searchService;

	@Test
	public void test() {
		// List<Information> information = searchService.search("刘鹤");
		// System.out.println(JSON.toJSON(information));
		searchService.importNews();
	}
}
