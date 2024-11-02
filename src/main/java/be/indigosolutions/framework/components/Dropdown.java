package be.indigosolutions.framework.components;

import javax.swing.*;

/**
 * User: christophe
 * Date: 20/10/13
 */
public class Dropdown<T> extends JComboBox {

//    private long previousEvent = 0;
//    private String keyBuffer = "";

    public Dropdown() {
        super();
    }

    public Dropdown(java.util.List<T> list) {
        super(list.toArray());
//        addKeyListener(new KeyAdapter() {
//            @Override
//            public void keyTyped(KeyEvent e) {
//                long currentTime = Calendar.getInstance().getTimeInMillis();
//                if (currentTime > previousEvent + 3000) {
//                    keyBuffer = "";
//                }
//                char keyChar = e.getKeyChar();
//                keyBuffer += keyChar;
//                System.out.println(keyChar + " " + keyBuffer + " [old:" + previousEvent + "|new:" + currentTime + "]");
//            }
//        });
    }

    public Dropdown(T[] items) {
        super(items);
    }
}
