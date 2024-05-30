package com.junitapp.models;

import java.math.BigDecimal;
import java.util.Objects;

public class Cuenta {
	
	private String persona;
	private BigDecimal saldo;
	
	
	
	public Cuenta(String persona, BigDecimal saldo) {
		this.saldo = saldo;
		this.persona = persona;
	}
	
	public String getPersona() {
		return persona;
	}
	
	public void setPersona(String persona) {
		this.persona = persona;
	}
	
	public BigDecimal getSaldo() {
		return saldo;
	}
	
	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}
	
	public void debito(BigDecimal monto) {
		this.saldo = this.saldo.subtract(monto);
	}
	
	public void credito(BigDecimal monto) {
		this.saldo = this.saldo.add(monto);
	}

	@Override
	public int hashCode() {
		return Objects.hash(persona, saldo);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cuenta other = (Cuenta) obj;
		return Objects.equals(persona, other.persona) && Objects.equals(saldo, other.saldo);
	}
	
	

}
