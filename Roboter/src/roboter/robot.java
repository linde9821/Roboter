package roboter;

import java.text.DecimalFormat;

import dynamixel.Dynamixel;

public class robot {
	public static final String version = "robot 0.3b";
	public static final double B1_DIAMATER = 52.5f * 2;
	public static final double B2_LENGTH = 222;
	public static final double B3_LENGTH = 197;
	public static final double BODENEBENE = 91; // In Berechnungen "-" (Minus) verwenden
	public static final double MIN_ANGEL_B2_B3 = 72; // minimaler Winkel zwischen Bauteil1 und Bauteil2
	public static final double MAX_LENGTH = B2_LENGTH + B3_LENGTH;
	public static final double MIN_LENGTH = Math.sqrt((B2_LENGTH * B2_LENGTH) + (B3_LENGTH * B3_LENGTH)
			- 2 * B2_LENGTH * B3_LENGTH * Math.cos((Math.PI / 180 * MIN_ANGEL_B2_B3)));

	public logo LOGO;

	private motor m1;
	private motor m2;
	private motor m3;
	private motor m4; // Werkzeug

	private bauteil b1;
	private bauteil b2;
	private bauteil b3;

	private String setup_pos;

	private boolean changed;

	public String moveStr;

	// Dynamixel stuff
	// Control table address
	short ADDR_MX_TORQUE_ENABLE = 24; // Control table address is different in Dynamixel model
	short ADDR_MX_GOAL_POSITION = 30;
	short ADDR_MX_PRESENT_POSITION = 36;

	// Protocol version
	int PROTOCOL_VERSION = 1; // See which protocol version is used in the Dynamixel

	// Default setting
	byte DXL_ID = 4; // Dynamixel ID: 1
	int BAUDRATE = 1000000;
	String DEVICENAME = "COM5"; // Check which port is being used on your controller
	// ex) Windows: "COM1" Linux: "/dev/ttyUSB0" Mac: "/dev/tty.usbserial-*"

	byte TORQUE_ENABLE = 1; // Value for enabling the torque
	byte TORQUE_DISABLE = 0; // Value for disabling the torque
	int DXL_MOVING_STATUS_THRESHOLD = 50; // Dynamixel moving status threshold

	int COMM_SUCCESS = 0; // Communication Success result value
	int COMM_TX_FAIL = -1001; // Communication Tx Failed

	// Dynamixel stuff
	Dynamixel dynamixel;
	int port_num;
	int dxl_comm_result;
	byte dxl_error;
	short dxl_present_position;

	public robot() {
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
			// fullStop();
		}

		// Set port baudrate
		if (dynamixel.setBaudRate(port_num, BAUDRATE)) {
			System.out.println("Succeeded to change the baudrate!");
		} else {
			System.out.println("Failed to change the baudrate!");
			// fullStop();
		}

		// Enable Dynamixel Torque
		dynamixel.write1ByteTxRx(port_num, PROTOCOL_VERSION, DXL_ID, ADDR_MX_TORQUE_ENABLE, TORQUE_ENABLE);
		if ((dxl_comm_result = dynamixel.getLastTxRxResult(port_num, PROTOCOL_VERSION)) != COMM_SUCCESS) {
			System.out.println(dynamixel.getTxRxResult(PROTOCOL_VERSION, dxl_comm_result));
		} else if ((dxl_error = dynamixel.getLastRxPacketError(port_num, PROTOCOL_VERSION)) != 0) {
			System.out.println(dynamixel.getRxPacketError(PROTOCOL_VERSION, dxl_error));
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

		setSetup_pos("T-Pos");

		changed = false;

		update_pos();
	}

	public robot(String temp) {
		LOGO = new logo();

		m1 = new motor();
		m2 = new motor();
		m3 = new motor();
		m4 = new motor();

		b1 = new bauteil();
		b2 = new bauteil();
		b3 = new bauteil();

		if (temp == "POS-1") {
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

			update_pos();
		} else {
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

			update_pos();
		}
	}

	// Setzt die Servos des Roboters auf die Werte wie Sie im System eingestellt ist
	// Debugg-Funktion! Sollte nicht im Skript bzw. der main-Funktion aufgerufen
	// werden!
	boolean update_pos() {
		if (m1.move(m1.getGrad()) == false)
			return false;

		else if (m2.move(m2.getGrad()) == false)
			return false;

		else if (m3.move(m3.getGrad()) == false)
			return false;

		return true;
	}

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

			update_pos();

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

			update_pos();

