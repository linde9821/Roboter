package roboter;

import java.text.DecimalFormat;

import dynamixel.Dynamixel;

public class robot {
    public static final String version = "robot 1.3b";
    public static final double B1_DIAMATER = 52.5f * 2;// Durchmesser Bauteil 1
    public static final double B2_LENGTH = 222;// Länge Bauteil 2
    public static final double B3_LENGTH = 197;// Länge Bauteil 3
    public static final double BODENEBENE = 91; // In Berechnungen "-" (Minus) verwenden
    public static final double MIN_ANGEL_B2_B3 = 72; // minimaler Winkel zwischen Bauteil1 und Bauteil2
    public static final double MAX_LENGTH = B2_LENGTH + B3_LENGTH;// Maximale länge des Greifarms
    public static final double MIN_LENGTH = Math.sqrt((B2_LENGTH * B2_LENGTH) + (B3_LENGTH * B3_LENGTH)
	    - 2 * B2_LENGTH * B3_LENGTH * Math.cos((Math.PI / 180 * MIN_ANGEL_B2_B3)));// Minimale länge des Greifarms

    public logo LOGO;// Logo

    private motor m1;// Motor 1
    private motor m2;// Motor 2
    private motor m3;// Motor 3
    private motor m4; // Werkzeug momentan nicht benutzt

    private bauteil b1;
    private bauteil b2;
    private bauteil b3;

    private double grad1, grad2, grad3;

    private String setup_pos;

    private boolean changed;

    public static String moveStr;

    // Dynamixel stuff
    // Control table address
    short ADDR_MX_TORQUE_ENABLE = 24; // Control table address is different in Dynamixel model
    short ADDR_MX_GOAL_POSITION = 30;
    short ADDR_MX_PRESENT_POSITION = 36;

    // Protocol version
    int PROTOCOL_VERSION = 1; // See which protocol version is used in the Dynamixel

    // Default setting
    byte[] DXL_ID = new byte[] { 1, 2, 3 };

    int BAUDRATE = 1000000;
    String DEVICENAME = "COM3"; // Check which port is being used on your controller
    // ex) Windows: "COM1" Linux: "/dev/ttyUSB0" Mac: "/dev/tty.usbserial-*"

    byte TORQUE_ENABLE = 1; // Value for enabling the torque
    byte TORQUE_DISABLE = 0; // Value for disabling the torque
    int DXL_MOVING_STATUS_THRESHOLD = 50; // Dynamixel moving status threshold currently to high

    int COMM_SUCCESS = 0; // Communication Success result value
    int COMM_TX_FAIL = -1001; // Communication Tx Failed

    // Dynamixel stuff
    Dynamixel dynamixel;
    int port_num;
    int dxl_comm_result;
    byte dxl_error;
    short dxl_present_position;

