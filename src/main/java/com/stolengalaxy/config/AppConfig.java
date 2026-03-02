package com.stolengalaxy.config;

import io.github.cdimascio.dotenv.Dotenv;

public class AppConfig {

    public static final String NanoBananaAPIKey;

    static {
        Dotenv dotenv = Dotenv.load();
        NanoBananaAPIKey = dotenv.get("NANO_BANANA_API_KEY");
    }
}
