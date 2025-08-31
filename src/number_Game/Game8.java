package number_Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Random;

public class Game8 {
    private JFrame frame;
    private JTextField textField;
    private JLabel label;
    private JLabel feedbackLabel;
    private JButton button;
    private JButton restartButton;
    private JButton hintButton;
    private JButton submitGuessButton;
    private JButton languageButton;
    private JComboBox<String> difficultyComboBox;
    private int attempts;
    private String target;
    private String difficulty = "";
    private int hintUsageCount = 0;
    private Timer timer;
    private int elapsedTime = 0;
    private JLabel timeLabel;
    private Locale currentLocale = Locale.getDefault();
    private ResourceBundle messages;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Game8 window = new Game8();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Game8() {
        loadLanguage(currentLocale);
        frame = new JFrame(messages.getString("difficulty_choice"));
        frame.setBounds(100, 100, 500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        languageButton = new JButton("切换语言");
        languageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentLocale.equals(Locale.getDefault())) {
                    currentLocale = new Locale("en", "US");
                } else {
                    currentLocale = Locale.getDefault();
                }
                loadLanguage(currentLocale);
                updateUI();
            }
        });
        frame.getContentPane().add(languageButton);

        difficultyComboBox = new JComboBox<>(new String[]{messages.getString("simple"), messages.getString("hard")});
        frame.getContentPane().add(difficultyComboBox);

        label = new JLabel(messages.getString("difficulty_choice"));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        frame.getContentPane().add(label);

        textField = new JTextField();
        textField.setColumns(10);
        frame.getContentPane().add(textField);

        feedbackLabel = new JLabel("");
        feedbackLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frame.getContentPane().add(feedbackLabel);

        timeLabel = new JLabel("时间：0秒");
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frame.getContentPane().add(timeLabel);

        button = new JButton(messages.getString("start_game"));
        button.setEnabled(false);
        button.setBackground(Color.YELLOW);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!difficulty.isEmpty()) {
                    resetGame();
                    label.setText(messages.getString("difficulty_choice"));
                }
            }
        });
        frame.getContentPane().add(button);

        restartButton = new JButton(messages.getString("restart_game"));
        restartButton.setEnabled(false);
        restartButton.setBackground(Color.CYAN);
        frame.getContentPane().add(restartButton);

        submitGuessButton = new JButton(messages.getString("submit_guess"));
        submitGuessButton.setBackground(Color.GREEN);
        submitGuessButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String guess = textField.getText();
                System.out.println("用户输入: " + guess);
                if (!guess.isEmpty()) {
                    checkGuess(guess);
                } else {
                    feedbackLabel.setText(messages.getString("guess_feedback"));
                }
            }
        });
        frame.getContentPane().add(submitGuessButton);

        hintButton = new JButton(messages.getString("get_hint"));
        hintButton.setBackground(Color.ORANGE);
        hintButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (hintUsageCount < 2) {
                    provideHint();
                } else {
                    feedbackLabel.setText(messages.getString("you_have_no_hint"));
                }
            }
        });
        frame.getContentPane().add(hintButton);

        difficultyComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                difficulty = (String) difficultyComboBox.getSelectedItem();
                button.setEnabled(true);
            }
        });

        resetGame();
    }

    private void loadLanguage(Locale locale) {
        messages = ResourceBundle.getBundle("resources.messages", locale);
    }

    private void updateUI() {
        frame.setTitle(messages.getString("difficulty_choice"));
        label.setText(messages.getString("difficulty_choice"));
        button.setText(messages.getString("start_game"));
        restartButton.setText(messages.getString("restart_game"));
        hintButton.setText(messages.getString("get_hint"));
        submitGuessButton.setText(messages.getString("submit_guess"));

        difficultyComboBox.removeAllItems();
        difficultyComboBox.addItem(messages.getString("simple"));
        difficultyComboBox.addItem(messages.getString("hard"));

        languageButton.setText(currentLocale.equals(Locale.getDefault()) ? "Switch to English" : "切换语言");
    }

    private void resetGame() {
        if (difficulty.isEmpty()) {
            feedbackLabel.setText(messages.getString("difficulty_choice"));
            return;
        }

        if (difficulty.equals(messages.getString("simple"))) {
            target = String.valueOf(new Random().nextInt(100) + 1);
        } else if (difficulty.equals(messages.getString("hard"))) {
            target = generateTarget();
            feedbackLabel.setText(messages.getString("difficulty_choice"));
        }
        attempts = 0;
        hintUsageCount = 0;
        textField.setText("");
        feedbackLabel.setText("");
        label.setText(messages.getString("difficulty_choice"));
        button.setEnabled(true);
        restartButton.setEnabled(true);
        hintButton.setEnabled(true);

        startTimer();
    }

    private void startTimer() {
        elapsedTime = 0;
        timeLabel.setText(messages.getString("time_label") + "0");

        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                elapsedTime++;
                timeLabel.setText(messages.getString("time_label") + elapsedTime);
            }
        });
        timer.start();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    private String generateTarget() {
        Random rand = new Random();
        StringBuilder targetString = new StringBuilder();
        int length = 3;
        for (int i = 0; i < length; i++) {
            int type = rand.nextInt(2);
            if (type == 0) {
                targetString.append(rand.nextInt(10));
            } else {
                targetString.append((char) ('A' + rand.nextInt(26)));
            }
        }
        return targetString.toString();
    }

    private void checkGuess(String guess) {
        attempts++;

        if (difficulty.equals(messages.getString("simple"))) {
            int guessNumber = Integer.parseInt(guess);
            if (guessNumber < Integer.parseInt(target)) {
                feedbackLabel.setText(messages.getString("too_small"));
            } else if (guessNumber > Integer.parseInt(target)) {
                feedbackLabel.setText(messages.getString("too_large"));
            } else {
                feedbackLabel.setText(String.format("Congratulations, you guessed it right! You guessed %d times!", attempts));
                stopTimer();
                restartButton.setEnabled(true);
                button.setEnabled(false);
            }
        } else if (difficulty.equals(messages.getString("hard"))) {
            if (guess.length() != target.length()) {
                feedbackLabel.setText("输入的长度不正确！目标长度为 " + target.length());
                return;
            }

            StringBuilder feedback = new StringBuilder();
            for (int i = 0; i < target.length(); i++) {
                char guessChar = Character.toLowerCase(guess.charAt(i));
                char targetChar = Character.toLowerCase(target.charAt(i));

                if (guessChar == targetChar) {
                    feedback.append("位置 " + (i + 1) + " 完全正确！ ");
                } else if (Character.isDigit(guessChar) && Character.isDigit(targetChar) ||
                        Character.isLetter(guessChar) && Character.isLetter(targetChar)) {
                    feedback.append("位置 " + (i + 1) + " 类型正确，但具体不对。 ");
                } else {
                    feedback.append("位置 " + (i + 1) + " 完全错误。 ");
                }
            }
            feedbackLabel.setText(feedback.toString());

            if (guess.equalsIgnoreCase(target)) {
                feedbackLabel.setText(String.format("Congratulations, you guessed it right! You guessed %d times!", attempts));
                stopTimer();
                restartButton.setEnabled(true);
                button.setEnabled(false);
            }
        }
    }

    private void provideHint() {
        if (hintUsageCount < 2) {
            Random rand = new Random();
            int randomIndex = rand.nextInt(target.length());
            char hintChar = target.charAt(randomIndex);
            feedbackLabel.setText("小提示：目标中的某个字符是 " + hintChar);
            hintUsageCount++;
        } else {
            feedbackLabel.setText("你已经用完了所有小提示！");
            hintButton.setEnabled(false);
        }
    }
}
