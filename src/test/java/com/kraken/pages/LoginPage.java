package com.kraken.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Allure;
import com.kraken.utils.ScreenshotUtils;

public class LoginPage {
    private final Page page;
    private String lastStepName = "";

    private final String loginButtonHome = "text=Sign In";
    private Locator languageMenu;
    private final String englishLanguageOption = "text=English";

    public LoginPage(Page page) {
        this.page = page;
        this.languageMenu = page.locator("language-menu svg");
    }

    public void goToHomePage() {
        step("Naviguer vers la page d'accueil", () -> {
            page.navigate("https://demo-futures.kraken.com/futures/PF_XBTUSD");
        });
    }

    public void clickLoginButton() {
        step("Cliquer sur le bouton 'Sign In'", () -> {
            page.click(loginButtonHome);
        });
    }

    public void changeLanguageToEnglish() {
        step("Changer la langue en anglais", () -> {
            languageMenu.click();
            page.click(englishLanguageOption);
        });
    }

    public void login(String username, String password) {
        goToHomePage();
        changeLanguageToEnglish();
        clickLoginButton();

        step("Remplir le champ 'Email'", () -> {
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email")).fill(username);
        });

        step("Remplir le champ 'Password'", () -> {
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).fill(password);
        });

        step("Soumettre le formulaire de connexion", () -> {
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign in with Email")).click();
        });
    }

    private void step(String name, Runnable executable) {
        Allure.step(name, () -> {
            lastStepName = name;
            executable.run();
        });
    }

    public void captureLastStepScreenshot(String status) {
        ScreenshotUtils.captureScreenshot(page, status + "_" + lastStepName.replaceAll("\\s+", "_"));
    }

    public String getLastStepName() {
        return lastStepName;
    }    
}
