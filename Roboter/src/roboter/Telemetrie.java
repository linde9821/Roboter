package roboter;

import java.time.LocalTime;
import java.time.Duration;
import java.time.Instant;

//testing needed 
public class Telemetrie {
    static int amount = 0;
    int id;

    LocalTime timestamp;
    Duration dur;

    boolean telemetrieerfassung;
    boolean error;
    boolean empty;

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

    public Telemetrie() {
	this(null);
    }

    Telemetrie(Robot crt) {
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

		error = false;
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
