package com.lucas.coding.meta;

import java.util.ArrayDeque;
import java.util.Deque;

public class MovingAverage_346 {
	private int size; // 窗口大小
	private Deque<Integer> window;  // 用队列维护窗口内的元素
	private int sum; // 当前窗口内所有元素的和


	public MovingAverage_346(int size) {
		this.size = size;
		this.window = new ArrayDeque<>();
		this.sum = 0;
	}

	// 每次加入一个新值，返回当前窗口的平均值
	public double next(int val) {
		sum += val;
		window.offer(val);

		while (window.size() > size) {
			int needRemove = window.poll(); // 计算需要移除的元素数量
			sum -= needRemove; // 如果队列超过了窗口大小，移除最早的元素
		}

		return (double) sum / window.size();
	}

	public static void main(String[] args) {
		MovingAverage_346 m = new MovingAverage_346(3);
		System.out.println(m.next(1));   // 1.0
		System.out.println(m.next(10));  // (1 + 10) / 2 = 5.5
		System.out.println(m.next(3));   // (1 + 10 + 3) / 3 = 4.66667
		System.out.println(m.next(5));   // (10 + 3 + 5) / 3 = 6.0
	}
}
