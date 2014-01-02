package straitjacket.gui;

import javax.swing.*;
import java.awt.*;

public class SudokuGUI {

    private static final int SIZE = 3;

    public static void main(String[] args) {

        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                initGUI();
            }
        });

    }


    private static void initGUI() {


        //Create and set up the window.
        JFrame frame = new JFrame("HelloWorldSwing");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new GridBagLayout());

        Panel sudokuPanel = new Panel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0);


        Panel[][] blockPanels = new Panel[SIZE][SIZE];
        TextField[][][][] textFields = new TextField[SIZE][SIZE][SIZE][SIZE];

        for (int blockRow = 0; blockRow < SIZE; blockRow++) {
            for (int blockCol = 0; blockCol < SIZE; blockCol++) {

                Panel panel = new Panel(new GridBagLayout());
                blockPanels[blockRow][blockCol] = panel;

                for (int row = 0; row < SIZE; row++) {
                    for (int col = 0; col < SIZE; col++) {

                        TextField textField = new TextField();
                        textFields[blockRow][blockCol][row][col] = textField;

                        constraints.gridx = col;
                        constraints.gridy = row;
                        panel.add(textField, constraints);
                    }
                }

                constraints.gridx = blockCol;
                constraints.gridy = blockRow;

                sudokuPanel.add(panel, constraints);

            }
        }

        contentPane.add(sudokuPanel, new GridBagConstraints(0, 0, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));

        Button startButton = new Button("Solve");
        contentPane.add(startButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0));


        //Display the window.
        frame.pack();
        frame.setVisible(true);

    }

}
