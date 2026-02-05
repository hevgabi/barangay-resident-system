package app;

import javax.swing.SwingUtilities;

import ui.frames.AppFrames;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AppFrames());
    }   
}
