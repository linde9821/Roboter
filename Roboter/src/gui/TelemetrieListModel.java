package gui;

import javax.swing.DefaultListModel;

import roboter.Telemetrie;

public class TelemetrieListModel extends DefaultListModel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public Object getElementAt(int index) {
	Telemetrie highscore = (Telemetrie) super.getElementAt(index);
	return highscore.getData();
    }

}