			return true;
		}

		return false;
	}

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

	public int quadrant(punkt temp) {
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

	public boolean moveto(punkt Zielpunkt) {
		if (ansteuerbarkeit(Zielpunkt) == false)
			return false;

		double h = Math.sqrt(x * x + y * y);
		double a = B1_DIAMATER / 2;
		double d = Math.sqrt((h - a) * (h - a) + (z * z));// Abstand Punkt P zu A
		short grad1, grad2, grad3;

		grad3 = Math.acos((b * b + c * c - d * d) / (2 * b * c));// Untersuchen wie ArcCos funktioniert

		/*
		 * // Winkelberechnung der Motoren double a = B1_DIAMATER / 2; double b =
		 * B2_LENGTH; double c = B3_LENGTH; double h = punkt.betrag(new punkt(0, 0, 0),
		 * new punkt(Zielpunkt.getX(), Zielpunkt.getY(), 0)); double d =
		 * Math.sqrt(Zielpunkt.getZ() * Zielpunkt.getZ() + (h - a) * (h - a));
		 * 
		 * 
		 * short grad1, grad2, grad3;
		 * 
		 */
		
		// Winkel Motor 1
		double phi = Math.atan(Math.abs(Zielpunkt.getY() / Zielpunkt.getX()));
		grad1 = 0;

		phi = (double) (phi * 180 / Math.PI);	//Umrechnen in GRAD

		if (quadrant(Zielpunkt) == 1) {
			grad1 = (short) phi;
		} else if (quadrant(Zielpunkt) == 2) {
			grad1 = (short) (180 - phi);
		} else if (quadrant(Zielpunkt) == 3) {
			grad1 = (short) (180 + phi);
		} else if (quadrant(Zielpunkt) == 4) {
			grad1 = (short) (360 - phi);
		} else {
			if (Zielpunkt.getY() >= 0)
				grad1 = 90;
			else
				grad1 = 270;
		}

		

		// Winkel Motor 2
		
		short g1, g2;
		g1 = Math.acos(B2_LENGTH*B2_LENGTH + d * d - B3_LENGTH * B3_LENGTH)/(2*B2_LENGTH*B3_LENGTH);
		g2 = Math.asin(Zielpunkt.getZ()/d);//BETRAG VON Z nehmen 
		
		g1 = (g1*180)/Math.PI;
		g2 = (g2*180)/Math.PI;
		
		grad2= 180 + g1 + g2;
		/*
		grad2 =(Math
				.cos(-((c * c - punkt.betrag(new punkt(a, 0, 0), new punkt(h, Zielpunkt.getZ(), 0)) - b * b)
						/ (2 * punkt.betrag(new punkt(a, 0, 0), new punkt(h, Zielpunkt.getZ(), 0)) - b * b * b))))
				+ (Math.cos((h - a) / d)));

		
		grad2 = (short) (grad2 * 180 / Math.PI);

*/
		/*
		 * Math.cos(-(((Math.sqrt(Zielpunkt.getX() * Zielpunkt.getX() + Zielpunkt.getY()
		 * Zielpunkt.getY()) - a) Math.sqrt(Zielpunkt.getX() * Zielpunkt.getX() +
		 * Zielpunkt.getY() * Zielpunkt.getY()) - a) - b * b - c * c) / (2 * b * c));
		 */

		grad3 = (short) (grad3 * 180 / Math.PI);

		System.out.println("----------------\nCONSOLE-LOG\n----------------");
		System.out.println("Winkelwerte:\n");
		System.out.println("M1: " + grad1);
		System.out.println("M2: " + grad2);
		System.out.println("M3: " + grad3);
		System.out.println("----------------\n");

		DecimalFormat f = new DecimalFormat("0.00");

		StringBuffer strbf = new StringBuffer("Winkelwerte:\n" + "M1: " + f.format(grad1) + "°\nM2: " + f.format(grad2)
				+ "°\nM3: " + f.format(grad3) + "°");

		moveStr = strbf.toString();

		// Bewegungsausführung
		/*
		 * old m1.move(grad1); m2.move(grad2); m3.move(grad3);
		 */
		
		grad1 *= 0.311;
		grad2 *= 0.311;
		grad3 *= 0.311;

		System.out.println("----------------\nCONSOLE-LOG\n----------------");
		System.out.println("Winkelwerte:\n");
		System.out.println("M1: " + grad1);
		System.out.println("M2: " + grad2);
		System.out.println("M3: " + grad3);
		System.out.println("----------------\n");

		if (grad1 >= 10 || grad1 <= 900)
			dynamixel.write2ByteTxRx(port_num, PROTOCOL_VERSION, DXL_ID, ADDR_MX_GOAL_POSITION, grad1);

		else
			System.out.println("ERRRORRRRR");

		changed = true;

		// Wenn beendet
		return true;
	}

	/*
	 * Operation ausführen //"Werkzeug" bool operation() {
	 * 
	 * }
	 */

	public boolean zurücksetzten() {
		/*
		 * Alte Variante jedoch sollte diese auch noch getestet werden
		 * m1.move(-m1.get_last_change()); m2.move(-m2.get_last_change());
		 * m3.move(-m3.get_last_change());
		 */

		m1.move(0);
		m2.move(0);
		m3.move(0);

		changed = false;

		return true;
	}

	public boolean ansteuerbarkeit(punkt Zielpunkt) {
		if ((punkt.betrag(new punkt(0, 0, 0), Zielpunkt) > MAX_LENGTH)
				|| (punkt.betrag(new punkt(0, 0, 0), Zielpunkt) < 90))
			return false;

		return true;
	}

	public static boolean ansteuerbarkeit(punkt Anfangspunkt, punkt Zielpunkt) {
		if ((punkt.betrag(Anfangspunkt, Zielpunkt) > MAX_LENGTH) || (punkt.betrag(Anfangspunkt, Zielpunkt) < 90))
			return false;

		return true;
	}

	public String getSetup_pos() {
		return setup_pos;
	}

	public void setSetup_pos(String setup_pos) {
		this.setup_pos = setup_pos;
	}
}
