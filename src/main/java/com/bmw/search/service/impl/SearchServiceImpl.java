package com.bmw.search.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bmw.search.dao.InformationDao;
import com.bmw.search.model.Information;
import com.bmw.search.service.SearchService;
import com.bmw.search.util.http.HttpClientUtil;
import com.bmw.search.util.http.HttpResult;
import com.bmw.search.util.redis.RedisUtil;

@Service
@Transactional
public class SearchServiceImpl implements SearchService {
	private static final Logger LOG = LoggerFactory.getLogger(SearchServiceImpl.class);

	private static final String URL = "https://news.baidu.com/widget?id=LocalNews&ajax=json";

	@Autowired
	InformationDao informationDao;

	@Autowired
	HttpClientUtil httpClient;

	@Autowired
	RedisUtil redisUtil;

	@Override
	public List<Information> search(String word) {
		if (StringUtils.isNotBlank(word)) {
			record(word);
			return informationDao.match(word);
		}
		return new ArrayList<Information>();
	}

	@Override
	public void importNews() {
		LOG.info("===[开始调用爬取新闻方法]===");
		try {
			HttpResult result = httpClient.doPost(URL);
			if (200 == result.getCode()) {
				JSONObject vo = JSON.parseObject(result.getBody());
				JSONArray arr = vo.getJSONObject("data").getJSONObject("LocalNews")
						.getJSONObject("data").getJSONObject("rows").getJSONArray("first");
				Date date = new Date();
				for (int i = 0; i < arr.size(); i++) {
					JSONObject o = arr.getJSONObject(i);
					Information info = new Information();
					info.setInfoSrc("baidu");
					info.setInfoImage(o.getString("imgUrl"));
					info.setInfoText(o.getString("title"));
					info.setInfoUrl(o.getString("url"));
					info.setTime(o.getString("time"));
					info.setSingleTag(DigestUtils.md5DigestAsHex(o.getString("title").getBytes()));
					info.setCreateTime(date);
					info.setUpdateTime(date);
					informationDao.insertIfNotExist(info);
					LOG.info("=====插入的id:{}=====", info.getId());
				}
			}
		} catch (Exception e) {
			LOG.error("爬取新闻方法异常：excp={}", e);
		}
		LOG.info("===[结束调用爬取新闻方法]===");
	}

	private Double record(String word) {
		try {
			return redisUtil.zincrby("NEWS_SEARCH:", word, 1);
		} catch (Exception e) {
			LOG.error("===[记录热搜词时出现异常：excp={}]===", e);
		}
		return 0D;
	}

	@Override
	public List<Map<String, Object>> searchRank() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Set<TypedTuple<Object>> set = redisUtil.zrevrangeByScoreWithScores("NEWS_SEARCH:", 0D,
				10000D);
		int i = 1;
		for (TypedTuple tuple : set) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("word", String.valueOf(tuple.getValue()));
			map.put("num", tuple.getScore().intValue());
			map.put("rank", i);
			i++;
			list.add(map);
		}
		return list;
	}
}
