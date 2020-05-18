package telemetrie;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class TelemetrieFenster extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private JPanel contentPane;


    /**
     * Create the frame.
     */
    public TelemetrieFenster(Telemetrie datensatz) {
	setBounds(100, 100, 263, 376);
	contentPane = new JPanel();
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	setContentPane(contentPane);
	contentPane.setLayout(null);
	
	JTextArea textArea = new JTextArea();
	textArea.setEditable(false);
	textArea.setBounds(10, 11, 237, 325);
	contentPane.add(textArea);
	
	setTitle("Datensatz " + datensatz.id);
	
	setResizable(false);
	
	textArea.append(datensatz.getInfo());
    }
}
