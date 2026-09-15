package main;

import javax.swing.*;
import java.awt.*;


public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame();

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(true);
        window.setTitle("Jogo Pokemon Cópia");
        window.setExtendedState(Frame.MAXIMIZED_BOTH);
        window.setUndecorated(false);
        window.setSize(800, 600);
        
        Painel painel = new Painel();
        window.add(painel);
        
        window.pack();
        
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        
        painel.startGameThread();
    }
}
