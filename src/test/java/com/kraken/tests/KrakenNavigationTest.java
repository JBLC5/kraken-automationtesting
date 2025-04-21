package com.kraken.tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;

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
        page = browser.newPage();
    }

    @AfterEach
    void afterEach(TestInfo testInfo) {
        String testNameClean = testInfo.getDisplayName().replaceAll("[^a-zA-Z0-9-_]", "_");
        ScreenshotUtils.captureScreenshot(page, testNameClean);
    
        page.close();
        page = browser.newPage();
    }

    @AfterAll
    static void teardown() {
        browser.close();
        playwright.close();
    }

    @Test
    @Story("Visiter la page Kraken Futures")
    @Description("Ce test vérifie que la page https://demo-futures.kraken.com est accessible et prend une capture.")
    @Severity(SeverityLevel.CRITICAL)
    void testKrakenNavigation() {

        Allure.step("Naviguer vers https://demo-futures.kraken.com", () -> {
            try {
            page.navigate("https://demo-futures.kraken.com");
            Locator successDisplay = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("icon1 name BTC/USD MCContinuel"));
            
            successDisplay.waitFor(new Locator.WaitForOptions().setTimeout(5000));
            assertTrue(successDisplay.isVisible(),
                "L'en-tete 'BTC/USD MC continuel' n’est pas visible alors qu’il devrait l’être.");
            } catch (TimeoutError e) {
                throw new AssertionError("Échec d'accès à la page. Titre non trouvé.", e);
            }

        });
    }
}
