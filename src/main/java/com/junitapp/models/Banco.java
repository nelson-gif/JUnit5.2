package com.junitapp.models;

import java.math.BigDecimal;
import java.util.*;

public class Banco {
	
	private String nombre;
	private List<Cuenta> cuentas;

	public Banco() {
		this.cuentas = new ArrayList<>();
	}
	
	public String getNombre() {
		return nombre;
	}

	public List<Cuenta> getCuentas() {
		return cuentas;
	}

	public void setCuentas(List<Cuenta> cuentas) {
		this.cuentas = cuentas;
	}

	public void addCuenta(Cuenta cuenta) {
		cuentas.add(cuenta);
		cuenta.setBanco(this);
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}



	public void transferir(Cuenta origen, Cuenta destino, BigDecimal monto) {
		origen.debito(monto);
		destino.credito(monto);
	}

}
