package com.isoftstone.crawl.template.IComponent;

import java.util.ArrayList;

public interface IIndexerHandler {

	public ArrayList<String> index(byte[] input, String encoding, String url);
}
