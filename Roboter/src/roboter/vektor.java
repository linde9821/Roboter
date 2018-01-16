package roboter;

public class vektor {
    private punkt begin;
    private punkt ende;

    public vektor() {
	this(0, 0, 0, 0, 0, 0);
    }

    public vektor(punkt P, punkt R) {
	this(P.getX(), P.getY(), P.getZ(), R.getX(), R.getY(), R.getZ());
    }

    public vektor(double x1, double y1, double z1, double x2, double y2, double z2) {
	begin = new punkt();
	ende = new punkt();

	begin.setX(x1);
	begin.setY(y1);
	begin.setZ(z1);

	ende.setX(x2);
	ende.setY(y2);
	ende.setZ(z2);
    }

    public punkt getBegin() {
	return begin;
    }

    public punkt getEnde() {
	return ende;
    }

    public void setBegin(punkt begin) {
	this.begin = begin;
    }

    public void setBegin(vektor temp) {
	begin.setX(temp.getBegin().getX());
	begin.setY(temp.getBegin().getY());
	begin.setZ(temp.getBegin().getZ());
    }

    public void setAll(vektor temp) {
	begin.setX(temp.getBegin().getX());
	begin.setY(temp.getBegin().getY());
	begin.setZ(temp.getBegin().getZ());

	ende.setX(temp.getEnde().getX());
	ende.setY(temp.getEnde().getY());
	ende.setZ(temp.getEnde().getZ());
    }

    public void setEnde(punkt ende) {
	this.ende = ende;
    }

    public void setEnde(vektor temp) {
	ende.setX(temp.getEnde().getX());
	ende.setY(temp.getEnde().getY());
	ende.setZ(temp.getEnde().getZ());
    }

    public double betrag() {
	return Math.sqrt((ende.getX() - begin.getX()) * (ende.getX() - begin.getX())
		+ (ende.getY() - begin.getY()) * (ende.getY() - begin.getY())
		+ (ende.getZ() - begin.getZ()) * (ende.getZ() - begin.getZ()));
    }

    public static double betrag(punkt begin, punkt ende) {
	return Math.sqrt((ende.getX() - begin.getX()) * (ende.getX() - begin.getX())
		+ (ende.getY() - begin.getY()) * (ende.getY() - begin.getY())
		+ (ende.getZ() - begin.getZ()) * (ende.getZ() - begin.getZ()));
    }
}
