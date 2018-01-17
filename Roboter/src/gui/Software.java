package gui;

//imports
//auto-imprts
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
import roboter.robot;//robot class

//class
public class Software extends JFrame {

    private static final long serialVersionUID = 1L;
    private String version = "programm 0.5pa";// version
    private robot myRobot = new robot();// robot

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
		    Software frame = new Software();
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
    public Software() {
	setFont(new Font("Arial", Font.PLAIN, 12));
	//setIconImage(Toolkit.getDefaultToolkit().getImage(Software.class.getResource("/pdf_res/logo1.png")));// load
													     // icon

	// auto resizeing of the textArea
	addComponentListener(new ComponentAdapter() {
	    @Override
	    public void componentResized(ComponentEvent e) {
		int x = 360;
		int y = 70;

		scrollPane.setBounds(332, 12, getBounds().width - x, getBounds().height - y);
	    }
	});
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setBounds(100, 100, 700, 250);
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

	// scrollPane
	scrollPane = new JScrollPane();
	scrollPane.setAutoscrolls(true);
	scrollPane.setBounds(332, 12, 342, 190);
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
	tfX.setFont(new Font("Arial", Font.PLAIN, 12));
	tfX.setBounds(66, 12, 119, 20);
	contentPane.add(tfX);
	tfX.setColumns(10);

	// textField y
	tfY = new JTextField();
	tfY.setFont(new Font("Arial", Font.PLAIN, 12));
	tfY.setBounds(66, 43, 119, 20);
	contentPane.add(tfY);
	tfY.setColumns(10);

	// textField z
	tfZ = new JTextField();
	tfZ.setFont(new Font("Arial", Font.PLAIN, 12));
	tfZ.setBounds(66, 74, 119, 20);
	contentPane.add(tfZ);
	tfZ.setColumns(10);

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
		perform();
	    }
	});
	btnAusfuehren.setBounds(66, 145, 119, 23);
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
		simulate();
	    }
	});
	btnSimulieren.setBounds(66, 111, 119, 23);
	contentPane.add(btnSimulieren);

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
		close();
	    }
	});
	btnClose.setBounds(66, 179, 119, 23);
	contentPane.add(btnClose);

	// button Leeren
	btnLeeren = new JButton("Leeren");
	btnLeeren.setBackground(SystemColor.controlShadow);
	btnLeeren.setFont(new Font("Arial", Font.PLAIN, 12));
	btnLeeren.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
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
	btnLeeren.setBounds(203, 111, 119, 23);
	contentPane.add(btnLeeren);

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
		rbA(1);
	    }
	});
	rdbtnAutokorrektur.setBounds(202, 73, 124, 23);
	contentPane.add(rdbtnAutokorrektur);

	setTitle("Roboter Testprogramm " + version + " " + robot.version);// sets title
	setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[] { tfX, tfY, tfZ, btnSimulieren, btnAusfuehren,
		btnLeeren, btnClose, rdbtnStatusausgaben, rdbtnFehlermeldungen, rdbtnAutokorrektur }));// sets up
												       // taborder

	tfX.requestFocus();

	ini();// gives currently some output in the textArea
    }

    // simulates movment
    private punkt simulate() {
	int x, y, z;// var. for coordinates
	boolean statusausgabe = rdbtnStatusausgaben.isSelected();
	boolean fehlermeldung = rdbtnFehlermeldungen.isSelected();
	boolean autokorrektur = rdbtnAutokorrektur.isSelected();

	if (statusausgabe)
	    textArea.append("Simulation wird gestartet\n\n");

	try {
	    // checks for empty Input
	    if (tfX.getText().equals("")) {
		tfX.requestFocus();
		throw new EmptyInputException("Keine Wert für die X-Koordinate");
	    }

	    if (tfY.getText().equals("")) {
		tfY.requestFocus();
		throw new EmptyInputException("Keine Wert für die Y-Koordinate");
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

	    // checks if the point is verifiable (currently robot.ansteuerbarkeit is more a
	    // placeholder than anything else: throws RoboterException if not verifiable
	    if (!robot.ansteuerbarkeit(new punkt(0, 0, 0), p))
		throw new RoboterException("Der Punkt ist nicht ansteuerbar");

	    if (statusausgabe)
		textArea.append(
			"Der Ablauf wird Simuliert für eine Bewegung zum Punkt P(" + x + "|" + y + "|" + z + ")\n\n");

	   // myRobot.moveto(p);// starts the simulation by calling the moveto methode of robot class

	    // angle output
	    if (statusausgabe) {
		textArea.append(myRobot.moveStr + "\n\n");
		textArea.append("Simulation beendet\n\n");
	    }

	    tfX.requestFocus();
	    tfX.selectAll();
	    
	    return p;
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
	} finally {
	    //currently disabeld
	   // System.gc();// garbag collector
	}
	return null;

    }

    // methode which will call the robot (not implimented and currently disabeld)
    private void perform() {
	punkt p = simulate();
	
	myRobot.moveto(p);
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

    // some information for the user
    private void ini() {
	textArea.append("Initialisiere Programm\n");
	textArea.append(version + "\n" + robot.version + "\n\n");
    }

    // clears textArea
    private void emptyTextArea() {
	textArea.setText("");
	tfX.setText("");
	tfY.setText("");
	tfZ.setText("");
    }

    // closes program
    private void close() {
	textArea.append("Beende Programm\n");
	System.exit(0);
    }

    public void fullStop() {
	textArea.append("Kein Kommunikation zu Roboter möglich\n");
    }
}
