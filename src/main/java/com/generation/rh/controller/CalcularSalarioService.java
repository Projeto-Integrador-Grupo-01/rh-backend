package com.generation.rh.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.generation.rh.model.Colaborador;
import com.generation.rh.records.CalculoSalario;
import com.generation.rh.records.Holerite;
import com.generation.rh.records.Imposto;
import com.generation.rh.repository.ColaboradorRepository;



/**
 * Service responsável pelo cálculo de salários, descontos de impostos e geração de holerites.
 * - Precisão correta da alíquota (sem arredondar para 2 casas).
 * - Tratamento do teto do INSS (não zera acima do teto).
 * - IRRF sem dedução por dependentes.
 *
 * Convenções:
 * - Holerite.tHorasExtras = VALOR TOTAL em R$ das horas extras.
 * - CalculoSalario.tHorasExtras = QUANTIDADE de horas extras.
 */
@Service
public class CalcularSalarioService {

    @Autowired
    private ColaboradorRepository colaboradorRepository;

    // Dinheiro (centavos)
    private static final int SCALE = 2;

    // Mais casas para percentuais (ex.: 7,5% = 0,075000)
    private static final int SCALE_PERCENT = 6;

    // Multiplicador para hora extra (50% de acréscimo = 1.5x)
    private static final BigDecimal PERCENTUAL_HORA_EXTRA = new BigDecimal("1.5");

    // Tabela INSS 2025: (limite, aliquota %, deducao)
    private static final List<Imposto> FAIXAS_INSS = List.of(
        new Imposto(1518.00,  7.5,   0.00),
        new Imposto(2793.87,  9.0,  28.80),
        new Imposto(4190.82, 12.0, 135.57),
        new Imposto(8381.66, 14.0, 259.17) // teto
    );

    // Tabela IRRF 2025: (limite, aliquota %, deducao)
    private static final List<Imposto> FAIXAS_IRRF = List.of(
        new Imposto(2352.00,          0.0,   0.00),
        new Imposto(2826.65,          7.5, 176.15),
        new Imposto(3751.05,         15.0, 404.78),
        new Imposto(4664.68,         22.5, 694.54),
        new Imposto(Double.MAX_VALUE, 27.5, 917.24)
    );

    /**
     * Calcula o holerite do colaborador.
     *
     * @param id            ID do colaborador
     * @param dadosSalario  tHorasExtras (quantidade), valorHora (opcional), descontos (opcional)
     * @return Holerite preenchido
     */
    public Holerite calcularSalario(Long id, CalculoSalario dadosSalario) {
        // 1) Colaborador
        Colaborador colaborador = buscarColaborador(id);

        // 2) Salário/hora (prioriza o valor enviado; senão calcula salario/horasMensais)
        BigDecimal salarioPorHora = obterSalarioHora(colaborador, dadosSalario);

        // 3) Valor unidade da hora extra (1,5x) e VALOR TOTAL das HEs
        BigDecimal valorHoraExtra = salarioPorHora.multiply(PERCENTUAL_HORA_EXTRA)
                                                 .setScale(SCALE, RoundingMode.HALF_UP);

        BigDecimal totalHEValor = valorHoraExtra
                .multiply(BigDecimal.valueOf(dadosSalario.tHorasExtras()))
                .setScale(SCALE, RoundingMode.HALF_UP);

        // 4) Base bruta considerada para impostos = salário base + valor total de HE
        BigDecimal salarioBrutoComHE = colaborador.getSalario()
                .add(totalHEValor)
                .setScale(SCALE, RoundingMode.HALF_UP);

        // 5) Descontos legais (sem dependentes no IRRF)
        BigDecimal descontoINSS = calcularINSS(salarioBrutoComHE);
        BigDecimal descontoIRRF = calcularIRRF(salarioBrutoComHE, descontoINSS);

        // 6) Descontos adicionais (nulos -> 0)
        BigDecimal descontosAdicionais = Optional.ofNullable(dadosSalario.descontos())
                                                 .orElse(BigDecimal.ZERO)
                                                 .setScale(SCALE, RoundingMode.HALF_UP);

        // 7) Total de descontos e líquido
        BigDecimal tDescontos = descontoINSS.add(descontoIRRF).add(descontosAdicionais)
                                            .setScale(SCALE, RoundingMode.HALF_UP);

        BigDecimal salarioLiquido = salarioBrutoComHE.subtract(tDescontos)
                                                     .setScale(SCALE, RoundingMode.HALF_UP);

        // 8) Montagem do Holerite
        return new Holerite(
            colaborador.getSalario().setScale(SCALE, RoundingMode.HALF_UP), // salário base (sem HE)
            obterDiasTrabalhados(colaborador),                                // ajuste conforme sua regra
            valorHoraExtra,                                                   // valor de 1h extra
            totalHEValor,                                                     // VALOR total em R$ de HEs
            descontoINSS,
            descontoIRRF,
            tDescontos,
            salarioLiquido
        );
    }

