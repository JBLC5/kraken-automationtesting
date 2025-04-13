package com.kraken.tests;

import com.microsoft.playwright.*;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import com.kraken.utils.ScreenshotUtils;


@Epic("Site Web Kraken Futures")
@Feature("Navigation de base")
public class KrakenNavigationTest {

    static Playwright playwright;
    static Browser browser;
    static Page page;

    @BeforeAll
    static void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    @AfterAll
    static void tearDown() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @Test
    @Story("Visiter la page Kraken Futures")
    @Description("Ce test vérifie que la page https://demo-futures.kraken.com est accessible et prend une capture.")
    @Severity(SeverityLevel.CRITICAL)
    void testKrakenNavigation() {
        page = browser.newPage();

        try {
            // Navigation vers la page et vérification du titre
            step("Naviguer vers https://demo-futures.kraken.com", () -> {
                page.navigate("https://demo-futures.kraken.com");
            });

            step("Vérifier le titre de la page", () -> {
                String title = page.title();
                assertTrue(title.toLowerCase().contains("kraken futures"));
            });

            // Capture d'écran en cas de succès
            ScreenshotUtils.captureScreenshot(page, "success");
            
        } catch (Exception e) {
            // Capture d'écran en cas d'échec
            ScreenshotUtils.captureScreenshot(page, "error");
            fail("Une exception est survenue : " + e.getMessage());
        }
    }

    // Méthode 'step' modifiée pour accepter un lambda avec une gestion appropriée de l'exception
    private void step(String name, Runnable executable) {
        Allure.step(name, () -> {
            try {
                executable.run();
            } catch (Exception e) {
                throw new RuntimeException("Erreur dans l'exécution de l'étape: " + name, e);
            }
        });
    }
}
