package com.bmw.search.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bmw.search.model.Information;
import com.bmw.search.service.SearchService;

@Controller
public class SearchController {

	@Autowired
	SearchService searchService;

	@RequestMapping("/api/search")
	@ResponseBody
	public List<Information> search(String word) {
		return searchService.search(word);
	}

	@RequestMapping("/page/search")
	public String searchIndexPage(String word, Model m) {
		m.addAttribute("word", word);
		m.addAttribute("list", searchService.search(word));
		return "search";
	}

	@RequestMapping("/api/rank")
	@ResponseBody
	public List<Map<String, Object>> searchRank() {
		return searchService.searchRank();
	}

	@RequestMapping("/page/rank")
	public String seachRankPage(Model m) {
		m.addAttribute("list", searchService.searchRank());
		return "rank";
	}

}
