package com.isoftstone.crawl.template.utils;

import com.isoftstone.crawl.template.global.Constants;
import com.isoftstone.crawl.template.impl.ParseResult;
import com.isoftstone.crawl.template.impl.TemplateResult;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtils {
	private static JedisPool pool = null;

	public static JedisPool getPool() {
		if (pool == null) {
			JedisPoolConfig config = new JedisPoolConfig();

			config.setMaxIdle(500);

			config.setMaxIdle(5);
			config.setMaxWaitMillis(1000 * 100);

			config.setTestOnBorrow(true);

			String ip = Constants.REDIS_IP;
			if (PropertiesUtils.getValue("template.redis.ip") != null)
				ip = PropertiesUtils.getValue("template.redis.ip");

			int port = Constants.REDIS_PORT;
			if (PropertiesUtils.getValue("template.redis.port") != null)
				port = Integer.parseInt(PropertiesUtils
						.getValue("template.redis.port"));

			pool = new JedisPool(config, ip, port);
		}
		return pool;
	}

	public static void returnResource(JedisPool pool, Jedis redis) {
		if (redis != null) {
			pool.returnResource(redis);
		}
	}

	public static TemplateResult getTemplateResult(String guid) {
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = getPool();
			jedis = pool.getResource();
			String json = jedis.get(guid);
			if(json!= null)
				return JSONUtils.getTemplateResultObject(json);
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			e.printStackTrace();
		} finally {
			returnResource(pool, jedis);
		}
		return null;
	}

	public static void setTemplateResult(TemplateResult templateResult,
			String guid) {
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			StringBuilder str = new StringBuilder();
			str.append(JSONUtils.getTemplateResultJSON(templateResult));
			pool = getPool();
			jedis = pool.getResource();
			jedis.set(guid, str.toString());
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			e.printStackTrace();
		} finally {
			returnResource(pool, jedis);
		}
	}

	public static void remove(String guid) {
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = getPool();
			jedis = pool.getResource();
			jedis.del(guid);
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			e.printStackTrace();
		} finally {
			returnResource(pool, jedis);
		}
	}

	public static ParseResult getParseResult(String guid) {
	
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = getPool();
			jedis = pool.getResource();
			System.out.println("guid="+guid);
			String json = jedis.get(guid);
			System.out.println("json="+json);
			return JSONUtils.getParseResultObject(json);
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			e.printStackTrace();
		} finally {
			returnResource(pool, jedis);
		}
		return null;
	}

	public static void setParseResult(ParseResult parseResult, String guid) {
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			StringBuilder str = new StringBuilder();
			str.append(JSONUtils.getParseResultJSON(parseResult));
			pool = getPool();
			jedis = pool.getResource();
			jedis.set(guid, str.toString());
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			e.printStackTrace();
		} finally {
			returnResource(pool, jedis);
		}
	}

	public static boolean contains(String guid) {
		JedisPool pool = null;
		Jedis jedis = null;
		boolean flag = false;
		try {
			pool = getPool();
			jedis = pool.getResource();
			flag = jedis.exists(guid);
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			e.printStackTrace();
		} finally {
			returnResource(pool, jedis);
		}
		return flag;
	}
}
