package roboter;

public class motor {
    private static int amount;
    private int ID;

    private punkt pos;
    private double grad;

    public motor() {
	pos = new punkt();

	grad = 0;

	pos.setX(0);
	pos.setY(0);
	pos.setZ(0);

	ID = amount;
	amount++;
    }

    boolean move(double temp) {
	grad += temp;

	if (ID == 1) {
	    if (grad > 360) {
		grad -= 360;
	    }

	    else if (grad > 180) {
		grad *= -1;
	    }

	    else if (grad == 0) {
		return false;
	    }
	}

	else if (ID == 2) {

	}

	else if (ID == 3) {

	}

	// FUNKTION ZUM BEWEGEN

	// Auf beendigung warten

	return true;
    }

    // Berechnet den Abstand zwischen 2 beliebigen Motoren
    public static double abstand(motor m1, motor m2) {
	vektor temp = new vektor(m1.pos.getX(), m1.pos.getY(), m1.pos.getZ(), m2.pos.getX(), m2.pos.getY(),
		m2.pos.getZ());

	return temp.betrag();
    }

    // Rechnet von Grad in Serv um
    public static double GradToServ(double Grad) {
	return ((100 / 9) * Grad);
    }

    // Rechnet von Serv in Grad um
    double ServToGrad(double Serv) {
	return 0.09f * Serv;
    }

    public static int getAmount() {
	return amount;
    }

    public int getID() {
	return ID;
    }

    public punkt getPos() {
	return pos;
    }

    public double getGrad() {
	return grad;
    }


    public static void setAmount(int amount) {
	motor.amount = amount;
    }

    public void setID(int iD) {
	ID = iD;
    }

    public void setPos(punkt pos) {
	this.pos = pos;
    }

    public void setGrad(double grad) {
	this.grad = grad;
    }

}
