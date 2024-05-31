package com.junitapp.models;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.*;

import com.junitapp.exceptions.DineroInsuficienteException;

class CuentaTest {
	
	@Test
	void testNombreCuenta() {
		Cuenta cuenta = new Cuenta("Andres as", new BigDecimal("1000.2343"));
//		cuenta.setPersona("Andres1");
		String esperado = "Andres";
		String real = cuenta.getPersona();
		Assertions.assertNotNull(real, () -> "el nombre no puede ser nulo");
		Assertions.assertTrue(esperado.equals(cuenta.getPersona()), () -> { return "Elnombre de la cuenta no es el esperado, "
				+ "se esperaba: "+esperado+", pero se obtuvo: "+ real;
				});
		Assertions.assertEquals(esperado, real, () -> "nombre esperado debe ser igual al actual");
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
	
	@Test
	void testTransferirDineroCuentas() {
		Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
		Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));
		Banco banco = new Banco();
		banco.setNombre("Bando del Estado");
		banco.transferir(cuenta2, cuenta1, new BigDecimal("500"));
		
		assertEquals("1000.8989",cuenta2.getSaldo().toPlainString());
		assertEquals("3000",cuenta1.getSaldo().toPlainString());
	}
	
	@Test
	void testRelacionBancoCuentas() {
		Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
		Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));
		
		Banco banco = new Banco();
		banco.addCuenta(cuenta1);
		banco.addCuenta(cuenta2);
		banco.setNombre("Banco del Estado");
		banco.transferir(cuenta2, cuenta1, new BigDecimal("500"));
		
		assertAll( () -> {
				assertEquals("1000.8989",cuenta2.getSaldo().toPlainString(), 
						() -> "El saldo de la cuenta2 no es el esperado");			
			}, 
			() -> {
				assertEquals("3000",cuenta1.getSaldo().toPlainString(), 
						() -> "El saldo de la cuenta1 no es el esperado");				
			}, 
			() -> {				
				assertEquals(2, banco.getCuentas().size(), 
						() -> "El banco no tiene las cuentas esperadas");//verifying we only have 2 acc in the bank
			},
			() -> {
				assertEquals("Banco del Estado", cuenta1.getBanco().getNombre(),
						() -> "La cuenta 1 no pertenece al banco del estado"); //validating this acc belongs to Banco del Estado
			},
			() -> {				
				//validating there is an account under the name andres
				assertEquals("Andres", banco.getCuentas().stream()
						.filter(  cuenta -> cuenta.getPersona().equals("Andres"))
						.findFirst()
						.get().getPersona(),
						() -> "No existe cuenta a nombre de Andres"
						);
			},
			() -> {				
				//validating there is an account under the name andres, mismo codigo de arriba pero usando assertTrue
				assertTrue(banco.getCuentas().stream()
						.filter(  cuenta -> cuenta.getPersona().equals("Andres"))
						.findFirst().isPresent()
						);
			},
			() -> {				
				//validating there is an account under the name andres, mismo codigo de arriba pero usando anyMatch en vez de is present
				assertTrue(banco.getCuentas().stream()
						.anyMatch(cuenta -> cuenta.getPersona().equals("Jhon Doe"))
						);
			}
			
			);
		
	}
	
}
