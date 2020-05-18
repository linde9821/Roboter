package roboter;

import telemetrie.Telemetrie;
import telemetrie.Telemetrieauswerter;

import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class RoboterException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static int amount = 0;
    private Robot ExceptionRobot;
    private LocalTime time;
    private LocalDate date;
    private int id;

    public RoboterException(Robot current) {
        this("", current);
    }

    public RoboterException(String s, Robot current) {
        super(s);
        time = LocalTime.now();
        date = LocalDate.now();

        ExceptionRobot = current.clone();

        id = amount;
        amount++;

        writeToProtocol("************************************************");
        writeToProtocol("RoboterException f�r Roboter " + ExceptionRobot.id + " " + ExceptionRobot.DEVICENAME);

        writeToProtocol("Aufgetreten am: " + time);

        writeToProtocol("Beschreibung: " + this.getMessage());

        writeToProtocol(getTelemetrieInfos());

        writeToProtocol("************************************************");

        saveTelemetrie();

        analyseTelemetrie();

        try {
            current.manualDisconnect();
        } catch (RoboterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
        ArrayList<Telemetrie> exceptionTelemetrie = ExceptionRobot.getTelemetrie();

        StringBuffer strbf = new StringBuffer();

        try {
            if (exceptionTelemetrie.isEmpty())
                throw new NullPointerException();

            // sollte nix ver�ndern da dies eigentlich nicht passieren kann
            for (int i = 0; i < exceptionTelemetrie.size(); i++) {
                if (time.isBefore(exceptionTelemetrie.get(i).timestamp)
                        || time.equals(exceptionTelemetrie.get(i).timestamp))
                    exceptionTelemetrie.get(i).setError(true);

                strbf.append(exceptionTelemetrie.get(i).getInfo());
            }

        } catch (NullPointerException e) {
            // e.printStackTrace();
            System.out.println(
                    "Keine Telemetriedaten auslesbar. (Vermutlich war grunds�tzlich keine Verbindung herstellbar)");

            strbf.append("Keine Telemetrie verf�gbar");
        }

        return strbf.toString();
    }

    public void analyseTelemetrie() {
        if (ExceptionRobot.getTelemetrie().size() > 0) {
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
