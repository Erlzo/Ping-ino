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
import java.sql.ResultSet;
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
        String username = userField.getText();
        String password = passField.getText();

        System.out.println("Login pressed: " + username + " / " + password);

        if (!username.isEmpty() && !password.isEmpty()) {
            String url = "jdbc:oracle:thin:@oracle.ilerna.com:1521/XEPDB2";
            
            try {
                // Conexión directa (como en tu versión original)
                Connection conn = DriverManager.getConnection(url, username, password);
                System.out.println("✅ Conexión exitosa a Oracle!");
                
                // Pasar a pantalla de juego (como en tu versión original)
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/pantallaJuego.fxml"));
                Parent pantallaJuegoRoot = loader.load();

                // Obtener ID del jugador
                int playerId = obtenerIdJugador(conn, username);
                
                // Pasar datos al controlador del juego
                pantallaJuegoController juegoController = loader.getController();
                juegoController.inicializarDatosJugador(playerId, conn);

                Scene pantallaJuegoScene = new Scene(pantallaJuegoRoot);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(pantallaJuegoScene);
                stage.setTitle("Pantalla de Juego");
                stage.setFullScreen(true);
                stage.setResizable(false);
                
            } catch (SQLException e) {
                System.out.println("❌ Error al conectar:");
                e.printStackTrace();
                mostrarAlert("Error", "Credenciales incorrectas", AlertType.ERROR);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Por favor ingrese usuario y contraseña.");
        }
    }

    private int obtenerIdJugador(Connection conn, String username) throws SQLException {
        String sql = "SELECT IDJUGADOR FROM JUGADOR WHERE NOMBRE = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt("IDJUGADOR") : -1;
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