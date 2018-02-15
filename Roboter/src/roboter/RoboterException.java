package roboter;

import java.awt.EventQueue;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import telemetrie.Telemetrie;
import telemetrie.Telemetrieauswerter;

import java.time.LocalDateTime;

public class RoboterException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Robot ExceptionRobot;
    private LocalDateTime time;

    private static int amount = 0;
    private int id;

    public RoboterException(Robot current) {
	this("", current);
    }

    public RoboterException(String s, Robot current) {
	super(s);
	time = LocalDateTime.now();
	ExceptionRobot = current.clone();

	id = amount;
	amount++;

	writeToProtocol("************************************************");
	writeToProtocol("RoboterException für Roboter " + ExceptionRobot.id + " " + ExceptionRobot.DEVICENAME);

	writeToProtocol("Aufgetreten am: " + time);

	writeToProtocol("Beschreibung: " + this.getMessage());

	writeToProtocol(getTelemetrieInfos());

	writeToProtocol("************************************************");

	saveTelemetrie();
    }

    private void saveTelemetrie() {
	ArrayList<Telemetrie> exceptionTelemetrie = ExceptionRobot.getTelemetrie();

	String dateiname = "." + File.separator + "TelemetrieException" + File.separator + "Telemetrie_" + id + ".tmt";

	ObjectOutputStream oos = null;

	try {
	    File verzeichnis = new File(dateiname.substring(0, 22));

	    if (!verzeichnis.exists())
		verzeichnis.mkdir();

	    oos = new ObjectOutputStream(new FileOutputStream(dateiname));
	    oos.writeObject(exceptionTelemetrie);
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

    public String getTelemetrieInfos() {
	final ArrayList<Telemetrie> exceptionTelemetrie = ExceptionRobot.getTelemetrie();

	StringBuffer strbf = new StringBuffer();

	try {
	    if (exceptionTelemetrie.isEmpty())
		throw new NullPointerException();

	    for (int i = 0; i < exceptionTelemetrie.size(); i++) {
		strbf.append("Zeitpunkt der Telemetriedaten: " + exceptionTelemetrie.get(i).timestamp.getHour() + ":"
			+ exceptionTelemetrie.get(i).timestamp.getMinute() + ":"
			+ exceptionTelemetrie.get(i).timestamp.getSecond() + "\n");

		strbf.append(exceptionTelemetrie.get(i).getInfo());
	    }

	} catch (NullPointerException e) {
	    // e.printStackTrace();
	    System.out.println(
		    "Keine Telemetriedaten auslesbar. (Vermutlich war grundsätzlich keine Verbindung herstellbar)");

	    strbf.append("Keine Telemetrie verfügbar");
	}

	return strbf.toString();
    }

    public void analyseTelemetrie() {
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		try {
		    Telemetrieauswerter frame = new Telemetrieauswerter(ExceptionRobot.getTelemetrie());
		    frame.setVisible(true);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    private void writeToProtocol(String s) {
	try {
	    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("Protokoll.txt", true)));
	    out.println(s);
	    out.close();
	} catch (IOException e) {
	    // exception handling left as an exercise for the reader
	}
    }
}
