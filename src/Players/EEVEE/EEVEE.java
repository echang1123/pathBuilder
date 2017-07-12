package Players.EEVEE;

import Interface.Coordinate;
import Interface.PlayerModulePart1;
import Interface.PlayerMove;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Eugene on 7/11/2017.
 */
public class EEVEE implements PlayerModulePart1{
    //FIELDS
    Map<Coordinate, Vertex<Integer>> graph;

    public EEVEE(int dim){
        graph = new HashMap<>();
        for(int r = 0; r < 2*dim+1; r++){
            for(int c = 0; c < 2*dim+1; c++){
                Vertex v = new Vertex(0);
                Coordinate co = new Coordinate(r, c);
                if(r % 2 == 1 && c % 2 == 0){
                    v.setData(1);
                }
                else if(r % 2 == 0 && c % 2 == 1){
                    v.setData(2);
                }
                graph.put(co, v);
            }
        }
    }
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
//        for(int r = 0; r < 2*dim+1; r++){
//            for(int c = 0; c < 2*dim+1; c++){
//
//            }
//        }
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
