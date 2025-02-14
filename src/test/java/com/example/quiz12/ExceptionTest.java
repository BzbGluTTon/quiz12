package com.example.quiz12;

import java.util.Scanner;

import org.junit.jupiter.api.Test;

public class ExceptionTest {

	@Test
	public void test() {
		// try-finally
		try {
			int x = 5;
			int y = 0;
			System.out.println(x / y);
		} catch (ArithmeticException e) {
			System.out.println(e);
			return;
		} finally { // 不管如何都會執行
			System.out.println("!!!!!!!!!!!!!");
		}

		// try-catch
		try {
			int x = 5;
			int y = 0;
			System.out.println(x / y);
		} catch (ArithmeticException e) {
			System.out.println(e);
			// 寫 log 紀錄
			return;
		}
		System.out.println("==============================");
	}

	@Test
	public void test1() {
		Scanner scan = new Scanner(System.in);

		try {

		} catch (Exception e) {

		} finally {
			scan.close();
		}
		int a = scan.nextInt();
	}
	
	@Test
	public void test2() {
		

		try(Scanner scan = new Scanner(System.in)) {

		} catch (Exception e) {

		} 
	}
}
