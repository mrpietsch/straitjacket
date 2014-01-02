package straitjacket.gui;

import straitjacket.*;
import straitjacket.constraints.AllDifferentConstraint;
import straitjacket.strategies.StrategyFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SudokuGUI {

    private static final int SIZE = 5;

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
        final JFrame frame = new JFrame("Sudoku");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new GridBagLayout());

        final Panel sudokuPanel = new Panel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0);


        Panel[][] blockPanels = new Panel[SIZE][SIZE];
        final JTextField[][][][] textFields = new JTextField[SIZE][SIZE][SIZE][SIZE];

        for (int blockRow = 0; blockRow < SIZE; blockRow++) {
            for (int blockCol = 0; blockCol < SIZE; blockCol++) {

                Panel panel = new Panel(new GridBagLayout());
                blockPanels[blockRow][blockCol] = panel;

                for (int row = 0; row < SIZE; row++) {
                    for (int col = 0; col < SIZE; col++) {

                        JTextField textField = new JTextField();
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

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                    ConstraintSet cs = new ConstraintSet();

                    Variable[][][][] variables = new Variable[SIZE][SIZE][SIZE][SIZE];
                    Variable[][] cols = new Variable[SIZE * SIZE][SIZE * SIZE];
                    Variable[][] rows = new Variable[SIZE * SIZE][SIZE * SIZE];
                    Variable[][] blocks = new Variable[SIZE * SIZE][SIZE * SIZE];

                    // set up variables
                    for (int blockRow = 0; blockRow < SIZE; blockRow++) {
                        for (int blockCol = 0; blockCol < SIZE; blockCol++) {
                            for (int row = 0; row < SIZE; row++) {
                                for (int col = 0; col < SIZE; col++) {
                                    Variable variable = cs.addVariable(String.format("sud_%d_%d_%d_%d", blockRow, blockCol, row, col), 1, SIZE * SIZE);

                                    JTextField textField = textFields[blockRow][blockCol][row][col];
                                    String value = textField.getText();
                                    if (value!=null && !value.isEmpty()) {
                                        int intValue = Integer.parseInt(value);
                                        variable.setDomain(intValue);
                                    }

                                    variables[blockRow][blockCol][row][col] = variable;
                                    rows[SIZE * blockRow + row][SIZE * blockCol + col] = variable;
                                    cols[SIZE * blockCol + col][SIZE * blockRow + row] = variable;
                                    blocks[SIZE * blockRow + blockCol][SIZE * row + col] = variable;
                                }
                            }
                        }
                    }

                    // set up the alldifferent constraints for the rows, columns and block
                    for (Variable[] vars : blocks) cs.add(new AllDifferentConstraint(vars));
                    for (Variable[] vars : rows) cs.add(new AllDifferentConstraint(vars));
                    for (Variable[] vars : cols) cs.add(new AllDifferentConstraint(vars));


                    // take the time
                    long overallTime = System.currentTimeMillis();

                    // initial AC3 if wanted
//                    if ( this.initialAC3Checkbox.isSelected() ) {
                        System.out.println("Initial AC3...\n");
                        ArcConsistency.ac3(cs);
                        System.out.println("" + cs);
//                    }

                    // does the user want forward checking?
//                    if ( this.useForwardCheckingCheckbox.isSelected() ) {
                        Backtracker.backtrackSolveForwardCheck(cs, StrategyFactory.AvailableStrategies.FIRST_FAIL);
//                    } else {
//                        Backtracker.backtrackSolve(cs,strat);
//                    }

                    // take the time
                    overallTime = System.currentTimeMillis() - overallTime;
                    System.out.printf("Overall time = %d%n", overallTime);

                    for (int blockRow = 0; blockRow < SIZE; blockRow++) {
                        for (int blockCol = 0; blockCol < SIZE; blockCol++) {
                            for (int row = 0; row < SIZE; row++) {
                                for (int col = 0; col < SIZE; col++) {

                                    Variable variable = variables[blockRow][blockCol][row][col];
                                    if (variable.isTiedToValue()) {
                                        Integer tiedValue = variable.getTiedValue();
                                        JTextField textField = textFields[blockRow][blockCol][row][col];
                                        textField.setText("" + tiedValue);
                                    }

                                }
                            }
                        }
                    }


                } catch (VariableNameExistsException ex) {
                    System.out.println("A Variable with that name already exists!");
                }
            }
        });

        //Display the window.
        frame.pack();
        frame.setVisible(true);

    }

}
