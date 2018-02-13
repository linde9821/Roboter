package roboter;

import java.awt.EventQueue;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import gui.Telemetrieauswerter;

import java.time.LocalDateTime;

public class RoboterException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Robot ExceptionRobot;
    private LocalDateTime time;

    public RoboterException(Robot current) {
	this("", current);
    }

    public RoboterException(String s, Robot current) {
	super(s);
	time = LocalDateTime.now();
	ExceptionRobot = current.clone();

	writeToProtocol("************************************************");
	writeToProtocol("RoboterException für Roboter " + ExceptionRobot.id + " " + ExceptionRobot.DEVICENAME);

	writeToProtocol("Aufgetreten am: " + time);

	writeToProtocol("Beschreibung: " + this.getMessage()); 

	writeToProtocol(getTelemetrieInfos());

	writeToProtocol("************************************************");
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
	    //e.printStackTrace();
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
