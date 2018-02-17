package Punkt;

public class Punkt {

    private double x, y, z;

    public Punkt(double x, double y, double z) {
	this.x = x;
	this.y = y;
	this.z = z;
    }

    public Punkt() {
	this(0.0, 0.0, 0.0);
    }

    public double getX() {
	return x;
    }

    public double getY() {
	return y;
    }

    public double getZ() {
	return z;
    }

    public void setX(double x) {
	this.x = x;
    }

    public void setY(double y) {
	this.y = y;
    }

    public void setZ(double z) {
	this.z = z;
    }
    
    public void translate(double x, double y, double z) {
	translate(new Punkt(x, y, z));
    }
    
    public void translate(Punkt T) {
	double x = T.getX();
	double y = T.getY();
	double z = T.getZ();
	
	this.x += x;
	this.y += y;
	this.z += z;
    }
    
    public String toString() {
	return x + " " + y + " " + z;
    }

    public static double betrag(Punkt P, Punkt R) {
	double a = (P.x - R.x) * (P.x - R.x);
	double b = (P.y - R.y) * (P.y - R.y);
	double c = (P.z - R.z) * (P.z - R.z);

	return Math.sqrt(a + b + c);
    }
}
