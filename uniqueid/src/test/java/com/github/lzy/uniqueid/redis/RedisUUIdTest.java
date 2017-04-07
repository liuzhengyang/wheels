package com.github.lzy.uniqueid.redis;


import org.junit.Assert;
import org.junit.Test;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-04-07
 */
public class RedisUUIdTest {
	@Test
	public void getId() throws InterruptedException {
		RedisUUId redisUUId = new RedisUUId();

		Set<Long> idSets = new ConcurrentSkipListSet<>();
		ExecutorService executorService = Executors.newFixedThreadPool(50);
		int totalSize = 10000000;
		for (int i = 0; i < totalSize; i++) {
			executorService.submit(() -> {
				long unique_id_test = redisUUId.getId("unique_id_test");
				idSets.add(unique_id_test);
			});
		}
		executorService.shutdown();
		executorService.awaitTermination(1, TimeUnit.HOURS);
		Assert.assertEquals(idSets.size(), totalSize);
	}

}