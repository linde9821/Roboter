package telemetrie;

import java.time.LocalTime;

import roboter.Robot;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;

//testing needed 
public class Telemetrie implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    static int amount = 0;
    int id;

    public LocalTime timestamp;
    Duration dur;

    boolean telemetrieerfassung;
    boolean error;
    boolean empty;
    boolean connection;

    short speedM1;
    short speedM2;
    short speedM3;

    short gradM1;
    short gradM2;
    short gradM3;

    short tempM1;
    short tempM2;
    short tempM3;

    short voltageM1;
    short voltageM2;
    short voltageM3;

    public boolean isError() {
	return error;
    }

    public void setError(boolean error) {
	this.error = error;
    }

    public Telemetrie() {
	this(null);
    }

    public Telemetrie(Robot crt) {
	id = amount;
	amount++;
	empty = true;
	update(crt);
    }

    private void update(Robot crt) {
	try {
	    timestamp = LocalTime.now();

	    telemetrieerfassung = Robot.isTelemetrieerfassung();

	    if (telemetrieerfassung) {
		Instant temp = Instant.now();

		gradM1 = crt.getPosition((byte) 0);
		gradM2 = crt.getPosition((byte) 1);
		gradM3 = crt.getPosition((byte) 2);

		speedM1 = crt.getSpeed((byte) 0);
		speedM2 = crt.getSpeed((byte) 1);
		speedM3 = crt.getSpeed((byte) 2);

		tempM1 = crt.getTemperature((byte) 0);
		tempM2 = crt.getTemperature((byte) 1);
		tempM3 = crt.getTemperature((byte) 2);

		voltageM1 = crt.getVoltage((byte) 0);
		voltageM2 = crt.getVoltage((byte) 1);
		voltageM3 = crt.getVoltage((byte) 2);

		dur = Duration.between(temp, Instant.now());

		error = checkForError();
		empty = false;
	    } else {
		gradM1 = -1;
		gradM2 = -1;
		gradM3 = -1;

		speedM1 = -1;
		speedM2 = -1;
		speedM3 = -1;

		tempM1 = -1;
		tempM2 = -1;
		tempM3 = -1;

		voltageM1 = -1;
		voltageM2 = -1;
		voltageM3 = -1;

		dur = null;

		error = false;
	    }

	} catch (Exception e) {
	    error = true;
	}
    }

    private boolean checkForError() {
	if (gradM1 < Robot.min[0] || gradM1 > Robot.max[0])
	    return true;

	if (gradM2 < Robot.min[1] || gradM2 > Robot.max[1])
	    return true;

	if (gradM3 < Robot.min[2] || gradM1 > Robot.max[2])
	    return true;

	if (speedM1 > Robot.speedM1)
	    return true;

	if (speedM2 > Robot.speedM2)
	    return true;

	if (speedM3 > Robot.speedM3)
	    return true;

	if (tempM1 > Robot.tempratureMax)
	    return true;

	if (tempM2 > Robot.tempratureMax)
	    return true;

	if (tempM3 > Robot.tempratureMax)
	    return true;

	if (voltageM1 > Robot.maxVoltage)
	    return true;

	if (voltageM2 > Robot.maxVoltage)
	    return true;

	if (voltageM3 > Robot.maxVoltage)
	    return true;

	return false;

    }

    public String getInfo() {
	StringBuffer strbf = new StringBuffer();

	strbf.append("Telemetrie " + timestamp + "\n");
	strbf.append("Telemetrie-ID: " + id + "\n");
	strbf.append("error: " + error + "\n");
	strbf.append("empty: " + empty + "\n");

	// strbf.append("Telemetrieerfassung: " + telemetrieerfassung);

	if (telemetrieerfassung)
	    strbf.append("Abfragedauer: " + dur.toMillis() + " ms\n");

	strbf.append("grad M1: " + gradM1 + "u\n");
	strbf.append("grad M2: " + gradM2 + "u\n");
	strbf.append("grad M3: " + gradM3 + "u\n");

	strbf.append("temp M1: " + tempM1 + " °C\n");
	strbf.append("temp M2: " + tempM2 + " °C\n");
	strbf.append("temp M3: " + tempM3 + " °C\n");

	strbf.append("voltage M1: " + voltageM1 + " V\n");
	strbf.append("voltage M2: " + voltageM2 + " V\n");
	strbf.append("voltage M3: " + voltageM3 + " V\n");

	strbf.append("speed M1: " + speedM1 + " rpm\n");
	strbf.append("speed M2: " + speedM2 + " rpm\n");
	strbf.append("speed M3: " + speedM3 + " rpm\n");

	return strbf.toString();
    }

    public String getData() {
	return ("ID: " + id + " Zeitpunkt: " + timestamp + " error: " + error + " empty: " + empty);
    }

    public Telemetrie getTelemetrie() {
	return this;
    }
}
