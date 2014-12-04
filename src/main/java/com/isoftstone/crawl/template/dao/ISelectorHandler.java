package com.isoftstone.crawl.template.dao;

import com.isoftstone.crawl.template.impl.ParseResult;

public interface ISelectorHandler {
	public int select(byte[] input, String encoding, String url,
			ParseResult parseResult);
}
