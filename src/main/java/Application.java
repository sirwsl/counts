import gui.MainGUI;

import java.awt.*;

public class Application {

    public static void main(String[] args) {

        // 显示应用 GUI
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    MainGUI mainGUI = new MainGUI();
                    mainGUI.setVisible(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
