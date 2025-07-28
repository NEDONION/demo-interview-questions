package com.lucas.coding.whatnot;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Notification Limit 设计类
 * - 设计一个系统，每用户每分钟只能收到不超过 N 条通知
 * - 支持 API：canSend(userId: String, timestamp: Long): boolean
 */

/**
 * 通用令牌桶限流器（Token Bucket Rate Limiter）。
 *
 * 用于控制任意 key（如用户 ID、IP 地址、设备号）在固定时间窗口内的最大请求频率。
 * 支持突发请求，适用于 API 调用控制、消息发送频控等场景。
 *
 * 示例用途：
 * - 每用户每分钟最多发送 N 条通知
 * - 每个 item 每秒最多展示 M 次
 *
 * 核心算法：令牌桶（Token Bucket）
 **/
public class TokenBucketRateLimiter {

	// 每个桶最多可容纳的令牌数量（令牌桶的容量
	private final long maxTokens;

	// 每毫秒生成的令牌数量
	private final double refillRate;

	// 每个 key（如 userId）对应一个令牌桶
	private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

	/**
	 * 构造限流器
	 *
	 * @param maxTokens            每个桶的最大容量（每个 key 最多可执行多少次）
	 * @param refillIntervalMillis 补充周期，例如 60_000 表示每分钟补满 maxTokens 个令牌
	 */
	public TokenBucketRateLimiter(long maxTokens, long refillIntervalMillis) {
		this.maxTokens = maxTokens;
		this.refillRate = (double) maxTokens / refillIntervalMillis;
	}

	/**
	 * 判断当前 key（例如 userId）是否允许执行操作
	 *
	 * @param key             被限流的标识，例如 userId、itemId
	 * @param timestampMillis 当前时间戳（毫秒）
	 * @return true 表示允许执行，false 表示已达限流
	 */
	public boolean allow(String key, long timestampMillis) {
		// 获取桶，如果不存在则创建新桶，初始令牌为满
		Bucket bucket = buckets.get(key);
		if (bucket == null) {
			bucket = new Bucket(maxTokens, timestampMillis);
			buckets.put(key, bucket);
		}

		// 保证每个桶的令牌操作是线程安全的
		synchronized (bucket) {
			// 计算距离上次补充的时间差
			long fromLastRefilled = timestampMillis - bucket.lastRefillTimestamp;

			// 计算应补充的令牌数量
			long tokensToAdd = (long) (fromLastRefilled * refillRate);

			// 如果可以补充，更新令牌数和时间戳
			if (tokensToAdd > 0) {
				bucket.tokens = Math.min(bucket.tokens + tokensToAdd, maxTokens);
				bucket.lastRefillTimestamp = timestampMillis;
			}

			// 如果桶中还有令牌，允许执行，并消耗一个
			if (bucket.tokens > 0) {
				bucket.tokens--;
				return true;
			} else {
				// 否则限流
				return false;
			}
		}
	}

	/**
	 * 内部类：桶对象，表示每个 key 的限流状态
	 */
	private static class Bucket {
		long tokens;               // 当前令牌数量
		long lastRefillTimestamp; // 上次补充令牌的时间

		Bucket(long tokens, long timestamp) {
			this.tokens = tokens;
			this.lastRefillTimestamp = timestamp;
		}
	}

	public static void main(String[] args) throws InterruptedException {
		// 每秒最多 5 个请求
		TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(5, 1000);

		String userId = "user-abc";
		for (int i = 0; i < 10; i++) {
			boolean allowed = limiter.allow(userId, System.currentTimeMillis());
			System.out.println("Request " + i + " allowed: " + allowed);
			Thread.sleep(100); // 模拟请求间隔
		}
	}
}


