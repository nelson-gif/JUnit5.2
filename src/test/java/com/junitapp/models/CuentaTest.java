package com.junitapp.models;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.*;

import com.junitapp.exceptions.DineroInsuficienteException;

class CuentaTest {
	
	@Test
	void testNombreCuenta() {
		Cuenta cuenta = new Cuenta("Andres", new BigDecimal("1000.2343"));
//		cuenta.setPersona("Andres1");
		String esperado = "Andres";
		String real = cuenta.getPersona();
		Assertions.assertNotNull(real);
		Assertions.assertTrue(esperado.equals(cuenta.getPersona()));
		Assertions.assertEquals(esperado, real);
	}
	
	@Test
	void testSaldoCuenta() {
		
		Cuenta cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
		
		Assertions.assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
		Assertions.assertNotNull(cuenta.getSaldo());
		Assertions.assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
		Assertions.assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
		
	}
	
	@Test
	void testReferenciaCuenta() {
		Cuenta cuenta = new Cuenta("Jhone doe", new BigDecimal("8900.999"));
		Cuenta cuenta2 = new Cuenta("Jhone doe", new BigDecimal("8900.999"));
//		Assertions.assertNotEquals(cuenta, cuenta2);
		Assertions.assertNotNull(cuenta.getSaldo());
		Assertions.assertEquals(cuenta, cuenta2);
	}
	
	@Test
	void testDebitoCuenta() {
		Cuenta cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
		cuenta.debito(new BigDecimal("100"));
		
		Assertions.assertNotNull(cuenta.getSaldo());
		Assertions.assertEquals(900.12345, cuenta.getSaldo().doubleValue());
		Assertions.assertEquals("900.12345", cuenta.getSaldo().toPlainString());
	}

	@Test
	void testCreditoCuenta() {
		Cuenta cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
		cuenta.credito(new BigDecimal("100"));
		
		assertNotNull(cuenta.getSaldo());
		assertEquals(1100.12345, cuenta.getSaldo().doubleValue());
		assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
	}
	
	@Test
	void testDineroInsuficienteExceptionCuenta() {
		Cuenta cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
		Exception excepcion = assertThrows(DineroInsuficienteException.class, () -> {
			cuenta.debito(new BigDecimal("1500"));
		});
		
		String actual = excepcion.getMessage();
		String esperado = "Dinero Insuficiente";
		
		assertEquals(esperado, actual);
		
	}
	
}
