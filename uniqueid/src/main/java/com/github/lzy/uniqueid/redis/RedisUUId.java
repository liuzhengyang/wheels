package com.github.lzy.uniqueid.redis;

import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static sun.net.www.protocol.http.HttpURLConnection.userAgent;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-04-07
 */
public class RedisUUId {
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisUUId.class);

	private RedissonClient redissonClient;
	private ConcurrentMap<String, RAtomicLong> agentAtomicLongMap = new ConcurrentHashMap<>();

	public RedisUUId() {
		redissonClient = Redisson.create();
	}

	public long getId(String userAgent) {
		RAtomicLong originAtomicLong = agentAtomicLongMap.get(userAgent);
		if (originAtomicLong == null) {
			RAtomicLong newAtomicLong = redissonClient.getAtomicLong(userAgent);
			originAtomicLong = agentAtomicLongMap.putIfAbsent(userAgent, newAtomicLong);
			if (originAtomicLong == null) {
				originAtomicLong = newAtomicLong;
			}
		}
		long id = originAtomicLong.incrementAndGet();
		LOGGER.info("{} Get ID {}", userAgent, id);
		return id;
	}
}
