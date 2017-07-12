package Players.EEVEE;

import Interface.PlayerModulePart1;
import Interface.PlayerMove;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Eugene on 7/11/2017.
 */
public class EEVEE implements PlayerModulePart1{
    //FIELDS

    //Emily's test comment

    Map<String, Vertex<String>> graph;

    /**
     * Has the player won?
     * @param playerId - player Id to test for winning path.
     * @return - true or false.
     */
    @Override
    public boolean hasWonGame(int playerId) {
        return false;
    }

    //METHODS
    /**
     * Method called at the beginning of each game to initialize the player module or reset.
     * For multiple games, only ONE instance of each PlayerModule is made.
     * @param dim - the number of nodes at an edge for a given player. the grid of nodes is of size dim (dim + 1).
     *            The board's dimensions are (2 * dim + 1)(2 * dim + 1).
     * @param playerId - player id (1 or 2)
     */
    @Override
    public void initPlayer(int dim, int playerId) {

    }

    /**
     * Method called after every move is made.  The engine will only call this after it verifies the validity of
     * the move, so we do not need to verify the move provided.  It is guaranteed to be valid.
     * @param m - most recent move
     */
    @Override
    public void lastMove(PlayerMove m) {

    }

    @Override
    public void otherPlayerInvalidated() {

    }

    @Override
    public PlayerMove move() {
        return null;
    }
}
