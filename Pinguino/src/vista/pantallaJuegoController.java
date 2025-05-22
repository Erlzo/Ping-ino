package vista;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;
import java.util.Scanner;

import controlador.bbdd;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Inventario;
import modelo.Jugador;
import vista.pantallaPrincipalController;

public class pantallaJuegoController {

	// Menu items
	@FXML
	private MenuItem newGame;
	@FXML
	private MenuItem saveGame;
	@FXML
	private MenuItem loadGame;
	@FXML
	private MenuItem quitGame;

	// Buttons
	@FXML
	private Button dado;
	@FXML
	private Button rapido;
	@FXML
	private Button lento;
	@FXML
	private Button peces;
	@FXML
	private Button nieve;

	// Texts
	@FXML
	private Text dadoResultText;
	@FXML
	private Text rapido_t;
	@FXML
	private Text lento_t;
	@FXML
	private Text peces_t;
	@FXML
	private Text nieve_t;
	@FXML
	private Text eventos;

	// Game board and player pieces
	@FXML
	private GridPane tablero;
	@FXML
	private Circle P1;

	// ONLY FOR TESTING!!!
	private int p1Position = 0; // Tracks current position (from 0 to 49 in a 5x10 grid)
	private final int COLUMNS = 5;

	public int dadoRapido;
	public int dadoLento;
	public int pecesObt;
	public int bolasNieve;
	
	public Inventario inventario; // Acceso al inventario

	Scanner scanner = new Scanner(System.in);

	@FXML
	private void initialize() {
		// This method is called automatically after the FXML is loaded
		// You can set initial values or add listeners here
		inventario = new Inventario();
		eventos.setText("¡El juego ha comenzado!");
	}

	// Button and menu actions

	@FXML
	private void handleNewGame() {
		System.out.println("New game.");
		// TODO
	}

