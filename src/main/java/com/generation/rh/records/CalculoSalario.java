package com.generation.rh.records;

import java.math.BigDecimal;

public record CalculoSalario(
		
		int tHorasExtras,
		BigDecimal valorHora,
		BigDecimal descontos) {
	
}