    // constructor
    public robot() throws RoboterException {
	// Dynamixel stuff

	// Initialize Dynamixel class for java
	dynamixel = new Dynamixel();

	// Initialize PortHandler Structs
	// Set the port path
	// Get methods and members of PortHandlerLinux or PortHandlerWindows
	port_num = dynamixel.portHandler(DEVICENAME);

	// Initialize PacketHandler Structs
	dynamixel.packetHandler();

	dxl_comm_result = COMM_TX_FAIL; // Communication result

	dxl_error = 0; // Dynamixel error
	dxl_present_position = 0; // Present position

	// Open port
	if (dynamixel.openPort(port_num)) {
	    System.out.println("Succeeded to open the port!");
	} else {
	    System.out.println("Failed to open the port!");
	    throw new RoboterException("Failed to open the port!");
	}

	// Set port baudrate
	if (dynamixel.setBaudRate(port_num, BAUDRATE)) {
	    System.out.println("Succeeded to change the baudrate!");
	} else {
	    System.out.println("Failed to change the baudrate!");
	    throw new RoboterException("Failed to change the baudrate!");
	}

	// Enable Dynamixel Torque
	dynamixel.write1ByteTxRx(port_num, PROTOCOL_VERSION, DXL_ID[0], ADDR_MX_TORQUE_ENABLE, TORQUE_ENABLE);
	dynamixel.write1ByteTxRx(port_num, PROTOCOL_VERSION, DXL_ID[1], ADDR_MX_TORQUE_ENABLE, TORQUE_ENABLE);
	dynamixel.write1ByteTxRx(port_num, PROTOCOL_VERSION, DXL_ID[2], ADDR_MX_TORQUE_ENABLE, TORQUE_ENABLE);

	if ((dxl_comm_result = dynamixel.getLastTxRxResult(port_num, PROTOCOL_VERSION)) != COMM_SUCCESS) {
	    System.out.println(dynamixel.getTxRxResult(PROTOCOL_VERSION, dxl_comm_result));
	} else if ((dxl_error = dynamixel.getLastRxPacketError(port_num, PROTOCOL_VERSION)) != 0) {
	    System.out.println(dynamixel.getRxPacketError(PROTOCOL_VERSION, dxl_error));
	    throw new RoboterException(dynamixel.getRxPacketError(PROTOCOL_VERSION, dxl_error));
	} else {
	    System.out.println("Dynamixel has been successfully connected");
	}

	// diffrent stuff
	LOGO = new logo(1);

	m1 = new motor();
	m2 = new motor();
	m3 = new motor();
	m4 = new motor();

	b1 = new bauteil();
	b2 = new bauteil();
	b3 = new bauteil();

	m1.setPos(new punkt(0.0, 0.0, 0.0));
	m1.setGrad(0);

	m2.setPos(new punkt(0, B1_DIAMATER / 2, 0));
	m2.setGrad(0);

	m3.setPos(new punkt(0, B1_DIAMATER / 2, B2_LENGTH));
	m3.setGrad(0);

	m4.setPos(new punkt(0, B1_DIAMATER / 2, B2_LENGTH + B3_LENGTH));
	// m4.set_grad();

	b1.setVek(new vektor(new punkt(0, 0, 0), new punkt(0, 0, 0)));

	b2.setVek(new vektor(new punkt(0, m2.getPos().getY(), 0), new punkt(0, m2.getPos().getY(), B2_LENGTH)));

	b3.setVek(new vektor(new punkt(0, m2.getPos().getY(), B2_LENGTH),
		new punkt(0, m2.getPos().getY(), B2_LENGTH + B3_LENGTH)));

	grad1 = 30;
	grad2 = 30;
	grad3 = 30;

	setSetup_pos("T-Pos");

	changed = false;
    }

    // boarding point for robot to point procedure
    public boolean moveto(punkt Zielpunkt) throws RoboterException {
	boolean ansteuerbar = calc(Zielpunkt);

	if (ansteuerbar) {
	    move(DXL_ID[0], grad1);
	    move(DXL_ID[0], grad2);
	    move(DXL_ID[0], grad3);
	    move((byte) 4, grad1);
	}

	changed = true;

	// Wenn beendet
	return true;
    }
    
