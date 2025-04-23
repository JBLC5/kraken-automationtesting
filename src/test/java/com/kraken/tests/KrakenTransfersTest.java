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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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

    void NaviguerVersFenetreTransferHolding() {
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

    void NaviguerVersFenetreTransferSingle() {
            loginPage.login(getLoginEmail(), getLoginPassword());
    
            Allure.step("Accès aux Wallets", () -> {
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

            Allure.step("Accès au dashboard Single Collateral Futures", () -> {
                try {
                    page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Single Collateral Futures")).click();
                Locator successDisplay = page.getByRole(AriaRole.PARAGRAPH).filter(new Locator.FilterOptions().setHasText("Single Collateral Futures Wallets"));
                
                successDisplay.waitFor(new Locator.WaitForOptions().setTimeout(5000));
                assertTrue(successDisplay.isVisible(),
                    "l'en-tete Single Collateral Futures Wallets n'est pas visible alors qu'il devrait l'etre.");
                } catch (TimeoutError e) {
                    throw new AssertionError("Échec d'accès au portefeuille : le 'Single Collateral wallet' n’a pas été trouvée après avoir clique sur l'icone.", e);
                }
    
            });

            Allure.step("Accès à la fenetre contextuelle Transfert", () -> {
                try {
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Transfer")).nth(4).click();
                Locator successPopUp = page.getByText("Funds Transfer");
                
                successPopUp.waitFor(new Locator.WaitForOptions().setTimeout(5000));
                assertTrue(successPopUp.isVisible(),
                        "La fenetre ne s'est pas ouverte comme attendu.");
                } catch (TimeoutError e) {
                    throw new AssertionError("Échec d'accès aux transferts le message 'Funds Transfer' n’a pas été trouvée après avoir cliqué sur l'icone.", e);
                };
    
            });
            Allure.step("Vérifier que le Ripple XRP est bien sélectionné.", () -> {
                try {
                Locator successCurrencyUn = page.getByText("Ripple XRP (XRP)");
                successCurrencyUn.waitFor(new Locator.WaitForOptions().setTimeout(5000));
                assertTrue(successCurrencyUn.isVisible(),
                        "L'asset a été sélectionné correctement.");
                } catch (TimeoutError e) {
                    throw new AssertionError("Échec de sélection de la crypto monnaie XRP. Elle n’a pas été trouvée.", e);
                };
        
            });}

            void NaviguerVersFenetreTransferMulti() {
                loginPage.login(getLoginEmail(), getLoginPassword());
        
                Allure.step("Accès aux Wallets", () -> {
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
    
                Allure.step("Accès au dashboard Multi-Collateral Futures", () -> {
                    try {
                        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Multi-Collateral Futures New")).click();
                    Locator successDisplay = page.getByRole(AriaRole.PARAGRAPH).filter(new Locator.FilterOptions().setHasText("MULTI-COLLATERAL FUTURES WALLETS"));
                    
                    successDisplay.waitFor(new Locator.WaitForOptions().setTimeout(5000));
                    assertTrue(successDisplay.isVisible(),
                        "l'en-tete MULTI-COLLATERAL FUTURES WALLETS n'est pas visible alors qu'il devrait l'etre.");
                    } catch (TimeoutError e) {
                        throw new AssertionError("Échec d'accès au portefeuille : le 'Multi-Collateral wallet' n’a pas été trouvée après avoir clique sur l'icone.", e);
                    }
        
                });
    
                Allure.step("Accès à la fenetre contextuelle Transfert", () -> {
                    try {
                        page.getByRole(AriaRole.ROW, new Page.GetByRoleOptions().setName("icon1 name British Pound GBP")).getByRole(AriaRole.BUTTON).click();
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
                page.getByRole(AriaRole.SPINBUTTON, new Page.GetByRoleOptions().setName("Amount")).nth(0).fill(String.valueOf(montant));
                page.locator("mat-card-title").click();
                Locator boutonSubmit = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit Transfer")).nth(0);
                boolean boutonEstActif = boutonSubmit.isEnabled();
        
                if (transfertAttendu) {
                    assertTrue(boutonEstActif, "Le bouton 'Submit Transfer' devrait être activé pour un montant valide.");
                    boutonSubmit.click();
                    Locator successMessage = page.getByText("Wallet transfer successful");
                    try {
                        successMessage.waitFor(new Locator.WaitForOptions().setTimeout(5000));
                        assertTrue(successMessage.isVisible(), "Le transfert aurait dû réussir mais le message ne s’est pas affiché.");
                    } catch (TimeoutError e) {
                        throw new AssertionError("Échec inattendu : le transfert de " + montant + " aurait dû réussir.", e);
                    }
                } else {
                    page.waitForTimeout(2000);
                    assertFalse(boutonEstActif, "Le bouton 'Submit Transfer' devrait être désactivé pour un montant invalide.");
                }
            });}

            
            
               
        
        @Test
        @Order(1)
        @Feature("Transfert du Holding Wallets vers le Multi-Collateral futures wallet")
        @Story("Transfert avec un solde suffisant.")
        @Description("Vérifier que le transfert est valide lorsqu'on a le solde sur le portefeuille.")
        @Severity(SeverityLevel.CRITICAL)
        void testTransfertSoldeSuffisant_CasPassant() {
        NaviguerVersFenetreTransferHolding();
        effectuerTransfertEtVerifier(1, true);
        }

        @Test
        @Order(2)
        @Feature("Transfert du Holding Wallets vers le Multi-Collateral futures wallet")
        @Story("Transfert avec un montant à 0.")
        @Description("Vérifier qu'il est impossible d'effectuer un transfert égal à 0.")
        @Severity(SeverityLevel.NORMAL)
        void testTransfertMontantZero_CasNonPassant() {
        NaviguerVersFenetreTransferHolding();
        effectuerTransfertEtVerifier(0, false);
        }


        @Test
        @Order(3)
        @Feature("Transfert du Holding Wallets vers le Multi-Collateral futures wallet")
        @Story("Transfert avec un solde insuffisant.")
        @Description("Vérifier qu'il n'est pas possible d'effectuer un transfert quand le solde de la devise est insuffisant.")
        @Severity(SeverityLevel.CRITICAL)
        void testTransfertSoldeInsuffisant_CasNonPassant() {
        NaviguerVersFenetreTransferHolding();
        effectuerTransfertEtVerifier(5000, false);
        }

        @Test
        @Order(4)
        @Feature("Transfert du Multi-Collateral futures wallet vers le Holding Wallets")
        @Story("Transfert avec un solde suffisant.")
        @Description("Vérifier que le transfert est valide lorsqu'on a le solde sur le portefeuille.")
        @Severity(SeverityLevel.CRITICAL)
        void testTransfertMultiCoSoldeSuffisant_CasPassant() {
        NaviguerVersFenetreTransferMulti();
        effectuerTransfertEtVerifier(1, true);
        }

        @Test
        @Order(5)
        @Feature("Transfert du Multi-Collateral futures wallet vers le Holding Wallets")
        @Story("Transfert avec un montant à 0.")
        @Description("Vérifier qu'il est impossible d'effectuer un transfert égal à 0.")
        @Severity(SeverityLevel.NORMAL)
        void testTransfertMultiCoMontantZero_CasNonPassant() {
        NaviguerVersFenetreTransferMulti();
        effectuerTransfertEtVerifier(0, false);
        }


        @Test
        @Order(6)
        @Feature("Transfert du Multi-Collateral futures wallet vers le Holding Wallets")
        @Story("Transfert avec un solde insuffisant.")
        @Description("Vérifier qu'il n'est pas possible d'effectuer un transfert quand le solde de la devise est insuffisant.")
        @Severity(SeverityLevel.CRITICAL)
        void testTransfertMultiCoSoldeInsuffisant_CasNonPassant() {
        NaviguerVersFenetreTransferMulti();
        effectuerTransfertEtVerifier(5000, false);
        }
    
        @Test
        @Order(7)
        @Feature("Transfert du Single Collateral futures wallet vers le Holding Wallets")
        @Story("Transfert avec un solde suffisant.")
        @Description("Vérifier que le transfert est valide lorsqu'on a le solde sur le portefeuille.")
        @Severity(SeverityLevel.CRITICAL)
        void testTransfertSingleCoSoldeSuffisant_CasPassant() {
        NaviguerVersFenetreTransferSingle();
        effectuerTransfertEtVerifier(1, true);
        }

        @Test
        @Order(8)
        @Feature("Transfert du Single Collateral futures wallet vers le Holding Wallets")
        @Story("Transfert avec un montant à 0.")
        @Description("Vérifier qu'il est impossible d'effectuer un transfert égal à 0.")
        @Severity(SeverityLevel.NORMAL)
        void testTransfertSingleCoMontantZero_CasNonPassant() {
        NaviguerVersFenetreTransferSingle();
        effectuerTransfertEtVerifier(0, false);
        }


        @Test
        @Order(9)
        @Feature("Transfert du Single Collateral futures wallet vers le Holding Wallets")
        @Story("Transfert avec un solde insuffisant.")
        @Description("Vérifier qu'il n'est pas possible d'effectuer un transfert quand le solde de la devise est insuffisant.")
        @Severity(SeverityLevel.CRITICAL)
        void testTransfertSingleCoSoldeInsuffisant_CasNonPassant() {
        NaviguerVersFenetreTransferSingle();
        effectuerTransfertEtVerifier(5000, false);
        }
}
 