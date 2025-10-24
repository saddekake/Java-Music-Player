import javax.swing.*;
import java.awt.*;
import javazoom.jl.player.Player;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.ArrayList;

public class Main {

    private static Player currentPlayer; // keeps track of currently playing song

    public static void main(String[] args) {
        System.out.println("debug 1");
        // -----------------------------
        // variables
        // -----------------------------

        File folder = new File("Songs"); // folder containing songs
        File[] songs = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3")); // get all .mp3 files in the folder

        JFrame frame = new JFrame("Music Player"); // create main frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // set it to stop program on close
        frame.setSize(400, 400); // set size

        // -----------------------------
        // dynamic song panel
        // -----------------------------
        JPanel songPanel = new JPanel();
        songPanel.setLayout(new BoxLayout(songPanel, BoxLayout.Y_AXIS)); // sets layout to vertical

        // scroll pane in case there are many songs
        JScrollPane scrollPane = new JScrollPane(songPanel);

        // -----------------------------
        // search field
        // -----------------------------
        JTextField searchField = new JTextField();
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30)); // height = 30, width flexible
        songPanel.add(searchField); // add at the top of the panel

        java.util.List<JButton> songButtons = new ArrayList<>(); // create new list with all buttons

        // -----------------------------
        // add song buttons dynamically
        // -----------------------------
        if (songs != null) {
            for (File song : songs) {
                // get the file name, stripping away .mp3 extension so it looks nicer
                String songName = song.getName().replaceFirst("(?i)\\.mp3$", "");

                // create button
                JButton songButton = new JButton(songName);
                songButton.setAlignmentX(Component.CENTER_ALIGNMENT); // set alignment to center
                songButton.addActionListener(e -> playSong(song.getPath())); // make it clickable and play playSong() function on click
                songPanel.add(songButton); // add button to the song panel
                songButtons.add(songButton); // keep reference for filtering
            }
        }

        // -----------------------------
        // search functionality
        // -----------------------------
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {

            public void update() {
                String filter = searchField.getText().toLowerCase();
                for (JButton btn : songButtons) {
                    btn.setVisible(btn.getText().toLowerCase().contains(filter));
                }
                songPanel.revalidate(); // refresh layout
                songPanel.repaint();
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                update();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                update();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                update();
            }
        });

        // -----------------------------
        // add scroll pane to frame
        // -----------------------------
        frame.setLayout(new BorderLayout()); // use BorderLayout for scroll pane
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);

        System.out.println("debug 2");
    }

    // -----------------------------
    // play song in a separate thread
    // -----------------------------
    public static void playSong(String path) {
        // stop previous song if any
        if (currentPlayer != null) {
            currentPlayer.close();
        }

        new Thread(() -> {
            try {
                FileInputStream fis = new FileInputStream(path);
                currentPlayer = new Player(fis);
                currentPlayer.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}