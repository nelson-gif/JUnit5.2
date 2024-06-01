package com.junitapp.junit5_app;

import java.math.BigDecimal;
import java.util.Properties;

import com.junitapp.models.*;

public class Test {

	private static Cuenta cuenta;
	
	public static void main(String args[]) {
//		cuenta = new Cuenta("ernesto", new BigDecimal("234"));
//		cuenta.debito(new BigDecimal("1500"));
		
		Properties property = System.getProperties();
		property.forEach( (k, v) -> {
			System.out.println(k+":"+v);
		});
	}
	
}
