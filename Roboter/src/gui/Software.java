package gui;

//imports
//auto-imprts
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.eclipse.wb.swing.FocusTraversalOnArray; //taborder

//manual-imports
import input.ControlInput;//autocorrection
import input.ControlInputException;//autocorrection exception
import input.EmptyInputException;//empty input exception
import roboter.RoboterException;//robot exception
import roboter.punkt;//point class
import roboter.robot;

//class
public class Software extends JFrame {
    private static final long serialVersionUID = 1L;
    private String version = "programm 0.5.1b";// version
    private robot myRobot;// robot
    private String device;
    ArrayList<punkt> liste = new ArrayList<punkt>();

    private JPanel contentPane;
    private JScrollPane scrollPane;
    private JTextArea textArea;
    private JTextField tfX;
    private JTextField tfY;
    private JTextField tfZ;
    private JRadioButton rdbtnStatusausgaben;
    private JRadioButton rdbtnFehlermeldungen;
    private JRadioButton rdbtnAutokorrektur;
    private JButton btnAusfuehren;
    private JButton btnLeeren;
    private JButton btnClose;
    private JTextField tfMID;
    private JTextField tfWert;
    private JButton btnAdd;
    JRadioButton rdbtnStandardablauf;

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
		    Software frame;
		    if (args.length == 0)
			frame = new Software("COM3");
		    else
			frame = new Software(args[0]);