    // simulates values for servors
    public static void sim(punkt Zielpunkt) {
	if (!ansteuerbarkeit(Zielpunkt))
	    return;

	double t1, t2, t3;
	double h = Math.sqrt(Zielpunkt.getX() * Zielpunkt.getX() + Zielpunkt.getY() * Zielpunkt.getY());
	double a = B1_DIAMATER / 2;
	double d = Math.sqrt((h - a) * (h - a) + (Zielpunkt.getZ() * Zielpunkt.getZ()));// Abstand Punkt P zu A
	double b = B2_LENGTH;
	double c = B3_LENGTH;

	// Winkel Motor 1
	double phi = Math.atan(Math.abs(Zielpunkt.getY() / Zielpunkt.getX()));
	t1 = 0;

	phi = (double) (phi * 180 / Math.PI); // Umrechnen in GRAD

	if (quadrant(Zielpunkt) == 1) {
	    t1 = phi;
	} else if (quadrant(Zielpunkt) == 2) {
	    t1 = (180 - phi);
	} else if (quadrant(Zielpunkt) == 3) {
	    t1 = (180 + phi);
	} else if (quadrant(Zielpunkt) == 4) {
	    t1 = (360 - phi);
	} else {
	    if (Zielpunkt.getY() >= 0)
		t1 = 90;
	    else
		t1 = 270;
	}

	// Winkel Motor 2
	double g1, g2;
	g1 = Math.acos((B2_LENGTH * B2_LENGTH + d * d - B3_LENGTH * B3_LENGTH) / (2 * B2_LENGTH * B3_LENGTH));
	g2 = Math.asin(Math.abs(Zielpunkt.getZ()) / d);

	g1 = ((g1 * 180) / Math.PI);
	g2 = ((g2 * 180) / Math.PI);

	t2 = (180 + g1 + g2);

	// Winkel Motor 3
	t3 = Math.acos((((b * b) + (c * c) - (d * d)) / (2 * b * c)));
	t3 = (t3 * 180 / Math.PI);

	// Winkel in String speichern
	DecimalFormat f = new DecimalFormat("0.00");
	StringBuffer strbf = new StringBuffer(
		"Winkelwerte:\n" + "M1: " + f.format(t1) + "°\nM2: " + f.format(t2) + "°\nM3: " + f.format(t3) + "°");
	moveStr = strbf.toString();

	// Winkelausgabe
	System.out.println("----------------\nCONSOLE-LOG\n----------------");
	System.out.println("Winkelwerte:\n");
	System.out.println("M1: " + t1);
	System.out.println("M2: " + t2);
	System.out.println("M3: " + t3);
	System.out.println("----------------\n");

	// Umrechnen von Grad in Einheiten
	t1 = graToUni(t1);
	t2 = graToUni(t2);
	t3 = graToUni(t3);

	// Einheitsausgabe
	System.out.println("----------------\nCONSOLE-LOG\n----------------");
	System.out.println("Einheiten:\n");
	System.out.println("M1: " + (short) t1);
	System.out.println("M2: " + (short) t2);
	System.out.println("M3: " + (short) t3);
	System.out.println("----------------\n");

    }

    // manual writing indivitual motors
    public void set(byte id, short goal) {
	do {
	    dynamixel.write2ByteTxRx(port_num, PROTOCOL_VERSION, id, ADDR_MX_GOAL_POSITION, goal);
	} while (Math.abs(dynamixel.read2ByteTxRx(port_num, PROTOCOL_VERSION, id, ADDR_MX_PRESENT_POSITION)
		- goal) >= ADDR_MX_PRESENT_POSITION);
    }


    // must be tested and evaluated
    public boolean zurücksetzten() {
	short saveGoalPosition = 30;
	// old but could become handy in the future
	// m1.move(0);
	// m2.move(0);
	// m3.move(0);

	for (int i = 0; i < 3; i++) {
	    try {
		move(DXL_ID[(byte) i], saveGoalPosition);
	    } catch (RoboterException e) {
		e.printStackTrace();
	    }
	}

	changed = false;

	return true;
    }

