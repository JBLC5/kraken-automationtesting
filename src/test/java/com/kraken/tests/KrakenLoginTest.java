package com.kraken.tests;

import com.kraken.pages.LoginPage;
import com.kraken.utils.ScreenshotUtils;
import com.microsoft.playwright.*;

import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import static com.kraken.utils.EnvConfig.*;

@Epic("Authentification")
@Feature("Connexion à Kraken")
@Story("Connexion au compte Kraken FUTURES")
public class KrakenLoginTest {

    static Playwright playwright;
    static Browser browser;
    static Page page;
    LoginPage loginPage;

    @BeforeAll
    static void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
    }

    @BeforeEach
    void beforeEach() {
        loginPage = new LoginPage(page);
    }

    @AfterEach
    void afterEach(TestInfo testInfo) {
        // Nettoie le nom du test pour qu’il soit safe pour un fichier
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
    @Description("Ce test vérifie que la connexion avec des identifiants valides fonctionne.")
    @Severity(SeverityLevel.CRITICAL)
    void testLoginToKraken_ValidCredentials() {
        loginPage.login(getLoginEmail(), getLoginPassword());
        Allure.step("Vérifier l'apparition de la pop-up de connexion réussie", () -> {
            Locator successPopup = page.getByText("Sign In Successful");

            try {
                successPopup.waitFor(new Locator.WaitForOptions().setTimeout(5000));
                assertTrue(successPopup.isVisible(),
                    "La pop-up 'Sign In Successful' n’est pas visible alors qu’elle devrait l’être.");
            } catch (TimeoutError e) {
                throw new AssertionError("Échec de la connexion : la pop-up 'Sign In Successful' n’a pas été trouvée après la soumission du formulaire.", e);
            }
        });
    }

    @Test
    @Description("Ce test vérifie que la connexion échoue avec un mot de passe incorrect.")
    @Severity(SeverityLevel.NORMAL)
    void testLoginToKraken_InvalidCredentials() {
        loginPage.login("9p3gkjq6@futures-dem.com", "wrong_password");

        Allure.step("Vérifier l'apparition du message d'erreur", () -> {
            Locator errorPopup = page.getByText("Sign In Failed - Invalid Credentials"); // <-- adapte ce texte au message réel affiché

            try {
                errorPopup.waitFor(new Locator.WaitForOptions().setTimeout(5000));
                assertTrue(errorPopup.isVisible(),
                    "Le message d’erreur 'Invalid credentials' n’est pas visible alors qu’il devrait s’afficher.");
            } catch (TimeoutError e) {
                throw new AssertionError("Échec attendu, mais aucun message d’erreur n’a été détecté.", e);
            }
        });
    }
}
