package com.kraken.tests;

import com.kraken.pages.LoginPage;
import com.kraken.utils.ScreenshotUtils;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;

import io.qameta.allure.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import static com.kraken.utils.EnvConfig.*;

@Epic("Trading Orders")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class KrakenTradingOrdersTest {

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

    void takeOrder(String orderTypeName) {
        loginPage.login(getLoginEmail(), getLoginPassword());

        Allure.step("Aller sur l'onglet Trading et choisir XRP comme base pour le trade", () -> {
            try {
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("icon1 name BTC/USD MCPerpetual")).click();
                page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Search")).click();
                page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Search")).fill("XRP");
                page.locator("market-picker-ticker").getByText("XRP/USD").click();

                Locator successDisplay = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("icon1 name XRP/USD MCPerpetual"));
                successDisplay.waitFor(new Locator.WaitForOptions().setTimeout(5000));
                assertTrue(successDisplay.isVisible(),
                    "Le XRP n'a pas pu etre sélectionné en tant que Base pour le trade.");
            } catch (TimeoutError e) {
                throw new AssertionError("Le XRP n'a pas pu etre sélectionné en tant que Base pour le trade.", e);
            }
        });

        Allure.step("Sélectionner et vérifier que le trading Order est : " + orderTypeName, () -> {
            try {
                page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Trading")).click();
                Locator successDisplay = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(orderTypeName));
                successDisplay.click();
                successDisplay.waitFor(new Locator.WaitForOptions().setTimeout(5000));
                assertTrue(successDisplay.isVisible(),
                    "Le type d'ordre '" + orderTypeName + "' n’est pas visible alors qu’il devrait l’être.");
            } catch (TimeoutError e) {
                throw new AssertionError("Échec d'accès au trading : '" + orderTypeName + "' n’a pas été trouvé après avoir cliqué sur l’icône.", e);
            }
        });

        Allure.step("Choisir le type d'ordre Market", () -> {
            String messageErreur = "Le type d'achat 'Market' n’est pas disponible alors qu’il devrait l’être.";
            try {
                page.locator("order-type-toggle").getByText("Market").click();
                Locator successDisplay = page.getByText("Reduce Only Stop Loss Take");

                successDisplay.waitFor(new Locator.WaitForOptions().setTimeout(5000));
                assertTrue(successDisplay.isVisible(),messageErreur);
            } catch (TimeoutError e) {
                throw new AssertionError(messageErreur, e);
            }
        });
    }

    void effectuerTrade(int montant, boolean transfertAttendu, String TypeOrder) {
        Allure.step("Effectuer le trade.", () -> {
            page.getByRole(AriaRole.SPINBUTTON, new Page.GetByRoleOptions().setName("Quantity:")).fill(String.valueOf(montant));
            page.locator("form").click();
            page.waitForTimeout(2000);
            String texteBouton = "";
            if (TypeOrder.equalsIgnoreCase("LONG")) {
                texteBouton = "Place Buy Order";
            } else if (TypeOrder.equalsIgnoreCase("SHORT")) {
                texteBouton = "Place Sell Order";
            } else {
                throw new IllegalArgumentException("Type d'ordre inconnu: " + TypeOrder);
            }

            Locator boutonSubmit = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(texteBouton));
            boolean boutonEstActif = boutonSubmit.isEnabled();

            if (transfertAttendu) {
                assertTrue(boutonEstActif, "Le bouton '" + texteBouton + "' devrait être activé pour un montant valide.");
                boutonSubmit.click();
                Locator successMessage = page.getByText("Order executed");
            
                try {
                    successMessage.waitFor(new Locator.WaitForOptions().setTimeout(5000));
                    assertTrue(successMessage.isVisible(), "La prise d'ordre aurait dû réussir mais le message ne s’est pas affiché.");
                
                } catch (TimeoutError e) {
                    throw new AssertionError("Échec inattendu : l'achat de l'option pour un notionnel de " + montant + " aurait dû réussir.", e);
                }    
            } else {
                page.waitForTimeout(2000);
                assertFalse(boutonEstActif, "Le bouton 'Place Buy Order' devrait être désactivé pour un montant invalide.");
            }
        });
    }
    void revendrePosition() {
        Allure.step("Sélectionner la position à revendre en cliquant sur -Market.", () -> {
            String messageErreur = "La position n'a pas été trouvé.";
            try {
                page.getByRole(AriaRole.ROW, new Page.GetByRoleOptions().setName("icon1 name XRP/USD MC Perp"))
                .locator("svg").nth(3).click();
                page.waitForTimeout(2500);
                Locator successDisplay = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Yes"));
                successDisplay.waitFor(new Locator.WaitForOptions().setTimeout(5000));
                assertTrue(successDisplay.isVisible(),messageErreur);
            } catch (TimeoutError e) {
                throw new AssertionError(messageErreur, e);
            } 
            
            });

        Allure.step("Valider la vente en cliquant sur Yes et vérifier sa prise en compte.", () -> {
            String messageErreur = "La vente n'a pas pu etre validé";
            try {
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Yes")).click();
                page.waitForTimeout(2500);    
                Locator successDisplay = page.getByText("Order executed");
                successDisplay.waitFor(new Locator.WaitForOptions().setTimeout(5000));
                assertTrue(successDisplay.isVisible(),messageErreur);
            } catch (TimeoutError e) {
                throw new AssertionError(messageErreur, e);
            } 
                
                });
        }

    @Test
    @Order(1)
    @Feature("Achat et vente d'un long XRP")
    @Story("Achat avec un solde suffisant.")
    @Description("Vérifier que l'achat et la vente sont valides lorsqu'on a le solde sur le portefeuille.")
    @Severity(SeverityLevel.CRITICAL)
    void testAchatLong_CasPassant() {
        takeOrder("Buy | Long"); // S'assure qu'on est bien sur l'onglet Trading
        effectuerTrade(1, true, "LONG");
        revendrePosition();
    }

    @Test
    @Order(2)
    @Feature("Achat et vente d'un long XRP")
    @Story("Achat avec une quantité de 0 XRP.")
    @Description("Vérifier que l'achat et la vente sont impossible pour une quantité de 0.")
    @Severity(SeverityLevel.CRITICAL)
    void testAchatLong_CasNonPassant() {
        takeOrder("Buy | Long"); // S'assure qu'on est bien sur l'onglet Trading
        effectuerTrade(0, false,"LONG");
    }

    @Test
    @Order(3)
    @Feature("Achat et vente d'un short XRP")
    @Story("Achat avec un solde suffisant.")
    @Description("Vérifier que l'achat et la vente sont valides lorsqu'on a le solde sur le portefeuille.")
    @Severity(SeverityLevel.CRITICAL)
    void testAchatShort_CasPassant() {
        takeOrder("Sell | Short"); // S'assure qu'on est bien sur l'onglet Trading
        effectuerTrade(1, true,"SHORT");
        revendrePosition();
    }

    @Test
    @Order(4)
    @Feature("Achat et vente d'un short XRP")
    @Story("Achat avec une quantité de 0 XRP.")
    @Description("Vérifier que l'achat et la vente sont impossible pour une quantité de 0.")
    @Severity(SeverityLevel.CRITICAL)
    void testAchatShort_CasNonPassant() {
        takeOrder("Sell | Short"); // S'assure qu'on est bien sur l'onglet Trading
        effectuerTrade(0, false,"SHORT");
    }
}