	@FXML
	private void handleSaveGame(ActionEvent event) {
	    // Datos de ejemplo — deberías obtenerlos de tus variables del juego
	    LocalDate fecha = LocalDate.now();
	    LocalTime hora = LocalTime.now();
	    int posiciones = p1Position;  // Ejemplo de estado de partida
	    String estado = "";
	    
	    int peces = inventario.getPeces();
	    int dadosRapidos = inventario.getDadoRapido();
	    int dadosLentos = inventario.getDadoLentos();
	    
	    String username = Jugador.username;
	    String password = Jugador.password;
	    
		if (p1Position >= 50) {
		     estado = "FINALIZADA"; // 5 columns * 10 rows = 50 cells (index 0 to 49)
		}else {
			estado = "EN_PROGRESO";
		}

	    // Convertimos fecha y hora al formato SQL
	    String fechaSQL = java.sql.Date.valueOf(fecha).toString();
	    String horaSQL = java.sql.Time.valueOf(hora).toString();

	    // Creamos la sentencia SQL de inserción
	    String sqlPartida = String.format(
	    	    "INSERT INTO PARTIDA (IDPARTIDA, NUMPARTIDA, FECHA, HORA, POSICIONES, ESTADO) " +
	    	    "VALUES (seq_idpartida.NEXTVAL, seq_numpartida.NEXTVAL, TO_DATE('%s', 'YYYY-MM-DD'), TO_DATE('%s', 'HH24:MI:SS'), '%s', '%s')",
	    	     fechaSQL, horaSQL, posiciones, estado
	    	);
	    
	    String sqlJugador = String.format(
	    	    "INSERT INTO JUGADOR (IDJUGADOR, NUMPARTIDAS, NOMBRE, CONTRASEÑA) " +
	    	    "VALUES (seq_idjugador.NEXTVAL, seq_numpartida.NEXTVAL, '%s', '%s')",
	    	     username, password
	    	);
	    
	    String sqlParticipacion = String.format(
	    	    "INSERT INTO PARTICIPACIÓN (IDPARTICIPACIÓN, POSICIONACTUAL, NUMPECES, NUMDADOLENTO, NUMDADORAPIDO, IDJUGADOR, IDPARTIDA) " +
	    	    "VALUES (seq_idparticipacion.NEXTVAL, '%s', '%s', '%s', '%s', seq_idjugador.CURRVAL, seq_idpartida.CURRVAL)",
	    	     posiciones, peces, dadosRapidos, dadosLentos
	    	);
	    
	    try {
	        Connection con = bbdd.conectarBaseDatos();  // Te pedirá los datos de conexión por consola
	        bbdd.insert(con, sqlPartida);                      // Ejecuta el insert
	        bbdd.insert(con, sqlJugador);
	        bbdd.insert(con, sqlParticipacion);                      // Ejecuta el insert
	        con.close();                                // Cierra la conexión
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	@FXML
	private void handleLoadGame(ActionEvent event) {
	    Scanner scanner = new Scanner(System.in);

	    System.out.print("🔎 Introduce el número de partida (NUMPARTIDA) que deseas cargar: ");
	    int numPartida = scanner.nextInt();  // Lee el número desde la consola

	    String sql = "SELECT * FROM PARTIDA WHERE NUMPARTIDA = " + numPartida;
	    String sqlJugador = "SELECT * FROM PARTICIPACIÓN WHERE NUMPARTIDA = " + numPartida;

	    try {
	        Connection con = bbdd.conectarBaseDatos();  // Te pedirá datos por consola
	        ResultSet rs = bbdd.select(con, sql);
	        ResultSet rsParticipacion = bbdd.select(con, sqlJugador);

	        if (rs != null && rs.next()) {
	            int idPartida = rs.getInt("IDPARTIDA");
	            Date fecha = rs.getDate("FECHA");
	            Time hora = rs.getTime("HORA");
	            p1Position = rs.getInt("POSICIONES");
	            String estado = rs.getString("ESTADO");
	            
	            int peces = rsParticipacion.getInt("");
	            
	    		int row = p1Position / COLUMNS;
	    		int col = p1Position % COLUMNS;
				GridPane.setRowIndex(P1, row);
				GridPane.setColumnIndex(P1, col);
				
	            // Aquí haces lo que necesites con los datos cargados
	            System.out.println("✅ Partida cargada:");
	            System.out.println("ID: " + idPartida);
	            System.out.println("Núm: " + numPartida);
	            System.out.println("Fecha: " + fecha);
	            System.out.println("Hora: " + hora);
	            System.out.println("Posiciones: " + p1Position);
	            System.out.println("Estado: " + estado);

	            // TODO: Actualiza tu lógica del juego con esta información

	        } else {
	            System.out.println("⚠️ No se encontró ninguna partida.");
	        }

	        con.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


	@FXML
	private void handleQuitGame() {
		System.out.println("Exit...");
		// TODO
	}

	public void eventoAleatorio() {
		Random random = new Random();

		int[] opciones = { 0, 0, 0, 0, 0, // cada posición representa un 5% de probabilidad
				1, 1, 1, 1, 1, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3 };

		int indice = random.nextInt(opciones.length);
		int numFinal = opciones[indice];

		switch (numFinal) {
		case 0:
			pecesObt = inventario.getPeces();
			if (pecesObt == 2) {
				eventos.setText("Has alcanzado el limite de peces (Max:2)");
			} else {
				eventos.setText("Has conseguido 1 pez ");
				inventario.setPeces(inventario.getPeces() + 1);
				peces_t.setText("Peces: " + inventario.getPeces());
			}
			break;
		case 1:
			bolasNieve = inventario.getBolasNieve();

			int bolas = random.nextInt(3) + 1;
			if (bolasNieve == 6) {
				eventos.setText("Has alcanzado el limite de bolas de nieve (Max:6)");
			} else {
				if (bolas == 1) {
					eventos.setText("Has conseguido " + bolas + " bola de nieve");
				} else {
					eventos.setText("Has conseguido " + bolas + " bolas de nieve");
				}

				inventario.setBolasNieve(inventario.getBolasNieve() + bolas);
			}
			break;
		case 2:
			dadoRapido = inventario.getDadoRapido();
			dadoLento = inventario.getDadoLentos();
			if ((dadoRapido + dadoLento) == 3) {
				eventos.setText("Has alcanzado el limite de dados (Max:3)");
			} else {
				eventos.setText("Has conseguido 1 dado rapido ");
				inventario.setDadoRapido(inventario.getDadoRapido() + 1);

				rapido_t.setText("Dado rápido: " + inventario.getDadoRapido());
			}
			break;
		case 3:
			dadoRapido = inventario.getDadoRapido();
			dadoLento = inventario.getDadoLentos();
			if ((dadoRapido + dadoLento) == 3) {
				eventos.setText("Has alcanzado el limite de dados (Max:3)");
			} else {
				eventos.setText("Has conseguido 1 dado lento  ");
				inventario.setDadoLentos(inventario.getDadoLentos() + 1);
				lento_t.setText("Dado lento: " + inventario.getDadoLentos());
			}
			break;
		}
	}

	private boolean mostrarVentanaOso() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/pantallaOso.fxml"));
			Parent root = loader.load();

			pantallaOsoController controller = loader.getController();

			Stage popupStage = new Stage();
			controller.setStage(popupStage);

			popupStage.setScene(new Scene(root));
			popupStage.setTitle("¡Te encontraste un oso!");
			popupStage.initModality(Modality.APPLICATION_MODAL); // bloquea hasta que se cierre
			popupStage.showAndWait();

			return controller.decisionJugador();

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@FXML
	private void mostrarReglas(ActionEvent event) {
	    try {
	        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/pantallaReglas.fxml"));
	        Parent root = fxmlLoader.load();

	        Stage stage = new Stage();
	        stage.setTitle("Reglas del Juego");
	        stage.setScene(new Scene(root));
	        stage.initModality(Modality.APPLICATION_MODAL); // Bloquea interacción con la ventana principal
	        stage.setResizable(false);
	        stage.showAndWait();

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}


	@FXML
	private void handleDado(ActionEvent event) {
		Random rand = new Random();
		int diceResult = rand.nextInt(6) + 1;

		// Update the Text
		dadoResultText.setText("Ha salido: " + diceResult);

		// Update the position
		moveP1(diceResult);
	}

	private void moveP1(int steps) {
		p1Position += steps;

		eventos.setText("Casilla: " + p1Position);

		// Bound player
		if (p1Position >= 50) {
			p1Position = 49; // 5 columns * 10 rows = 50 cells (index 0 to 49)
		}

		
		
		// Check row and column
		// fila y columna recibidas
		int row = p1Position / COLUMNS;
		int col = p1Position % COLUMNS;
		
		// Variable para comprobar si alguna condición se cumplió
		boolean accionRealizada = false;
		boolean bucle = true;

		boolean moverDeTrineo = false; // Variable que indica si se debe mover al siguiente trineo

		while (bucle) {
			// Verificar las condiciones para las diferentes filas y columnas
			// fila 0
			if (row == 0 && col == 3) { // eventoAleatorio
				GridPane.setRowIndex(P1, row);
				GridPane.setColumnIndex(P1, col);
				eventoAleatorio();
				accionRealizada = true;
				bucle = false;
			} else if (row == 0 && col == 4) { // trineo
				if (!moverDeTrineo) {
					eventos.setText("Casilla de trineo. Nos movemos .");
					row = 2;
					col = 1;
					p1Position = 11;
					GridPane.setRowIndex(P1, row);
					GridPane.setColumnIndex(P1, col);
					accionRealizada = true;
					moverDeTrineo = true; // Activa el movimiento al siguiente trineo
				} else {
					bucle = false; // Termina el turno después de moverse
				}
			}else if(row == 0 && col == 1) {
				eventos.setText("Casilla de agujero de hielo. No te mueves al ser el agujero de hielo mas bajo .");
				accionRealizada = true;
				bucle = false;
			}

			// fila 2
			if (row == 2 && col == 1) { // trineo
				if (!moverDeTrineo) {
					eventos.setText("Casilla de trineo. Nos movemos .");
					row = 5;
					col = 4;
					p1Position = 29;
					GridPane.setRowIndex(P1, row);
					GridPane.setColumnIndex(P1, col);
					accionRealizada = true;
					moverDeTrineo = true; // Activa el movimiento al siguiente trineo
				} else {
					bucle = false; // Termina el turno después de moverse
				}
			} else if (row == 2 && col == 3) { // eventoAleatorio
				GridPane.setRowIndex(P1, row);
				GridPane.setColumnIndex(P1, col);
				eventoAleatorio();
				accionRealizada = true;
				bucle = false;
			}

			// fila 3
			if (row == 3 && col == 0) { // agujeroHielo
				eventos.setText("Casilla de agujero de hielo. Nos vamos hacia atras .");
				row = 0;
				col = 1;
				p1Position = 1;
				GridPane.setRowIndex(P1, row);
				GridPane.setColumnIndex(P1, col);
				accionRealizada = true;
				bucle = false;
			}

			// fila 4
			if (row == 4 && col == 3) { // oso
				if (inventario.getPeces() < 1) {
					eventos.setText("Casilla de oso. No tienes peces. Pal lobby.");
				} else {

					eventos.setText("Casilla de oso. ¿Quieres usar un pez?");

					boolean decision = mostrarVentanaOso();
					if (decision) {
						// El jugador usó un pez, puedes dejarlo quedarse
						inventario.setPeces(inventario.getPeces() - 1);
						peces_t.setText("Peces: " + inventario.getPeces());
						row = 4;
						col = 3;
						p1Position = 23;
						GridPane.setRowIndex(P1, row);
						GridPane.setColumnIndex(P1, col);

					} else {
						// Lo regresamos al inicio
						row = 0;
						col = 0;
						p1Position = 0;
						GridPane.setRowIndex(P1, row);
						GridPane.setColumnIndex(P1, col);
					}
					accionRealizada = true;
					bucle = false;
				}
			}

			// fila 5
			if (row == 5 && col == 1) { // eventoAleatorio
				GridPane.setRowIndex(P1, row);
				GridPane.setColumnIndex(P1, col);
				eventoAleatorio();
				accionRealizada = true;
				bucle = false;
			} else if (row == 5 && col == 4) { // trineo
				if (!moverDeTrineo) {
					eventos.setText("Casilla de trineo. Nos movemos .");
					row = 8;
					col = 0;
					p1Position = 40;
					GridPane.setRowIndex(P1, row);
					GridPane.setColumnIndex(P1, col);
					accionRealizada = true;
					moverDeTrineo = true; // Activa el movimiento al siguiente trineo
				} else {
					bucle = false; // Termina el turno después de moverse
				}
			}

			// fila 6
			if (row == 6 && col == 2) { // agujeroHielo
				eventos.setText("Casilla de agujero de hielo. Nos vamos hacia atras .");
				row = 3;
				col = 0;
				p1Position = 15;
				GridPane.setRowIndex(P1, row);
				GridPane.setColumnIndex(P1, col);
				accionRealizada = true;
				bucle = false;
			}

			// fila 7
			if (row == 7 && col == 0) { // oso
				if (inventario.getPeces() < 1) {
					eventos.setText("Casilla de oso. No tienes peces. Pal lobby.");
				} else {

					eventos.setText("Casilla de oso. ¿Quieres usar un pez?");

					boolean decision = mostrarVentanaOso();
					if (decision) {
						// El jugador usó un pez, puedes dejarlo quedarse
						inventario.setPeces(inventario.getPeces() - 1);
						peces_t.setText("Peces: " + inventario.getPeces());
						row = 7;
						col = 0;
						p1Position = 35;
						GridPane.setRowIndex(P1, row);
						GridPane.setColumnIndex(P1, col);
					} else {
						// Lo regresamos al inicio
						row = 0;
						col = 0;
						p1Position = 0;
						GridPane.setRowIndex(P1, row);
						GridPane.setColumnIndex(P1, col);
					}
					accionRealizada = true;
					bucle = false;
				}
			}

			// fila 8
			if (row == 8 && col == 0) { // trineo
				// Acción de trineo
				if (!moverDeTrineo) {
					eventos.setText("Casilla de trineo. No te mueves al ser el trineo mas alto .");
					accionRealizada = true;
					moverDeTrineo = true; // Activa el movimiento al siguiente trineo
				} else {
					bucle = false; // Termina el turno después de moverse
				}
			} else if (row == 8 && col == 2) { // eventoAleatorio
				GridPane.setRowIndex(P1, row);
				GridPane.setColumnIndex(P1, col);
				eventoAleatorio();
				accionRealizada = true;
				bucle = false;
			}

			// fila 9
			if (row == 9 && col == 2) { // oso

				if (inventario.getPeces() < 1) {
					eventos.setText("Casilla de oso. No tienes peces. Pal lobby.");
				} else {

					eventos.setText("Casilla de oso. ¿Quieres usar un pez?");

					boolean decision = mostrarVentanaOso();
					if (decision) {
						// El jugador usó un pez, puedes dejarlo quedarse
						inventario.setPeces(inventario.getPeces() - 1);
						peces_t.setText("Peces: " + inventario.getPeces());
						row = 9;
						col = 2;
						p1Position = 47;
						GridPane.setRowIndex(P1, row);
						GridPane.setColumnIndex(P1, col);
					} else {
						// Lo regresamos al inicio
						row = 0;
						col = 0;
						p1Position = 0;
						GridPane.setRowIndex(P1, row);
						GridPane.setColumnIndex(P1, col);
					}
					accionRealizada = true;
					bucle = false;
				}
			} else if (row == 9 && col == 3) { // agujeroHielo
				eventos.setText("Casilla de agujero de hielo. Nos vamos hacia atras .");
				row = 6;
				col = 2;
				p1Position = 32;
				GridPane.setRowIndex(P1, row);
				GridPane.setColumnIndex(P1, col);
				accionRealizada = true;
				bucle = false;
			}

			// Si no se realizó ninguna acción (es decir, no se cumplió ninguna condición)
			if (!accionRealizada) {
				// Actualizar la posición en el GridPane a la fila y columna originales
				GridPane.setRowIndex(P1, row);
				GridPane.setColumnIndex(P1, col);
				bucle = false; // Aquí finaliza el turno si no hubo acción
			}

			// Si se realizó alguna acción (se movió el trineo), reiniciar 'moverDeTrineo'
			// para la siguiente tirada
			if (accionRealizada) {
				moverDeTrineo = false; // Reinicia para permitir lanzar el dado nuevamente
				accionRealizada = false; // Reinicia la acción
			}
		}

	}

	@FXML
	private void handleRapido(ActionEvent event) {

		if (inventario.getDadoRapido() > 0) {
			Random rand = new Random();
			int diceResult = rand.nextInt(6) + 5;

			// Update the Text
			dadoResultText.setText("Ha salido: " + diceResult);
			inventario.setDadoRapido(inventario.getDadoRapido() - 1);
			rapido_t.setText("Dado rápido: " + inventario.getDadoRapido());

			// Update the position
			moveP1(diceResult);
		} else {
			eventos.setText("No tienes dados rapidos.");
		}

	}

	@FXML
	private void handleLento(ActionEvent event) {

		if (inventario.getDadoLentos() > 0) {
			Random rand = new Random();
			int diceResult = rand.nextInt(3) + 1;

			// Update the Text
			dadoResultText.setText("Ha salido: " + diceResult);
			inventario.setDadoLentos(inventario.getDadoLentos() - 1);
			lento_t.setText("Dado lento: " + inventario.getDadoLentos());

			// Update the position
			moveP1(diceResult);
		} else {
			eventos.setText("No tienes dados lentos.");
		}
	}

	@FXML
	private void handleNieve(ActionEvent event) {
		System.out.println("Snow.");
		// TODO
	}
}