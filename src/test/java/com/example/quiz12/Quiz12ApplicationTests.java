package com.example.quiz12;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Quiz12ApplicationTests {

	@Test
	void contextLoads() {
	}
	
	@Test
	public void mapTest() {
		Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();
		map.put(1, List.of("A","三明治","三明治","C"));
		System.out.println(map.get(1));
		System.out.println(map.get(2));
		String str = String.join("", map.get(1)); //ABBC
		System.out.println(str);
		int a = str.length();
		String newStr = str.replace("三明治", "");
		int b = newStr.length();
		System.out.println((a-b)/"三明治".length());

	}

}
