package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import roboter.Robot;
import roboter.Telemetrie;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JList;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.ListSelectionModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Telemetrieauswerter extends JFrame {

    private JPanel contentPane;
    private JList<Telemetrie> list;
    private TelemetrieListModel Tel;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		try {
		    Telemetrieauswerter frame = new Telemetrieauswerter();
		    frame.setVisible(true);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    public Telemetrieauswerter(ArrayList<Telemetrie> input) {
	// font
	setFont(new Font("Arial", Font.PLAIN, 12));

	setTitle("Telemetrieauswerter");
	setBounds(100, 100, 366, 300);

	JMenuBar menuBar = new JMenuBar();
	setJMenuBar(menuBar);

	JMenu mnDatei = new JMenu("Datei");
	menuBar.add(mnDatei);

	JButton btnDateiLaden = new JButton("Datei Laden");
	btnDateiLaden.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (input == null) {
		    ArrayList<Telemetrie> input = new ArrayList<Telemetrie>();
		    input.add(new Telemetrie());
		    input.add(new Telemetrie());

		    for (int i = 0; i < input.size(); i++) {
			Tel.addElement(input.get(i));
		    }
		} else {
		    for (int i = 0; i < input.size(); i++) {
			Tel.addElement(input.get(i));
		    }
		}

	    }
	});
	mnDatei.add(btnDateiLaden);
	contentPane = new JPanel();
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	setContentPane(contentPane);
	contentPane.setLayout(null);

	JScrollPane scrollPane = new JScrollPane();
	scrollPane.setBounds(10, 11, 324, 218);
	contentPane.add(scrollPane);

	list = new JList<Telemetrie>();
	list.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		EventQueue.invokeLater(new Runnable() {
		    public void run() {
			try {
			    int datenIndex = list.getSelectedIndex();
			    TelemetrieFenster frame = new TelemetrieFenster((Telemetrie) Tel.get(datenIndex));
			    frame.setVisible(true);
			} catch (Exception e) {
			    e.printStackTrace();
			}
		    }
		});
	    }
	});
	list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	scrollPane.setViewportView(list);

	Tel = new TelemetrieListModel();
	list.setModel(Tel);
    }

    /**
     * Create the frame.
     */
    public Telemetrieauswerter() {
	this(null);
    }
}
