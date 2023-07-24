package com.bmw.search.service;

import java.util.List;
import java.util.Map;

import com.bmw.search.model.Information;

public interface SearchService {

	List<Information> search(String word);

	void importNews();

	List<Map<String, Object>> searchRank();
}
