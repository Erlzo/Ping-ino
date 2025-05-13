package vista;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;

import controlador.GameStateManager;
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

	private Connection dbConnection;
	private int currentPlayerId;
	private int currentGameId;
	
	Scanner scanner = new Scanner(System.in);

	@FXML
	private void initialize() {
		// This method is called automatically after the FXML is loaded
		// You can set initial values or add listeners here
		inventario = new Inventario();
		eventos.setText("¡El juego ha comenzado!");
		
	    // Obtener conexión (deberías pasar esto desde pantallaPrincipalController)
	    try {
	        dbConnection = bbdd.conectarBaseDatos();
	    } catch (Exception e) {
	        mostrarError("Error al conectar con la base de datos");
	    }
	}

	// Button and menu actions

	@FXML
	private void handleNewGame() {
	    try {
	        // Reiniciar estado del juego
	        p1Position = 0;
	        inventario = new Inventario();
	        GridPane.setRowIndex(P1, 0);
	        GridPane.setColumnIndex(P1, 0);
	        
	        // Crear nueva partida en la base de datos
	        currentGameId = GameStateManager.saveGame(dbConnection, currentPlayerId, p1Position, inventario);
	        eventos.setText("Nueva partida creada!");
	    } catch (SQLException e) {
	        mostrarError("Error al crear nueva partida");
	    }
	}

	@FXML
	private void handleSaveGame() {
		 if (dbConnection == null) {
		        mostrarError("No hay conexión a la base de datos");
		        return;
		    }

		    String sql;
		    if (currentGameId == 0) { // Nueva partida
		        sql = "BEGIN " +
		              "INSERT INTO PARTIDA (IDPARTIDA, FECHA, HORA, ESTADO) " +
		              "VALUES (SEQ_IDPARTIDA.NEXTVAL, SYSDATE, TO_CHAR(SYSTIMESTAMP, 'HH24:MI:SS'), 'EN_CURSO') " +
		              "RETURNING IDPARTIDA INTO ?; " +
		              "INSERT INTO PARTICIPACION (IDPARTICIPACION, POSICIONACTUAL, NUMPECES, NUMDADOLENTO, NUMDADORAPIDO, NUMBOLAS, IDJUGADOR, IDPARTIDA) " +
		              "VALUES (SEQ_IDPARTICIPACION.NEXTVAL, ?, ?, ?, ?, ?, ?, ?); " +
		              "END;";
		    } else { // Actualizar partida existente
		        sql = "UPDATE PARTICIPACION SET " +
		              "POSICIONACTUAL = ?, NUMPECES = ?, NUMDADOLENTO = ?, NUMDADORAPIDO = ?, NUMBOLAS = ? " +
		              "WHERE IDJUGADOR = ? AND IDPARTIDA = ?";
		    }

		    try (PreparedStatement pstmt = dbConnection.prepareStatement(sql)) {
		        if (currentGameId == 0) {
		            pstmt.registerOutParameter(1, Types.INTEGER);
		            pstmt.setInt(2, p1Position);
		            pstmt.setInt(3, inventario.getPeces());
		            pstmt.setInt(4, inventario.getDadoLentos());
		            pstmt.setInt(5, inventario.getDadoRapido());
		            pstmt.setInt(6, inventario.getBolasNieve());
		            pstmt.setInt(7, currentPlayerId);
		            pstmt.setInt(8, currentGameId);
		            pstmt.execute();
		            currentGameId = pstmt.getInt(1); // Obtener el ID generado
		        } else {
		            pstmt.setInt(1, p1Position);
		            pstmt.setInt(2, inventario.getPeces());
		            pstmt.setInt(3, inventario.getDadoLentos());
		            pstmt.setInt(4, inventario.getDadoRapido());
		            pstmt.setInt(5, inventario.getBolasNieve());
		            pstmt.setInt(6, currentPlayerId);
		            pstmt.setInt(7, currentGameId);
		            pstmt.executeUpdate();
		        }
		        eventos.setText("Partida guardada correctamente");
		    } catch (SQLException e) {
		        mostrarError("Error al guardar partida: " + e.getMessage());
		        e.printStackTrace();
		    }
	}

	@FXML
	private void handleLoadGame() {
		 try {
		        String sql = "SELECT p.IDPARTIDA, TO_CHAR(p.FECHA, 'DD/MM/YY HH24:MI') AS FECHA_FORMATEADA " +
		                     "FROM PARTIDA p JOIN PARTICIPACION par ON p.IDPARTIDA = par.IDPARTIDA " +
		                     "WHERE par.IDJUGADOR = ? ORDER BY p.FECHA DESC";
		        
		        List<PartidaGuardada> partidas = new ArrayList<>();
		        
		        try (PreparedStatement pstmt = dbConnection.prepareStatement(sql)) {
		            pstmt.setInt(1, currentPlayerId);
		            ResultSet rs = pstmt.executeQuery();
		            
		            while (rs.next()) {
		                partidas.add(new PartidaGuardada(
		                    rs.getInt("IDPARTIDA"),
		                    rs.getString("FECHA_FORMATEADA")
		                ));
		            }
		        }
		        
		        // Mostrar diálogo con las partidas disponibles
		        if (!partidas.isEmpty()) {
		            Optional<PartidaGuardada> seleccionada = mostrarDialogoCarga(partidas);
		            seleccionada.ifPresent(this::cargarPartidaSeleccionada);
		        } else {
		            eventos.setText("No hay partidas guardadas");
		        }
		    } catch (SQLException e) {
		        mostrarError("Error al cargar partidas");
		    }
	}
	
	private Optional<PartidaGuardada> mostrarDialogoCarga(List<PartidaGuardada> partidas) {
	    Dialog<PartidaGuardada> dialog = new Dialog<>();
	    dialog.setTitle("Cargar Partida");
	    
	    ListView<PartidaGuardada> listView = new ListView<>();
	    listView.getItems().addAll(partidas);
	    
	    dialog.getDialogPane().setContent(listView);
	    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
	    
	    dialog.setResultConverter(buttonType -> {
	        if (buttonType == ButtonType.OK) {
	            return listView.getSelectionModel().getSelectedItem();
	        }
	        return null;
	    });
	    
	    return dialog.showAndWait();
	}

	private void cargarPartidaSeleccionada(PartidaGuardada partida) {
	    String sql = "SELECT POSICIONACTUAL, NUMPECES, NUMDADOLENTO, NUMDADORAPIDO, NUMBOLAS " +
	                 "FROM PARTICIPACION WHERE IDJUGADOR = ? AND IDPARTIDA = ?";
	    
	    try (PreparedStatement pstmt = dbConnection.prepareStatement(sql)) {
	        pstmt.setInt(1, currentPlayerId);
	        pstmt.setInt(2, partida.getId());
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            currentGameId = partida.getId();
	            p1Position = rs.getInt("POSICIONACTUAL");
	            inventario = new Inventario(
	                rs.getInt("NUMPECES"),
	                rs.getInt("NUMDADOLENTO"),
	                rs.getInt("NUMDADORAPIDO"),
	                rs.getInt("NUMBOLAS")
	            );
	            actualizarUI();
	            eventos.setText("Partida cargada: " + partida.getFecha());
	        }
	    } catch (SQLException e) {
	        mostrarError("Error al cargar partida seleccionada");
	    }
	}
	
	class PartidaGuardada {
	    private int id;
	    private String fecha;
	    
	    public PartidaGuardada(int id, String fecha) {
	        this.id = id;
	        this.fecha = fecha;
	    }
	    
	    @Override
	    public String toString() {
	        return "Partida del " + fecha;
	    }
	    
	    // Getters
	    public int getId() { return id; }
	    public String getFecha() { return fecha; }
	}
	
	public void inicializarDatosJugador(int playerId, Connection dbConnection) {
	    this.currentPlayerId = playerId;
	    this.dbConnection = dbConnection;
	    cargarPartidaReciente(); // Opcional: cargar automáticamente la última partida
	}
	
	private void cargarPartidaReciente() {
	    String sql = "SELECT p.IDPARTIDA, par.POSICIONACTUAL, par.NUMPECES, par.NUMDADOLENTO, par.NUMDADORAPIDO, par.NUMBOLAS " +
	                 "FROM PARTIDA p JOIN PARTICIPACION par ON p.IDPARTIDA = par.IDPARTIDA " +
	                 "WHERE par.IDJUGADOR = ? AND p.ESTADO = 'EN_CURSO' ORDER BY p.FECHA DESC";
	    
	    try (PreparedStatement pstmt = dbConnection.prepareStatement(sql)) {
	        pstmt.setInt(1, currentPlayerId);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            currentGameId = rs.getInt("IDPARTIDA");
	            p1Position = rs.getInt("POSICIONACTUAL");
	            inventario = new Inventario(
	                rs.getInt("NUMPECES"),
	                rs.getInt("NUMDADOLENTO"),
	                rs.getInt("NUMDADORAPIDO"),
	                rs.getInt("NUMBOLAS")
	            );
	            actualizarUI();
	            eventos.setText("Partida cargada automáticamente");
	        } else {
	            eventos.setText("No hay partidas guardadas. Comienza una nueva.");
	        }
	    } catch (SQLException e) {
	        mostrarError("Error al cargar partida reciente");
	    }
	}
	
	private void actualizarUI() {
	    // Actualizar posición del jugador
	    int row = p1Position / COLUMNS;
	    int col = p1Position % COLUMNS;
	    GridPane.setRowIndex(P1, row);
	    GridPane.setColumnIndex(P1, col);
	    
	    // Actualizar inventario
	    rapido_t.setText("Dado rápido: " + inventario.getDadoRapido());
	    lento_t.setText("Dado lento: " + inventario.getDadoLentos());
	    peces_t.setText("Peces: " + inventario.getPeces());
	    nieve_t.setText("Bolas de nieve: " + inventario.getBolasNieve());
	}
	
	private void mostrarError(String mensaje) {
	    Alert alert = new Alert(Alert.AlertType.ERROR);
	    alert.setTitle("Error");
	    alert.setHeaderText(null);
	    alert.setContentText(mensaje);
	    alert.showAndWait();
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
				eventoAleatorio();
				accionRealizada = true;
				bucle = false;
			} else if (row == 0 && col == 4) { // trineo
				if (!moverDeTrineo) {
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
			}

			// fila 2
			if (row == 2 && col == 1) { // trineo
				if (!moverDeTrineo) {
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
				eventoAleatorio();
				accionRealizada = true;
				bucle = false;
			}

			// fila 3
			if (row == 3 && col == 0) { // agujeroHielo
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
				if (inventario.getPeces() < 0) {
					eventos.setText("Casilla de oso. No tienes peces. Pal lobby.");
				} else {

					eventos.setText("Casilla de oso. ¿Quieres usar un pez?");

					boolean decision = mostrarVentanaOso();
					if (decision) {
						// El jugador usó un pez, puedes dejarlo quedarse
						inventario.setPeces(inventario.getPeces() - 1);
						peces_t.setText("Peces: " + inventario.getPeces());

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
				eventoAleatorio();
				accionRealizada = true;
				bucle = false;
			} else if (row == 5 && col == 4) { // trineo
				if (!moverDeTrineo) {
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
				if (inventario.getPeces() < 0) {
					eventos.setText("Casilla de oso. No tienes peces. Pal lobby.");
				} else {

					eventos.setText("Casilla de oso. ¿Quieres usar un pez?");

					boolean decision = mostrarVentanaOso();
					if (decision) {
						// El jugador usó un pez, puedes dejarlo quedarse
						inventario.setPeces(inventario.getPeces() - 1);
						peces_t.setText("Peces: " + inventario.getPeces());

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
					accionRealizada = true;
					moverDeTrineo = true; // Activa el movimiento al siguiente trineo
				} else {
					bucle = false; // Termina el turno después de moverse
				}
			} else if (row == 8 && col == 2) { // eventoAleatorio
				eventoAleatorio();
				accionRealizada = true;
				bucle = false;
			}

			// fila 9
			if (row == 9 && col == 2) { // oso

				if (inventario.getPeces() < 0) {
					eventos.setText("Casilla de oso. No tienes peces. Pal lobby.");
				} else {

					eventos.setText("Casilla de oso. ¿Quieres usar un pez?");

					boolean decision = mostrarVentanaOso();
					if (decision) {
						// El jugador usó un pez, puedes dejarlo quedarse
						inventario.setPeces(inventario.getPeces() - 1);
						peces_t.setText("Peces: " + inventario.getPeces());
						
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
