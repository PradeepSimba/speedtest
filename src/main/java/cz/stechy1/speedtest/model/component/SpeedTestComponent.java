package cz.stechy1.speedtest.model.component;

import cz.stechy1.speedtest.model.ObservableMessage;
import cz.stechy1.speedtest.model.SpeedTestResult;
import cz.stechy1.speedtest.model.service.CustomLoader;
import cz.stechy1.speedtest.reference.R;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Třída představuje komponentu, která se zobrazí při výsledku testu.
 */
public class SpeedTestComponent extends Tab implements Observer {

    private static int COUNT = 0;

    public ProgressBar allProgressBar;
    public ProgressBar downloadProgressBar;
    public VBox mainContainer;
    public VBox progressContainer;
    public Accordion resultContainer;
    public TitledPane totalScore;
    public BarChart<String, Long> downloadChart;
    public BarChart<String, Long> latencyChart;
    public Label avargeSpeedLabel;
    public Label bestSpeedLabel;
    public Label avargeLatencyLabel;

    /**
     * Creates a tab.
     */
    public SpeedTestComponent() {
        super("Test - " + ++COUNT);

        try {
            FXMLLoader loader = CustomLoader.getLoader("speedTestComponent");
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            System.out.println("Nepodařilo se načíst fxml soubor pro speedTest komponentu!");
            e.printStackTrace();
        }
    }

    private void updateProgressBar(double progress) {
        downloadProgressBar.setProgress(progress);
    }

    private void nextDownloadAction(double progress) {
        allProgressBar.setProgress(progress / R.TEST_COUNT);
        downloadProgressBar.setProgress(0);
    }

    private void showResult(List<SpeedTestResult> results) {
        allProgressBar.setProgress(1d);
        mainContainer.getChildren().remove(progressContainer);
        resultContainer.setVisible(true);

        downloadChart.setLegendVisible(false);
        latencyChart.setLegendVisible(false);

        XYChart.Series<String, Long> downloadSeries = new XYChart.Series<>();
        XYChart.Series<String, Long> latencySeries = new XYChart.Series<>();
        ObservableList<XYChart.Data<String, Long>> downloadData = downloadSeries.getData();
        ObservableList<XYChart.Data<String, Long>> latencyData = latencySeries.getData();

        Double avargeSpeedTime = 0d;
        Double bestSpeedTime = 0d;
        Double avargeLatency = 0d;

        for (SpeedTestResult result : results) {
            //System.out.println(result.toString());
            downloadData.add(result.getDownloadSpeed());
            latencyData.add(result.getLatency());

            bestSpeedTime = Math.max(bestSpeedTime, result.speed);
            avargeSpeedTime += result.speed;
            avargeLatency += result.latency;
        }

        avargeSpeedTime /= SpeedTestResult.TEST_COUNT;
        avargeLatency /= SpeedTestResult.TEST_COUNT;

        downloadChart.getData().add(downloadSeries);
        latencyChart.getData().add(latencySeries);

        avargeSpeedLabel.setText(String.format("Průměrná rychlost stahování: %.0fMbit/s", avargeSpeedTime));
        bestSpeedLabel.setText(String.format("Nejvyšší rychlost stahování: %.0fMbit/s", bestSpeedTime));
        avargeLatencyLabel.setText(String.format("Průměrná latence: %.0fms", avargeLatency));

        totalScore.setExpanded(true);
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
        final Double progress = msg.progress;
        final List<SpeedTestResult> results = msg.results;
        Platform.runLater(() -> {
            switch (action) {
                case ObservableMessage.ACTION_UPDATE:
                        updateProgressBar(progress);
                    break;

                case ObservableMessage.ACTION_NEXT:
                        nextDownloadAction(progress);
                    break;

                case ObservableMessage.ACTION_DONE:
                        showResult(results);
                    break;
            }
        });

    }
}
