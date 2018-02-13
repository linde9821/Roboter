package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import roboter.Telemetrie;
import javax.swing.JTextArea;

public class TelemetrieFenster extends JFrame {

    private JPanel contentPane;


    /**
     * Create the frame.
     */
    public TelemetrieFenster(Telemetrie Datensatz) {
	setBounds(100, 100, 645, 472);
	contentPane = new JPanel();
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	setContentPane(contentPane);
	contentPane.setLayout(null);
	
	JTextArea textArea = new JTextArea();
	textArea.setEditable(false);
	textArea.setBounds(10, 11, 616, 411);
	contentPane.add(textArea);
	
	textArea.append(Datensatz.getInfo());
	
    }
}
