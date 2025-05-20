package vista;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.scene.Node;

import controlador.bbdd;

public class pantallaPrincipalController {

    @FXML private MenuItem newGame;
    @FXML private MenuItem saveGame;
    @FXML private MenuItem loadGame;
    @FXML private MenuItem quitGame;

    @FXML private TextField userField;
    @FXML private PasswordField passField;

    @FXML private Button loginButton;
    @FXML private Button registerButton;

    String username;
    String password;
    
    public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@FXML
    private void initialize() {
        // This method is called automatically after the FXML is loaded
        // You can set initial values or add listeners here
        System.out.println("pantallaPrincipalController initialized");
    }

    @FXML
    private void handleNewGame() {
        System.out.println("New Game clicked");
        // TODO
    }

    @FXML
    private void handleSaveGame() {
        System.out.println("Save Game clicked");
        // TODO
    }

    @FXML
    private void handleLoadGame() {
        System.out.println("Load Game clicked");
        // TODO
    }

    @FXML
    private void handleQuitGame() {
        System.out.println("Quit Game clicked");
        // TODO
        System.exit(0);
    }
    
    @FXML
    private void handleLogin(ActionEvent event) {

    	this.username = userField.getText();
        this.password = passField.getText();
        
        System.out.println("Login pressed: " + username + " / " + password);

        // Basic check (just for demo, replace with real login logic)
        if (!username.isEmpty() && !password.isEmpty()) {
           
        	String url = "jdbc:oracle:thin:@192.168.3.26:1521/XEPDB2";
           /* String url = "jdbc:oracle:thin:@oracle.ilerna.com:1521/XEPDB2";*/
           
            try {
                Connection conn = DriverManager.getConnection(url, username, password);
                System.out.println("✅ Conexión exitosa a Oracle!");
                conn.close();
            } catch (SQLException e) {
                System.out.println("❌ Error al conectar:");
                e.printStackTrace();
            }
        	
        	try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/pantallaJuego.fxml"));
                Parent pantallaJuegoRoot = loader.load();

                Scene pantallaJuegoScene = new Scene(pantallaJuegoRoot);

                // Get the current stage using the event
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(pantallaJuegoScene);
                stage.setTitle("Pantalla de Juego");
        	    stage.setFullScreen(true);
        	    stage.setResizable(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Please. Enter user and password.");
        }
    }


@FXML
public void handleRegister(ActionEvent event) throws SQLException {
    // Obtener los valores de los campos de texto
    String username = userField.getText();
    String password = passField.getText();

    if (username.isEmpty() || password.isEmpty()) {
        showAlert("Error", "El nombre de usuario y la contraseña no pueden estar vacíos.", AlertType.ERROR);
        return;
    }

    // Establecer la conexión a la base de datos
    Connection con = bbdd.conectarBaseDatos();
    
    if (con != null) {
        try {
            // Crear la sentencia SQL para insertar el nuevo usuario
            String sql = "INSERT INTO JUGADOR (NOMBRE, CONTRASEÑA) VALUES ('" + username + "', '" + password + "')";
            
            // Realizar la inserción usando la clase bbdd
            bbdd.insert(con, sql);

            // Mostrar un mensaje de éxito
            showAlert("Éxito", "Usuario registrado correctamente.", AlertType.INFORMATION);
        } finally {
            try {
                // Cerrar la conexión a la base de datos si es necesario
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

// Método para mostrar alertas
private void showAlert(String title, String message, AlertType type) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}

}