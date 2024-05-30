package com.junitapp.models;

import java.math.BigDecimal;

import org.junit.jupiter.api.*;

class CuentaTest {
	
	@Test
	void testNombreCuenta() {
		Cuenta cuenta = new Cuenta("Andres", new BigDecimal("1000.2343"));
//		cuenta.setPersona("Andres1");
		String esperado = "Andres";
		String real = cuenta.getPersona();
		
		Assertions.assertTrue(esperado.equals(cuenta.getPersona()));
		Assertions.assertEquals(esperado, real);
	}
	
	@Test
	void testSaldoCuenta() {
		
		Cuenta cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
		
		Assertions.assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
		Assertions.assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
		Assertions.assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
		
	}

}
