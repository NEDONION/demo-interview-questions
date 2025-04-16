package com.lucas.demo.coding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/***
 * 1.given csvString = “employer,employee,role
 * 			amazon,becky, senior
 * 			google, john, junior ”
 *
 * given a map<String,List<String>> to store list under each header. each header as key in the map
 * 2.stream api: given “amazon”, find how many strings are matching
 */
public class Coding_202504_csvString {

	// 简单测试
	public static void main(String[] args) {
		String csv = "employer,employee,role\n" +
				"amazon,becky,senior\n" +
				"google,john,junior";

		Map<String, List<String>> dataMap = parseCSVToMap(csv);
		long count = countMatches(dataMap, "employer", "amazon");

		System.out.println("CSV Map: " + dataMap);
		System.out.println("匹配 'amazon' 的个数: " + count);
	}


	// 方法1：解析CSV成Map
	public static Map<String, List<String>> parseCSVToMap(String csv) {
		String[] lines = csv.strip().split("\n");
		String[] headers = lines[0].split(",");

		Map<String, List<String>> map = Arrays.stream(headers)
				.map(String::trim)
				.collect(Collectors.toMap(h -> h, h -> new ArrayList<>()));

		System.out.println("parseCSVToMap#init map: " + map);
		for (int i = 1; i < lines.length; i++) {
			String[] values = lines[i].split(",");
			for (int j = 0; j < headers.length; j++) {
				// remove spaces
				map.get(headers[j].trim()).add(values[j].trim());
			}
		}

		System.out.println("parseCSVToMap#finalize map: " + map);
		return map;
	}

	// 方法2：查询某列中匹配关键字的数量
	public static long countMatches(Map<String, List<String>> map, String column, String keyword) {
		return map.getOrDefault(column, Collections.emptyList())
				.stream()
				.filter(val -> val.equals(keyword))
				.count();
	}

}
