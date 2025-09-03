import javax.swing.*;
import java.awt.event.*;

public class SlidingButtonDemo {
    int n = 1;
    public static void main(String[] args) {

        JFrame frame = new JFrame("Sliding JButton Demo");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null); // absolute positioning

        JButton button = new JButton("Slide Me →");
        button.setBounds(20, 150, 120, 40); // initial position
        frame.add(button);

        // Timer controls animation
        Timer slideTimer = new Timer(5, null);
        slideTimer.addActionListener(new ActionListener() {
            int targetX = 400; // where we want to stop

            @Override
            public void actionPerformed(ActionEvent e) {
                int currentX = button.getX();
                if (currentX < targetX) currentX+=2;
                else if (currentX > targetX) currentX-=2;
                button.setLocation(currentX + 2, button.getY()); // speed = 2px/frame
            }
        });

        // Start animation on button click
        button.addActionListener(e -> {
            if (!slideTimer.isRunning()) {
                slideTimer.start();
            }
        });

        frame.setVisible(true);
    }
}
