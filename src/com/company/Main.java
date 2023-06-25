package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;

public class Main extends JFrame {
    private static final JTextField playerNameText = new JTextField("Игрок");
    private final JButton startNewGameButton = new JButton("Новая игра");
    private final JButton loadOldGameButton = new JButton("Продолжить прошлую игру");
    private final JButton statisticsButton = new JButton("Статистика");
    private final JButton exitButton = new JButton("Выход");
    private static String playerName;

    static JFrame menuFrame;
    static JFrame gameFrame;

    public static void main(String[] args){
        menuFrame = new Main();
        menuFrame.setVisible(true);

    }

    public Main(){
        initMenu();
    }

    private void initMenu() {
        setTitle("Главное Меню");
        setSize (350, 400);
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container containerMenu = getContentPane();
        containerMenu.setLayout(new GridLayout(5, 1,10,30));
        containerMenu.add(playerNameText);
        containerMenu.add(startNewGameButton);
        containerMenu.add(loadOldGameButton);
        containerMenu.add(statisticsButton);
        containerMenu.add(exitButton);

        playerNameText.setHorizontalAlignment(JTextField.CENTER);
        playerNameText.setFont(new Font("SansSerif", Font.BOLD, 18));
        startNewGameButton.setFocusable(false);
        loadOldGameButton.setFocusable(false);
        statisticsButton.setFocusable(false);
        exitButton.setFocusable(false);

        startNewGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    loadNewGame();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        });

        loadOldGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    loadOldGame();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        statisticsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    showStatistics();
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });

    }

    private void loadNewGame() throws IOException {
        playerName = playerNameText.getText();
        gameFrame = new Game(playerName, false);
        gameFrame.setVisible(true);
        menuFrame.setVisible(false);

        gameFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                menuFrame.setVisible(true);
            }
        });
    }

    private void loadOldGame() throws IOException {
        File file = new File("SaveGame.txt");
        if(file.length() == 0){
            JOptionPane.showMessageDialog(null, "Нет сохраненных игр", "Ошибка", JOptionPane.PLAIN_MESSAGE);
        }else {
            gameFrame = new Game(playerName, true);
            gameFrame.setVisible(true);
            menuFrame.setVisible(false);

            gameFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    menuFrame.setVisible(true);
                }
            });
        }
    }

    private void showStatistics() throws FileNotFoundException {
        StringBuilder statisticMessage = new StringBuilder();
        Scanner scanner = new Scanner(new FileInputStream("statistics.txt"));
        while (scanner.hasNextLine())
            statisticMessage.append(scanner.nextLine()).append("\n");

        JOptionPane.showMessageDialog(null, statisticMessage.toString(), "Статистика", JOptionPane.PLAIN_MESSAGE);
    }
}
