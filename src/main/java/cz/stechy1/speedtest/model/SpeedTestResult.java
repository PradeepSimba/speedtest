package cz.stechy1.speedtest.model;

import javafx.scene.chart.XYChart;

/**
 * Třída představující jeden výsledek testu.
 * Využívá návrhového vzoru přepravka - atributy jsou veřejné a finální
 */
public class SpeedTestResult {

    public static int TEST_COUNT = 0;

    public final long fileSize;
    public final long downloadTime;
    public final long latency;
    public final double speed;

    public SpeedTestResult(long fileSize, Long downloadTime, Long latency) {
        this.fileSize = (fileSize / 1024) / 1024;
        this.downloadTime = downloadTime;
        this.latency = latency;

        speed = ((fileSize*8) / (((double) downloadTime) / 1000)) / 1024 / 1024;
    }

    public XYChart.Data<String, Long> getDownloadSpeed() {
        return new XYChart.Data<>(++TEST_COUNT + "", downloadTime / 1000);
    }

    public XYChart.Data<String, Long> getLatency() {
        return new XYChart.Data<>(TEST_COUNT + "", latency);
    }

    public String toString() {

        return String.format("Velikost souboru: %d, čas stažení: %dms, rychlost: %fMbit/sec, Odezva: %dms", fileSize, downloadTime, speed, latency);
    }
}