    // basic output (might be not usefull any longer)
    public void testausgabe() {
	System.out.print("Testausgabe:\n");

	System.out.print("Roboter in veränderter Position: " + changed + "\n");

	System.out.println("\nB1-Durchmesser: " + B1_DIAMATER);
	System.out.println("\nB2-Länge: " + B2_LENGTH);
	System.out.println("\nB3-Länge: " + B3_LENGTH);
	System.out.println("\nMinimaler Winkel zwischen B2 und B3: " + MIN_ANGEL_B2_B3);
	System.out.println("\nBodenebene: " + -BODENEBENE);
	System.out.println("\nMaxmimale Spannweite: " + MAX_LENGTH);
	System.out.println("\nMinimale Spannweite: " + MIN_LENGTH + "\n");

	System.out.print("Bauteil 1: \n");
	System.out.print("x1: " + b1.getVek().getBegin().getX() + " y1: " + b1.getVek().getBegin().getY() + " z1:"
		+ b1.getVek().getBegin().getZ() + "\n");
	System.out.print("x2: " + b1.getVek().getEnde().getX() + " y2: " + b1.getVek().getEnde().getY() + " z2:"
		+ b1.getVek().getEnde().getZ() + "\n");

	System.out.print("Bauteil 2: \n");
	System.out.print("x1: " + b2.getVek().getBegin().getX() + " y1: " + b2.getVek().getBegin().getY() + " z1:"
		+ b2.getVek().getBegin().getZ() + "\n");
	System.out.print("x2: " + b2.getVek().getEnde().getX() + " y2: " + b2.getVek().getEnde().getY() + " z2:"
		+ b2.getVek().getEnde().getZ() + "\n");
	System.out.print("Betrag: " + b2.betrag() + "\n\n");

	System.out.print("Bauteil 3: \n");
	System.out.print("x1: " + b3.getVek().getBegin().getX() + " y1: " + b3.getVek().getBegin().getY() + " z1:"
		+ b3.getVek().getBegin().getZ() + "\n");
	System.out.print("x2: " + b3.getVek().getEnde().getX() + " y2: " + b3.getVek().getEnde().getY() + " z2:"
		+ b3.getVek().getEnde().getZ() + "\n");
	System.out.print("Betrag: " + b3.betrag() + "\n\n");

	System.out.print("Motor 1: [" + m1.getID() + "] \n");
	System.out.print("x: " + m1.getPos().getX() + "y: " + m1.getPos().getY() + "z: " + m1.getPos().getZ() + "\n\n");

	System.out.print("Motor 2: [" + m2.getID() + "] \n");
	System.out.print("x: " + m2.getPos().getX() + "y: " + m2.getPos().getY() + "z: " + m2.getPos().getZ() + "\n\n");

	System.out.print("Motor 3: [" + m3.getID() + "] \n");
	System.out.print("x: " + m3.getPos().getX() + "y: " + m3.getPos().getY() + "z: " + m3.getPos().getZ() + "\n\n");

	System.out.print("Abstand Motor 1 zu Motor 3: " + motor.abstand(m1, m3) + "\n" + "\n\n");
    }

