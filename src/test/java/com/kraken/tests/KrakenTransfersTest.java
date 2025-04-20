package com.kraken.tests;

import com.kraken.pages.LoginPage;
import com.kraken.utils.ScreenshotUtils;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;

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

@Epic("Transfers")
public class KrakenTransfersTest {

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

    void NaviguerVersFenetreTransfer() {
        loginPage.login(getLoginEmail(), getLoginPassword());

        Allure.step("Accès au dashboard Holding Wallets", () -> {
            try {
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Wallets")).click();
            Locator successDisplay = page.getByRole(AriaRole.PARAGRAPH).filter(new Locator.FilterOptions().setHasText("Holding Wallets"));
            
            successDisplay.waitFor(new Locator.WaitForOptions().setTimeout(5000));
            assertTrue(successDisplay.isVisible(),
                "L'en-tete 'Holding Wallets' n’est pas visible alors qu’il devrait l’être.");
            } catch (TimeoutError e) {
                throw new AssertionError("Échec d'accès au portefeuille : le 'Holding Wallets' n’a pas été trouvée après avoir clique sur l'icone.", e);
            }

        });
        Allure.step("Accès à la fenetre contextuelle Transfert", () -> {
            try {
            page.getByRole(AriaRole.ROW, new Page.GetByRoleOptions().setName("icon1 name British Pound GBP")).getByRole(AriaRole.BUTTON).nth(1).click();
            Locator successPopUp = page.getByText("Funds Transfer");
            
            successPopUp.waitFor(new Locator.WaitForOptions().setTimeout(5000));
            assertTrue(successPopUp.isVisible(),
                    "La fenetre ne s'est pas ouverte comme attendu.");
            } catch (TimeoutError e) {
                throw new AssertionError("Échec d'accès aux transferts le message 'Funds Transfer' n’a pas été trouvée après avoir cliqué sur l'icone.", e);
            };

        });
        Allure.step("Sélection de la devise GBP", () -> {
            try {
            page.getByText("British Pound (GBP)Asset").click();
            Locator successCurrencyUn = page.getByText("British Pound (GBP)Asset");
            
            successCurrencyUn.waitFor(new Locator.WaitForOptions().setTimeout(5000));
            assertTrue(successCurrencyUn.isVisible(),
                    "La devise a été sélectionné correctement.");
            } catch (TimeoutError e) {
                throw new AssertionError("Échec de sélection de la devise 'GBP'. Elle n’a pas été trouvée.", e);
            };

        });
        Allure.step("Changement de la devise GBP vers USD", () -> {
            try {
            page.getByText("Dollar (USD)").click();
            Locator successCurrencyDeux = page.getByText("Dollar (USD)Asset");
                
            successCurrencyDeux.waitFor(new Locator.WaitForOptions().setTimeout(5000));
            assertTrue(successCurrencyDeux.isVisible(),
                    "La devise a été modifié correctement.");
            } catch (TimeoutError e) {
                throw new AssertionError("Échec de modification de la devise Vers 'USD'. Elle n’a pas été trouvée après avoir cliqué sur USD.", e);
            };
    
        });}
        void effectuerTransfertEtVerifier(int montant, boolean transfertAttendu) {
            Allure.step("Effectuer un transfert.", () -> {
                page.getByRole(AriaRole.SPINBUTTON, new Page.GetByRoleOptions().setName("Amount"))
                    .fill(String.valueOf(montant));
        
                Locator boutonSubmit = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit Transfer"));
                boolean boutonEstActif = boutonSubmit.isEnabled();
        
                if (transfertAttendu) {
                    assertTrue(boutonEstActif, "Le bouton 'Submit Transfer' devrait être activé pour un montant valide.");
                    boutonSubmit.click();
                    Locator successMessage = page.getByText("Wallet transfer successful");
                    try {
                        successMessage.waitFor(new Locator.WaitForOptions().setTimeout(5000));
                        assertTrue(successMessage.isVisible(), "Le transfert aurait dû réussir mais le message ne s’est pas affiché.");
                        Allure.step("✅ Transfert réussi comme prévu.");
                    } catch (TimeoutError e) {
                        throw new AssertionError("Échec inattendu : le transfert de " + montant + " aurait dû réussir.", e);
                    }
                } else {
                    assertFalse(boutonEstActif, "Le bouton 'Submit Transfer' devrait être désactivé pour un montant invalide.");
                    Allure.step("✅ Le transfert de " + montant + " a bien été bloqué comme attendu (bouton désactivé).");
                }
            });
        }        
        
        
        @Test
        @Feature("Transfert du Holding Wallets vers le Multi-Collateral futures wallet")
        @Story("Transfert avec un montant à 0.")
        @Description("Vérifier qu'il est impossible d'effectuer un transfert égal à 0.")
        @Severity(SeverityLevel.NORMAL)
        void testTransfertMontantZero_CasNonPassant() {
        NaviguerVersFenetreTransfer();
        effectuerTransfertEtVerifier(0, false);
        }

        @Test
        @Feature("Transfert du Holding Wallets vers le Multi-Collateral futures wallet")
        @Story("Transfert avec un solde suffisant.")
        @Description("Vérifier que le transfert est valide lorsqu'on a le solde sur le portefeuille.")
        @Severity(SeverityLevel.CRITICAL)
        void testTransfertSoldeSuffisant_CasPassant() {
        NaviguerVersFenetreTransfer();
        effectuerTransfertEtVerifier(1, true);
        }

        @Test
        @Feature("Transfert du Holding Wallets vers le Multi-Collateral futures wallet")
        @Story("Transfert avec un solde insuffisant.")
        @Description("Vérifier qu'il n'est pas possible d'effectuer un transfert quand le solde de la devise est insuffisant.")
        @Severity(SeverityLevel.CRITICAL)
        void testTransfertSoldeInsuffisant_CasNonPassant() {
        NaviguerVersFenetreTransfer();
        effectuerTransfertEtVerifier(5000, false);
        }
    
}
 