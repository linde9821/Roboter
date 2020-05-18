package gui;

import java.awt.Color;
//awt imports
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
//time imports
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
//util import
import java.util.ArrayList;

import javax.swing.DefaultListModel;
//swing import 
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

//eclipse import
import org.eclipse.wb.swing.FocusTraversalOnArray; //taborder

import Punkt.Ablaufkonfigurator;
import Punkt.Punkt;
//input imports 
import input.ControlInput;//autocorrection
import input.ControlInputException;//autocorrection exception
import input.EmptyInputException;//empty input exception
import roboter.Robot;
//robot imports 
import roboter.RoboterException;//robot exception
import telemetrie.Telemetrie;
import telemetrie.Telemetrieauswerter;

/**
 * Window application for controlling the robot
 */
public class Software extends JFrame implements Runnable {
    private static final long serialVersionUID = 1L;
    private String version = "3.0";// version
    public Robot myRobot;// robot
    private String device;// devicename (essential for controlling the robot)

    ArrayList<Punkt> liste;
    public ArrayList<RoboterException> roboterExceptionListe;
    public ArrayList<Telemetrie> aktuelleTelemetrie;

    private JPanel contentPane;
    private JScrollPane scrollPane;
    public JTextArea textArea;
    private JTextField tfX;
    private JTextField tfY;
    private JTextField tfZ;
    private JTextField tfMID;
    private JTextField tfWert;
    private JRadioButton rdbtnStatusausgaben;
    public JRadioButton rdbtnFehlermeldungen;
    private JRadioButton rdbtnAutokorrektur;
    private JRadioButton rbTelemetrie;
    private JButton btnAusfuehren;
    private JButton btnLeeren;
    private JButton btnClose;
    private JButton btnBefehl;
    private JButton btnAblauf;
    private JButton btnVerbinden;
    private JButton btnSetzen;
    private JButton btnAuslesen;

    // temp
    private JMenu mnHilfe;
    private JMenuItem mntmNeustart;
    private JMenuItem mntmSchlieen;
    private JMenuItem mntmZurcksetzen;
    private JMenuItem mntmTelemetrieanalyse;
    private JMenuItem mntmAktuelleTelemetrieSpeichern;
    private JMenuItem mntmVerbindungstest;
    private JMenuItem mntmReadme;
    private JButton btnZeigeAblauf;
    public boolean statusausgabe;

    public boolean isRunningP = false;
    public boolean isRunningA = false;
    public boolean stop = false;
    Thread t;
    Punkt p = null;

