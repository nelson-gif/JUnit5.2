package com.junitapp.models;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import com.junitapp.exceptions.DineroInsuficienteException;

class CuentaTest {
	
	Cuenta cuenta;
	private TestInfo testInfo; 
	private TestReporter testReporter;
	
	@BeforeAll
	static void beforeAll() {
		System.out.println("Inicializando el test");
	}
	
	@AfterAll
	static void afterAll() {
		System.out.println("Finalizando el test");
	}
	
	@BeforeEach
	void initMetodoTest(TestInfo testInfo, TestReporter testReporter) {
		this.testInfo = testInfo;
		this.testReporter = testReporter;
		this.cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
		System.out.println("iniciando metodo de prueba");
		
		//System.out.println("ejecutando: "+testInfo.getDisplayName() +" metodo:" +testInfo.getTestMethod().get().getName()+" con las etiquetas" + testInfo.getTags());
		testReporter.publishEntry("ejecutando: "+testInfo.getDisplayName() +" metodo:" +testInfo.getTestMethod().get().getName()+" con las etiquetas" + testInfo.getTags());
	}
	
	@AfterEach
	void teadDown() {
		System.out.println("finalizando el metodo de prueba");
	}
	
	@Tag("cuenta")
	@Nested
	@DisplayName("probando atributos de la cuenta corriente")
	class cuentaTestNombreSaldo{
		@Test
		@DisplayName("el nombre")
		void testNombreCuenta() {
			System.out.println(testInfo.getTags());
			
			if(testInfo.getTags().contains("cuenta")) {
				System.out.println("hacer algo con la etiqueta cuenta");
			}
			
//			cuenta.setPersona("Andres1");
			String esperado = "Andres";
			String real = cuenta.getPersona();
			Assertions.assertNotNull(real, () -> "el nombre no puede ser nulo");
			Assertions.assertTrue(esperado.equals(cuenta.getPersona()), () -> { return "Elnombre de la cuenta no es el esperado, "
					+ "se esperaba: "+esperado+", pero se obtuvo: "+ real;
					});
			Assertions.assertEquals(esperado, real, () -> "nombre esperado debe ser igual al actual");
		}
		