    // =================== Auxiliares ===================

    private Colaborador buscarColaborador(Long id) {
        return colaboradorRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Colaborador não encontrado"));
    }

    private BigDecimal obterSalarioHora(Colaborador colaborador, CalculoSalario dados) {
        if (dados.valorHora() != null) {
            return dados.valorHora().setScale(SCALE, RoundingMode.HALF_UP);
        }
        return calcularSalarioPorHora(colaborador);
    }

    private BigDecimal calcularSalarioPorHora(Colaborador colaborador) {
        return colaborador.getSalario()
            .divide(new BigDecimal(colaborador.getHorasMensais()), SCALE, RoundingMode.HALF_UP);
    }

    /**
     * INSS com teto: se a base excede a última faixa, aplica a última faixa sobre o teto.
     */
    private BigDecimal calcularINSS(BigDecimal salarioBruto) {
        return buscarFaixaAplicavelComTeto(salarioBruto, FAIXAS_INSS);
    }

    /**
     * IRRF sem dependentes.
     * Base = salárioBruto − INSS.
     */
    private BigDecimal calcularIRRF(BigDecimal salarioBruto, BigDecimal descontoINSS) {
        BigDecimal baseDeCalculo = salarioBruto
            .subtract(descontoINSS)
            .setScale(SCALE, RoundingMode.HALF_UP);

        return buscarFaixaAplicavel(baseDeCalculo, FAIXAS_IRRF);
    }

    /**
     * Busca faixa aplicável (para IRRF).
     */
    private BigDecimal buscarFaixaAplicavel(BigDecimal valor, List<Imposto> faixas) {
        for (Imposto faixa : faixas) {
            if (valor.compareTo(BigDecimal.valueOf(faixa.limite())) <= 0) {
                return aplicarFaixa(valor, faixa);
            }
        }
        // Segurança (IRRF tem MAX_VALUE, então não deve cair aqui)
        return BigDecimal.ZERO.setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Busca faixa com tratamento de teto (para INSS).
     */
    private BigDecimal buscarFaixaAplicavelComTeto(BigDecimal valor, List<Imposto> faixas) {
        for (Imposto faixa : faixas) {
            if (valor.compareTo(BigDecimal.valueOf(faixa.limite())) <= 0) {
                return aplicarFaixa(valor, faixa);
            }
        }
        // Acima do teto: aplica a última faixa sobre o próprio teto
        Imposto ultima = faixas.get(faixas.size() - 1);
        BigDecimal teto = BigDecimal.valueOf(ultima.limite()).setScale(SCALE, RoundingMode.HALF_UP);
        return aplicarFaixa(teto, ultima);
    }

    /**
     * Aplica (base × % − dedução) com alta precisão na alíquota.
     * Resultado monetário arredondado para 2 casas. Nunca negativo.
     */
    private BigDecimal aplicarFaixa(BigDecimal base, Imposto faixa) {
        BigDecimal aliquotaDecimal = BigDecimal
            .valueOf(faixa.aliquota())
            .divide(new BigDecimal("100"), SCALE_PERCENT, RoundingMode.HALF_UP);

        BigDecimal valorAliquota = base.multiply(aliquotaDecimal).setScale(SCALE, RoundingMode.HALF_UP);
        BigDecimal desconto = valorAliquota.subtract(BigDecimal.valueOf(faixa.deducao()));
        return desconto.max(BigDecimal.ZERO).setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Ajuste conforme seu modelo (se existir no Colaborador, use o getter real).
     */
    private int obterDiasTrabalhados(Colaborador c) {
        return 30;
    }
}
