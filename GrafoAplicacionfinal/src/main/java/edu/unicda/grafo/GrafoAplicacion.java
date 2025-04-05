/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.unicda.grafo;

/**
 *
 * @author Luisv
 */

import javax.swing.*;

public class GrafoAplicacion {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GrafoAplicacionGUI app = new GrafoAplicacionGUI();
            app.setVisible(true);
        });
    }
}
