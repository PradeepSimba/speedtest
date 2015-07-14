package cz.stechy1.speedtest.controller;

import cz.stechy1.speedtest.model.ObservableMessage;
import cz.stechy1.speedtest.model.manager.SpeedTestManager;
import cz.stechy1.speedtest.reference.R;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

/**
 * Kontroler pro ovládání hlavního pohledu (main.fxml).
 */
public class MainController implements Initializable, Observer {

    public TabPane resultTabPane;
    public Button startTestBtn;
    public Spinner<Integer> testCount;

    private SpeedTestManager speedTest;


    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        speedTest = new SpeedTestManager(resultTabPane);

        testCount.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue > 0 && newValue < 8)
                R.TEST_COUNT = newValue;
            else {
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setHeaderText("Varování...");
                a.setContentText("Není povoleno zadávat jiné hodnoty než ty, co jsou povolené!");
                a.showAndWait();
                newValue = oldValue;
            }
        });
    }

    public void startTest() {

        try {
            speedTest.start();
            speedTest.addObserver(this);
            startTestBtn.setDisable(true);
            testCount.setDisable(true);
        } catch (Exception e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText("Chyba...");
            a.setContentText(e.getMessage());
            a.showAndWait();
        }
    }


    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>Observable</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notified of the change.
     *
     * @param o   the observable object.
     * @param arg an argument passed to the <code>notifyObservers</code>
     */
    @Override
    public void update(Observable o, Object arg) {
        ObservableMessage msg = (ObservableMessage) arg;
        final int action = msg.action;
        if(action == ObservableMessage.ACTION_DONE) {
            startTestBtn.setDisable(false);
            testCount.setDisable(false);
        }
    }
}