    // gives status update in console (many parts of it dont work and are not
    // necessery in any way)
    public void statusausgabe() {
	System.out.print("Statusausgabe\n");

	System.out.print("Roboter in veränderter Position: " + changed + "\n\n");

	if (changed == true) {
	    System.out.print("Bauteil 1: \n");
	    System.out.print("Drehgrad: " + m1.getGrad() + "\n" + "\n");

	    System.out.print("Bauteil 2: \n");
	    System.out.print("Betrag: " + b2.betrag() + "\n");
	    System.out.print("Winkel bezueglich B1: " + m2.getGrad() + "\n" + "\n");

	    System.out.print("Bauteil 3: \n");
	    System.out.print("Betrag: " + b3.betrag() + "\n");
	    System.out.print("Winkel bezueglich B2: " + m3.getGrad() + "\n" + "\n");

	    System.out.print("Motor 1: [" + m1.getID() + "] \n");
	    System.out.print("Aktuelle Gradzahl: " + m1.getGrad() + "\n" + "\n");

	    System.out.print("Motor 2: [" + m2.getID() + "] \n");
	    System.out.print("Aktuelle Gradzahl: " + m2.getGrad() + "\n" + "\n");

	    System.out.print("Motor 3: [" + m3.getID() + "] \n");
	    System.out.print("Aktuelle Gradzahl: " + m3.getGrad() + "\n" + "\n");

	    System.out.print("Ende der Statusausgabe\n\n");
	} else {
	    System.out.print("Bauteil 1: \n");
	    System.out.print("Drehgrad: " + m1.getGrad() + "\n" + "\n");

	    System.out.print("Bauteil 2: \n");
	    System.out.print("x1: " + b2.getVek().getBegin().getX() + " y1: " + b2.getVek().getBegin().getY() + " z1:"
		    + b2.getVek().getBegin().getZ() + "\n");
	    System.out.print("x2: " + b2.getVek().getEnde().getX() + " y2: " + b2.getVek().getEnde().getY() + " z2:"
		    + b2.getVek().getEnde().getZ() + "\n");
	    System.out.print("Betrag: " + b2.betrag() + "\n");
	    System.out.print("Winkel bezueglich B1: " + m2.getGrad() + "\n" + "\n");

	    System.out.print("Bauteil 3: \n");
	    System.out.print("x1: " + b3.getVek().getBegin().getX() + " y1: " + b3.getVek().getBegin().getY() + " z1:"
		    + b3.getVek().getBegin().getZ() + "\n");
	    System.out.print("x2: " + b3.getVek().getEnde().getX() + " y2: " + b3.getVek().getEnde().getY() + " z2:"
		    + b3.getVek().getEnde().getZ() + "\n");
	    System.out.print("Betrag: " + b3.betrag() + "\n");
	    System.out.print("Winkel bezueglich B2: " + m3.getGrad() + "\n" + "\n");

	    System.out.print("Motor 1: [" + m1.getID() + "] \n");
	    System.out.print(
		    "x: " + m1.getPos().getX() + " y: " + m1.getPos().getY() + " z: " + m1.getPos().getZ() + "\n");
	    System.out.print("Aktuelle Gradzahl: " + m1.getGrad() + "\n" + "\n");

	    System.out.print("Motor 2: [" + m2.getID() + "] \n");
	    System.out.print(
		    "x: " + m2.getPos().getX() + " y: " + m2.getPos().getY() + " z: " + m2.getPos().getZ() + "\n");
	    System.out.print("Aktuelle Gradzahl: " + m2.getGrad() + "\n" + "\n");

	    System.out.print("Motor 3: [" + m3.getID() + "] \n");
	    System.out.print(
		    "x: " + m3.getPos().getX() + " y: " + m3.getPos().getY() + " z: " + m3.getPos().getZ() + "\n");
	    System.out.print("Aktuelle Gradzahl: " + m3.getGrad() + "\n" + "\n");

	    System.out.print("Abstand Motor 1 zu Motor 2: " + motor.abstand(m1, m2) + "\n");
	    System.out.print("Abstand Motor 1 zu Motor 3: " + motor.abstand(m1, m3) + "\n");
	    System.out.print("Abstand Motor 2 zu Motor 3: " + motor.abstand(m2, m3) + "\n");

	    System.out.println("Gesammtspannweite der beiden Bauelemente: "
		    + vektor.betrag(b1.getVek().getBegin(), b3.getVek().getBegin()) + "\n");

	    System.out.print("Ende der Statusausgabe\n\n");
	}
    }

    /*
     * Within methodes methodes
     */

    // returns the quadrant of the goalpoint
    public static int quadrant(punkt temp) {
	if (temp.getX() > 0 && temp.getY() >= 0) {
	    return 1;
	} else if (temp.getX() < 0 && temp.getY() >= 0) {
	    return 2;
	} else if (temp.getX() < 0 && temp.getY() < 0) {
	    return 3;
	} else if (temp.getX() > 0 && temp.getY() < 0) {
	    return 4;
	} else {
	    return 0;
	}
    }