    /**
     * Launch the application.
     */
    public static void main(String[] args, int x, int y) {
	try {
	    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
	} catch (Throwable e) {
	    e.printStackTrace();
	}
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		try {
		    Software frame;

		    // decides if the default devicename gets used or a spacific one
		    if (args.length == 0)
			frame = new Software("COM3", x, y);
		    else {
			// if (args[0].contains("COM"))
			frame = new Software(args[0], x, y);
			/*
			 * else { frame = new Software("COM3", x, y);
			 * System.out.println("The programm was started with the argument \"" + args[0]
			 * + "\"\nIt seems like this is not correct. To make sure the frame gets " +
			 * "created and the robot is controlabel it was started with the default value auf \"COM3\""
			 * ); }
			 */
		    }

		    frame.setVisible(true);
		} catch (Exception e) {
		    System.out.println("Exception while creating frame");
		    e.printStackTrace();
		}
	    }
	});
    }

    public static void main(String[] args) {
	main(args, 100, 100);
    }

    /**
     * Create the frame.
     * 
     * @wbp.parser.constructor
     */
    public Software(String str) {
	this(str, 100, 100);
    }

    public Software(String str, int x, int y) {
	t = new Thread(this, "Perform");

	aktuelleTelemetrie = null;

	device = str;// get device name

	liste = new ArrayList<Punkt>();// list of point for ablauf();

	getPunktListe();

	roboterExceptionListe = new ArrayList<RoboterException>();

	// Titel setzen
	setTitle("Robot Toolkit " + version);

	// font
	setFont(new Font("Arial", Font.PLAIN, 12));

	// auto resizeing of the textArea
	addComponentListener(new ComponentAdapter() {
	    @Override
	    public void componentResized(ComponentEvent e) {
		int x = 360;
		int y = 80;

		scrollPane.setBounds(332, 12, getBounds().width - x, getBounds().height - y);
	    }

	    @Override
	    public void componentShown(ComponentEvent e) {
		// startUpProcedure();
	    }
	});
	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	setBounds(x, y, 900, 428);

	JMenuBar menuBar = new JMenuBar();
	menuBar.setBorderPainted(false);
	menuBar.setBackground(SystemColor.controlShadow);
	setJMenuBar(menuBar);

	JMenu mnDatei = new JMenu("Datei");
	mnDatei.setForeground(Color.BLACK);
	mnDatei.setFont(new Font("Arial", Font.PLAIN, 12));
	mnDatei.setBackground(SystemColor.controlShadow);
	menuBar.add(mnDatei);

	JMenuItem mntmNewMenuItem = new JMenuItem("Neues Fenster");
	mntmNewMenuItem.setFont(new Font("Arial", Font.PLAIN, 12));
	mntmNewMenuItem.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		String[] args = new String[0];
		main(args, 100, 100);
	    }
	});
	mnDatei.add(mntmNewMenuItem);

	mntmNeustart = new JMenuItem("Neustart");
	mntmNeustart.setFont(new Font("Arial", Font.PLAIN, 12));
	mntmNeustart.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		String[] args = new String[0];

		Rectangle rt = getBounds();

		main(args, rt.x, rt.y);
		setVisible(false);
	    }
	});
	mnDatei.add(mntmNeustart);

	mntmSchlieen = new JMenuItem("Schließen");
	mntmSchlieen.setFont(new Font("Arial", Font.PLAIN, 12));
	mntmSchlieen.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		close();
	    }
	});
	mnDatei.add(mntmSchlieen);

	JMenu mnBearbeiten = new JMenu("Bearbeiten");
	mnBearbeiten.setForeground(Color.BLACK);
	mnBearbeiten.setFont(new Font("Arial", Font.PLAIN, 12));
	mnBearbeiten.setBackground(SystemColor.controlShadow);
	menuBar.add(mnBearbeiten);

	mntmZurcksetzen = new JMenuItem("Zurücksetzen");
	mntmZurcksetzen.setFont(new Font("Arial", Font.PLAIN, 12));
	mntmZurcksetzen.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		emptyTextArea();
	    }
	});

	JMenuItem mntmAblaufkonfigurator = new JMenuItem("Ablaufkonfigurator");
	mntmAblaufkonfigurator.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
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
	});
	mntmAblaufkonfigurator.setFont(new Font("Arial", Font.PLAIN, 12));
	mnBearbeiten.add(mntmAblaufkonfigurator);
	mnBearbeiten.add(mntmZurcksetzen);

	JMenu mnRobot = new JMenu("Robot");
	mnRobot.setForeground(Color.BLACK);
	mnRobot.setBackground(SystemColor.controlShadow);
	mnRobot.setFont(new Font("Arial", Font.PLAIN, 12));
	menuBar.add(mnRobot);

	mntmTelemetrieanalyse = new JMenuItem("Telemetrieauswerter");
	mntmTelemetrieanalyse.setFont(new Font("Arial", Font.PLAIN, 12));
	mntmTelemetrieanalyse.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
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
	});

	mntmVerbindungstest = new JMenuItem("Verbindungstest");
	mntmVerbindungstest.setFont(new Font("Arial", Font.PLAIN, 12));
	mntmVerbindungstest.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		connectionTest();
	    }
	});
	mnRobot.add(mntmVerbindungstest);

	JMenuItem mntmRoboterStresstest = new JMenuItem("Roboter Stresstest");
	mntmRoboterStresstest.setEnabled(false);
	mntmRoboterStresstest.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent k) {
		int dialogButton = JOptionPane.YES_NO_OPTION;

		int dialogResult = JOptionPane.showConfirmDialog(null, "Stresstest starten? (Dauert bis zu 48 Minuten)",
			"Meldung", dialogButton);

		if (dialogResult == JOptionPane.YES_OPTION) {
		    ArrayList<Punkt> stresstestPunkte = new ArrayList<Punkt>();

		    aktuelleTelemetrie = new ArrayList<Telemetrie>();

		    for (int i = 0; i < 150; i++) {
			stresstestPunkte.add(new Punkt(100, 100, 100));
			stresstestPunkte.add(new Punkt(200, 100, 100));
		    }

		    // ablauf(stresstestPunkte);

		    String dateiname = "." + "StresstestTelemetrie"
			    + LocalDateTime.now().toString().replaceAll(":", "_") + ".tmt";

		    if (aktuelleTelemetrie != null) {
			ObjectOutputStream oos = null;

			try {
			    oos = new ObjectOutputStream(new FileOutputStream(dateiname));
			    oos.writeObject(aktuelleTelemetrie);
			} catch (IOException e) { // TODO Auto-generated catch block
			    e.printStackTrace();
			} finally {
			    try {
				oos.close();
			    } catch (IOException e) { // TODO Auto-generated catch block
				e.printStackTrace();
			    }
			}
		    } else {
			JOptionPane.showMessageDialog(null,
				"Es existieren aktuell keine Telemetriedaten in der Laufzeitumgebung.");
		    }

		}
	    }
	});
	mntmRoboterStresstest.setFont(new Font("Arial", Font.PLAIN, 12));
	mnRobot.add(mntmRoboterStresstest);
	mnRobot.add(mntmTelemetrieanalyse);

	mntmAktuelleTelemetrieSpeichern = new JMenuItem("aktuelle Telemetrie Speichern");
	mntmAktuelleTelemetrieSpeichern.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent k) {
		String dateiname = "." + File.separator + "TelemetrieDaten" + File.separator + "Telemetrie"
			+ LocalDateTime.now().toString().replaceAll(":", "_") + ".tmt";

		File file = new File(dateiname);

		if (!file.exists())
		    new File("." + File.separator + "TelemetrieDaten").mkdirs();

		if (aktuelleTelemetrie != null) {
		    ObjectOutputStream oos = null;

		    try {
			oos = new ObjectOutputStream(new FileOutputStream(dateiname));
			oos.writeObject(aktuelleTelemetrie);
		    } catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		    } finally {
			try {
			    oos.close();
			} catch (IOException e) { // TODO Auto-generated catch block
			    e.printStackTrace();
			}
		    }
		} else {
		    JOptionPane.showMessageDialog(null,
			    "Es existieren aktuell keine Telemetriedaten in der Laufzeitumgebung.");
		}

	    }
	});

	JMenuItem mntmAktuelleTelemetrieAuswerten = new JMenuItem("aktuelle Telemetrie Auswerten");
	mntmAktuelleTelemetrieAuswerten.setFont(new Font("Arial", Font.PLAIN, 12));
	mntmAktuelleTelemetrieAuswerten.setForeground(Color.BLACK);
	mntmAktuelleTelemetrieAuswerten.setBackground(SystemColor.controlShadow);
	mntmAktuelleTelemetrieAuswerten.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (!(aktuelleTelemetrie == null)) {

		    EventQueue.invokeLater(new Runnable() {
			public void run() {
			    try {
				Telemetrieauswerter frame = new Telemetrieauswerter(aktuelleTelemetrie);
				frame.setVisible(true);
			    } catch (Exception e) {
				e.printStackTrace();
			    }
			}
		    });
		} else {
		    JOptionPane.showMessageDialog(null,
			    "Es existieren aktuell keine Telemetriedaten in der Laufzeitumgebung.");
		}
	    }
	});
	mnRobot.add(mntmAktuelleTelemetrieAuswerten);
	mntmAktuelleTelemetrieSpeichern.setFont(new Font("Arial", Font.PLAIN, 12));
	mnRobot.add(mntmAktuelleTelemetrieSpeichern);

	mnHilfe = new JMenu("Hilfe");
	mnHilfe.setFont(new Font("Arial", Font.PLAIN, 12));
	mnHilfe.setForeground(Color.BLACK);
	menuBar.add(mnHilfe);

	mntmReadme = new JMenuItem("Readme");
	mntmReadme.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		String defaultLocation = "." + File.separator + "Readme.txt";
		File readMeLocation = new File(defaultLocation);

		if (!readMeLocation.exists() || !readMeLocation.isFile()) {
		    generateReadme();

		    JOptionPane.showMessageDialog(null,
			    "Readme konnte nicht gefunden werden. Wurde automatisch generiert!");

		    actionPerformed(e);

		} else
		    try {
			java.awt.Desktop.getDesktop().edit(readMeLocation);
		    } catch (IOException e1) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "Konnte Readme finden jedoch nicht öffnen.");
			e1.printStackTrace();
		    }

	    }
	});
	mnHilfe.add(mntmReadme);
	contentPane = new JPanel();
	contentPane.setBackground(SystemColor.controlShadow);// frame background color
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	contentPane.setLayout(null);
	setContentPane(contentPane);

	// label x
	JLabel lblx = new JLabel("x-Wert:");
	lblx.setFont(new Font("Arial", Font.PLAIN, 12));
	lblx.setBounds(10, 15, 46, 14);
	contentPane.add(lblx);

	// label y
	JLabel lbly = new JLabel("y-Wert:");
	lbly.setFont(new Font("Arial", Font.PLAIN, 12));
	lbly.setBounds(10, 46, 46, 14);
	contentPane.add(lbly);

	// label z
	JLabel lblz = new JLabel("z-Wert:");
	lblz.setFont(new Font("Arial", Font.PLAIN, 12));
	lblz.setBounds(10, 77, 46, 14);
	contentPane.add(lblz);

	// Label für Motor-ID
	JLabel lblMotor = new JLabel("Motor: ");
	lblMotor.setBounds(10, 273, 46, 14);
	contentPane.add(lblMotor);

	// Label für Wert des manuell angesteuerten Motors
	JLabel lblWert = new JLabel("Wert:");
	lblWert.setBounds(10, 298, 46, 14);
	contentPane.add(lblWert);

	// scrollPane
	scrollPane = new JScrollPane();
	scrollPane.setBounds(332, 12, 542, 338);
	contentPane.add(scrollPane);

	// textArea
	textArea = new JTextArea();
	scrollPane.setViewportView(textArea);
	textArea.setEditable(false);
	textArea.setLineWrap(true);
	textArea.setWrapStyleWord(true);
	textArea.setCaretPosition(textArea.getDocument().getLength());
	textArea.setFont(new Font("Arial", Font.PLAIN, 12));// sets font

	// textField x
	tfX = new JTextField();
	tfX.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
		    tfY.requestFocus();
		    tfY.selectAll();
		}
	    }
	});
	tfX.setText("170");
	tfX.setFont(new Font("Arial", Font.PLAIN, 12));
	tfX.setBounds(66, 12, 119, 20);
	contentPane.add(tfX);
	tfX.setColumns(10);

	// textField y
	tfY = new JTextField();
	tfY.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
		    tfZ.requestFocus();
		    tfZ.selectAll();
		}
	    }
	});
	tfY.setText("0");
	tfY.setFont(new Font("Arial", Font.PLAIN, 12));
	tfY.setBounds(66, 43, 119, 20);
	contentPane.add(tfY);
	tfY.setColumns(10);

	// Textfeld für Motor-ID
	tfMID = new JTextField();
	tfMID.setFont(new Font("Arial", Font.PLAIN, 12));
	tfMID.setColumns(10);
	tfMID.setBounds(66, 269, 31, 20);
	contentPane.add(tfMID);

	// Textfeld für Wert des Manuell angesteuerten Motors
	tfWert = new JTextField();
	tfWert.setFont(new Font("Arial", Font.PLAIN, 12));
	tfWert.setColumns(10);
	tfWert.setBounds(66, 296, 31, 20);
	contentPane.add(tfWert);

	// button Ausführen
	btnAusfuehren = new JButton("Ausführen");
	btnAusfuehren.setBackground(SystemColor.controlShadow);
	btnAusfuehren.setFont(new Font("Arial", Font.PLAIN, 12));
	btnAusfuehren.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
		    perform();
		}
	    }
	});
	btnAusfuehren.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (e.getModifiers() == ActionEvent.MOUSE_EVENT_MASK)
		    perform();
	    }
	});
	btnAusfuehren.setBounds(66, 146, 119, 23);
	contentPane.add(btnAusfuehren);

	// button Simulieren
	JButton btnSimulieren = new JButton("Simulieren");
	btnSimulieren.setBackground(SystemColor.controlShadow);
	btnSimulieren.setFont(new Font("Arial", Font.PLAIN, 12));
	btnSimulieren.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		    simulate();
	    }
	});
	btnSimulieren.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (e.getModifiers() == ActionEvent.MOUSE_EVENT_MASK)
		    simulate();
	    }
	});
	btnSimulieren.setBounds(66, 112, 119, 23);
	contentPane.add(btnSimulieren);

	// textField z
	tfZ = new JTextField();
	tfZ.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
		    btnSimulieren.requestFocus();
		}
	    }
	});
	tfZ.setText("40");
	tfZ.setFont(new Font("Arial", Font.PLAIN, 12));
	tfZ.setBounds(66, 74, 119, 20);
	contentPane.add(tfZ);
	tfZ.setColumns(10);

	// button Schließen
	btnClose = new JButton("Schlie\u00DFen");
	btnClose.setBackground(SystemColor.controlShadow);
	btnClose.setFont(new Font("Arial", Font.PLAIN, 12));
	btnClose.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		    close();
	    }
	});
	btnClose.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (e.getModifiers() == ActionEvent.MOUSE_EVENT_MASK)
		    close();
	    }
	});
	btnClose.setBounds(107, 327, 119, 23);
	contentPane.add(btnClose);

	// button Leeren
	btnLeeren = new JButton("Leeren");
	btnLeeren.setBackground(SystemColor.controlShadow);
	btnLeeren.setFont(new Font("Arial", Font.PLAIN, 12));
	btnLeeren.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (e.getModifiers() == ActionEvent.MOUSE_EVENT_MASK)
		    emptyTextArea();
	    }
	});
	btnLeeren.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		    emptyTextArea();
	    }
	});
	btnLeeren.setBounds(203, 146, 119, 23);
	contentPane.add(btnLeeren);

	// button Verbinden
	btnVerbinden = new JButton("Verbindungstest");
	btnVerbinden.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		    connectionTest();
	    }
	});
	btnVerbinden.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (e.getModifiers() == ActionEvent.MOUSE_EVENT_MASK)
		    connectionTest();
	    }
	});
	btnVerbinden.setFont(new Font("Arial", Font.PLAIN, 11));
	btnVerbinden.setBackground(SystemColor.controlShadow);
	btnVerbinden.setBounds(203, 112, 119, 23);
	contentPane.add(btnVerbinden);

	// button Setzen
	btnSetzen = new JButton("Setzen");
	btnSetzen.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		    set();
	    }
	});
	btnSetzen.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (e.getModifiers() == ActionEvent.MOUSE_EVENT_MASK)
		    set();
	    }
	});
	btnSetzen.setFont(new Font("Arial", Font.PLAIN, 12));
	btnSetzen.setBackground(SystemColor.controlShadow);
	btnSetzen.setBounds(107, 295, 119, 23);
	contentPane.add(btnSetzen);

	// button Ablauf
	btnAblauf = new JButton("Ablauf");
	btnAblauf.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		    ablauf();
	    }
	});
	btnAblauf.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (e.getModifiers() == ActionEvent.MOUSE_EVENT_MASK)
		    ablauf();
	    }
	});
	btnAblauf.setFont(new Font("Arial", Font.PLAIN, 12));
	btnAblauf.setBackground(SystemColor.controlShadow);
	btnAblauf.setBounds(66, 180, 119, 23);
	contentPane.add(btnAblauf);

	btnBefehl = new JButton("Befehl");
	btnBefehl.setEnabled(false);
	btnBefehl.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		    befehl();
	    }
	});
	btnBefehl.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (e.getModifiers() == ActionEvent.MOUSE_EVENT_MASK)
		    befehl();
	    }
	});
	btnBefehl.setFont(new Font("Arial", Font.PLAIN, 12));
	btnBefehl.setBackground(SystemColor.controlShadow);
	btnBefehl.setBounds(66, 214, 119, 23);
	contentPane.add(btnBefehl);

	btnAuslesen = new JButton("Auslesen");
	btnAuslesen.setBackground(SystemColor.controlShadow);
	btnAuslesen.setFont(new Font("Arial", Font.PLAIN, 12));
	btnAuslesen.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		    auslesen();
	    }
	});
	btnAuslesen.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (e.getModifiers() == ActionEvent.MOUSE_EVENT_MASK)
		    auslesen();
	    }
	});
	btnAuslesen.setBounds(107, 268, 119, 23);
	contentPane.add(btnAuslesen);

	// radioButton Statusausgabe (dis-/enabels textArea)
	rdbtnStatusausgaben = new JRadioButton("Statusausgaben");
	rdbtnStatusausgaben.setBackground(SystemColor.controlShadow);
	rdbtnStatusausgaben.setFont(new Font("Arial", Font.PLAIN, 12));
	rdbtnStatusausgaben.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		    rbS(0);
	    }
	});
	rdbtnStatusausgaben.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (e.getModifiers() == ActionEvent.MOUSE_EVENT_MASK)
		    rbS(1);
	    }
	});
	rdbtnStatusausgaben.setBounds(202, 12, 124, 23);
	rdbtnStatusausgaben.setSelected(true);
	contentPane.add(rdbtnStatusausgaben);

	// radioButton Fehlermeldung (dis-/enabels errormessages via JOptionPane)
	rdbtnFehlermeldungen = new JRadioButton("Fehlermeldungen");
	rdbtnFehlermeldungen.setBackground(SystemColor.controlShadow);
	rdbtnFehlermeldungen.setFont(new Font("Arial", Font.PLAIN, 12));
	rdbtnFehlermeldungen.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		    rbF(0);
	    }
	});
	rdbtnFehlermeldungen.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (e.getModifiers() == ActionEvent.MOUSE_EVENT_MASK)
		    rbF(1);
	    }
	});
	rdbtnFehlermeldungen.setBounds(202, 42, 124, 23);
	contentPane.add(rdbtnFehlermeldungen);

	// radioButton Autokorrektur (dis-/enabels autocorrection)
	rdbtnAutokorrektur = new JRadioButton("Autokorrektur ");
	rdbtnAutokorrektur.setBackground(SystemColor.controlShadow);
	rdbtnAutokorrektur.setFont(new Font("Arial", Font.PLAIN, 12));
	rdbtnAutokorrektur.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		    rbA(0);
	    }
	});
	rdbtnAutokorrektur.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (e.getModifiers() == ActionEvent.MOUSE_EVENT_MASK)
		    rbA(1);
	    }
	});
	rdbtnAutokorrektur.setBounds(202, 73, 124, 23);
	contentPane.add(rdbtnAutokorrektur);

	rbTelemetrie = new JRadioButton("Telemetrie ");
	rbTelemetrie.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		    rbT(0);
	    }
	});
	rbTelemetrie.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (e.getModifiers() == ActionEvent.MOUSE_EVENT_MASK)
		    rbT(1);
	    }
	});
	rbTelemetrie.setSelected(true);
	rbTelemetrie.setFont(new Font("Arial", Font.PLAIN, 12));
	rbTelemetrie.setBackground(SystemColor.controlShadow);
	rbTelemetrie.setBounds(202, 214, 124, 23);
	contentPane.add(rbTelemetrie);

	btnZeigeAblauf = new JButton("zeige Ablauf");
	btnZeigeAblauf.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
		    getPunktListe();
		    for (Punkt p : liste) {
			textArea.append(p.getX() + " " + p.getY() + " " + p.getZ() + "\n");
		    }
		}
	    }
	});
	btnZeigeAblauf.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (e.getModifiers() == ActionEvent.MOUSE_EVENT_MASK) {
		    getPunktListe();
		    for (Punkt p : liste) {

			textArea.append(p.getX() + " " + p.getY() + " " + p.getZ() + "\n");
		    }
		}
	    }
	});
	btnZeigeAblauf.setFont(new Font("Arial", Font.PLAIN, 12));
	btnZeigeAblauf.setBackground(SystemColor.controlShadow);
	btnZeigeAblauf.setBounds(203, 181, 119, 23);
	contentPane.add(btnZeigeAblauf);

	JButton btnStop = new JButton("Stop");
	btnStop.addActionListener(new ActionListener() {
	    @SuppressWarnings("deprecation")
	    public void actionPerformed(ActionEvent e) {
		t.stop();

		stop = true; // TODO: mark
		t.interrupt();
		try {
		    t.join(10);
		} catch (InterruptedException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}
		t.stop();

		if (!t.isInterrupted())
		    System.exit(0);
	    }
	});
	btnStop.setFont(new Font("Arial", Font.PLAIN, 12));
	btnStop.setBackground(SystemColor.controlShadow);
	btnStop.setBounds(236, 266, 79, 84);
	contentPane.add(btnStop);
	setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[] { tfX, tfY, tfZ, btnSimulieren, btnAusfuehren,
		btnAblauf, btnVerbinden, btnLeeren, btnBefehl, tfMID, tfWert, btnAuslesen, btnSetzen, btnClose,
		rdbtnStatusausgaben, rdbtnFehlermeldungen, rdbtnAutokorrektur }));

	tfX.requestFocus();
	tfX.selectAll();

	textArea.append("Initialisiere Programm\n");
	textArea.append(version + "\n" + Robot.version + "\ndevicename: " + this.device + " \n\n");

	// enables telemetrie (default setting)
	Robot.setTelemetrieerfassung(rbTelemetrie.isSelected());

	File verzeichnis = new File("." + File.separator + "TelemetrieException");

	if (!verzeichnis.exists())
	    verzeichnis.mkdir();

	verzeichnis = new File("." + File.separator + "TelemetrieDaten");

	if (!verzeichnis.exists())
	    verzeichnis.mkdir();

	// TODO: dies sollte Betriebssystem unabhaehing werden
	verzeichnis = new File("C:\\Windows\\System32\\dxl_x64_c.dll");
	//
	// if (!verzeichnis.exists()) {
	// JOptionPane.showMessageDialog(null,
	// "Die Bibilothek dxl_x64_c.dll ist nicht vorhanden. Der Roboter"
	// + " ist daher nicht ansteuerbar und einige Funktionen sind deaktiviert, "
	// + "die anderen Tools sollten aber weiterhin funktionieren.\nIm Readme wird"
	// + " beschrieben wie das Problem gelöst werden kann. (Das Readme kann, falls"
	// + " nicht auffindbar, mit dem Programm generiert werden)");

	// btnSimulieren.setEnabled(false);
	// btnAusfuehren.setEnabled(false);
	// btnAblauf.setEnabled(false);
	// btnVerbinden.setEnabled(false);
	// mntmVerbindungstest.setEnabled(false);
	// btnAuslesen.setEnabled(false);
	// btnSetzen.setEnabled(false);
	// rbTelemetrie.setEnabled(false);
	// }

	// startUpProcedure();
    }

    /**
     * Simulates movment to a point
     */
    private void simulate() {
	int x, y, z;// var. for coordinates

	if (rdbtnStatusausgaben.isSelected())
	    textArea.append("**************************\nSimulation wird gestartet\n");

	try {
	    // checks for empty Input
	    if (tfX.getText().equals("")) {
		tfX.requestFocus();
		throw new EmptyInputException("Keine Wert für die X-Koordinate");
	    }

	    if (tfY.getText().equals("")) {
		tfY.requestFocus();
		throw new EmptyInputException("Keine Wert fr die Y-Koordinate");
	    }

	    if (tfZ.getText().equals("")) {
		tfZ.requestFocus();
		throw new EmptyInputException("Keine Wert für die Z-Koordinate");

	    }

	    // checks if autocorrection is enabeld
	    if (rdbtnAutokorrektur.isSelected()) {
		// sets up ControlInput
		ControlInput.setCanBeNegative(true);
		ControlInput.setCanBeAFloat(false);
		ControlInput.setUseExceptions(false);

		// creats ControlInput obj.
		ControlInput ctrx = ControlInput.inspect(tfX.getText());
		ControlInput ctry = ControlInput.inspect(tfY.getText());
		ControlInput ctrz = ControlInput.inspect(tfZ.getText());

		boolean usabel = true;// var. for controlling autocorrect (might be redundant)

		// if not usabeld/not correctabel
		if (!ctrz.isUsabel) {
		    usabel = false;
		    tfZ.requestFocus();
		    tfZ.selectAll();
		}

		if (!ctry.isUsabel) {
		    usabel = false;
		    tfY.requestFocus();
		    tfY.selectAll();
		}

		if (!ctrx.isUsabel) {
		    usabel = false;
		    tfX.requestFocus();
		    tfX.selectAll();
		}

		// if corret but corrected
		if (ctrz.wasCorrected) {
		    tfZ.setText(ctrz.getSuggestedInput());
		}

		if (ctry.wasCorrected) {
		    tfY.setText(ctry.getSuggestedInput());
		}

		if (ctrx.wasCorrected) {
		    tfX.setText(ctrx.getSuggestedInput());
		}

		// some outputs in textArea
		if (rdbtnStatusausgaben.isSelected()) {
		    if (ctrx.wasCorrected) {
			textArea.append("X-Koordinate wurde Korrigiert:\n");
			textArea.append(ctrx.output() + "\n");
		    }

		    if (ctry.wasCorrected) {
			textArea.append("Y-Koordinate wurde Korrigiert:\n");
			textArea.append(ctry.output() + "\n");
		    }

		    if (ctrz.wasCorrected) {
			textArea.append("Z-Koordinate wurde Korrigiert:\n");
			textArea.append(ctrz.output() + "\n");
		    }

		    if (ctrx.isUsabel)
			textArea.append("X-Koordinate ist nutzbar\n");
		    else {
			textArea.append("X-Koordinate ist nicht nutzbar\n");
		    }

		    if (ctry.isUsabel)
			textArea.append("Y-Koordinate ist nutzbar\n");
		    else {
			textArea.append("Y-Koordinate ist nicht nutzbar\n");
		    }

		    if (ctrz.isUsabel)
			textArea.append("Z-Koordinate ist nutzbar\n\n");
		    else {
			textArea.append("Z-Koordinate ist nicht nutzbar\n\n");
		    }
		}

		// some error output via JOptionPane if necessery
		if (rdbtnFehlermeldungen.isSelected()) {
		    if (!ctrx.isUsabel)
			JOptionPane.showMessageDialog(null, "X-Koordinate ist nicht nutzbar");

		    if (!ctry.isUsabel)
			JOptionPane.showMessageDialog(null, "Y-Koordinate ist nicht nutzbar");

		    if (!ctrz.isUsabel)
			JOptionPane.showMessageDialog(null, "Z-Koordinate ist nicht nutzbar");
		}

		// if usabel: reading the values
		if (usabel) {
		    x = Integer.parseInt(ctrx.getSuggestedInput());
		    y = Integer.parseInt(ctry.getSuggestedInput());
		    z = Integer.parseInt(ctrz.getSuggestedInput());
		} else // if not usabel: throws ControlInputException
		    throw new ControlInputException("Keine Autokorrektur möglich");
	    } else {// reading the values without autocorrection
		x = Integer.parseInt(tfX.getText());
		y = Integer.parseInt(tfY.getText());
		z = Integer.parseInt(tfZ.getText());
	    }

	    Punkt p = new Punkt(x, y, z);// creat usabel point for the robot

	    Robot simRobot = Robot.sim(p);

	    // checks if the point is verifiable
	    if (!Robot.ansteuerbarkeit(p))
		throw new RoboterException("Der Punkt ist nicht ansteuerbar", simRobot);
	    else {
		if (rdbtnStatusausgaben.isSelected())
		    textArea.append("Der Punkt ist Ansteuerbar\n\n");
	    }

	    if (rdbtnStatusausgaben.isSelected())
		textArea.append("Bewegung zum Punkt P(" + x + "|" + y + "|" + z + ") simulieren\n\n");

	    if (rdbtnStatusausgaben.isSelected()) {
		try {
		    textArea.append(simRobot.moveStr + "\n");

		} catch (Exception e) {
		    e.printStackTrace();

		    if (rdbtnStatusausgaben.isSelected())
			textArea.append(e.getMessage() + "\n\n");

		    if (rdbtnFehlermeldungen.isSelected())
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	    }

	    simRobot = null;
	    tfX.requestFocus();
	    tfX.selectAll();
	}
	// error handling
	catch (EmptyInputException e) {
	    e.printStackTrace();
	    if (rdbtnStatusausgaben.isSelected())
		textArea.append(e.getMessage() + "\n\n");

	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, e.getMessage());
	} catch (ControlInputException e) {
	    e.printStackTrace();
	    if (rdbtnStatusausgaben.isSelected())
		textArea.append(e.getMessage() + "\n\n");

	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, e.getMessage());
	} catch (RoboterException e) {
	    e.printStackTrace();

	    roboterExceptionListe.add(e);

	    if (rdbtnStatusausgaben.isSelected())
		textArea.append(e.getMessage() + "\n\n");

	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, e.getMessage());

	} catch (NumberFormatException e) {
	    e.printStackTrace();
	    if (rdbtnStatusausgaben.isSelected())
		textArea.append("Bitte überprüfen Sie die Eingegeben Koordinatenwerte\n\n");

	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, "Bitte überprüfen Sie die Eingegeben Koordinatenwerte");

	} catch (Exception e) {
	    e.printStackTrace();
	    if (rdbtnStatusausgaben.isSelected())
		textArea.append("Unbekannter Fehler aufgetreten\n\n");

	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, "Unbekannter Fehler aufgetreten");

	    e.printStackTrace();
	} finally {
	    textArea.append("Simulation beendet\n**************************\n");
	}
    }

    /**
     * Performs movement to a point
     */
    private void perform() {
	int x, y, z;// var. for coordinates
	boolean statusausgabe = rdbtnStatusausgaben.isSelected();
	boolean fehlermeldung = rdbtnFehlermeldungen.isSelected();
	boolean autokorrektur = rdbtnAutokorrektur.isSelected();

	if (statusausgabe)
	    textArea.append("**************************\nAusführen beginnt\n");

	try {
	    // checks for empty Input
	    if (tfX.getText().equals("")) {
		tfX.requestFocus();
		throw new EmptyInputException("Keine Wert für die X-Koordinate");
	    }

	    if (tfY.getText().equals("")) {
		tfY.requestFocus();
		throw new EmptyInputException("Keine Wert fr die Y-Koordinate");
	    }

	    if (tfZ.getText().equals("")) {
		tfZ.requestFocus();
		throw new EmptyInputException("Keine Wert für die Z-Koordinate");
	    }

	    // checks if autocorrection is enabeld
	    if (autokorrektur) {
		// sets up ControlInput
		ControlInput.setCanBeNegative(true);
		ControlInput.setCanBeAFloat(false);
		ControlInput.setUseExceptions(false);

		// creats ControlInput obj.
		ControlInput ctrx = ControlInput.inspect(tfX.getText());
		ControlInput ctry = ControlInput.inspect(tfY.getText());
		ControlInput ctrz = ControlInput.inspect(tfZ.getText());

		boolean usabel = true;// var. for controlling autocorrect (might be redundant)

		// if not usabeld/not correctabel
		if (!ctrz.isUsabel) {
		    usabel = false;
		    tfZ.requestFocus();
		    tfZ.selectAll();
		}

		if (!ctry.isUsabel) {
		    usabel = false;
		    tfY.requestFocus();
		    tfY.selectAll();
		}

		if (!ctrx.isUsabel) {
		    usabel = false;
		    tfX.requestFocus();
		    tfX.selectAll();
		}

		// if corret but corrected
		if (ctrz.wasCorrected) {
		    tfZ.setText(ctrz.getSuggestedInput());
		}

		if (ctry.wasCorrected) {
		    tfY.setText(ctry.getSuggestedInput());
		}

		if (ctrx.wasCorrected) {
		    tfX.setText(ctrx.getSuggestedInput());
		}

		// some outputs in textArea
		if (statusausgabe) {
		    if (ctrx.wasCorrected) {
			textArea.append("X-Koordinate wurde Korrigiert:\n");
			textArea.append(ctrx.output() + "\n");
		    }

		    if (ctry.wasCorrected) {
			textArea.append("Y-Koordinate wurde Korrigiert:\n");
			textArea.append(ctry.output() + "\n");
		    }

		    if (ctrz.wasCorrected) {
			textArea.append("Z-Koordinate wurde Korrigiert:\n");
			textArea.append(ctrz.output() + "\n");
		    }

		    if (ctrx.isUsabel)
			textArea.append("X-Koordinate ist nutzbar\n");
		    else {
			textArea.append("X-Koordinate ist nicht nutzbar\n");
		    }

		    if (ctry.isUsabel)
			textArea.append("Y-Koordinate ist nutzbar\n");
		    else {
			textArea.append("Y-Koordinate ist nicht nutzbar\n");
		    }

		    if (ctrz.isUsabel)
			textArea.append("Z-Koordinate ist nutzbar\n\n");
		    else {
			textArea.append("Z-Koordinate ist nicht nutzbar\n\n");
		    }
		}

		// some error output via JOptionPane if necessery
		if (fehlermeldung) {
		    if (!ctrx.isUsabel)
			JOptionPane.showMessageDialog(null, "X-Koordinate ist nicht nutzbar");

		    if (!ctry.isUsabel)
			JOptionPane.showMessageDialog(null, "Y-Koordinate ist nicht nutzbar");

		    if (!ctrz.isUsabel)
			JOptionPane.showMessageDialog(null, "Z-Koordinate ist nicht nutzbar");

		}

		// if usabel reading the values
		if (usabel) {
		    x = Integer.parseInt(ctrx.getSuggestedInput());
		    y = Integer.parseInt(ctry.getSuggestedInput());
		    z = Integer.parseInt(ctrz.getSuggestedInput());
		} else // if not usabel: throws ControlInputException
		    throw new ControlInputException("Keine Autokorrektur möglich");
	    } else {// reading the values without autocorrection
		x = Integer.parseInt(tfX.getText());
		y = Integer.parseInt(tfY.getText());
		z = Integer.parseInt(tfZ.getText());
	    }

	    p = new Punkt(x, y, z);// creat usabel point for the robot

	    // checks if the point is verifiable (currently robot.ansteuerbarkeit is more a
	    // placeholder than anything else: throws RoboterException if not verifiable
	    if (!Robot.ansteuerbarkeit(p))
		throw new RoboterException("Der Punkt ist nicht ansteuerbar", myRobot);

	    if (statusausgabe)
		textArea.append("\nBewegung zum Punkt P(" + x + "|" + y + "|" + z + ")\n\n");

	    tfX.requestFocus();
	    tfX.selectAll();

	    if (!t.isAlive())
		t.start();

	    isRunningP = true;

	}
	// error handling
	catch (EmptyInputException e) {
	    e.printStackTrace();
	    if (statusausgabe)
		textArea.append(e.getMessage() + "\n\n");

	    if (fehlermeldung)
		JOptionPane.showMessageDialog(null, e.getMessage());
	} catch (ControlInputException e) {
	    e.printStackTrace();
	    if (statusausgabe)
		textArea.append(e.getMessage() + "\n\n");

	    if (fehlermeldung)
		JOptionPane.showMessageDialog(null, e.getMessage());
	} catch (RoboterException e) {
	    e.printStackTrace();

	    roboterExceptionListe.add(e);
	    if (statusausgabe)
		textArea.append(e.getMessage() + "\n\n");

	    if (fehlermeldung)
		JOptionPane.showMessageDialog(null, e.getMessage());
	} catch (NumberFormatException e) {
	    e.printStackTrace();
	    if (statusausgabe)
		textArea.append("Bitte überprüfen Sie die Eingegeben Koordinatenwerte\n\n");

	    if (fehlermeldung)
		JOptionPane.showMessageDialog(null, "Bitte überprüfen Sie die Eingegeben Koordinatenwerte");

	} catch (Exception e) {
	    e.printStackTrace();
	    if (statusausgabe)
		textArea.append("Unbekannter Fehler aufgetreten\n\n");

	    if (fehlermeldung)
		JOptionPane.showMessageDialog(null, "Unbekannter Fehler aufgetreten");

	    e.printStackTrace();
	}
    }

    /**
     * Performs movements to a list point
     */

    private void ablauf() {
	if (!t.isAlive())
	    t.start();

	getPunktListe();

	textArea.append("**************************\nAblauf beginnt\n");

	isRunningA = true;
    }

    /**
     * takes orders from the orders and executes them
     */
    private void befehl() {
	boolean statusausgaben = rdbtnStatusausgaben.isSelected();
	textArea.append("**************************\nBefehlsoperation beginnt\n");

	int befehlsId = Integer.parseInt(JOptionPane.showInputDialog("Befehlsnummer eingeben."));

	int port_num = 0;
	// int port_num = myRobot.getDynamixel().portHandler(device);

	switch (befehlsId) {
	case (0):
	    if (statusausgaben)
		textArea.append("Reboot der Servos");

	    connect();

	    try {
		for (byte i = 0; i < 3; i++)
		    myRobot.getDynamixel().reboot(port_num, 1, i);
	    } catch (Exception e) {
		if (rdbtnStatusausgaben.isSelected())
		    textArea.append(e.getMessage() + "\n\n");

		if (rdbtnFehlermeldungen.isSelected())
		    JOptionPane.showMessageDialog(null, e.getMessage());

		System.out.println("Fehler während des rebootens der Servos");
	    }

	    break;

	case (1):

	    break;

	case (2):

	    break;

	case (3):

	    break;

	case (4):

	    break;

	default:
	}

	textArea.append("Befehlsoperation beendet\n**************************\n");
    }

    /**
     * starts a connectiontest (connects and disconnects)
     */
    private void connectionTest() {
	textArea.append("**************************\nVerbindungstest gestartet\n");
	if (connect()) {
	    for (int i = 0; i < 6; i++) {
		textArea.append(
			"Motor " + (byte) i + " hat eine Spannung von " + myRobot.getVoltage((byte) i) + "  mV\n");
	    }
	    textArea.append("Betriebsspannung:  9  ~ 12V (Empfohlen 11.1V)\n");

	    for (int i = 0; i < 6; i++) {
		textArea.append("Motor " + (byte) i + " hat eine Temperatur von " + myRobot.getTemperature((byte) i)
			+ "  °C\n");
	    }
	    textArea.append("Betriebstemperatur: -5°C~ +70°C\n");

	    boolean problem = false;
	    for (int i = 0; i < 3; i++) {
		textArea.append(
			"Motor " + (byte) i + " hat eine Geschwindigkeit von " + myRobot.getSpeed((byte) i) + "\n");

		if (myRobot.getSpeed((byte) i) == 0 || myRobot.getSpeed((byte) i) > 80)
		    problem = true;

	    }
	    textArea.append("\n");

	    if (problem == true) {
		int dialogButton = JOptionPane.YES_NO_OPTION;

		int dialogResult = JOptionPane.showConfirmDialog(null,
			"Es scheint ein Probelm bei den Geschwindigkeiten zu geben! Beheben?", "Warnung!",
			dialogButton);

		if (dialogResult == JOptionPane.YES_NO_OPTION) {
		    final short movingSpeedM1 = 80;
		    final short movingSpeedM2 = 40;
		    final short movingSpeedM3 = movingSpeedM2;

		    textArea.append("Problem behoben\n");
		    myRobot.setSpeed((byte) 0, movingSpeedM1);
		    myRobot.setSpeed((byte) 1, movingSpeedM2);
		    myRobot.setSpeed((byte) 2, movingSpeedM3);
		    myRobot.setSpeed((byte) 3, movingSpeedM3);
		    myRobot.setSpeed((byte) 4, movingSpeedM3);
		    myRobot.setSpeed((byte) 5, movingSpeedM3);
		}

	    }

	    for (int i = 0; i < 3; i++) {
		textArea.append("Motor " + (byte) i + " steht auf " + myRobot.getPosition((byte) i) + " Einheiten ("
			+ Robot.uniToGra(myRobot.getPosition((byte) i)) + "°)\n");
	    }
	    textArea.append("\n");

	    if (rdbtnStatusausgaben.isSelected())
		textArea.append("Es konnte eine Verbindung zum Roboter hergestellt werden!\n\n");

	    disconnect();

	}

	textArea.append("Verbindungstest beendet\n**************************\n");
    }

    /**
     * emptys Textarea
     */
    private void emptyTextArea() {
	textArea.setText("");
	tfX.setText("");
	tfY.setText("");
	tfZ.setText("");
    }

    /**
     * sets one servos position to a bypass value
     */
    private void set() {
	byte id;
	short goal;

	if (rdbtnStatusausgaben.isSelected())
	    textArea.append("Manuelles setzen wird gestartet\n\n");

	try {
	    // checks for empty Input
	    if (tfMID.getText().equals("")) {
		tfX.requestFocus();
		throw new EmptyInputException("Keine Wert für die Motor ID");
	    }

	    if (tfWert.getText().equals("")) {
		tfY.requestFocus();
		throw new EmptyInputException("Keine Wert für den Wert");
	    }

	    // checks if autocorrection is enabeld
	    if (rdbtnAutokorrektur.isSelected()) {
		// sets up ControlInput
		ControlInput.setCanBeNegative(false);
		ControlInput.setCanBeAFloat(false);
		ControlInput.setUseExceptions(false);

		boolean usabel = true;

		ControlInput ctrId = ControlInput.inspect(tfMID.getText());

		if (!ctrId.isUsabel) {
		    usabel = false;
		    tfX.requestFocus();
		    tfX.selectAll();
		}

		// if corret but corrected
		if (ctrId.wasCorrected)
		    tfZ.setText(ctrId.getSuggestedInput());

		if (rdbtnStatusausgaben.isSelected()) {
		    if (ctrId.wasCorrected) {
			textArea.append("Motor ID wurde Korrigiert:\n");
			textArea.append(ctrId.output() + "\n");
		    }

		    if (ctrId.isUsabel)
			textArea.append("Motor ID ist nutzbar\n");
		    else
			textArea.append("Motor ID ist nicht nutzbar\n");
		}

		if (rdbtnFehlermeldungen.isSelected()) {
		    if (!ctrId.isUsabel)
			JOptionPane.showMessageDialog(null, "Motor ID ist nicht nutzbar");
		}

		ControlInput.setCanBeNegative(true);
		ControlInput ctrVal = ControlInput.inspect(tfWert.getText());
		usabel = true;

		if (!ctrVal.isUsabel) {
		    usabel = false;
		    tfX.requestFocus();
		    tfX.selectAll();
		}

		// if corret but corrected
		if (ctrVal.wasCorrected)
		    tfZ.setText(ctrVal.getSuggestedInput());

		if (rdbtnStatusausgaben.isSelected()) {
		    if (ctrVal.wasCorrected) {
			textArea.append("Der Wert wurde Korrigiert:\n");
			textArea.append(ctrVal.output() + "\n");
		    }

		    if (ctrVal.isUsabel)
			textArea.append("Der Wert ist nutzbar\n");
		    else
			textArea.append("Der Wert ist nicht nutzbar\n");
		}

		if (rdbtnFehlermeldungen.isSelected()) {
		    if (!ctrId.isUsabel)
			JOptionPane.showMessageDialog(null, "Der Wert ist nicht nutzbar");
		}

		if (usabel) {
		    id = Byte.parseByte(ctrId.getSuggestedInput());
		    goal = Short.parseShort(ctrVal.getSuggestedInput());
		} else // if not usabel: throws ControlInputException
		    throw new ControlInputException("Keine Autokorrektur möglich");
	    } else {
		id = Byte.parseByte(tfMID.getText());
		goal = Short.parseShort(tfWert.getText());
	    }

	    if (id >= 0 && id <= 3 && goal >= 0 && goal <= 1023) {
		connect();
		myRobot.setPosition(id, goal);
		disconnect();

		if (rdbtnStatusausgaben.isSelected())
		    textArea.append("Motor " + id + " erfolgreich auf " + goal + " (" + Robot.graToUni(goal)
			    + "°) gesetzt\n\n");
	    } else if (goal <= 10 || goal >= 900) {
		if (rdbtnStatusausgaben.isSelected())
		    textArea.append("Der Wert " + goal + " ist keine sichere Zielposition\n");

		if (rdbtnFehlermeldungen.isSelected())
		    JOptionPane.showMessageDialog(null, "Der Wert " + goal + " ist keine sichere Zielposition\n");
	    } else {
		if (rdbtnStatusausgaben.isSelected())
		    textArea.append("Es existier kein Motor mit der ID: " + id + "\n");

		if (rdbtnFehlermeldungen.isSelected())
		    JOptionPane.showMessageDialog(null, "Es existier kein Motor mit der ID: " + id + "\n");
	    }

	} catch (EmptyInputException e) {
	    e.printStackTrace();
	    if (rdbtnStatusausgaben.isSelected())
		textArea.append("Es sind keine Werte eingegeben\n\n");

	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, "Es sind keine Werte eingegeben");
	} catch (ControlInputException e) {
	    e.printStackTrace();
	    if (rdbtnStatusausgaben.isSelected())
		textArea.append("Keine Autokorrektur möglich\n\n");

	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, "Keine Autokorrektur möglich");
	} catch (NullPointerException e) {
	    e.printStackTrace();
	    // Nichts tuhen da dieser Fehler schon früher behandelt wird
	} catch (Exception e) {
	    e.printStackTrace();
	    if (rdbtnStatusausgaben.isSelected())
		textArea.append("Unbekannter Fehler bei der Eingabe\n\n");

	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, "Unbekannter Fehler bei der Eingabe");
	}
    }

    /**
     * reads one servos position
     */
    private void auslesen() {
	byte id;

	if (rdbtnStatusausgaben.isSelected())
	    textArea.append("**************************\nAuslesen wird gestartet\n");

	try {
	    // checks for empty Input
	    if (tfMID.getText().equals("")) {
		tfX.requestFocus();
		throw new EmptyInputException("Keine Wert für die Motor ID");
	    }

	    // checks if autocorrection is enabeld
	    if (rdbtnAutokorrektur.isSelected()) {
		// sets up ControlInput
		ControlInput.setCanBeNegative(false);
		ControlInput.setCanBeAFloat(false);
		ControlInput.setUseExceptions(false);

		boolean usabel = true;

		ControlInput ctrId = ControlInput.inspect(tfMID.getText());

		if (!ctrId.isUsabel) {
		    usabel = false;
		    tfX.requestFocus();
		    tfX.selectAll();
		}

		// if corret but corrected
		if (ctrId.wasCorrected)
		    tfZ.setText(ctrId.getSuggestedInput());

		if (rdbtnStatusausgaben.isSelected()) {
		    if (ctrId.wasCorrected) {
			textArea.append("Motor ID wurde Korrigiert:\n");
			textArea.append(ctrId.output() + "\n");
		    }

		    if (ctrId.isUsabel)
			textArea.append("Motor ID ist nutzbar\n");
		    else
			textArea.append("Motor ID ist nicht nutzbar\n");
		}

		if (rdbtnFehlermeldungen.isSelected()) {
		    if (!ctrId.isUsabel)
			JOptionPane.showMessageDialog(null, "Motor ID ist nicht nutzbar");
		}

		if (usabel) {
		    id = Byte.parseByte(ctrId.getSuggestedInput());
		} else // if not usabel: throws ControlInputException
		    throw new ControlInputException("Keine Autokorrektur möglich");
	    } else {
		id = Byte.parseByte(tfMID.getText());
	    }

	    if (id >= 0 && id <= 3) {
		connect();
		short pos = myRobot.getPosition(id);
		disconnect();

		if (rdbtnStatusausgaben.isSelected())
		    textArea.append(
			    "Motor " + id + " steht auf " + pos + " (" + Robot.graToUni(pos) + "°) gesetzt\n\n");
	    } else {
		if (rdbtnStatusausgaben.isSelected())
		    textArea.append("Es existier kein Motor mit der ID: " + id + "\n");

		if (rdbtnFehlermeldungen.isSelected())
		    JOptionPane.showMessageDialog(null, "Es existier kein Motor mit der ID: " + id + "\n");
	    }

	} catch (EmptyInputException e) {
	    e.printStackTrace();
	    if (rdbtnStatusausgaben.isSelected())
		textArea.append("Es ist keine Motor eingegeben\n");

	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, "Es ist keine Motor eingegeben");
	} catch (ControlInputException e) {
	    e.printStackTrace();
	    if (rdbtnStatusausgaben.isSelected())
		textArea.append("Keine Autokorrektur möglich\n");

	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, "Keine Autokorrektur möglich");
	} catch (NullPointerException e) {
	    e.printStackTrace();
	    // Nichts tuhen da dieser Fehler schon früher behandelt wird
	} catch (Exception e) {
	    e.printStackTrace();
	    if (rdbtnStatusausgaben.isSelected())
		textArea.append("Unbekannter Fehler bei der Eingabe\n");

	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, "Unbekannter Fehler bei der Eingabe");
	}
	textArea.append("Auslesen beendet\n**************************\n");

    }

    /**
     * closes frame
     */
    private void close() {
	textArea.append("Beende Programm\n");
	stop = true;
	System.gc();
	this.disconnect();
	this.setVisible(false);
	System.exit(0);
    }

    /*
     * within functions
     */

    /**
     * connects with robot
     */
    public boolean connect() {
	try {
	    myRobot = new Robot(device);
	    return true;
	} catch (RoboterException e) {

	    roboterExceptionListe.add(e);
	    e.printStackTrace();

	    if (rdbtnStatusausgaben.isSelected())
		textArea.append("Es konnte keine Verbindung zum Roboter hergestellt werden\n");

	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, "Es konnte keine Verbindung zum Roboter hergestellt werden\n");

	    return false;
	} catch (Exception e) {
	    if (rdbtnStatusausgaben.isSelected())
		textArea.append("Unbekannter fehler beim connecten aufgetreten\n");

	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, "Unbekannter fehler beim connecten aufgetreten\\n");

	    return false;
	}
    }

    /**
     * disconnects from robot
     */
    public void disconnect() {
	try {
	    myRobot.manualDisconnect();
	    myRobot = null;
	} catch (RoboterException e) {

	    roboterExceptionListe.add(e);
	    e.printStackTrace();

	    if (rdbtnStatusausgaben.isSelected())
		textArea.append("Es konnte keine Verbindung zum Roboter hergestellt werden!\n\n");

	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, "Es konnte keine Verbindung zum Roboter hergestellt werden\n");
	} catch (NullPointerException e) {

	} catch (Exception e) {
	    e.printStackTrace();

	    if (rdbtnStatusausgaben.isSelected())
		textArea.append("Unbekannter Fehler beim Disconnecten aufgetreten!\n");

	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, "Unbekannter Fehler beim Disconnecten aufgetreten!\n");
	}
    }

    /**
     * controlls radiobutton Statusausgabe
     */
    private void rbS(int typ) {
	if (typ == 0)
	    rdbtnStatusausgaben.setSelected(!rdbtnStatusausgaben.isSelected());

	if (rdbtnStatusausgaben.isSelected()) {
	    textArea.setVisible(true);
	    scrollPane.setVisible(true);
	} else {
	    textArea.setVisible(false);
	    scrollPane.setVisible(false);
	}

	textArea.append("Statusausgabe auf " + textArea.isVisible() + " gesetzt\n\n");
    }

    /**
     * controlls radiobutton Fehlermeldung
     */
    private void rbF(int typ) {
	if (typ == 0)
	    rdbtnFehlermeldungen.setSelected(!rdbtnFehlermeldungen.isSelected());
	textArea.append("Fehlerausgabe auf " + rdbtnFehlermeldungen.isSelected() + " gesetzt\n\n");
    }

    /**
     * controlls radiobutton Autokorrektur
     */
    private void rbA(int typ) {
	if (typ == 0)
	    rdbtnAutokorrektur.setSelected(!rdbtnAutokorrektur.isSelected());
	textArea.append("Autokorrektur auf " + rdbtnAutokorrektur.isSelected() + " gesetzt\n\n");
    }

    /**
     * controlls radiobutton Telemetrie
     */
    private void rbT(int typ) {
	if (typ == 0)
	    rbTelemetrie.setSelected(!rbTelemetrie.isSelected());
	textArea.append("Telemetrie Erfassung auf " + rbTelemetrie.isSelected() + " gesetzt\n\n");

	Robot.setTelemetrieerfassung(rbTelemetrie.isSelected());

	// if (!rbTelemetrie.isSelected())
	// aktuelleTelemetrie = null;
    }

    private void generateReadme() {
	try {

	    String s = "Hey User,\r\n" + "\r\n" + "please read this to make sure everything works.\r\n" + "\r\n"
		    + "Make sure you have the dxl_x64_c.dll library in your System32 folder or what ever the equivalent linux or mac folder is.\r\n"
		    + "The libraries can be found hear:\r\n" + "\r\n"
		    + "https://drive.google.com/drive/folders/16lcrK7qOWh4ED8cf3U_4tEO2zktoKygQ?usp=sharing\r\n"
		    + "\r\n" + "Or look for it at the official GitHub page frome dynamixel:\r\n" + "\r\n"
		    + "https://github.com/ROBOTIS-GIT/DynamixelSDK\r\n" + "\r\n"
		    + "If you need to change the devicename (on Windows this should be something like COM1, COM2, ..., COM5) start the program\r\n"
		    + "from the command line with the argument you need for the devicename. You can find the devicename under your device manager.\r\n"
		    + "If the devicename is \"COM3\" you dont need to change it because this is the default devicename.\r\n"
		    + "It should look something like this:\r\n" + "\r\n" + "C:\\Your\\current\\path>Robot.jar COM2\r\n"
		    + "\r\n"
		    + "Also make sure you got the latest Java-version installed because this is a Java program! \r\n"
		    + "\r\n" + "Cheers!";

	    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("Readme.txt", true)));
	    out.println(s);
	    out.close();
	} catch (IOException e) {
	    // exception handling left as an exercise for the reader
	}
    }

    private void getPunktListe() {
	String dateiname = "." + File.separator + "punktListe.pkt";

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
		DefaultListModel<String> punktListe = new DefaultListModel<String>();
		liste = new ArrayList<Punkt>();
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

		    liste.add(new Punkt(x, y, z));

		}
	    } catch (IOException ex) {
		ex.printStackTrace();
		System.out.println("Error while getting Pointlist");
		JOptionPane.showMessageDialog(null, "Warnung! Fehler beim auslesen der Punkte für den Ablauf");
	    }
	}
    }

    @Override
    public void run() {
	int counter = 0;
	while (!stop) {
	    System.out.println("is running");
	    counter++;
	    if (isRunningP) {
		counter = 0;
		btnAusfuehren.setEnabled(false);
		btnAblauf.setEnabled(false);
		btnVerbinden.setEnabled(false);
		mntmVerbindungstest.setEnabled(false);
		btnSetzen.setEnabled(false);
		btnAuslesen.setEnabled(false);
		moveP();
		btnAusfuehren.setEnabled(true);
		btnAblauf.setEnabled(true);
		btnVerbinden.setEnabled(true);
		mntmVerbindungstest.setEnabled(true);
		btnSetzen.setEnabled(true);
		btnAuslesen.setEnabled(true);
	    } else if (isRunningA) {
		counter = 0;
		btnAusfuehren.setEnabled(false);
		btnAblauf.setEnabled(false);
		btnVerbinden.setEnabled(false);
		mntmVerbindungstest.setEnabled(false);
		btnSetzen.setEnabled(false);
		btnAuslesen.setEnabled(false);
		moveA();
		btnAusfuehren.setEnabled(true);
		btnAblauf.setEnabled(true);
		btnVerbinden.setEnabled(true);
		mntmVerbindungstest.setEnabled(true);
		btnSetzen.setEnabled(true);
		btnAuslesen.setEnabled(true);
	    }

	    if (counter > 15) {
		Thread.currentThread().interrupt();
		counter = 0;
	    }
	}
	btnAusfuehren.setEnabled(true);
	btnAblauf.setEnabled(true);
	btnVerbinden.setEnabled(true);
	mntmVerbindungstest.setEnabled(true);
    }

    public void moveP() {
	try {
	    connect();
	    myRobot.moveto(p);
	} catch (RoboterException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	// Duration dur = Duration.between(begin, Instant.now());

	textArea.append(myRobot.moveStr + "\n\n");

	aktuelleTelemetrie = myRobot.getTelemetrie();

	disconnect();

	textArea.append("Ausführen beendet\n**************************\n");

	isRunningP = false;
    }

    public void moveA() {
	final long delay = 200; // delay between 2 operaton in ms
	if (connect()) {
	    Instant begin = Instant.now();

	    for (int i = 0; i < liste.size(); i++) {
		try {
		    System.out.println("i:" + i);

		    myRobot.moveto(liste.get(i));

		    System.out.println(myRobot.getTemperature((byte) 0));
		    System.out.println(myRobot.getTemperature((byte) 1));
		    System.out.println(myRobot.getTemperature((byte) 2));

		    textArea.append("Punkt " + (i + 1) + " von " + liste.size() + " angesteuert\n");

		    try {
			Thread.sleep(delay);
		    } catch (InterruptedException e) {
			e.printStackTrace();
		    }
		} catch (RoboterException e) {
		    JOptionPane.showMessageDialog(null, "Abbr");

		    roboterExceptionListe.add(e);

		    i = liste.size();

		    // TODO Auto-generated catch block
		    e.printStackTrace();

		    System.out.println("Abbr");

		    // e.analyseTelemetrie();
		}
	    }

	    Duration dur = Duration.between(begin, Instant.now());

	    textArea.append("Ausführung hat " + dur.toMillis() + " ms gedauert.\n");
	}

	aktuelleTelemetrie = new ArrayList<Telemetrie>();

	for (Telemetrie daten : myRobot.getTelemetrie()) {
	    System.out.println(daten.getData());
	    aktuelleTelemetrie.add(daten);
	}

	disconnect();

	textArea.append("Ablauf beendet\n**************************\n");

	isRunningA = false;
    }
}
