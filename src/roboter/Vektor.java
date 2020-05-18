package roboter;

import Punkt.Punkt;

//currently not used 

public class Vektor {
    private Punkt begin;
    private Punkt ende;

    public Vektor() {
	this(0, 0, 0, 0, 0, 0);
    }

    public Vektor(Punkt P, Punkt R) {
	this(P.getX(), P.getY(), P.getZ(), R.getX(), R.getY(), R.getZ());
    }

    public Vektor(double x1, double y1, double z1, double x2, double y2, double z2) {
	begin = new Punkt();
	ende = new Punkt();

	begin.setX(x1);
	begin.setY(y1);
	begin.setZ(z1);

	ende.setX(x2);
	ende.setY(y2);
	ende.setZ(z2);
    }

    public Punkt getBegin() {
	return begin;
    }

    public Punkt getEnde() {
	return ende;
    }

    public void setBegin(Punkt begin) {
	this.begin = begin;
    }

    public void setBegin(Vektor temp) {
	begin.setX(temp.getBegin().getX());
	begin.setY(temp.getBegin().getY());
	begin.setZ(temp.getBegin().getZ());
    }

    public void setAll(Vektor temp) {
	begin.setX(temp.getBegin().getX());
	begin.setY(temp.getBegin().getY());
	begin.setZ(temp.getBegin().getZ());

	ende.setX(temp.getEnde().getX());
	ende.setY(temp.getEnde().getY());
	ende.setZ(temp.getEnde().getZ());
    }

    public void setEnde(Punkt ende) {
	this.ende = ende;
    }

    public void setEnde(Vektor temp) {
	ende.setX(temp.getEnde().getX());
	ende.setY(temp.getEnde().getY());
	ende.setZ(temp.getEnde().getZ());
    }

    public double betrag() {
	return Math.sqrt((ende.getX() - begin.getX()) * (ende.getX() - begin.getX())
		+ (ende.getY() - begin.getY()) * (ende.getY() - begin.getY())
		+ (ende.getZ() - begin.getZ()) * (ende.getZ() - begin.getZ()));
    }

    public static double betrag(Punkt begin, Punkt ende) {
	return Math.sqrt((ende.getX() - begin.getX()) * (ende.getX() - begin.getX())
		+ (ende.getY() - begin.getY()) * (ende.getY() - begin.getY())
		+ (ende.getZ() - begin.getZ()) * (ende.getZ() - begin.getZ()));
    }
}
