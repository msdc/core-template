package com.isoftstone.crawl.template.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.isoftstone.crawl.template.global.Constants;
import com.isoftstone.crawl.template.impl.ParseResult;
import com.isoftstone.crawl.template.impl.TemplateResult;

public class RedisUtils {
	private static JedisPool pool = null;
	private static PropertiesUtils propert = PropertiesUtils.getInstance();

	public static JedisPool getPool() {
		if (pool == null) {
			JedisPoolConfig config = new JedisPoolConfig();

			config.setMaxTotal(1024);
			// 最大能够保持idel状态的对象数
			config.setMaxIdle(500);
			// 当池内没有返回对象时，最大等待时间
			config.setMaxWaitMillis(1000 * 100);
			// 当调用borrow Object方法时，是否进行有效性检查
			config.setTestOnBorrow(true);

			String ip = Constants.REDIS_IP;
			if (propert.getValue("template.redis.ip") != null)
				ip = propert.getValue("template.redis.ip");

			int port = Constants.REDIS_PORT;
			if (propert.getValue("template.redis.port") != null)
				port = Integer.parseInt(propert.getValue("template.redis.port"));

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
			if (json != null)
				return JSONUtils.getTemplateResultObject(json);
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			e.printStackTrace();
		} finally {
			returnResource(pool, jedis);
		}
		return null;
	}

	public static TemplateResult getTemplateResult(String guid, int dbindex) {
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = getPool();
			jedis = pool.getResource();
			jedis.select(dbindex);
			String json = jedis.get(guid);
			if (json != null)
				return JSONUtils.getTemplateResultObject(json);
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			e.printStackTrace();
		} finally {
			returnResource(pool, jedis);
		}
		return null;
	}

	
	public static void setTemplateResult(TemplateResult templateResult, String guid) {
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
	
	public static void setTemplateResult(TemplateResult templateResult, String guid, int dbindex) {
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			StringBuilder str = new StringBuilder();
			str.append(JSONUtils.getTemplateResultJSON(templateResult));
			pool = getPool();
			jedis = pool.getResource();
			jedis.select(dbindex);
			jedis.set(guid, str.toString());
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			e.printStackTrace();
		} finally {
			returnResource(pool, jedis);
		}
	}

	public static void saveStr(String str, String guid, int dbindex) {
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = getPool();
			jedis = pool.getResource();
			jedis.select(dbindex);
			jedis.set(guid, str);
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			e.printStackTrace();
		} finally {
			returnResource(pool, jedis);
		}
	}

	public static long remove(String guid) {
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = getPool();
			jedis = pool.getResource();
			return jedis.del(guid);
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			e.printStackTrace();
		} finally {
			returnResource(pool, jedis);
		}
		return -1;
	}

	public static long remove(String guid, int dbindex) {
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = getPool();
			jedis = pool.getResource();
			jedis.select(dbindex);
			return jedis.del(guid);
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			e.printStackTrace();
		} finally {
			returnResource(pool, jedis);
		}
		return -1;
	}

	public static ParseResult getParseResult(String guid, int dbindex) {

		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = getPool();
			jedis = pool.getResource();
			jedis.select(dbindex);
			// System.out.println("guid=" + guid);
			String json = jedis.get(guid);
			// System.out.println("json=" + json);
			return JSONUtils.getParseResultObject(json);
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			e.printStackTrace();
		} finally {
			returnResource(pool, jedis);
		}
		return null;
	}

	public static void setParseResult(ParseResult parseResult, String guid, int dbindex) {
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			StringBuilder str = new StringBuilder();
			str.append(JSONUtils.getParseResultJSON(parseResult));
			pool = getPool();
			jedis = pool.getResource();
			jedis.select(dbindex);
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

	public static boolean contains(String guid, int dbindex) {
		JedisPool pool = null;
		Jedis jedis = null;
		boolean flag = false;
		try {
			pool = getPool();
			jedis = pool.getResource();
			jedis.select(dbindex);
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
