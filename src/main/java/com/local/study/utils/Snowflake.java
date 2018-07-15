public class Snowflake {

	private final long twepoch = 1288834974657L;
	private final long workerIdBits = 5L;
	private final long datacenterIdBits = 5L;
	private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
	private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
	private final long sequenceBits = 12L;
	private final long workerIdShift = sequenceBits;
	private final long datacenterIdShift = sequenceBits + workerIdBits;
	private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
	private final long sequenceMask = -1L ^ (-1L << sequenceBits);

	private long workerId;
	private long datacenterId;
	private long sequence = 0L;
	private long lastTimestamp = -1L;
	private boolean useSystemClock;
	
	/**
	 * 构造
	 * @param workerId 终端ID
	 * @param datacenterId 数据中心ID
	 */
	public Snowflake(long workerId, long datacenterId) {
		this(workerId, datacenterId, false);
	}

	/**
	 * 构造
	 * @param workerId 终端ID
	 * @param datacenterId 数据中心ID
	 * @param isUseSystemClock 是否使用{@link SystemClock} 获取当前时间戳
	 */
	public Snowflake(long workerId, long datacenterId, boolean isUseSystemClock) {
		if (workerId > maxWorkerId || workerId < 0) {
			throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
		}
		if (datacenterId > maxDatacenterId || datacenterId < 0) {
			throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
		}
		this.workerId = workerId;
		this.datacenterId = datacenterId;
		this.useSystemClock = isUseSystemClock;
	}

	/**
	 * 下一个ID
	 * @return ID
	 */
	public synchronized long nextId() {
		long timestamp = useSystemClock ? SystemClock.now() : System.currentTimeMillis();
		if (timestamp < lastTimestamp) {
			throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
		}
		if (lastTimestamp == timestamp) {
			sequence = (sequence + 1) & sequenceMask;
			if (sequence == 0) {
				timestamp = tilNextMillis(lastTimestamp);
			}
		} else {
			sequence = 0L;
		}

		lastTimestamp = timestamp;

		return ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift) | (workerId << workerIdShift) | sequence;
	}

	private long tilNextMillis(long lastTimestamp) {
		long timestamp = useSystemClock ? SystemClock.now() : System.currentTimeMillis();
		while (timestamp <= lastTimestamp) {
			timestamp = useSystemClock ? SystemClock.now() : System.currentTimeMillis();
		}
		return timestamp;
	}
}