    // calculates values for servors
    public boolean calc(punkt Zielpunkt) {
	if (!ansteuerbarkeit(Zielpunkt))
	    return false;

	double h = Math.sqrt(Zielpunkt.getX() * Zielpunkt.getX() + Zielpunkt.getY() * Zielpunkt.getY());
	double a = B1_DIAMATER / 2;
	double d = Math.sqrt((h - a) * (h - a) + (Zielpunkt.getZ() * Zielpunkt.getZ()));// Abstand Punkt P zu A
	double b = B2_LENGTH;
	double c = B3_LENGTH;

	// Winkel Motor 1
	double phi = Math.atan(Math.abs(Zielpunkt.getY() / Zielpunkt.getX()));
	grad1 = 0;

	phi = (double) (phi * 180 / Math.PI); // Umrechnen in GRAD

	if (quadrant(Zielpunkt) == 1) {
	    grad1 = phi;
	} else if (quadrant(Zielpunkt) == 2) {
	    grad1 = (180 - phi);
	} else if (quadrant(Zielpunkt) == 3) {
	    grad1 = (180 + phi);
	} else if (quadrant(Zielpunkt) == 4) {
	    grad1 = (360 - phi);
	} else {
	    if (Zielpunkt.getY() >= 0)
		grad1 = 90;
	    else
		grad1 = 270;
	}

	// Winkel Motor 2
	double g1, g2;
	g1 = Math.acos((B2_LENGTH * B2_LENGTH + d * d - B3_LENGTH * B3_LENGTH) / (2 * B2_LENGTH * B3_LENGTH));
	g2 = Math.asin(Math.abs(Zielpunkt.getZ()) / d);

	g1 = ((g1 * 180) / Math.PI);
	g2 = ((g2 * 180) / Math.PI);

	grad2 = (180 + g1 + g2);

	// Winkel Motor 3
	grad3 = Math.acos((((b * b) + (c * c) - (d * d)) / (2 * b * c)));
	grad3 = (grad3 * 180 / Math.PI);

	// Winkel in String speichern
	DecimalFormat f = new DecimalFormat("0.00");
	StringBuffer strbf = new StringBuffer("Winkelwerte:\n" + "M1: " + f.format(grad1) + "°\nM2: " + f.format(grad2)
		+ "°\nM3: " + f.format(grad3) + "°");
	moveStr = strbf.toString();

	// Winkelausgabe
	System.out.println("----------------\nCONSOLE-LOG\n----------------");
	System.out.println("Winkelwerte:\n");
	System.out.println("M1: " + grad1);
	System.out.println("M2: " + grad2);
	System.out.println("M3: " + grad3);
	System.out.println("----------------\n");

	// Umrechnen von Grad in Einheiten
	grad1 = graToUni(grad1);
	grad2 = graToUni(grad2);
	grad3 = graToUni(grad3);

	// Einheitsausgabe
	System.out.println("----------------\nCONSOLE-LOG\n----------------");
	System.out.println("Einheiten:\n");
	System.out.println("M1: " + (short) grad1);
	System.out.println("M2: " + (short) grad2);
	System.out.println("M3: " + (short) grad3);
	System.out.println("----------------\n");

	return true;
    }

    // moves the motors to the calculated positions within the robot to point
    // procedure
    public void move(byte id, double goal) throws RoboterException {
	if (goal >= 10 || goal <= 900) {
	    short dxl_present_position;

	    do {
		if ((dxl_error = dynamixel.getLastRxPacketError(port_num, PROTOCOL_VERSION)) != 0) {
		    System.out.println(dynamixel.getRxPacketError(PROTOCOL_VERSION, dxl_error));
		    throw new RoboterException("Fehler beim ansteuern eines Motors");
		}

		dxl_present_position = dynamixel.read2ByteTxRx(port_num, PROTOCOL_VERSION, id,
			ADDR_MX_PRESENT_POSITION);

		System.out.println(
			"[ID: " + id + "] GoalPos:" + (short) goal + " PresPos: " + dxl_present_position + " \n");

		dynamixel.write2ByteTxRx(port_num, PROTOCOL_VERSION, id, ADDR_MX_GOAL_POSITION, (short) goal);

	    } while (Math.abs(dxl_present_position - (short) goal) >= ADDR_MX_PRESENT_POSITION);
	} else
	    throw new RoboterException("Nicht nutzbarer Wert für Motor " + id + " mit " + goal);
    }

