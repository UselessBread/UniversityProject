package com.company;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PopupActionListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        (new ThreadResourceUpdater(e.getActionCommand())).run();
    }
}
