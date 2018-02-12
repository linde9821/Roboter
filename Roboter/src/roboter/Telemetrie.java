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

    Telemetrie(Robot crt) {
	id = amount;
	amount++;
	update(crt);
    }

    private void update(Robot crt) {
	try {
	    Instant temp = Instant.now();
	    timestamp = LocalTime.now();  
	    
	    telemetrieerfassung=Robot.isTelemetrieerfassung();

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
	    voltageM2 = crt.getVoltage((byte) 0);
	    voltageM3 = crt.getVoltage((byte) 0);

	    dur = Duration.between(temp, Instant.now());

	    error = false;
	} catch (Exception e) {
	    error = true;
	}
    }

    public String getInfo() {
	StringBuffer strbf = new StringBuffer();

	strbf.append(
		"Telemetrie " + timestamp.getHour() + ":" + timestamp.getMinute() + ":" + timestamp.getSecond() + "\n");
	strbf.append("Telemetrie-ID: " + id);
	
	strbf.append("Telemetrieerfassung: " + telemetrieerfassung);

	strbf.append("Abfragedauer: " + dur.toMillis() + " ms");

	strbf.append("grad M1: " + gradM1 + "u\n");
	strbf.append("grad M2: " + gradM2 + "u\n");
	strbf.append("grad M3: " + gradM3 + "u\n");

	strbf.append("speed M1: " + speedM1 + " rpm\n");
	strbf.append("speed M2: " + speedM2 + " rpm\n");
	strbf.append("speed M3: " + speedM3 + " rpm\n");

	strbf.append("temp M1: " + tempM1 + " °C\n");
	strbf.append("temp M2: " + tempM2 + " °C\n");
	strbf.append("temp M3: " + tempM3 + " °C\n");

	strbf.append("voltage M1: " + voltageM1 + " V\n");
	strbf.append("voltage M2: " + voltageM2 + " V\n");
	strbf.append("voltage M3: " + voltageM3 + " V\n");
	
	strbf.append("error: " + error);

	return strbf.toString();
    }

    public Telemetrie getTelemetrie() {
	return this;
    }
}
