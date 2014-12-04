package com.isoftstone.crawl.template.dao;

import java.util.ArrayList;

public interface IIndexerHandler {

	public ArrayList<String> index(byte[] input, String encoding, String url);
}
