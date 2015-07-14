package cz.stechy1.speedtest.model;

import java.util.List;

/**
 * Jednoduchá třída pro přepravu informace mezi pozorovaným a pozorovatelem.
 */
public class ObservableMessage {

    public static final int ACTION_UPDATE = 0;
    public static final int ACTION_NEXT = 1;
    public static final int ACTION_DONE = 3;

    public final int action;
    public final Double progress;
    public final List<SpeedTestResult> results;

    public ObservableMessage(int action, Double progress) {
        this.action = action;
        this.progress = progress;
        this.results = null;
    }

    public ObservableMessage(int action, List<SpeedTestResult> results) {
        this.action = action;
        this.results = results;
        this.progress = 0d;
    }
}
