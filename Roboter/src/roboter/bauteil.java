package roboter;

public class bauteil {
    private vektor vek;

    public vektor getVek() {
	return vek;
    }

    public void setVek(vektor vek) {
	this.vek = vek;
    }

    public bauteil() {
	vek = new vektor();

	vek.setBegin(new punkt(0, 0, 0));
	vek.setEnde(new punkt(0, 0, 0));
    }

    double betrag() {
	return vek.betrag();
    }

}
