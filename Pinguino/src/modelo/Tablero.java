package modelo;

import java.util.ArrayList;



public class Tablero {
	
	public static String oso ;
	public static String agujeroHielo ;
	public static String trineo;
	public static String eventoAleatorio;
	
	private ArrayList<Casilla> casillas;

	
	public Tablero(ArrayList<Casilla> casillas) {
		super();
		this.casillas = casillas;
	}

	public ArrayList<Casilla> getCasillas() {
		return casillas;
	}
	public void setCasillas(ArrayList<Casilla> casillas) {
		this.casillas = casillas;
	}
	
	String[][] tablero = {
	/* 0 */	    {"[00]", agujeroHielo, "[02]", eventoAleatorio, trineo},
	/* 1 */	    {"[05]", "[06]", "[07]", "[08]", "[09]"},
	/* 2 */	    {"[10]", trineo, "[12]", eventoAleatorio, "[14]"},
	/* 3 */	    {agujeroHielo, "[16]", "[17]", "[18]", "[19]"},
	/* 4 */	    {"[20]", "[21]", "[22]", oso, "[24]"},
	/* 5 */	    {"[25]", eventoAleatorio, "[27]", "[28]", trineo},
	/* 6 */	    {"[30]", "[31]", agujeroHielo, "[33]", "[34]"},
	/* 7 */	    {oso, "[36]", "[37]", "[38]", "[39]"},
	/* 8 */	    {trineo, "[41]", eventoAleatorio, "[43]", "[44]"},
	/* 9 */	    {"[45]", "[46]", oso, agujeroHielo, "[49]"}
			};

}
