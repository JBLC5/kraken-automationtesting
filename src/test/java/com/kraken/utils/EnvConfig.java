package com.kraken.utils;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {
    private static final Dotenv dotenv = Dotenv.configure()
                                               .filename("Identifiants.env") // 👈 nom personnalisé
                                               .load();

    public static String getLoginEmail() {
        return dotenv.get("LOGIN_EMAIL");
    }

    public static String getLoginPassword() {
        return dotenv.get("LOGIN_PASSWORD");
    }
}

