package com.isoftstone.crawl.template.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.isoftstone.crawl.template.dao.TSelectorHandler;
import com.isoftstone.crawl.template.global.Constants;
import com.isoftstone.crawl.template.vo.ParseResult;
import com.isoftstone.crawl.template.vo.Selector;
import com.isoftstone.crawl.template.vo.SelectorFilter;
import com.isoftstone.crawl.template.vo.SelectorIndexer;

public class PaginationSelector implements TSelectorHandler {
	private static final Log LOG = LogFactory.getLog(PaginationSelector.class);

	private Selector selector;

	public PaginationSelector() {

	}

	public PaginationSelector(Selector selector) {
		this.selector = selector;
	}

	@Override
	public int select(byte[] input, String encoding, String url,
			ParseResult parseResult) {
		String type = this.selector.getPagitationType();
		List<SelectorIndexer> indexers = selector.getIndexers();
		String current = this.selector.getCurrent();
		String replaceTo = this.selector.getReplaceTo();
		String pagitationUrl = this.selector.getPagitationUrl();
		String startNumber = this.selector.getStartNumber();

		if (Constants.PAGINATION_TYPE_PAGE.equals(type)) {
			if (indexers != null) {
				ArrayList<String> outlinks = new ArrayList<String>();
				for (int i = 0; i < indexers.size(); i++) {
					ArrayList<String> results = indexers.get(i).process(input,
							encoding, url);
					if (results.size() > 0) {
						outlinks.addAll(results);
					}
				}
				parseResult.setResult(Constants.PAGINATION_OUTLINK,
						String.valueOf(outlinks.size()));
				for (int i = 0; i < outlinks.size(); i++) {
					parseResult.setResult(Constants.PAGINATION_OUTLINK + "_"
							+ i, outlinks.get(i));
				}
				return 1;
			} else {
				LOG.error("Pagitation selector defined error.");
				return -1;
			}
		} else if (Constants.PAGINATION_TYPE_PAGENUMBER.equals(type)) {
			if (indexers != null) {
				String lastNumber = "0";
				for (int i = 0; i < indexers.size(); i++) {
					ArrayList<String> results = indexers.get(i).process(input,
							encoding, url);
					if (results.size() > 0) {
						lastNumber = results.get(0);
						break;
					}
				}
				// 处理过滤器
				List<SelectorFilter> filters = selector.getFilters();
				if (filters != null) {
					for (int j = 0; j < filters.size(); j++) {
						lastNumber = filters.get(j).process(lastNumber);
					}
				}
				int last = Integer.parseInt(lastNumber);
				int start = Integer.parseInt(startNumber);
				parseResult.setResult(Constants.PAGINATION_OUTLINK,
						String.valueOf(last - 1));
				for (int i = 0; i <= last - 1; i++) {
					parseResult.setResult(Constants.PAGINATION_OUTLINK + "_"
							+ i,
							pagitationUrl.replace(current, replaceTo + start));
					start++;
				}
				return 1;
			} else {
				LOG.error("Pagitation selector defined error.");
				return -1;
			}
		} else if (Constants.PAGINATION_TYPE_PAGERECORD.equals(type)) {
			if (indexers != null) {
				String totalRecordNumber = "0";
				for (int i = 0; i < indexers.size(); i++) {
					ArrayList<String> results = indexers.get(i).process(input,
							encoding, url);
					if (results.size() > 0) {
						totalRecordNumber = results.get(0);
						break;
					}
				}
				// 处理过滤器
				List<SelectorFilter> filters = selector.getFilters();
				if (filters != null) {
					for (int j = 0; j < filters.size(); j++) {
						totalRecordNumber = filters.get(j).process(
								totalRecordNumber);
					}
				}
				double total = Double.parseDouble(totalRecordNumber);
				double record = Double.parseDouble(this.selector
						.getRecordNumber());
				int last = (int) Math.ceil(total / record);
				int start = Integer.parseInt(startNumber);
				parseResult.setResult(Constants.PAGINATION_OUTLINK,
						String.valueOf(last - 1));
				for (int i = 0; i < last - 1; i++) {
					parseResult.setResult(Constants.PAGINATION_OUTLINK + "_"
							+ i,
							pagitationUrl.replace(current, replaceTo + start));
					start++;
				}
				return 1;
			} else {
				LOG.error("Pagination selector defined error.");
				return -1;
			}
		} else {
			return -1;
		}
	}
}
