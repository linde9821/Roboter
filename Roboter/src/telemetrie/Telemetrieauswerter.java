package telemetrie;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.eclipse.wb.swing.FocusTraversalOnArray;

public class Telemetrieauswerter extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public static final String version = "0.1b";

    private JPanel contentPane;
    private JList<String> list;
    private DefaultListModel<String> programmtelemetrie;
    private JButton btnDatensatzUntersuchen;
    private JButton btnSchlieen;
    private ArrayList<Telemetrie> telemetrieListe;

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
		    Telemetrieauswerter frame = new Telemetrieauswerter();
		    frame.setVisible(true);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    public Telemetrieauswerter(final ArrayList<Telemetrie> telemetrieParameter) {
	// font
	setFont(new Font("Arial", Font.PLAIN, 12));

	setTitle("Telemetrieauswerter " + version);
	setBounds(100, 100, 366, 505);
	contentPane = new JPanel();
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	setContentPane(contentPane);
	contentPane.setLayout(null);

	JScrollPane scrollPane = new JScrollPane();
	scrollPane.setBounds(10, 79, 324, 376);
	contentPane.add(scrollPane);

	btnDatensatzUntersuchen = new JButton("markierten Datensatz untersuchen");
	btnDatensatzUntersuchen.setVisible(false);
	scrollPane.setColumnHeaderView(btnDatensatzUntersuchen);
	btnDatensatzUntersuchen.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		int index = list.getSelectedIndex();

		if (index != -1) {
		    Telemetrie datensatz = telemetrieListe.get(index);

		    EventQueue.invokeLater(new Runnable() {
			public void run() {
			    try {
				TelemetrieFenster frame = new TelemetrieFenster(datensatz);
				frame.setVisible(true);
			    } catch (Exception e) {
				e.printStackTrace();
			    }
			}
		    });
		} else
		    JOptionPane.showMessageDialog(null, "Kein Datensatz ausgewählt");
	    }
	});
	setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[] { btnDatensatzUntersuchen, btnSchlieen }));

	JButton btnDateiLaden = new JButton("Datei laden");
	btnDateiLaden.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		    dateiLaden(telemetrieParameter);
	    }
	});
	btnDateiLaden.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		dateiLaden(telemetrieParameter);
	    }
	});
	btnDateiLaden.setBounds(10, 11, 115, 23);
	contentPane.add(btnDateiLaden);

	btnSchlieen = new JButton("schlie\u00DFen");
	btnSchlieen.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		    close();
	    }
	});
	btnSchlieen.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		close();
	    }
	});
	btnSchlieen.setBounds(10, 45, 115, 23);
	contentPane.add(btnSchlieen);

	list = new JList<String>();
	list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	scrollPane.setViewportView(list);

	JButton btnDateiSpeichern = new JButton("Datei speichern");
	btnDateiSpeichern.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		    dateiSpeichern();
	    }
	});
	btnDateiSpeichern.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		dateiSpeichern();
	    }
	});
	btnDateiSpeichern.setBounds(147, 11, 115, 23);
	contentPane.add(btnDateiSpeichern);

	this.requestFocus();

	if (telemetrieParameter != null) {
	    dateiLaden(telemetrieParameter);
	}
    }

    /**
     * Create the frame.
     */
    public Telemetrieauswerter() {
	this(null);
    }

    private void dateiSpeichern() {
	if (programmtelemetrie == null) {
	    JOptionPane.showMessageDialog(null, "Kein Daten zum speichern verfügbar");
	} else {
	    JFileChooser fc = new JFileChooser();
	    fc.setCurrentDirectory(new File("."));
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("TELEMETRIE FILES", "tmt");
	    fc.setFileFilter(filter);
	    int status = fc.showSaveDialog(null);

	    if (status == JFileChooser.APPROVE_OPTION) {
		String dateiname = fc.getSelectedFile().getAbsolutePath();

		ObjectOutputStream oos = null;

		try {
		    if (!dateiname.endsWith(".tmt"))
			dateiname += ".tmt";

		    oos = new ObjectOutputStream(new FileOutputStream(dateiname));
		    oos.writeObject(telemetrieListe);
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    System.out.println("Error while writing to the file " + dateiname);
		} finally {
		    try {
			oos.close();
		    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error while closing the file " + dateiname);
		    }
		}

	    }

	}
    }

    @SuppressWarnings("unchecked")
    private void dateiLaden(ArrayList<Telemetrie> telemetrieParameter) {
	programmtelemetrie = new DefaultListModel<String>();
	list.setModel(programmtelemetrie);

	if (telemetrieParameter == null) {
	    JFileChooser fc = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("TELEMETRIE FILES", "tmt");
	    fc.setFileFilter(filter);
	    fc.setCurrentDirectory(new File("."));

	    if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
		String dateiname = fc.getSelectedFile().getAbsolutePath();
		ObjectInputStream ois = null;

		try {
		    ois = new ObjectInputStream(new FileInputStream(dateiname));

		    telemetrieListe = ((ArrayList<Telemetrie>) ois.readObject());

		    if (telemetrieListe == null) {
			JOptionPane.showMessageDialog(null, "Die Datei ist leer und kann nicht geladen werdne.");
		    } else {
			for (Telemetrie datensatz : telemetrieListe) {
			    programmtelemetrie.addElement(datensatz.getData());
			}
		    }

		    btnDatensatzUntersuchen.setVisible(true);

		} catch (IOException e) {
		    e.printStackTrace();
		    JOptionPane.showMessageDialog(null, "Fehler beim Einlesen der Datei " + dateiname);
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		    JOptionPane.showMessageDialog(null, "Fehler beim Einlesen der Datei " + dateiname);
		} finally {
		    try {
			ois.close();
		    } catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		    }
		}
	    }

	} else {
	    telemetrieListe = new ArrayList<Telemetrie>(telemetrieParameter);

	    JOptionPane.showMessageDialog(null, "Übergebener Telemetriedatensatz wird geladen.");

	    for (Telemetrie datensatz : telemetrieListe) {
		programmtelemetrie.addElement(datensatz.getData());
	    }
	}
	list.setModel(programmtelemetrie);

    }

    private void close() {
	this.setVisible(false);
    }
}
