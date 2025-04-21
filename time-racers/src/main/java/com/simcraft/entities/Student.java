package com.simcraft.entities;
import java.awt.Image;

import javax.swing.JPanel;

public class Student extends NPC {
    public Student(JPanel panel, int xPos, int yPos, Image backgroundImage) {
        super(panel, xPos, yPos, 2, "Student/FStud", backgroundImage); // Slow speed
    }
}
