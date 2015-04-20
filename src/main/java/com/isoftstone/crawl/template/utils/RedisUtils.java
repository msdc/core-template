package com.isoftstone.crawl.template.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.alibaba.fastjson.JSON;
import com.isoftstone.crawl.template.global.Constants;
import com.isoftstone.crawl.template.impl.ParseResult;
import com.isoftstone.crawl.template.impl.TemplateResult;
import com.isoftstone.crawl.template.vo.DispatchVo;

public class RedisUtils {
	private static JedisPool pool = null;
	private static PropertiesUtils propert = PropertiesUtils.getInstance();
	
	private static final Log LOG = LogFactory.getLog(RedisUtils.class);

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
	
	
	public static void setHtmlResult(String url, String html) {
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			String guid=MD5Utils.MD5(url)+"_rawHtml";
			pool = getPool();
			jedis = pool.getResource();
			jedis.select(Constants.RAWHTML_REDIS_DBINDEX);
			jedis.set(guid, html);
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			e.printStackTrace();
		} finally {
			returnResource(pool, jedis);
		}
	}
	
	public static void setHtmlResult(String url, byte[] html) {
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			String guid=MD5Utils.MD5(url)+"_rawHtml";
			pool = getPool();
			jedis = pool.getResource();
			jedis.select(Constants.RAWHTML_REDIS_DBINDEX);
			jedis.set(guid.getBytes(), html);
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			e.printStackTrace();
		} finally {
			returnResource(pool, jedis);
		}
	}
	
	public static byte[] getHtmlResultByte(String url) {
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			String guid=MD5Utils.MD5(url)+"_rawHtml";
			pool = getPool();
			jedis = pool.getResource();
			jedis.select(Constants.RAWHTML_REDIS_DBINDEX);
			byte[] json = jedis.get(guid.getBytes());
			if (json != null)
				return json;
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			e.printStackTrace();
		} finally {
			returnResource(pool, jedis);
		}
		return null;
	}
	
	public static String getHtmlResult(String url) {
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			String guid=MD5Utils.MD5(url)+"_rawHtml";
			pool = getPool();
			jedis = pool.getResource();
			jedis.select(Constants.RAWHTML_REDIS_DBINDEX);
			String json = jedis.get(guid);
			if (json != null)
				return json;
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			e.printStackTrace();
		} finally {
			returnResource(pool, jedis);
		}
		return null;
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

	 public static byte[] getByteArrayByJSONString(String jsonString) {
		  byte[] byteArray = null;
		  try {
		   ObjectMapper objectmapper = new ObjectMapper();
		   byteArray = objectmapper.readValue(jsonString, byte[].class);
		  } catch (JsonParseException e) {
		   e.printStackTrace();
		  } catch (JsonMappingException e) {
		   e.printStackTrace();
		  } catch (IOException e) {
		   e.printStackTrace();
		  }
		  return byteArray;
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
			if(json!=null && !json.isEmpty())
			{
				return JSONUtils.getParseResultObject(json);
			}
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			e.printStackTrace();
		} finally {
			returnResource(pool, jedis);
		}
		return null;
	}
	
	public static ParseResult getParseResult(String guid) {

		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = getPool();
			jedis = pool.getResource();
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
	
	public static List<DispatchVo> getDispatchListResult(List<String> redisKeys,int dbIndex){
        JedisPool pool = null;
        Jedis jedis = null;
        List<DispatchVo> result= new ArrayList<>();

        try {
            pool = RedisUtils.getPool();
            jedis = pool.getResource();
            jedis.select(dbIndex);
            List<String> json = jedis.mget(redisKeys.toArray(new String[0]));
            if (json != null) {
                for(String js : json)
                    result.add(JSON.parseObject(js, DispatchVo.class));
            }
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            LOG.error("get dispatch result from redis failed", e);
        } finally {
            RedisUtils.returnResource(pool, jedis);
        }
        return result;
    }
}