		    frame.setVisible(true);
		} catch (Exception e) {
		    System.out.println("Exception while creating frame");
		    e.printStackTrace();
		}
	    }
	});
    }

    /**
     * Create the frame.
     */
    public Software(String str) {
	device = str;// get device name

	// sets up point for ablauf()
	liste.add(new punkt(170, 0, 40));
	liste.add(new punkt(200, 100, 50));
	liste.add(new punkt(100, 200, -50));
	liste.add(new punkt(-200, 100, 40));
	liste.add(new punkt(-250, 50, 0));
	liste.add(new punkt(170, 0, 40));

	// font
	setFont(new Font("Arial", Font.PLAIN, 12));

	// auto resizeing of the textArea
	addComponentListener(new ComponentAdapter() {
	    @Override
	    public void componentResized(ComponentEvent e) {
		int x = 360;
		int y = 70;

		scrollPane.setBounds(332, 12, getBounds().width - x, getBounds().height - y);
	    }

	    @Override
	    public void componentShown(ComponentEvent arg0) {
		startUpProcedure();
	    }
	});
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setBounds(100, 100, 700, 400);
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
	lblMotor.setBounds(10, 256, 46, 14);
	contentPane.add(lblMotor);

	// Label für Wert des manuell angesteuerten Motors
	JLabel lblWert = new JLabel("Wert:");
	lblWert.setBounds(10, 291, 46, 14);
	contentPane.add(lblWert);

	// scrollPane
	scrollPane = new JScrollPane();
	scrollPane.setAutoscrolls(true);
	scrollPane.setBounds(332, 12, 342, 279);
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
	tfMID.setBounds(66, 252, 31, 20);
	contentPane.add(tfMID);

	// Textfeld für Wert des Manuell angesteuerten Motors
	tfWert = new JTextField();
	tfWert.setFont(new Font("Arial", Font.PLAIN, 12));
	tfWert.setColumns(10);
	tfWert.setBounds(66, 287, 31, 20);
	contentPane.add(tfWert);

	// button Ausführen
	btnAusfuehren = new JButton("Ausf\u00FChren");
	btnAusfuehren.setBackground(SystemColor.controlShadow);
	btnAusfuehren.setFont(new Font("Arial", Font.PLAIN, 12));
	btnAusfuehren.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		    perform();
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
	btnClose.setBounds(124, 327, 119, 23);
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
	JButton btnVerbinden = new JButton("Verbinden");
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
	btnVerbinden.setFont(new Font("Arial", Font.PLAIN, 12));
	btnVerbinden.setBackground(SystemColor.controlShadow);
	btnVerbinden.setBounds(203, 112, 119, 23);
	contentPane.add(btnVerbinden);

	// button Setzen
	JButton btnSetzen = new JButton("Setzen");
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
	btnSetzen.setBounds(124, 252, 119, 55);
	contentPane.add(btnSetzen);

	// button Ablauf
	JButton btnAblauf = new JButton("Ablauf");
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

	// button hinzufügen
	btnAdd = new JButton("Hinzufügen");
	btnAdd.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		    hinzufuegen();
	    }
	});
	btnAdd.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (e.getModifiers() == ActionEvent.MOUSE_EVENT_MASK)
		    hinzufuegen();
	    }
	});
	btnAdd.setFont(new Font("Arial", Font.PLAIN, 12));
	btnAdd.setBackground(SystemColor.controlShadow);
	btnAdd.setBounds(203, 180, 119, 23);
	contentPane.add(btnAdd);

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

	rdbtnStandardablauf = new JRadioButton("Standard-Ablauf");
	rdbtnStandardablauf.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		    rbAL(0);
	    }
	});
	rdbtnStandardablauf.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (e.getModifiers() == ActionEvent.MOUSE_EVENT_MASK)
		    rbAL(1);
	    }
	});
	rdbtnStandardablauf.setSelected(true);
	rdbtnStandardablauf.setFont(new Font("Arial", Font.PLAIN, 12));
	rdbtnStandardablauf.setBackground(SystemColor.controlShadow);
	rdbtnStandardablauf.setBounds(198, 211, 124, 23);
	contentPane.add(rdbtnStandardablauf);

	// Titel setzen
	setTitle("Roboter Testprogramm " + version + " " + robot.version);
	setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{tfX, tfY, tfZ, btnSimulieren, btnAusfuehren, btnAblauf, btnVerbinden, btnLeeren, btnAdd, tfMID, tfWert, btnSetzen, btnClose, rdbtnStatusausgaben, rdbtnFehlermeldungen, rdbtnAutokorrektur, rdbtnStandardablauf}));
	// taborder

	tfX.requestFocus();
	tfX.selectAll();

	ini();// gives currently some output in the textArea
    }

    // simulates movment
    private void simulate() {
	int x, y, z;// var. for coordinates

	if (rdbtnStatusausgaben.isSelected())
	    textArea.append("Simulation wird gestartet\n\n");

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

	    punkt p = new punkt(x, y, z);// creat usabel point for the robot

	    // checks if the point is verifiable
	    if (!robot.ansteuerbarkeit(p))
		throw new RoboterException("Der Punkt ist nicht ansteuerbar");
	    else {
		if (rdbtnStatusausgaben.isSelected())
		    textArea.append("Der Punkt ist Ansteuerbar\n\n");
	    }

	    if (rdbtnStatusausgaben.isSelected())
		textArea.append("Bewegung zum Punkt P(" + x + "|" + y + "|" + z + ")\n\n");

	    if (rdbtnStatusausgaben.isSelected()) {
		robot.sim(p);
		textArea.append(robot.moveStr + "\n");
		textArea.append("Simulation beendet\n\n");
	    }

	    tfX.requestFocus();
	    tfX.selectAll();
	}
	// error handling
	catch (EmptyInputException e) {
	    if (rdbtnStatusausgaben.isSelected())
		textArea.append(e.getMessage() + "\n\n");

	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, e.getMessage());
	} catch (ControlInputException e) {
	    if (rdbtnStatusausgaben.isSelected())
		textArea.append(e.getMessage() + "\n\n");

	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, e.getMessage());
	} catch (RoboterException e) {
	    if (rdbtnStatusausgaben.isSelected())
		textArea.append(e.getMessage() + "\n\n");

	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, e.getMessage());
	} catch (NumberFormatException e) {
	    if (rdbtnStatusausgaben.isSelected())
		textArea.append("Bitte überprüfen Sie die Eingegeben Koordinatenwerte\n\n");

	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, "Bitte überprüfen Sie die Eingegeben Koordinatenwerte");

	} catch (Exception e) {
	    if (rdbtnStatusausgaben.isSelected())
		textArea.append("Unbekannter Fehler aufgetreten\n\n");

	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, "Unbekannter Fehler aufgetreten");

	    e.printStackTrace();
	}
    }

    // methode which will calls the robot
    private void perform() {
	punkt p = null;
	int x, y, z;// var. for coordinates
	boolean statusausgabe = rdbtnStatusausgaben.isSelected();
	boolean fehlermeldung = rdbtnFehlermeldungen.isSelected();
	boolean autokorrektur = rdbtnAutokorrektur.isSelected();

	if (statusausgabe)
	    textArea.append("Starte Methode\n\n");

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

	    p = new punkt(x, y, z);// creat usabel point for the robot

	    // checks if the point is verifiable (currently robot.ansteuerbarkeit is more a
	    // placeholder than anything else: throws RoboterException if not verifiable
	    if (!robot.ansteuerbarkeit(p))
		throw new RoboterException("Der Punkt ist nicht ansteuerbar");

	    if (statusausgabe)
		textArea.append("Bewegung zum Punkt P(" + x + "|" + y + "|" + z + ")\n\n");

	    if (statusausgabe)
		textArea.append(
			"Bewegung beginnt. Das Programm nicht schließen und auf das beenden der Bewegung warten!\n\n");

	    tfX.requestFocus();
	    tfX.selectAll();

	    try {
		connect();
		Instant begin = Instant.now();
		myRobot.moveto(p);
		Duration dur = Duration.between(begin, Instant.now());

		textArea.append(robot.moveStr + "\n\n");
		disconnect();
		textArea.append("Bewegung beendet. Sie hat " + dur.toMillis() + " ms gedauert.\n");
	    } catch (RoboterException e) {
		if (statusausgabe)
		    textArea.append(e.getMessage());

		if (fehlermeldung)
		    JOptionPane.showMessageDialog(null, e.getMessage());
		e.printStackTrace();
	    } catch (NullPointerException e) {
	    }
	}
	// error handling
	catch (EmptyInputException e) {
	    if (statusausgabe)
		textArea.append(e.getMessage() + "\n\n");

	    if (fehlermeldung)
		JOptionPane.showMessageDialog(null, e.getMessage());
	} catch (ControlInputException e) {
	    if (statusausgabe)
		textArea.append(e.getMessage() + "\n\n");

	    if (fehlermeldung)
		JOptionPane.showMessageDialog(null, e.getMessage());
	} catch (RoboterException e) {
	    if (statusausgabe)
		textArea.append(e.getMessage() + "\n\n");

	    if (fehlermeldung)
		JOptionPane.showMessageDialog(null, e.getMessage());
	} catch (NumberFormatException e) {
	    if (statusausgabe)
		textArea.append("Bitte überprüfen Sie die Eingegeben Koordinatenwerte\n\n");

	    if (fehlermeldung)
		JOptionPane.showMessageDialog(null, "Bitte überprüfen Sie die Eingegeben Koordinatenwerte");

	} catch (Exception e) {
	    if (statusausgabe)
		textArea.append("Unbekannter Fehler aufgetreten\n\n");

	    if (fehlermeldung)
		JOptionPane.showMessageDialog(null, "Unbekannter Fehler aufgetreten");

	    e.printStackTrace();
	}
    }

    // manual control
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
		myRobot.set(id, goal);
		disconnect();

		if (rdbtnStatusausgaben.isSelected())
		    textArea.append("Motor " + id + " erfolgreich auf " + goal + " (" + robot.graToUni(goal)
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
	    if (rdbtnStatusausgaben.isSelected())
		textArea.append("Es sind keine Werte eingegeben\n\n");

	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, "Es sind keine Werte eingegeben");
	} catch (ControlInputException e) {
	    if (rdbtnStatusausgaben.isSelected())
		textArea.append("Keine Autokorrektur möglich\n\n");

	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, "Keine Autokorrektur möglich");
	} catch (NullPointerException e) {
	    // Nichts tuhen da dieser Fehler schon früher behandelt wird
	} catch (Exception e) {
	    if (rdbtnStatusausgaben.isSelected())
		textArea.append("Unbekannter Fehler bei der Eingabe\n\n");

	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, "Unbekannter Fehler bei der Eingabe");
	}
    }

    private void hinzufuegen() {
	int x, y, z;// var. for coordinates

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

	    punkt p = new punkt(x, y, z);// creat usabel point for the robot

	    if (!robot.ansteuerbarkeit(p))
		throw new RoboterException("Der Punkt ist nicht ansteuerbar");

	    liste.add(p);

	    if (rdbtnStatusausgaben.isSelected()) {
		textArea.append("Neuer Punkt P(" + x + "|" + y + "|" + z + ") zur Liste hinzugefügt\n\n");
		textArea.append("Liste:\n");

		for (int i = 0; i < liste.size(); i++) {
		    int a, b, c;

		    a = (int) liste.get(i).getX();
		    b = (int) liste.get(i).getY();
		    c = (int) liste.get(i).getZ();

		    textArea.append("Punkte " + (i + 1) + ": " + "P(" + a + "|" + b + "|" + c + ")" + "\n");
		}
	    }

	    textArea.append("\n");

	    tfX.requestFocus();
	    tfX.selectAll();
	}
	// error handling
	catch (EmptyInputException e) {
	    if (rdbtnStatusausgaben.isSelected())
		textArea.append(e.getMessage() + "\n\n");

	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, e.getMessage());
	} catch (ControlInputException e) {
	    if (rdbtnStatusausgaben.isSelected())
		textArea.append(e.getMessage() + "\n\n");

	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, e.getMessage());
	} catch (RoboterException e) {
	    if (rdbtnStatusausgaben.isSelected())
		textArea.append(e.getMessage() + "\n\n");

	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, e.getMessage());
	} catch (NumberFormatException e) {
	    if (rdbtnStatusausgaben.isSelected())
		textArea.append("Bitte überprüfen Sie die Eingegeben Koordinatenwerte\n\n");

	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, "Bitte überprüfen Sie die Eingegeben Koordinatenwerte");

	} catch (Exception e) {
	    if (rdbtnStatusausgaben.isSelected())
		textArea.append("Unbekannter Fehler aufgetreten\n\n");

	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, "Unbekannter Fehler aufgetreten");

	    e.printStackTrace();
	}
    }

    private void ablauf() {
	final long delay = 1000; // delay between 2 operaton in ms

	connect();
	Instant begin = Instant.now();

	for (int i = 0; i < liste.size(); i++) {
	    try {
		myRobot.moveto(liste.get(i));

		textArea.append("Punkt " + (i + 1) + " von " + liste.size() + " angesteuert\n");

		try {
		    Thread.sleep(delay);
		} catch (InterruptedException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    } catch (RoboterException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	Duration dur = Duration.between(begin, Instant.now());
	disconnect();

	textArea.append("Ablauf beendet. Er hat " + dur.toMillis() + " ms gedauert.\n");
    }

    // trys to connect and disconnect
    private void connectionTest() {
	try {
	    connect();

	    for (int i = 0; i < 3; i++) {
		textArea.append(
			"Motor " + (byte) i + " hat eine Spannung von " + myRobot.getVoltage((byte) i) + "  mV\n");
	    }
	    textArea.append("Betriebsspannung:  9  ~ 12V (Empfohlen 11.1V)\n");

	    for (int i = 0; i < 3; i++) {
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
			"Es scheint ein Probelm bei den Geschwindigkeiten geben! Fixen?", "Warnung", dialogButton);

		if (dialogResult == JOptionPane.YES_NO_OPTION) {
		    textArea.append("Problem behoben\n");
		    myRobot.setSpeed((byte) 0, (short) 80);
		    myRobot.setSpeed((byte) 1, (short) 40);
		    myRobot.setSpeed((byte) 2, (short) 40);
		}

	    }

	    for (int i = 0; i < 3; i++) {
		textArea.append("Motor " + (byte) i + " steht auf " + myRobot.get((byte) i) + " Einheiten ("
			+ robot.uniToGra(myRobot.get((byte) i)) + "°)\n");
	    }
	    textArea.append("\n");

	    disconnect();

	    if (rdbtnStatusausgaben.isSelected())
		textArea.append("Es konnte eine Verbindung zum Roboter hergestellt werden\n");

	} catch (Exception e) {
	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, "Es konnte keine verbindung zum Roboter hergestellt werden\n");
	}
    }

    // connects with robot (disconnect() should be called after operation is done)
    private void connect() {
	try {
	    myRobot = new robot(device);
	} catch (Exception e) {
	    if (rdbtnStatusausgaben.isSelected())
		textArea.append("Es konnte keine Verbindung zum Roboter hergestellt werden\n");

	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, "Es konnte keine Verbindung zum Roboter hergestellt werden\n");
	}
    }

    // disconnects from the robot
    private void disconnect() {
	try {
	    myRobot.disconnect();
	    myRobot = null;
	} catch (Exception e) {
	    if (rdbtnStatusausgaben.isSelected())
		textArea.append("Es konnte keine Verbindung zum Roboter hergestellt werden\n");

	    if (rdbtnFehlermeldungen.isSelected())
		JOptionPane.showMessageDialog(null, "Es konnte keine Verbindung zum Roboter hergestellt werden\n");
	}
    }

    // some information for the user
    private void ini() {
	textArea.append("Initialisiere Programm\n");
	textArea.append(version + "\n" + robot.version + "\ndevicename: " + this.device + " \n\n");
    }

    // clears textArea
    private void emptyTextArea() {
	textArea.setText("");
	tfX.setText("");
	tfY.setText("");
	tfZ.setText("");
    }

    // radioButton Statusausgabe control
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

    // radioButton Fehlermeldungen control
    private void rbF(int typ) {
	if (typ == 0)
	    rdbtnFehlermeldungen.setSelected(!rdbtnFehlermeldungen.isSelected());
	textArea.append("Fehlerausgabe auf " + rdbtnFehlermeldungen.isSelected() + " gesetzt\n\n");
    }

    // radioButton Autokorrektur control
    private void rbA(int typ) {
	if (typ == 0)
	    rdbtnAutokorrektur.setSelected(!rdbtnAutokorrektur.isSelected());
	textArea.append("Autokorrektur auf " + rdbtnAutokorrektur.isSelected() + " gesetzt\n\n");
    }

    // radioButton Ablauf control
    private void rbAL(int typ) {
	if (typ == 0)
	    rdbtnStandardablauf.setSelected(!rdbtnStandardablauf.isSelected());

	textArea.append("Standard-Ablauf auf " + rdbtnStandardablauf.isSelected() + " gesetzt\n\n");

	if (rdbtnStandardablauf.isSelected()) {
	    liste.clear();
	    // sets up point for ablauf()
	    liste.add(new punkt(170, 0, 40));
	    liste.add(new punkt(200, 100, 50));
	    liste.add(new punkt(100, 200, -50));
	    liste.add(new punkt(-200, 100, 40));
	    liste.add(new punkt(-250, 50, 0));
	    liste.add(new punkt(170, 0, 40));
	} else {
	    liste.clear();
	}

	for (int i = 0; i < liste.size(); i++) {
	    int a, b, c;

	    a = (int) liste.get(i).getX();
	    b = (int) liste.get(i).getY();
	    c = (int) liste.get(i).getZ();

	    textArea.append("Punkte " + (i + 1) + ": " + "P(" + a + "|" + b + "|" + c + ")" + "\n");
	}
    }

    private void startUpProcedure() {
	int dialogButton = JOptionPane.YES_NO_OPTION;

	int dialogResult = JOptionPane.showConfirmDialog(null, "Sind die IDs richtig konfiguriert?", "Warnung",
		dialogButton);

	if (dialogResult == JOptionPane.NO_OPTION)
	    close();

	dialogResult = JOptionPane.showConfirmDialog(null,
		"Ist der Devicename " + this.device
			+ " korrekt?Wenn nicht dann manuell über Geräte-Manager und Konsole starten.",
		"Warnung", dialogButton);

	if (dialogResult == JOptionPane.NO_OPTION)
	    close();

	dialogResult = JOptionPane.showConfirmDialog(null, "Ist RoboPlus disconnected?", "Warnung", dialogButton);

	if (dialogResult == JOptionPane.NO_OPTION)
	    close();

	dialogResult = JOptionPane.showConfirmDialog(null, "Soll eine Testverbindung hergestellt werden?(Empfohlen)",
		"Testverbindung", dialogButton);

	if (dialogResult == JOptionPane.YES_OPTION)
	    connectionTest();
    }

    // closes program
    private void close() {
	textArea.append("Beende Programm\n");
	System.exit(0);
    }
}
