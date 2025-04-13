package com.kraken.utils;

import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtils {

    // Génère un nom de fichier unique pour la capture d'écran avec un timestamp
    public static String generateScreenshotName(String resultType) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return "allure-results/" + resultType + "_screenshot_" + timestamp + ".png";
    }

    // Prend une capture d'écran et l'ajoute à Allure
    public static void captureScreenshot(Page page, String resultType) {
        String screenshotPath = generateScreenshotName(resultType);

        try {
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(screenshotPath)));

            // Vérifie si le fichier existe avant de l'ajouter à Allure
            if (Files.exists(Paths.get(screenshotPath))) {
                Allure.addAttachment("Capture d'écran", Files.newInputStream(Paths.get(screenshotPath)));
            } else {
                System.out.println("Le fichier de capture d'écran n'a pas été créé : " + screenshotPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
