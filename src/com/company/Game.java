package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class Game extends JFrame {
    private JMenuBar saveMenu = null;
    private final JPanel panel = new JPanel(new GridLayout(4, 4, 2, 2));
    private static final Random random = new Random();
    private final int[][] numbers = new int[4][4];
    private int counterStats;
    private static String playerName;

    public Game(String playerName, boolean itsOldGame) throws IOException {
        Game.playerName = playerName;

        if (itsOldGame){
            reload();
        }else {
            initGame();
        }

        setTitle("Пятнашки - Игрок: " + Game.playerName);
        setSize (350, 400);
        createSaveMenu();
        setJMenuBar(saveMenu);
        setLocationRelativeTo(null);
        setResizable(true);

        Container container = getContentPane();

        panel.setBackground(Color.white);
        container.add(panel);

        repaintField();
    }

    private void createSaveMenu() {
        saveMenu = new JMenuBar();
        JMenu fileMenu = new JMenu("Сохранение");

        JMenuItem item = new JMenuItem("Сохранить игру");
        JSeparator separator = new JSeparator();
        item.setActionCommand("Сохранить игру");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Игра будет сохранена", "Статистика", JOptionPane.PLAIN_MESSAGE);
                saveGame();
            }
        });
        fileMenu.add(item);
        fileMenu.add(separator);

        saveMenu.add(fileMenu);
    }

    public void saveGame() {
        try(FileWriter writer = new FileWriter("saveGame.txt", false))
        {
            writer.write(counterStats + " "+ playerName + "\n");
            for(int i=0;i<4;i++){
                for(int j = 0; j<4;j++) {
                    writer.write(numbers[i][j] + " ");
                }
                writer.write("\n");
            }
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void initGame() {
        int[] invariants = new int[16];

        // зануление массивов
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                numbers[i][j] = 0;
            }
        }

        for (int i = 1; i < 16; i++) {
            int k;
            int l;
            do {
                k = random.nextInt(100) % 4;
                l = random.nextInt(100) % 4;
            }
            while (numbers[k][l] != 0);

            numbers[k][l] = i;
            invariants[k*4+l] = i;
        }

        //Считаем сумму меньших чисел стоящих справа + номер строки где 0
        //Если полученная сумма четная – позиция соберется. Если нечетная – то не соберется.
        int counter=0;
        for (int i = 0; i < 16; i++) {
            int f=0;
               for (int j = i+1; j < 16; j++) {
                   if(invariants[i]>invariants[j] && invariants[j]!=0)
                   {
                       f++;
                   }
               }
            counter+=f;
        }
        int row = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (numbers[i][j] == 0) {
                    row = i;
                    break;
                }
            }
        }
        counter+=row+1;

        if (counter % 2 != 0) {
            initGame();
        }

    }


    private void reload() throws FileNotFoundException {
        Scanner scanner = new Scanner(new FileInputStream("SaveGame.txt"));
        counterStats = Integer.parseInt(scanner.next());
        playerName = scanner.next();
        for (int i =0 ;i<4;i++){
            for (int j =0 ;j<4;j++){
                numbers[i][j] = Integer.parseInt(scanner.next());
            }
        }
    }

    private void repaintField() {  //расстановка кнопок со значениями
        panel.removeAll();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                JButton button = new JButton(Integer.toString(numbers[i][j]));
                button.setFocusable(false);
                panel.add(button);
                button.setBackground(Color.getHSBColor(0.6f, 0.7f, 0.9f));
                if (numbers[i][j] == 0) {
                    button.setVisible(false);
                } else {
                    int currentRow = i;
                    int currentCol = j;
                    button.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JButton button = (JButton) e.getSource();
                            button.setVisible(false);
                            String name = button.getText();
                            try {
                                change(Integer.parseInt(name),currentRow,currentCol);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                }
            }
        }

        panel.validate();//проверят все элементы контейнера
    }

    private void change(int num, int currentRow, int currentCol) throws IOException {
        int i = currentRow;
        int j = currentCol;

        //сдвиг вверх по строкам
        if (i > 0) {
            if (numbers[i - 1][j] == 0) {
                numbers[i - 1][j] = num;
                numbers[i][j] = 0;
                counterStats++;
            }
        }
        //сдвиг вниз по строкам
        if (i < 3) {
            if (numbers[i + 1][j] == 0) {
                numbers[i + 1][j] = num;
                numbers[i][j] = 0;
                counterStats++;
            }
        }
        //сдвиг влево по столбцам
        if (j > 0) {
            if (numbers[i][j - 1] == 0) {
                numbers[i][j - 1] = num;
                numbers[i][j] = 0;
                counterStats++;
            }
        }
        //сдвиг вправо по столбцам
        if (j < 3) {
            if (numbers[i][j + 1] == 0) {
                numbers[i][j + 1] = num;
                numbers[i][j] = 0;
                counterStats++;
            }
        }

        repaintField();

        if (checkWin()) {
            JOptionPane.showMessageDialog(null, playerName+", Вы Выиграли!\nХодов: "+counterStats,
                    "Поздравляем", 1);
            initGame();
            repaintField();
            saveRecordInFile();
            setVisible(false);
            setVisible(true);
            counterStats = 0;
        }
    }

    private boolean checkWin() {
        boolean status = true;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == 3 && j > 2){  // последняя ячейка в сетке пустая
                    break;
                }
                if (numbers[i][j] != i * 4 + j + 1) { // соотвествие элементам массива координатам
                    status = false;
                    break;
                }

            }
        }
        return status;
    }

    private void saveRecordInFile() throws IOException {
        String record = String.valueOf(counterStats);

        try(FileWriter writer = new FileWriter("statistics.txt", true))
        {
            writer.write( "\n" + record + " ходов - " + playerName);
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
        // Сортировка статистики
        ArrayList<String> lines = new ArrayList<>();
        try {
            lines = new ArrayList<>(Files.readAllLines(Paths.get("statistics.txt")));
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        lines.removeAll(Arrays.asList("", null));
        lines.sort(Comparator.comparing(str -> Integer.parseInt(str.split("\\s+")[0])));

        Files.write(Paths.get("statistics.txt"), lines, StandardOpenOption.CREATE);
    }

}
