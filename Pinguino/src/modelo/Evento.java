package modelo;

import java.util.Random;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class Evento {
	
	@FXML
	private Text eventos;
	
	// Obt = obtener
	public static Random random = new Random();
	public Inventario inventario; // Acceso al inventario

	public Evento(Inventario inventario) {
		this.inventario = inventario; // Inicializamos el inventario al recibirlo por constructor
	}

	int obtPez;
	int obtBolaNieve;
	int obtDadoRapido;
	int obtDadoLento;

	public Evento(int obtPez, int obtBolaNieve, int obtDadoRapido, int obtDadoLento) {
		this.obtPez = obtPez;
		this.obtBolaNieve = obtBolaNieve;
		this.obtDadoRapido = obtDadoRapido;
		this.obtDadoLento = obtDadoLento;
	}

	public int getObtPez() {
		return obtPez;
	}

	public void setObtPez(int obtPez) {
		this.obtPez = obtPez;
	}

	public int getObtBolaNieve() {
		return obtBolaNieve;
	}

	public void setObtBolaNieve(int obtBolaNieve) {
		this.obtBolaNieve = obtBolaNieve;
	}

	public int getObtDadoRapido() {
		return obtDadoRapido;
	}

	public void setObtDadoRapido(int obtDadoRapido) {
		this.obtDadoRapido = obtDadoRapido;
	}

	public int getObtDadoLento() {
		return obtDadoLento;
	}

	public void setObtDadoLento(int obtDadoLento) {
		this.obtDadoLento = obtDadoLento;
	}

	public void eventoAleatorio() {


		int[] opciones = { 0, 0, 0, 0, 0, // cada posición representa un 5% de probabilidad
				1, 1, 1, 1, 1, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3 };

		int indice = random.nextInt(opciones.length);
		int numFinal = opciones[indice];

		switch (numFinal) {
		case 0:
			obtPez = inventario.getPeces();
			if (obtPez == 2) {
				eventos.setText("Has alcanzado el limite de peces (Max:2)");
			} else {
				eventos.setText("Has conseguido 1 pez ");
				inventario.setPeces(inventario.getPeces() + 1);
			}
			break;
		case 1:
			
			obtBolaNieve = inventario.getBolasNieve();
			
			int bolas = random.nextInt(3) + 1;
			if(obtBolaNieve == 6) {
				eventos.setText("Has alcanzado el limite de bolas de nieve (Max:6)");
			}else {
			if (bolas == 1) {
				eventos.setText("Has conseguido " + bolas + " bola de nieve");
			} else {
				eventos.setText("Has conseguido " + bolas + " bolas de nieve");
			}

			inventario.setBolasNieve(inventario.getBolasNieve() + bolas);
			}
			break;
		case 2:
			obtDadoRapido = inventario.getDadoRapido();
			obtDadoLento = inventario.getDadoLentos();

			if ((obtDadoRapido + obtDadoLento) == 3) {
				eventos.setText("Has alcanzado el limite de dados (Max:3)");
			} else {
				eventos.setText("Has conseguido 1 dado rapido ");
				inventario.setDadoRapido(inventario.getDadoRapido() + 1);
			}
			break;
		case 3:
			obtDadoRapido = inventario.getDadoRapido();
			obtDadoLento = inventario.getDadoLentos();

			if ((obtDadoRapido + obtDadoLento) == 3) {
				eventos.setText("Has alcanzado el limite de dados (Max:3)");
			} else {
				eventos.setText("Has conseguido 1 dado lento  ");
				inventario.setDadoLentos(inventario.getDadoLentos() + 1);
			}
			break;
		}

	}

}