		@Test
		@DisplayName("el saldo, que no sea null, mayor que cero, valor esperado")
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
//			Assertions.assertNotEquals(cuenta, cuenta2);
			Assertions.assertNotNull(cuenta.getSaldo());
			Assertions.assertEquals(cuenta, cuenta2);
		}
	}
	
	@Nested
	class cuentaOperacionesTest{
		
		@Tag("cuenta")
		@Test
		void testDebitoCuenta() {
			cuenta.debito(new BigDecimal("100"));
			
			Assertions.assertNotNull(cuenta.getSaldo());
			Assertions.assertEquals(900.12345, cuenta.getSaldo().doubleValue());
			Assertions.assertEquals("900.12345", cuenta.getSaldo().toPlainString());
		}

		@Tag("cuenta")
		@Test
		void testCreditoCuenta() {
			cuenta.credito(new BigDecimal("100"));
			
			assertNotNull(cuenta.getSaldo());
			assertEquals(1100.12345, cuenta.getSaldo().doubleValue());
			assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
		}
		
		@Tag("cuenta")
		@Tag("banco")
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
	}
	
	@Tag("cuenta")
	@Tag("error")
	@Test
	void testDineroInsuficienteExceptionCuenta() {
		Exception excepcion = assertThrows(DineroInsuficienteException.class, () -> {
			cuenta.debito(new BigDecimal("1500"));
		});
		
		String actual = excepcion.getMessage();
		String esperado = "Dinero Insuficiente";
		
		assertEquals(esperado, actual);
		
	}
	
	
	@Tag("cuenta")
	@Tag("banco")
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
	
	@Nested
	class SistemaOperativoTest{

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
	}
	
	class JavaVersionTest{

		@Test
		@EnabledOnJre(JRE.JAVA_8)
		void soloJdk8() {}
		
		@Test
		@EnabledOnJre(JRE.JAVA_21)
		void soloJdk21() {}
		
		@Test
		@DisabledOnJre(JRE.JAVA_21)
		void noJdk21() {}
	}
	
	@Nested
	class SystemPropertiesTest{

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
	}
	
	@Nested
	class variablesAmbiente{


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
	
	@RepeatedTest(value=5, name="Repeticion numero {currentRepetition} de {totalRepetitions}")
	void testDebitoCuentaRepetir(RepetitionInfo info) {
		
		if(info.getCurrentRepetition() == 3) {
			System.out.println("repetition numero "+info.getCurrentRepetition());
		}
		
		cuenta.debito(new BigDecimal("100"));
		
		Assertions.assertNotNull(cuenta.getSaldo());
		Assertions.assertEquals(900.12345, cuenta.getSaldo().doubleValue());
		Assertions.assertEquals("900.12345", cuenta.getSaldo().toPlainString());
	}
	
	@Nested
	@Tag("param")
	class pruebasParametrizadas{

		@ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}") 
		@ValueSource(strings = {"100", "200", "300", "500", "700", "2000"})
		void testDebitoCuentaParametrizado(String monto) {
			
			cuenta.debito(new BigDecimal(monto));
			
			Assertions.assertNotNull(cuenta.getSaldo());
			assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
		}
		
		@ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}") 
		@CsvSource({"1, 100", "2, 200", "3, 300", "4, 500", "5, 700", "6, 2000"})
		void testDebitoCuentaCsvSource(String index, String monto) {
			System.out.println(index +" -> "+monto);
			cuenta.debito(new BigDecimal(monto));
			
			Assertions.assertNotNull(cuenta.getSaldo());
			assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
		}

		@ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}") 
		@CsvSource({"200, 100, John, Andres", "250, 200, Pepe, Pepe", "310, 300, maria, Maria", "510, 500, Pepa, Pepa", "750, 700, Lucas, Lucas", "2000, 2000, Cata, Cata"})
		void testDebitoCuentaCsvSource2(String saldo, String monto, String esperado, String actual) {
			
			System.out.println(saldo +" -> "+monto);
			cuenta.setSaldo(new BigDecimal(saldo));
			cuenta.debito(new BigDecimal(monto));
			
			cuenta.setPersona(actual);
			
			Assertions.assertNotNull(cuenta.getSaldo());
			Assertions.assertNotNull(cuenta.getPersona());
			assertEquals(esperado, actual);
			assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
		}
		
		@ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}") 
		@CsvFileSource(resources = "/data.csv")
		void testDebitoCuentaCsvFileSource(String monto) {
			cuenta.debito(new BigDecimal(monto));
			
			Assertions.assertNotNull(cuenta.getSaldo());
			assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
		}
	
	
	}
	
	
	@Tag("param")
	@ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}") 
	@MethodSource("montoList")
	void testDebitoCuentaMethodSource(String monto) {
		cuenta.debito(new BigDecimal(monto));
		
		Assertions.assertNotNull(cuenta.getSaldo());
		assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
	}
	
	static List<String> montoList() {
		return Arrays.asList("100", "200", "300", "500", "700", "2000");
	}
	
	@Nested
	@Tag("timeOut")
	class EjemploTimeOut{		
		
		@Test
		@Timeout(5)
		void pruebaTimeOut() throws InterruptedException {
			TimeUnit.SECONDS.sleep(6);
		}
		
		@Test
		@Timeout(value=500, unit = TimeUnit.MILLISECONDS)
		void pruebaTimeOut2() throws InterruptedException {
			TimeUnit.SECONDS.sleep(6);
		}
		
		@Test
		void testTimeOutAssertions() {
			assertTimeout(Duration.ofSeconds(5), () -> {
				TimeUnit.MILLISECONDS.sleep(5500);
			});
		}
		
	}
	

}
