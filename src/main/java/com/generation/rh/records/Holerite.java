package com.generation.rh.records;

import java.math.BigDecimal;

public record Holerite(
		
		BigDecimal salarioBruto,
		int diasTrabalhados,
		BigDecimal valorHoraExtra,
		BigDecimal tHorasExtras,
		BigDecimal inss,
		BigDecimal irrf,
		BigDecimal tDescontos,
		BigDecimal salarioLiquido
		
		) {

}
