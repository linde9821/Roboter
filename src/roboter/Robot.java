package roboter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import Punkt.Punkt;
import dynamixel.Dynamixel;
import telemetrie.Telemetrie;

public class Robot implements Cloneable {
    public static final String version = "robot 3.0"; // current version
    public static final double B1_DIAMATER = 52.5f * 2;// Durchmesser Bauteil 1
    public static final double B2_LENGTH = 222;// L�nge Bauteil 2
    public static final double B3_LENGTH = 197;// L�nge Bauteil 3
    public static final double BODENEBENE = 91; // In Berechnungen "-" (Minus) verwenden
    public static final double MIN_ANGEL_B2_B3 = 72; // minimaler Winkel zwischen Bauteil1 und Bauteil2
    public static final double MAX_LENGTH = B2_LENGTH + B3_LENGTH;// Maximale l�nge des Greifarms
    public static final double MIN_LENGTH = Math.sqrt((B2_LENGTH * B2_LENGTH) + (B3_LENGTH * B3_LENGTH)
            - 2 * B2_LENGTH * B3_LENGTH * Math.cos((Math.PI / 180 * MIN_ANGEL_B2_B3)));// Minimale l�nge des Greifarms

    public final static short speedM1 = 80;
    public final static short speedM2 = 40;
    public final static short speedM3 = 40;
    public final static short speedM4 = 0;
    public final static short speedM5 = 0;
    public final static short speedM6 = 0;
    public final static short tempratureMax = 60;// in �C
    public final static short[] min = new short[]{0, 150, 10};
    public final static short[] max = new short[]{1023, 517, 517};
    public final static short maxVoltage = 15000;

    private static int amount = 0;
    int id;

    boolean stop = false;

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    private static boolean telemetrieerfassung;
    private ArrayList<Telemetrie> robotTelemetrie;

    // goal values for the angels
    private double grad1, grad2, grad3;

    public String moveStr;

    // Dynamixel
    // Control table address
    short ADDR_MX_TORQUE_ENABLE = 24; // Control table address is different in Dynamixel model
    short ADDR_MX_GOAL_POSITION = 30;
    short ADDR_MX_PRESENT_POSITION = 36;

    short ADDR_Present_Voltage = 42;
    short ADDR_Present_Temp = 43;
    short ADDR_Moving_Speed_Low = 32;

    // [in Source Code]-Settings
    private final boolean allowTelemetrieDuringMovment = true;

    // Protocol version
    int PROTOCOL_VERSION = 1; // See which protocol version is used in the Dynamixel

    // IDs for the Dynamixels
    byte[] DXL_ID = new byte[]{0, 1, 2, 3, 4, 5};

    int BAUDRATE = 1000000;
    String DEVICENAME = "COM3"; // Check which port is being used on your controller Windows: "COM1-COM5"

    byte TORQUE_ENABLE = 1; // Value for enabling the torque
    byte TORQUE_DISABLE = 0; // Value for disabling the torque
    int DXL_MOVING_STATUS_THRESHOLD = 50; // Dynamixel moving status threshold

    int COMM_SUCCESS = 0; // Communication Success result value
    int COMM_TX_FAIL = -1001; // Communication Tx Failed

    private Dynamixel dynamixel;// Dynamixel class
    int port_num;// Port number (normaly 0)
    int dxl_comm_result;
    byte dxl_error;
    short dxl_present_position;// Check if Really needed!

