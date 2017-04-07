package com.github.lzy.uniqueid.snowflake;


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
public class IdWorkerTest {
	@Test
	public void getId() throws InterruptedException {
		IdWorker idWorker = new IdWorker(1, 1);

		Set<Long> idSets = new ConcurrentSkipListSet<>();
		ExecutorService executorService = Executors.newFixedThreadPool(50);
		int totalSize = 100000;
		for (int i = 0; i < totalSize; i++) {
			executorService.submit(() -> {
				long unique_id_test = idWorker.getId("unique_id_test");
				idSets.add(unique_id_test);
			});
		}
		executorService.shutdown();
		executorService.awaitTermination(1, TimeUnit.HOURS);
		Assert.assertEquals(idSets.size(), totalSize);
	}

}