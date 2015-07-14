package cz.stechy1.speedtest.model.manager;

import cz.stechy1.speedtest.model.ObservableMessage;
import cz.stechy1.speedtest.model.SpeedTestResult;
import cz.stechy1.speedtest.model.component.SpeedTestComponent;
import cz.stechy1.speedtest.reference.R;
import javafx.collections.ObservableList;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

/**
 * Třída pro správu rychlostních testů
 */
public class SpeedTestManager extends Observable implements Runnable {

    private static final String TEMP_FILE_NAME = "tmp.dat";

    private boolean running;

    private static final String[] urls = {
            "http://217.198.117.127/data-50528.png?tr=0.2271849880926311",
            "http://89.185.225.37/data-50528.png?tr=0.11779921595007181",
            "http://81.95.108.152/data-50528.png?tr=0.95176768489182",
            "http://89.185.225.37/data-50528.png?tr=0.22383572277612984",
            "http://217.198.117.127/data-50528.png?tr=0.9926817908417434",
            "http://81.95.108.152/data-50528.png?tr=0.5169414361007512",
            "http://81.95.108.152/data-50528.png?tr=0.5068056765012443",
            "http://217.198.117.127/data-50528.png?tr=0.21273257373832166"
    };

    private ObservableList<Tab> tabChildren;
    private SingleSelectionModel<Tab> selectionModel;
    private List<SpeedTestResult> results;
    private long completeFileSize;
    private long latency;

    public SpeedTestManager(TabPane tabPane) {
        tabChildren = tabPane.getTabs();
        selectionModel = tabPane.getSelectionModel();
        results = new LinkedList<>();
        running = false;
    }

    public void start() throws Exception{
        if(running)
            throw new Exception("Test již běží");
        running = true;
        results.clear();
        completeFileSize = 0;
        latency = 0;
        SpeedTestResult.TEST_COUNT = 0;
        addSpeedTestComponent();
        new Thread(this).start();

    }

    /**
     * Přidá novou komponentu do tabu
     */
    private void addSpeedTestComponent() {
        SpeedTestComponent component = new SpeedTestComponent();
        this.addObserver(component);
        tabChildren.add(component);
        selectionModel.select(component);
    }

    private void downloadFile(String url) throws Exception {
//        Vytvoření URL adresy
        URL webServer = new URL(url);
        Long start = System.currentTimeMillis();
//        Nový HTTP připojení
        HttpURLConnection httpURLConnection = (HttpURLConnection) (webServer.openConnection());
//        Zjištění velikosti souboru
        completeFileSize = httpURLConnection.getContentLength();
//        Vytvoření buffered input streamu z HTTP připojení
        BufferedInputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
        Long end = System.currentTimeMillis();
        latency = end - start;
        //System.out.println(latency);
//        Vytvoření file output streamu pro zápis do souboru
        FileOutputStream fos = new FileOutputStream(TEMP_FILE_NAME);
//        Vytvoření buffered output streamu z file output streamu
        BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);

        byte[] data = new byte[1024];
//        Aktuální počet stažených bytů
        long downloadedFileSize = 0;
        int x;
//        Dokud je co číst
        while ((x = in.read(data, 0, 1024)) >= 0) {
            downloadedFileSize += x;

//            Uložení aktuálního progressu
            final double currentProgress = (((double) downloadedFileSize) / ((double) completeFileSize));
            //System.out.println(currentProgress);
//            Notifikace pro progressbar, aby se aktualizoval
            setChanged();
            notifyObservers(new ObservableMessage(ObservableMessage.ACTION_UPDATE, currentProgress));

//            Zápis do souboru
            bout.write(data, 0, x);
        }
//        Zavření streamů
        bout.close();
        in.close();

        /*ReadableByteChannel rbc = Channels.newChannel(webServer.openStream());
        FileOutputStream fos = new FileOutputStream("test.jpg");
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();*/
    }

    private void cleanUp() {
        File file = new File(TEMP_FILE_NAME);
        try {
            Files.delete(file.toPath() );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        try {
            for (Integer i = 0; i < R.TEST_COUNT; i++) {
                Double ii = (double) i;
                setChanged();
                notifyObservers(new ObservableMessage(ObservableMessage.ACTION_NEXT, ii));
                String url = urls[i];
                //System.out.println("Stahuji soubor z url adresy: " + url);
                Long startTime = System.currentTimeMillis();
                downloadFile(url);
                Long endTime = System.currentTimeMillis();
                Long delta = endTime - startTime;
                results.add(new SpeedTestResult(completeFileSize, delta, latency));
                //System.out.printf("Stahování dokončeno za: %d milisekund%n", delta);
            }

            setChanged();
            notifyObservers(new ObservableMessage(ObservableMessage.ACTION_DONE, results));

            this.deleteObservers();

            cleanUp();

            running = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