    // constructor
    public Robot(String temp) throws RoboterException {
        id = amount;
        amount++;

        writeToProtocol("------------------------------------------------\r\nRoboter " + id + " " + temp + " Protokoll "
                + LocalDateTime.now());

        // Dynamixel stuff
        DEVICENAME = temp;

        // Initialize Dynamixel class for java
        dynamixel = new Dynamixel();

        if (DEVICENAME != "SIM") {
            robotTelemetrie = new ArrayList<Telemetrie>();

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
                writeToProtocol("Failed to open the port!");
                decreaseAmount();
                throw new RoboterException("Failed to open the port!", this);
            }

            // Set port baudrate
            if (dynamixel.setBaudRate(port_num, BAUDRATE)) {
                System.out.println("Succeeded to change the baudrate!");
            } else {
                System.out.println("Failed to change the baudrate!");
                decreaseAmount();
                throw new RoboterException("Failed to change the baudrate!", this);
            }

            // Enable Dynamixel Torque
            dynamixel.write1ByteTxRx(port_num, PROTOCOL_VERSION, DXL_ID[0], ADDR_MX_TORQUE_ENABLE, TORQUE_ENABLE);
            dynamixel.write1ByteTxRx(port_num, PROTOCOL_VERSION, DXL_ID[1], ADDR_MX_TORQUE_ENABLE, TORQUE_ENABLE);
            dynamixel.write1ByteTxRx(port_num, PROTOCOL_VERSION, DXL_ID[2], ADDR_MX_TORQUE_ENABLE, TORQUE_ENABLE);
            dynamixel.write1ByteTxRx(port_num, PROTOCOL_VERSION, DXL_ID[3], ADDR_MX_TORQUE_ENABLE, TORQUE_DISABLE);
            dynamixel.write1ByteTxRx(port_num, PROTOCOL_VERSION, DXL_ID[4], ADDR_MX_TORQUE_ENABLE, TORQUE_DISABLE);
            dynamixel.write1ByteTxRx(port_num, PROTOCOL_VERSION, DXL_ID[5], ADDR_MX_TORQUE_ENABLE, TORQUE_DISABLE);

            if ((dxl_comm_result = dynamixel.getLastTxRxResult(port_num, PROTOCOL_VERSION)) != COMM_SUCCESS) {
                System.out.println(dynamixel.getTxRxResult(PROTOCOL_VERSION, dxl_comm_result));
            } else if ((dxl_error = dynamixel.getLastRxPacketError(port_num, PROTOCOL_VERSION)) != 0) {
                System.out.println(dynamixel.getRxPacketError(PROTOCOL_VERSION, dxl_error));
                decreaseAmount();
                throw new RoboterException(dynamixel.getRxPacketError(PROTOCOL_VERSION, dxl_error), this);
            } else {
                System.out.println("Dynamixel has been successfully connected");
                writeToProtocol("Erfolgreich Connected");
            }

            // set default moving speed values
            dynamixel.write2ByteTxRx(port_num, PROTOCOL_VERSION, DXL_ID[0], ADDR_Moving_Speed_Low, speedM1);
            dynamixel.write2ByteTxRx(port_num, PROTOCOL_VERSION, DXL_ID[1], ADDR_Moving_Speed_Low, speedM2);
            dynamixel.write2ByteTxRx(port_num, PROTOCOL_VERSION, DXL_ID[2], ADDR_Moving_Speed_Low, speedM3);
            dynamixel.write2ByteTxRx(port_num, PROTOCOL_VERSION, DXL_ID[3], ADDR_Moving_Speed_Low, speedM4);
            dynamixel.write2ByteTxRx(port_num, PROTOCOL_VERSION, DXL_ID[4], ADDR_Moving_Speed_Low, speedM5);
            dynamixel.write2ByteTxRx(port_num, PROTOCOL_VERSION, DXL_ID[5], ADDR_Moving_Speed_Low, speedM6);

            // initialise angel values with current servo positions
            grad1 = this.getPosition(DXL_ID[0]);
            grad2 = this.getPosition(DXL_ID[1]);
            grad3 = this.getPosition(DXL_ID[2]);

            addCurrentTelemetrie();
        }
        writeToProtocol("Erfolgreich initialisiert");
    }

    public Robot() throws RoboterException {
        this("SIM");
    }

    // clones current entity
    @Override
    public Robot clone() {
        try {
            return ((Robot) super.clone());
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    // getter and setter
    public Dynamixel getDynamixel() {
        return dynamixel;
    }

    public ArrayList<Telemetrie> getTelemetrie() {
        ArrayList<Telemetrie> temp = new ArrayList<Telemetrie>(robotTelemetrie);
        return temp;
    }

    public static boolean isTelemetrieerfassung() {
        return telemetrieerfassung;
    }

    public static void setTelemetrieerfassung(boolean tf) {
        telemetrieerfassung = tf;
    }

    // read and write
    public short getSpeed(byte ID) {
        return dynamixel.read2ByteTxRx(port_num, PROTOCOL_VERSION, ID, ADDR_Moving_Speed_Low);
    }

    public short getPosition(byte ID) {
        return dynamixel.read2ByteTxRx(port_num, PROTOCOL_VERSION, ID, ADDR_MX_PRESENT_POSITION);
    }

    public void setSpeed(byte ID, short speed) {
        dynamixel.write2ByteTxRx(port_num, PROTOCOL_VERSION, ID, ADDR_Moving_Speed_Low, speed);
    }

    public short getVoltage(byte ID) {
        return dynamixel.read2ByteTxRx(port_num, PROTOCOL_VERSION, ID, ADDR_Present_Voltage);
    }

    public short getTemperature(byte ID) {
        return dynamixel.read2ByteTxRx(port_num, PROTOCOL_VERSION, ID, ADDR_Present_Temp);
    }

    // manual writing indivitual motors
    public void setPosition(byte id, short goal) {
        try {
            writeGoalPosition(id, goal);
        } catch (RoboterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean stopUpdate() {
        return false;
    }

    // moves the motors to the calculated positions within the robot to point
    // procedure
    public void writeGoalPosition(byte id, double goal) throws RoboterException {
        addCurrentTelemetrie();

        // Instant beg = Instant.now();

        if (goal < min[id] || goal > max[id]) {
            throw new RoboterException("Nicht nutzbarer Wert f�r Motor " + id + " mit " + goal, this);
        }

        if (getTemperature(id) <= tempratureMax) {
            short dxl_present_position = dynamixel.read2ByteTxRx(port_num, PROTOCOL_VERSION, id,
                    ADDR_MX_PRESENT_POSITION);
            do {
                if ((dxl_error = dynamixel.getLastRxPacketError(port_num, PROTOCOL_VERSION)) != 0) {
                    System.out.println(dynamixel.getRxPacketError(PROTOCOL_VERSION, dxl_error));
                    throw new RoboterException("Fehler beim ansteuern von Motor " + id, this);
                }

                if (stopUpdate()) {
                    throw new RoboterException("Stop wurde getriggert!", this);
                }

                dxl_present_position = dynamixel.read2ByteTxRx(port_num, PROTOCOL_VERSION, id,
                        ADDR_MX_PRESENT_POSITION);

                System.out.println(
                        "[ID: " + id + "] GoalPos:" + (short) goal + " PresPos: " + dxl_present_position + " \n");

                dynamixel.write2ByteTxRx(port_num, PROTOCOL_VERSION, id, ADDR_MX_GOAL_POSITION, (short) goal);

                if (allowTelemetrieDuringMovment)
                    addCurrentTelemetrie();

                /*
                 * if (Duration.between(beg, Instant.now()).compareTo(otherDuration) throw new
                 * RoboterException("Zeitablauf", this);
                 */

            } while (Math.abs(dxl_present_position - (short) goal) >= ADDR_MX_PRESENT_POSITION);

            addCurrentTelemetrie();

        } else
            throw new RoboterException("Zu hohe Temperatur bei Motor  " + id + " mit " + getTemperature(id), this);
    }

    /*
     * frontend methodes
     */
    // boarding point for robot to point procedure
    public boolean moveto(Punkt Zielpunkt) throws RoboterException {
        writeToProtocol("Bewegung zu P(" + Zielpunkt.getX() + "|" + Zielpunkt.getY() + "|" + Zielpunkt.getZ() + ")");

        boolean ansteuerbar = calc(Zielpunkt);

        if (ansteuerbar) {
            grad2 = graToUni(330) - grad2;
            grad3 -= graToUni(30);

            writeGoalPosition(DXL_ID[1], (short) 200);
            writeGoalPosition(DXL_ID[2], (short) 13);

            writeGoalPosition(DXL_ID[1], grad2);
            writeGoalPosition(DXL_ID[2], grad3);
            writeGoalPosition(DXL_ID[0], grad1);
        }

        writeToProtocol("Bewegung abgeschlossen");

        // throw new RoboterException("Telemetrieauswertungstest", this);

        // Wenn beendet
        return true;
    }

    // simulates values for servors and return an object with the calulated values
    public static Robot sim(Punkt Zielpunkt) throws RoboterException {
        Robot simRobot = null;
        try {
            simRobot = new Robot();
        } catch (RoboterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        simRobot.writeToProtocol(
                "Simulation f�r P(" + Zielpunkt.getX() + "|" + Zielpunkt.getY() + "|" + Zielpunkt.getZ() + ")");

        boolean ansteuerbar;

        ansteuerbar = simRobot.calc(Zielpunkt);

        if (ansteuerbar) {
            simRobot.grad2 = graToUni(330) - simRobot.grad2;
            simRobot.grad3 -= graToUni(30);

            StringBuffer strbf = new StringBuffer(simRobot.moveStr);

            // Winkelausgabe
            System.out.println("----------------\nCONSOLE-LOG sim()\n----------------");
            System.out.println("Winkelwerte (am Ende):\n");
            System.out.println("M1: " + simRobot.grad1);
            System.out.println("M2: " + simRobot.grad2);
            System.out.println("M3: " + simRobot.grad3);
            System.out.println("----------------\n");

            // Einheitsausgabe
            System.out.println("----------------\nCONSOLE-LOG sim()\n----------------");
            System.out.println("Einheiten (am Ende):\n");
            System.out.println("M1: " + (short) simRobot.grad1);
            System.out.println("M2: " + (short) simRobot.grad2);
            System.out.println("M3: " + (short) simRobot.grad3);
            System.out.println("----------------\n");

            DecimalFormat f = new DecimalFormat("0.00");
            strbf = strbf.append("\nWinkelwerte(am Ende):\n" + "M1: " + f.format(simRobot.grad1) + "�\nM2: "
                    + f.format(simRobot.grad2) + "�\nM3: " + f.format(simRobot.grad3) + "�");

            strbf = strbf.append("\nEinheitswerte(am Ende):\n" + "M1: " + (short) simRobot.grad1 + "u\nM2: "
                    + (short) simRobot.grad2 + "u\nM3: " + (short) simRobot.grad3 + "u");

            simRobot.moveStr = strbf.toString();

            simRobot.writeToProtocol(simRobot.moveStr);
        } else {
            simRobot.writeToProtocol("P ist nicht ansteuerbar");
        }

        // not best solution
        simRobot.decreaseAmount();

        return simRobot;
    }

    // gives status update in console (many parts of it dont work and are not
    // necessery in any way)
    public void statusausgabe() {
        System.out.print("Statusausgabe\n");

        for (int i = 0; i < 3; i++) {
            System.out.println("Motor " + (byte) i + " hat eine Spannung von " + getVoltage((byte) i) + "  mV\n");
        }
        System.out.println("Betriebsspannung:  9  ~ 12V (Empfohlen 11.1V)\n");

        for (int i = 0; i < 3; i++) {
            System.out.println("Motor " + (byte) i + " hat eine Temperatur von " + getTemperature((byte) i) + "  �C\n");
        }
        System.out.println("Betriebstemperatur: -5�C~ +70�C\n");

        boolean problem = false;
        for (int i = 0; i < 3; i++) {
            System.out.println("Motor " + (byte) i + " hat eine Geschwindigkeit von " + getSpeed((byte) i) + "\n");

            if (getSpeed((byte) i) == 0 || getSpeed((byte) i) > 80)
                problem = true;

        }
        System.out.println("\n");

        if (problem == true) {
            int dialogButton = JOptionPane.YES_NO_OPTION;

            int dialogResult = JOptionPane.showConfirmDialog(null,
                    "Es scheint ein Probelm bei den Geschwindigkeiten zu geben! Beheben?", "Warnung!", dialogButton);

            if (dialogResult == JOptionPane.YES_NO_OPTION) {

                System.out.println("Problem behoben\n");
                setSpeed((byte) 0, speedM1);
                setSpeed((byte) 1, speedM2);
                setSpeed((byte) 2, speedM3);
            }

        }

        for (int i = 0; i < 3; i++) {
            System.out.println("Motor " + (byte) i + " steht auf " + getPosition((byte) i) + " Einheiten ("
                    + Robot.uniToGra(getPosition((byte) i)) + "�)\n");
        }
        System.out.println("\n");
    }

    /*
     * Backend methodes
     */
    // returns the quadrant of the goalpoint
    private int quadrant(Punkt temp) {
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
    public boolean calc(Punkt Zielpunkt) throws RoboterException {
        if (!ansteuerbarkeit(Zielpunkt))
            return false;

        double h = Math.sqrt(Zielpunkt.getX() * Zielpunkt.getX() + Zielpunkt.getY() * Zielpunkt.getY());
        double a = B1_DIAMATER / 2;
        double d = Math.sqrt((h - a) * (h - a) + (Zielpunkt.getZ() * Zielpunkt.getZ()));// Abstand Punkt P zu A

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
        g2 = Math.asin(Zielpunkt.getZ() / d);

        g1 = ((g1 * 180) / Math.PI);
        g2 = ((g2 * 180) / Math.PI);

        if (Double.isNaN(g1) || Double.isNaN(g2))
            throw new RoboterException("Error while calulating g1 and g2", this);

        grad2 = (180 + g1 + g2);

        // Winkel Motor 3
        grad3 = Math
                .acos((((B2_LENGTH * B2_LENGTH) + (B3_LENGTH * B3_LENGTH) - (d * d)) / (2 * B2_LENGTH * B3_LENGTH)));
        grad3 = (grad3 * 180 / Math.PI);

        // Winkel in String speichern
        DecimalFormat f = new DecimalFormat("0.00");
        StringBuffer strbf = new StringBuffer("Winkelwerte:\n" + "M1: " + f.format(grad1) + "�\nM2: " + f.format(grad2)
                + "�\nM3: " + f.format(grad3) + "�");

        // Winkelausgabe
        System.out.println("----------------\nCONSOLE-LOG calc()\n----------------");
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
        System.out.println("----------------\nCONSOLE-LOG calc()\n----------------");
        System.out.println("Einheiten:\n");
        System.out.println("M1: " + (short) grad1);
        System.out.println("M2: " + (short) grad2);
        System.out.println("M3: " + (short) grad3);
        System.out.println("----------------\n");

        strbf = strbf.append("\nEinheitswerte:\n" + "M1: " + grad1 + "u\nM2: " + grad2 + "u\nM3: " + grad3 + "u");

        moveStr = strbf.toString();

        return true;
    }

    // checks if point is usabel
    public static boolean ansteuerbarkeit(Punkt Zielpunkt) {
        double h = Math.sqrt(Zielpunkt.getX() * Zielpunkt.getX() + Zielpunkt.getY() * Zielpunkt.getY());
        double a = B1_DIAMATER / 2;

        if (h <= a)
            return false;
        else if (((h - a) * (h - a)) + Zielpunkt.getZ() * Zielpunkt.getZ() >= (B2_LENGTH + B3_LENGTH)
                * (B2_LENGTH + B3_LENGTH))
            return false;
        else if (Zielpunkt.getZ() <= -BODENEBENE)
            return false;

        return true;
    }

    // directly controls the disconnection (lowest point! very important)
    public boolean manualDisconnect() throws RoboterException {
        try {
            dynamixel.clearPort(port_num);
            dynamixel.closePort(port_num);
            dynamixel.packetHandler();
            writeToProtocol("Erfolgreich Disconnected");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RoboterException("Fehler beim Disconnecten", this);
            // return false;
        } finally {
            decreaseAmount();
        }
    }

    // from grad to units
    public static double graToUni(double gra) {
        return gra / 0.29;
    }

    // from units to grad
    public static double uniToGra(double gra) {
        return gra * 0.29;
    }

    // addes current telemetrie to telemetrie-list
    void addCurrentTelemetrie() {
        final int storagePoints = 99;
        if (telemetrieerfassung) {
            if (robotTelemetrie.size() > storagePoints) {
                robotTelemetrie.remove(0);
            }

            Instant now = Instant.now();
            robotTelemetrie.add(new Telemetrie(this));
            System.out.println("Dauer f�r eine Telemetrieerfassung: " + Duration.between(now, Instant.now()));

            this.writeToProtocol(robotTelemetrie.get(robotTelemetrie.size() - 1).getInfo());
        }
    }

    // decreased amount of entitys (a bit like a delete function)
    private void decreaseAmount() {
        amount--;
        writeToProtocol("------------------------------------------------");
    }

    // writes String into protocol
    public void writeToProtocol(String s) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("Protokoll.txt", true)));
            out.println(s);
            out.close();
        } catch (IOException e) {
            // exception handling left as an exercise for the reader
        }
    }

    public boolean hasPower() {
        short befor = getSpeed((byte) 2);

        setSpeed((byte) 2, (short) (befor + (short) 20));

        short after = getSpeed((byte) 2);

        if (after == befor)
            return false;
        else {
            setSpeed((byte) 2, (short) (befor));
            return false;
        }

    }
}