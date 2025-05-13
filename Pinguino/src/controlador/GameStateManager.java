package controlador;

import java.sql.*;
import modelo.Inventario;

public class GameStateManager {
    private static final String SAVE_GAME_SQL = 
        "INSERT INTO PARTICIPACION (IDPARTICIPACION, POSICIONACTUAL, NUMPECES, NUMDADOLENTO, NUMDADORAPIDO, NUMBOLAS, IDJUGADOR, IDPARTIDA) " +
        "VALUES (SEQ_IDPARTICIPACION.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String LOAD_GAME_SQL =
        "SELECT POSICIONACTUAL, NUMPECES, NUMDADOLENTO, NUMDADORAPIDO, NUMBOLAS " +
        "FROM PARTICIPACION WHERE IDJUGADOR = ? AND IDPARTIDA = ?";
    
    private static final String CREATE_GAME_SQL =
        "INSERT INTO PARTIDA (IDPARTIDA, FECHA, HORA, ESTADO) " +
        "VALUES (SEQ_IDPARTIDA.NEXTVAL, SYSDATE, TO_CHAR(SYSTIMESTAMP, 'HH24:MI:SS'), 'EN_CURSO')";

    public static int saveGame(Connection conn, int playerId, int position, Inventario inventario) throws SQLException {
        // Crear nueva partida
        int gameId;
        try (PreparedStatement pstmt = conn.prepareStatement(CREATE_GAME_SQL, new String[]{"IDPARTIDA"})) {
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                gameId = rs.getInt(1);
            } else {
                throw new SQLException("No se pudo obtener el ID de partida");
            }
        }

        // Guardar estado del jugador
        try (PreparedStatement pstmt = conn.prepareStatement(SAVE_GAME_SQL)) {
            pstmt.setInt(1, position);
            pstmt.setInt(2, inventario.getPeces());
            pstmt.setInt(3, inventario.getDadoLentos());
            pstmt.setInt(4, inventario.getDadoRapido());
            pstmt.setInt(5, inventario.getBolasNieve());
            pstmt.setInt(6, playerId);
            pstmt.setInt(7, gameId);
            pstmt.executeUpdate();
        }

        return gameId;
    }

    public static GameState loadGame(Connection conn, int playerId, int gameId) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(LOAD_GAME_SQL)) {
            pstmt.setInt(1, playerId);
            pstmt.setInt(2, gameId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new GameState(
                    rs.getInt("POSICIONACTUAL"),
                    new Inventario(
                        rs.getInt("NUMPECES"),
                        rs.getInt("NUMDADOLENTO"),
                        rs.getInt("NUMDADORAPIDO"),
                        rs.getInt("NUMBOLAS")
                    )
                );
            }
        }
        return null;
    }
}

// Clase auxiliar para representar el estado del juego
class GameState {
    private int position;
    private Inventario inventory;

    public GameState(int position, Inventario inventory) {
        this.position = position;
        this.inventory = inventory;
    }

    // Getters
    public int getPosition() { return position; }
    public Inventario getInventory() { return inventory; }
}