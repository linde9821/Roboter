/* 
 Entwickler: Moritz Lindner 
 letztes Update: 14.11.2017

 ToDO:
 -Dynamixel Bibilothek einbinden																							
 -Dynamixel Servos ansteuern																								
 -Schnittstelle f�r die Eingabe von Befehlen zur Laufzeit																																							
 -die Positionen der Bauteile w�rend und nach einer Operation berechnen (Formeln m�ssen noch entwickelt werden)
 -Maximalwerte f�r alle Motorenfestlegen und vor einer Bewegung pr�fen 

 -Programmfunktionalit�t testen 
	-der Ablauf von Folgeoperationen  
	-
 -erstellen der Struktogramm und sonstiger Dokumentationen 
 -erstellen einer Schnittstellenbeschreibung 
 -argument einbauen das ein Changelog ausgibt 

 Erledigt:
 -Werte f�r die real-L�ngen der Arme eintragen			
 -Testen der Methodenfunktionalit�t		
 -Umrechnen der Motorenwerte (0-4000) in Grad
 -�berpr�fen ob die Konstruktoren von der Klasse roboter korrekt implimentiert sind 
 -Java implimentierung 
 
 Verschoben:
 -implimentieren der script-Klasse		
 -nach Fertigstellung der script-Klasse einige Standart-Scripts vorbereiten 
 -Logfile 
 -keine Sprachsolit�t im Programm (�ndern)
 
 
 Sonstiges:
  -aufteilen des Quellcodes in sepperate cpp's und h's (nicht in Java-Version)

 Known Bugs:
 -
 Changelog:
 Version 0.2.4 Java-Version
 -Implimentierung der Klasse Logo und Ausgabe eines Logos zum Programmstart
 -Erweitern der robot-Klasse
  	-neue statische Konstanten und deren Ausgabe 
  	-Erweitern der Methode Testausgabe
  	-einfache Pr�fung ob ein Punkt ansteuerbar ist (basierend auf dem Betrag des Punktes zum Ursprung und der maximalen Spannweite der Arme)
 -allgeime Erweiterungen des Programms 
 
 Version 0.2.3 Java-Version
 -Bugfixing verschiedener Ausgaben des Programms aufgrund der Java-Poertierung 
 -l�schen der roboter-Klasse zum testen und deren neu implimentieren und erweiterung in der test-Klasse
 -Erweiterung des Anwenderdialogs in der Commandpromp (funktioniert nun mehr wie ein Befehl der Commandpromp)
 -Erweitern der Vektor-Klasse: 
 	betrag(punkt begin, punkt ende) : static double - vektor 
 -Readme hinzugef�gt 
 
 Version 0.2.2 Java-Version:
 -Portierung als Java Programm
 -Entwincklung des roboter Package 
 -das Programm kann nun zu jedem Programmstart einen neu festgelegten Punkt "ansteuern" und muss nicht jedes mal neu generiert werden 

 Version 0.2.1
 -Bugfixes 
 -implimentierun der verschiedener Konstruktoren f�r die vektor-Klasse 
 -implimentieren von Methoden zur Umrechnung von Grad und Serv 
 
 Version 0.2
 -implimentierung der Methodenfunktionalit�t 

 Version 0.1
 -implimentierung der Klassenstruktur 
*/

package testprogramme;

import Punkt.Punkt;
import roboter.Robot;
import roboter.RoboterException;

public class test {
    public static final String VERSION = "0.2.6 Java-Version";

    public static void main(String[] args) throws RoboterException {

        if (args.length == 0) {
            System.out.println(
                    "Fehler: Ung�ltiger Syntax. Es wurden keine x-, y- und z-Argument angegben.\nGeben Sie \"java roboter.roboter ?\" ein, um die Syntax anzuzeigen.");
            System.exit(0);
        } else if (args[0].charAt(0) == '?') {
            System.out.println(
                    "Beschreibung:\n    Dieses Programm dient zum Testen der Module des Roboterprojektes, durch die Eingabe eines biebigen Punktes. Ohne Motoransteuerung.");
            System.out.println("Parameterliste:\n    ?    help        Zeigt die Hilfe an.\n");
            System.out.println(
                    "    [x] [y] [z]    Punktkoordinaten        Starten das Programm f�r die eingegebenen Argumente.");
            System.out.println(
                    "    0    Beispielrechnung        F�hrt Beispeilprogrammablauf zum Punkt 100; 100; 10 aus.");
            System.out.println("Beispiel:");
            System.out.println(
                    "java roboter.roboter 200 100 40		Beispieleingabe f�r einen Programmstart zum Punkt 200; 100; 40");
        } else if (args[0].charAt(0) == '0') {
            System.out.println("Version: " + VERSION);

            Robot myRobot = new Robot("COM3");
            myRobot.statusausgabe();

            myRobot.moveto(new Punkt(100, 100, 10));

            myRobot.statusausgabe();

        } else {
            int x, y, z;

            Robot myRobot = new Robot("COM3");


            System.out.println("Version: " + VERSION);

            x = Integer.parseInt(args[0]);
            y = Integer.parseInt(args[1]);
            z = Integer.parseInt(args[2]);

            System.out.println(
                    "\nDer Ablauf wird Simuliert f�r eine Bewegung zum Punkt P(" + x + "|" + y + "|" + z + ")\n");

            //myRobot.testausgabe();

            /*
             * myRobot.setup("POS-1");
             *
             * myRobot.testausgabe();
             *
             * myRobot.setup("T-Pos");
             *
             * myRobot.testausgabe();
             *
             *
             * myRobot.setup("POS-1");
             *
             * myRobot.statusausgabe();
             */

            myRobot.statusausgabe();

            if (myRobot.moveto(new Punkt(x, y, z)) == false)
                System.out.println("Der Punkt ist nicht ansteuerbar!\n");
            else
                System.out.println("Der Punkt ist ansteuerbar!\n");

            myRobot.statusausgabe();

            /*
             * myRobot.zur�cksetzten();
             *
             * myRobot.statusausgabe();
             */
        }

    }
}
