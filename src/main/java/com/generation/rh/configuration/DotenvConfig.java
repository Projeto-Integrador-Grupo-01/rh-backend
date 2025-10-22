package com.generation.rh.configuration;

import java.util.Arrays;
import java.util.List;

import io.github.cdimascio.dotenv.Dotenv;

public final class DotenvConfig {
    private static final Dotenv dotenv = Dotenv.configure()
                                              .ignoreIfMissing() // evita exceção se .env não existir (opcional)
                                              .load();

    private DotenvConfig() {}

    // Pega variável do .env primeiro, se não existir tenta System.getenv()
    public static String get(String key) {
        String v = dotenv.get(key);
        if (v == null) {
            v = System.getenv(key); // fallback para variáveis do sistema
        }
        return v;
    }

    // Pega com fallback padrão
    public static String getOrDefault(String key, String defaultValue) {
        String v = get(key);
        return v != null ? v : defaultValue;
    }

    // Pega como inteiro (lança NumberFormatException se inválido)
    public static int getInt(String key, int defaultValue) {
        String v = get(key);
        return v != null ? Integer.parseInt(v) : defaultValue;
    }

    // Verifica variáveis obrigatórias — lança IllegalStateException se faltar
    public static void ensurePresent(String... keys) {
        List<String> missing = Arrays.stream(keys)
                                     .filter(k -> get(k) == null || get(k).isBlank())
                                     .toList();
        if (!missing.isEmpty()) {
            throw new IllegalStateException("Variáveis de ambiente obrigatórias ausentes: " + missing);
        }
    }
}

