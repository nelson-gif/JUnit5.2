package com.junitapp.models;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfSystemProperties;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;

import com.junitapp.exceptions.DineroInsuficienteException;

class CuentaTest {
	
	Cuenta cuenta;
	
	@BeforeAll
	static void beforeAll() {
		System.out.println("Inicializando el test");
	}
	
	@AfterAll
	static void afterAll() {
		System.out.println("Finalizando el test");
	}
	
	@BeforeEach
	void initMetodoTest() {
		this.cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
		System.out.println("iniciando metodo de prueba");
	}
	
	@AfterEach
	void teadDown() {
		System.out.println("finalizando el metodo de prueba");
	}
	
	@Test
	@DisplayName("Probando nombre de la cuenta")
	void testNombreCuenta() {
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
	@DisplayName("probando el saldo de la cuenta corriente, que no sea null, mayor que cero, valor esperado")
	void testSaldoCuenta() {
		
		cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
		
		Assertions.assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
		Assertions.assertNotNull(cuenta.getSaldo());
		Assertions.assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
		Assertions.assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
		
	}
	
	@Test
	@DisplayName("testeando referencias que sean iguales con el metodo equals")
	void testReferenciaCuenta() {
		Cuenta cuenta = new Cuenta("Jhone doe", new BigDecimal("8900.999"));
		Cuenta cuenta2 = new Cuenta("Jhone doe", new BigDecimal("8900.999"));
//		Assertions.assertNotEquals(cuenta, cuenta2);
		Assertions.assertNotNull(cuenta.getSaldo());
		Assertions.assertEquals(cuenta, cuenta2);
	}
	
	@Test
	void testDebitoCuenta() {
		cuenta.debito(new BigDecimal("100"));
		
		Assertions.assertNotNull(cuenta.getSaldo());
		Assertions.assertEquals(900.12345, cuenta.getSaldo().doubleValue());
		Assertions.assertEquals("900.12345", cuenta.getSaldo().toPlainString());
	}

	@Test
	void testCreditoCuenta() {
		cuenta.credito(new BigDecimal("100"));
		
		assertNotNull(cuenta.getSaldo());
		assertEquals(1100.12345, cuenta.getSaldo().doubleValue());
		assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
	}
	
	@Test
	void testDineroInsuficienteExceptionCuenta() {
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
	@DisplayName("probando relaciones entre las cuentas y el banco con assertAll")
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
	
	@DisplayName("Test se ejecuta si entorno es windows")
	@Test
	@EnabledOnOs(OS.WINDOWS)
	void testSoloWindows() {	}
	
	@DisplayName("Test se ejecuta si entorno es linux o mac")
	@Test
	@EnabledOnOs({OS.LINUX, OS.MAC})
	void testSoloLinuxMac() {	}
	
	@Test
	@DisabledOnOs(OS.WINDOWS)
	void testNoWindows() {}
	
	@Test
	@EnabledOnJre(JRE.JAVA_8)
	void soloJdk8() {}
	
	@Test
	@EnabledOnJre(JRE.JAVA_21)
	void soloJdk21() {}
	
	@Test
	@DisabledOnJre(JRE.JAVA_21)
	void noJdk21() {}
	
	@Test
	void printSystemProperties() {
		Properties property = System.getProperties();
		
		property.forEach( (k, v) -> {
			System.out.println(k + ":" + v);
		} );
	}
	
	@Test
	@EnabledIfSystemProperty(named = "java.version", matches = ".*21.*") //expresion regular, que evalue lo que se acerque a 21
	void testJavaVersion() {}
	
	@Test
	@DisabledIfSystemProperty(named = "os.arch:amd64", matches = ".*64.*") //expresion regular, que evalue lo que se acerque a 32
	void testSolo64bits() {	}

	@Test
	void printVariablesAmbiente() {
		Map<String, String> varEnv = System.getenv();
		
		varEnv.forEach( (k, v) -> {
			System.out.println(k+" = "+v);
		});
	}
	
	@Test
	@EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk-21.*")
	void testJavaHome() {}
	
	@Test
	@EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "16")
	void testProcesadores() {}
	
	@Test
	@DisabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "prod")
	void testx() {}

}
