package com.mybaas.currencyanalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CurrencyanalyzerApplication {

	public static void main(String[] args) {
		System.out.println("EXECUTING : command line runner");
		SpringApplication.run(CurrencyanalyzerApplication.class, args);
	}

	//@Override
	public void run(String... args) {
		System.out.println("EXECUTING : command line runner");

		for (int i = 0; i < args.length; ++i) {
			System.out.println(String.format("args[{%d}]: {%s}", i, args[i]));
		}
	}

}