    // funktioniert nicht (liefert aktuell immer true)
    public static boolean ansteuerbarkeit(punkt Zielpunkt) {
	double h = Math.sqrt(Zielpunkt.getX() * Zielpunkt.getX() + Zielpunkt.getY() * Zielpunkt.getY());
	double a = B1_DIAMATER / 2;
	double b = B2_LENGTH;
	double c = B3_LENGTH;

	// if ((punkt.betrag(new punkt(0, 0, 0), Zielpunkt) > MAX_LENGTH)
	// || (punkt.betrag(new punkt(0, 0, 0), Zielpunkt) < 90))
	// return false;
	// else if (((h - a) * (h - a)) + Zielpunkt.getZ() * Zielpunkt.getZ() <= (b + c)
	// * (b + c)) {
	// return false;
	// } else if (Zielpunkt.getZ() >= -BODENEBENE)
	// return false;

	return true;
    }

    // directly controls the disconnect (lowest point! very important)
    public void disconnect() {
	dynamixel.closePort(port_num);
	dynamixel.clearPort(port_num);
    }

    // from grad to units
    public static double graToUni(double gra) {
	return gra / 0.29;
    }

    // from units to grad
    public static double uniToGra(double gra) {
	return gra * 0.29;
    }

    // old might can be deleted
    boolean setup(String temp) {
	if (temp == "T-Pos") {
	    m1.setPos(new punkt(0, 0, 0));
	    m1.setGrad(0);

	    m2.setPos(new punkt(0, B1_DIAMATER / 2, 0));
	    m2.setGrad(0);

	    m3.setPos(new punkt(0, B1_DIAMATER / 2, B2_LENGTH));
	    m3.setGrad(0);

	    m4.setPos(new punkt(0, B1_DIAMATER / 2, B2_LENGTH + B3_LENGTH));
	    // m4.set_grad();

	    b1.setVek(new vektor(new punkt(0, 0, 0), new punkt(0, 0, 0)));

	    b2.setVek(new vektor(new punkt(0, m2.getPos().getY(), 0), new punkt(0, m2.getPos().getY(), B2_LENGTH)));

	    b3.setVek(new vektor(new punkt(0, m2.getPos().getY(), B2_LENGTH),
		    new punkt(0, m2.getPos().getY(), B2_LENGTH + B3_LENGTH)));

	    setSetup_pos("T-Pos");

	    changed = false;

	    return true;
	}

	else if (temp == "POS-1") {
	    m1.setPos(new punkt(0, 0, 0));
	    m1.setGrad(0);

	    m2.setPos(new punkt(0, B1_DIAMATER / 2, 0));
	    m2.setGrad(0);

	    m3.setPos(new punkt(0, B1_DIAMATER / 2, B2_LENGTH));
	    m3.setGrad(0);

	    m4.setPos(new punkt(0, B1_DIAMATER / 2, B2_LENGTH + B3_LENGTH));

	    b1.setVek(new vektor(new punkt(0, 0, 0), new punkt(0, 0, 0)));

	    b2.setVek(new vektor(new punkt(0, m2.getPos().getY(), 0), new punkt(0, m2.getPos().getY(), B2_LENGTH)));

	    b3.setVek(new vektor(new punkt(0, m2.getPos().getY(), B2_LENGTH),
		    new punkt(0, m2.getPos().getY(), B2_LENGTH + B3_LENGTH)));

	    changed = false;

	    setSetup_pos("POS-1");

	    return true;
	}

	return false;
    }

    // old might can be deleted
    public String getSetup_pos() {
	return setup_pos;
    }

    // old might can be deleted
    public void setSetup_pos(String setup_pos) {
	this.setup_pos = setup_pos;
    }
}