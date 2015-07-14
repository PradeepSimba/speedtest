package cz.stechy1.speedtest.model.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;

import java.io.IOException;

/**
 * Pomocná třída pro načítání fxml souborů.
 */
public class CustomLoader {

    private static final String resourceFolder = "/resources/";
    private static final String fxmlFolder = resourceFolder + "fxml/";
    private static final String imgFolder = resourceFolder + "img/";

    /**
     * Načte FXML soubor a vrátí ho jako instanci třídy Parent.
     * @param fileName Název fxml souboru.
     * @return Vrátí instanci třídy Parent.
     * @throws IOException Pokud soubor není nalezen
     */
    public static Parent load(String fileName) throws IOException {
        return getLoader(fileName).load();
    }

    public static FXMLLoader getLoader(String fileName) {
        return new FXMLLoader(CustomLoader.class.getResource(fxmlFolder + fileName + ".fxml"));
    }

    public static Image getImage(String fileName) {
        return new Image(imgFolder + fileName);
    }
}
