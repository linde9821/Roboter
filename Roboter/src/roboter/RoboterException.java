package roboter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.time.LocalDateTime;

public class RoboterException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private robot ExceptionRobot;
    private LocalDateTime time;

    public RoboterException(robot current) {
	this("", current);
    }

    public RoboterException(String s, robot current) {
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
	final ArrayList<telemetrie> exceptionTelemetrie = ExceptionRobot.getTelemetrie();

	StringBuffer strbf = new StringBuffer();

	try {
	    if (exceptionTelemetrie.isEmpty())
		throw new NullPointerException();

	    for (int i = 0; i < exceptionTelemetrie.size(); i++) {
		//muss noch ausgefüllt werden 
		strbf.append("Zeitpunkt der Telemetriedaten: " + exceptionTelemetrie.get(i).timestamp.getHour() + ":"
			+ exceptionTelemetrie.get(i).timestamp.getMinute() + ":"
			+ exceptionTelemetrie.get(i).timestamp.getSecond() + "\n");
		strbf.append("Telemetrie-ID: " + exceptionTelemetrie.get(i).id + "\n");

		strbf.append(exceptionTelemetrie.get(i).getInfo());
	    }

	} catch (NullPointerException e) {
	    e.printStackTrace();
	    System.out.println(
		    "Keine Telemetriedaten auslesbar. (Vermutlich war grundsätzlich keine Verbindung herstellbar)");

	    strbf.append("Keine Telemetrie verfügbar");
	}

	return strbf.toString();
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
