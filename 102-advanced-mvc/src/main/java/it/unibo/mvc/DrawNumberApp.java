package it.unibo.mvc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 */
public final class DrawNumberApp implements DrawNumberViewObserver {
    private static final int MIN = 0;
    private static final int MAX = 100;
    private static final int ATTEMPTS = 10;
    private static final int LINE_MINIMUM = 0;
    private static final int LINE_MAXIMUM = 1;
    private static final int LINE_ATTEMPTS = 2;

    private final DrawNumber model;
    private final List<DrawNumberView> views;

    /**
     * @param views
     *            the views to attach
     * @throws IOException
     */
    public DrawNumberApp(final DrawNumberView... views) throws IOException {
        /*
         * Side-effect proof
         */
        this.views = Arrays.asList(Arrays.copyOf(views, views.length));
        for (final DrawNumberView view: views) {
            view.setObserver(this);
            view.start();
        }
        final Configuration config = new Configuration.Builder().
            setAttempts(getConfigurationValue(LINE_ATTEMPTS))
            .setMin(getConfigurationValue(LINE_MINIMUM))
            .setMax(getConfigurationValue(LINE_MAXIMUM))
            .build();
        this.model = new DrawNumberImpl(config.getMin(), config.getMax(), config.getAttempts());
    }

    @Override
    public void newAttempt(final int n) {
        try {
            final DrawResult result = model.attempt(n);
            for (final DrawNumberView view: views) {
                view.result(result);
            }
        } catch (IllegalArgumentException e) {
            for (final DrawNumberView view: views) {
                view.numberIncorrect();
            }
        }
    }

    @Override
    public void resetGame() {
        this.model.reset();
    }

    @Override
    public void quit() {
        /*
         * A bit harsh. A good application should configure the graphics to exit by
         * natural termination when closing is hit. To do things more cleanly, attention
         * should be paid to alive threads, as the application would continue to persist
         * until the last thread terminates.
         */
        System.exit(0);
    }
    private BufferedReader getConfigurationFileReader() {
        final InputStream in = ClassLoader.getSystemResourceAsStream("/settings/settings");
        final BufferedReader br = new BufferedReader(new InputStreamReader(in));
        return br;
    }

    private int getConfigurationValue(int lineNumber) throws IOException {
        final BufferedReader br = getConfigurationFileReader();
        String line = "";
        for(int i=0; i<lineNumber; i++){
            line = br.readLine();
        }
        StringTokenizer st = new StringTokenizer(line);
        st.nextToken();
        st.nextToken();
        return Integer.parseInt(st.toString());
    }

    /**
     * @param args
     *            ignored
     * @throws IOException
     */
    public static void main(final String... args) throws IOException {
        new DrawNumberApp(new DrawNumberViewImpl(), new DrawNumberViewImpl(),);
    }

}
