package com.simcraft;

import javax.swing.SwingUtilities;

import com.simcraft.graphics.GameFrame;

public class App {

    /**
     * How often the app should refresh the rendered frame in milliseconds.
     * <p>
     * Note that game updates occur once per frame.
     */
    public static final long FRAME_RATE_MS = (long) 16.7;  // 16.7 ms is approx. 60 FPS

    /**
     * The entry point for the application. This method schedules the creation
     * of the {@link GameFrame} on the Event Dispatch Thread (EDT).
     *
     * @param args The command line arguments (not used in this application).
     */
    public static void main(String[] args) {
        // Schedules GameFrame creation on the EDT
        SwingUtilities.invokeLater(GameFrame::new);
    }
}
