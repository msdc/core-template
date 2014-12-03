package com.isoftstone.crawl.template.IComponent;

import com.isoftstone.crawl.template.Component.ParseResult;

public interface ISelectorHandler {
	public int select(byte[] input, String encoding, String url,
			ParseResult parseResult);
}
