package com.github.lzy.uniqueid.snowflake;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-04-07
 */
public class IdWorker {
	private static final Logger LOGGER = LoggerFactory.getLogger(IdWorker.class);

	private Random random = new Random();

	private static final long Epoch = 1288834974657L;
	private static final long workerIdBits = 5L;
	private static final long maxWorkerId = -1L ^ (-1L << workerIdBits);
	private static final long dataCenterIdBits = 5L;
	private static final long maxDataCenterIdBits = -1L ^ (-1L << dataCenterIdBits);
	private static final long sequenceBits = 12L;

	private static final long workerIdShift = sequenceBits;
	private static final long dataCenterIdShift = sequenceBits + workerIdBits;
	private static final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;
	private static final long sequenceMask = -1L ^ (-1L << sequenceBits);

	private static long lastTimestamp = -1L;

	private long workerId;
	private long dataCenterId;
	private long sequence = 0L;

	public IdWorker(long workerId, long dataCenterId) {
		this.workerId = workerId;
		this.dataCenterId = dataCenterId;
	}

	public long getId(String userAgent) {
		long id = nextId();
		LOGGER.info("{} Get Id {}", userAgent, id);
		return id;
	}

	private synchronized long nextId() {
		long timestamp = timeGen();

		if (timestamp < lastTimestamp) {
			LOGGER.error("clock is moving backwards. Rejecting requests until {}", lastTimestamp);
			throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds"
					,(lastTimestamp - timestamp)));
		}

		if (timestamp == lastTimestamp) {
			sequence = (sequence + 1) & sequenceMask;
			if (sequence == 0) {
				timestamp = tilNextMillis(lastTimestamp);
			}
		} else {
			sequence = 0;
		}

		lastTimestamp = timestamp;

		return ((timestamp - Epoch) << timestampLeftShift) | (dataCenterId << dataCenterIdShift) |
				(workerId << workerIdShift) | sequence;
	}

	private long tilNextMillis(long lastTimestamp) {
		long timestamp = timeGen();
		while (timestamp <= lastTimestamp) {
			timestamp = timeGen();
		}
		return timestamp;
	}

	private long timeGen() {
		return System.currentTimeMillis();
	}
}
