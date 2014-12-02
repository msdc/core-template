package com.isoftstone.crawl.template.dao;

import com.isoftstone.crawl.template.vo.ParseResult;

public interface TSelectorHandler {
	public int select(byte[] input, String encoding, String url,
			ParseResult parseResult);
}
