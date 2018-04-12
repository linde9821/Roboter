package Punkt;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.eclipse.wb.swing.FocusTraversalOnArray;

public class Ablaufkonfigurator extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField tfX;
    private JTextField tfY;
    private JTextField tfZ;
    private DefaultListModel<String> punktListe;
    private ArrayList<Punkt> punktArrayListe;
    private String dateiname = "." + File.separator + "punktListe.pkt";

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
	try {
	    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
	} catch (Throwable e) {
	    e.printStackTrace();
	}
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		try {
		    Ablaufkonfigurator frame = new Ablaufkonfigurator();
		    frame.setVisible(true);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    /**
     * Create the frame.
     */
    public Ablaufkonfigurator() {
	setBounds(100, 100, 450, 392);
	contentPane = new JPanel();
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	setContentPane(contentPane);
	contentPane.setLayout(null);

	JScrollPane scrollPane = new JScrollPane();
	scrollPane.setBounds(10, 103, 414, 239);
	contentPane.add(scrollPane);

	JList<String> list = new JList<String>();
	list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	scrollPane.setViewportView(list);
	punktListe = new DefaultListModel<String>();
	list.setModel(punktListe);
	punktArrayListe = new ArrayList<Punkt>();

	File datei = new File(dateiname);
	BufferedReader in = null;

	if (!datei.exists()) {
	    try {
		datei.createNewFile();
	    } catch (IOException ex) {
		ex.printStackTrace();
	    }
	} else {
	    String punktzeile;
	    try {
		in = new BufferedReader(new FileReader(dateiname));
		while ((punktzeile = in.readLine()) != null) {
		    punktListe.addElement(punktzeile);
		}

		for (int i = 0; i < punktListe.size(); i++) {
		    String strbf = punktListe.elementAt(i);

		    short x, y, z;

		    x = (short) Double.parseDouble(strbf.substring(0, strbf.indexOf(" ")));

		    strbf = strbf.substring(strbf.indexOf(" ") + 1);

		    y = (short) Double.parseDouble(strbf.substring(0, strbf.indexOf(" ")));

		    strbf = strbf.substring(strbf.indexOf(" ") + 1);

		    z = (short) Double.parseDouble(strbf.substring(0));

		    punktArrayListe.add(new Punkt(x, y, z));

		}
	    } catch (IOException ex) {
		ex.printStackTrace();
	    }
	}

	tfX = new JTextField();
	tfX.setBounds(30, 11, 86, 20);
	contentPane.add(tfX);
	tfX.setColumns(10);

	tfY = new JTextField();
	tfY.setBounds(30, 42, 86, 20);
	contentPane.add(tfY);
	tfY.setColumns(10);

	tfZ = new JTextField();
	tfZ.setBounds(30, 73, 86, 20);
	contentPane.add(tfZ);
	tfZ.setColumns(10);

	JLabel lblX = new JLabel("X:");
	lblX.setBounds(10, 14, 10, 14);
	contentPane.add(lblX);

	JLabel lblY = new JLabel("Y:");
	lblY.setBounds(10, 45, 10, 14);
	contentPane.add(lblY);

	JLabel lblZ = new JLabel("Z:");
	lblZ.setBounds(10, 73, 10, 14);
	contentPane.add(lblZ);

	JButton btnHinzufgen = new JButton("hinzuf\u00FCgen");
	btnHinzufgen.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		short x = Short.parseShort(tfX.getText());
		short y = Short.parseShort(tfY.getText());
		short z = Short.parseShort(tfZ.getText());

		punktArrayListe.add(new Punkt(x, y, z));
		punktListe.addElement(new Punkt(x, y, z).toString());

		tfX.setText("");
		tfY.setText("");
		tfZ.setText("");

		tfX.requestFocus();
	    }
	});
	btnHinzufgen.setBounds(126, 10, 89, 23);
	contentPane.add(btnHinzufgen);

	JButton btnMarkiertenPunktBearbeiten = new JButton("markierten Punkt bearbeiten");
	btnMarkiertenPunktBearbeiten.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		int index = list.getSelectedIndex();

		if (index != -1) {
		    short x = (short) punktArrayListe.get(index).getX();
		    short y = (short) punktArrayListe.get(index).getY();
		    short z = (short) punktArrayListe.get(index).getZ();

		    tfX.setText(Short.toString(x));
		    tfY.setText(Short.toString(y));
		    tfZ.setText(Short.toString(z));

		    punktArrayListe.remove(index);
		    punktListe.remove(index);
		} else {
		    JOptionPane.showMessageDialog(null, "Kein Eintrag markiert");
		}
	    }
	});
	btnMarkiertenPunktBearbeiten.setBounds(225, 10, 184, 23);
	contentPane.add(btnMarkiertenPunktBearbeiten);

	JButton btnLschen = new JButton("l\u00F6schen");
	btnLschen.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		int index = list.getSelectedIndex();

		if (index != -1) {
		    punktArrayListe.remove(index);
		    punktListe.remove(index);
		} else {
		    JOptionPane.showMessageDialog(null, "Kein Eintrag markiert");
		}
	    }
	});
	btnLschen.setBounds(126, 41, 89, 23);
	contentPane.add(btnLschen);

	JButton btnSchließen = new JButton("schlie\u00DFen");
	btnSchließen.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		close();
	    }
	});
	btnSchließen.setBounds(225, 41, 184, 23);
	contentPane.add(btnSchließen);
	setFocusTraversalPolicy(new FocusTraversalOnArray(
		new Component[] { tfX, tfY, tfZ, btnHinzufgen, btnLschen, btnMarkiertenPunktBearbeiten }));

	setTitle("Ablaufkonfigurator");
    }

    private void close() {
	BufferedWriter out = null;
	try {
	    out = new BufferedWriter(new FileWriter(dateiname));

	    for (int i = 0; i < punktArrayListe.size(); i++) {
		out.write(punktArrayListe.get(i).toString());
		out.newLine();
	    }
	} catch (IOException ex) {
	    ex.printStackTrace();
	} finally {
	    if (out != null) {
		try {
		    out.close();
		} catch (IOException ex) {
		    ex.printStackTrace();
		}
	    }
	}

	setVisible(false);
    }
}
