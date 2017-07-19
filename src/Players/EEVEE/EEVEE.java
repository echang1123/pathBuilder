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
    int dimension;

//    public EEVEE(int dim){
//        graph = new HashMap<>();
//        for(int r = 0; r < 2*dim+1; r++){
//            for(int c = 0; c < 2*dim+1; c++){
//                Vertex v = new Vertex(0);
//                Coordinate co = new Coordinate(r, c);
//                if(r % 2 == 1 && c % 2 == 0){
//                    v.setData(1);
//                }
//                else if(r % 2 == 0 && c % 2 == 1){
//                    v.setData(2);
//                }
//                graph.put(co, v);
//            }
//        }
//    }
    /**
     * Has the player won?
     * @param playerId - player Id to test for winning path.
     * @return - true or false.
     */
    @Override
    public boolean hasWonGame(int playerId) {
        boolean hasWon = false;
        //will need to check for a path from one side to the other based on the player's ID- start w/ row 0 vs. col 0
        //for player 1- Left to Right, player 2 Top to Bottom
        //need to use back-tracking? follow a given edge path of weight 0 until all of the surrounding edges are 1,
        // then no more moves connected to that vertex so then check if at the last row or column
        if(playerId == 1){
            for(int i=0; i<2*dimension+1; i++){
                Coordinate c = new Coordinate(i,2*dimension);
                Vertex v = graph.get(c);
                //go through the list of neighbors- if all of the edge weights are 1 then it is not connected to anything

            }

            //for each vertex in the last column belonging to player 1, check if it is connected to any of the 'space' edges
        }
        else{ //playerId is 2, check top to bottom
            //for each vertex in the bottom row, belonging to player 2- check if any are connected to a 'space' edge

        }
        return hasWon;
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
    public void initPlayer(int dim, int playerId) {/*wasn't sure if we NEED to use playerId*/
        graph = new HashMap<>();
        dimension = dim;

        for (int row = 0; row < 2 * dim + 1; row++) {
            for (int col = 0; col < 2 * dim + 1; col++) {
                Coordinate coordinate = new Coordinate(row, col);
                Vertex v = new Vertex(-1); //default vertex data- corners will have -1, P1/P2 vertices will be 1 or 2, unowned will be 0
                //DO CORNERS EVEN NEED A VERTEX? maybe to avoid null pointer? use try/catch?
                boolean atCorner = (row==0 && col==0) || //top left
                        (row==0 && col==2*dimension+1) || //top right
                        (row==2*dimension+1 && col==0)|| //bottom left
                        (row==2*dimension+1 && col==2*dimension+1); //botom right
                if(!atCorner){
                    if(row==0 || row==2*dim){ //TOP OR BOTTOM ROWS
                        v.setData(2); //top and bottoms rows all belong to P2
                        if(col!=1){ //add left neighbors but not 2 left corners
                            Vertex leftNeighbor = graph.get(new Coordinate(row, col-1)); //get vertex to left of current (v)
                            v.addNeighbor(leftNeighbor, 0); //connect w/ weight of 0
                        }
                        if(row==2*dim && col%2==1){//also need to add top neighbor for bottom row, but only for unowned spots (don't connect to P1 owned vertices in second to last row)
                            Vertex topNeighbor = graph.get(new Coordinate(row-1, col));
                            v.addNeighbor(topNeighbor, 1); //topNeighbors are unowned at start
                        }
                    }
                    else if(col==0 || col==2*dim){ //LEFT OR RIGHT COLUMNS
                        // THIS IS JUST FLIPPING LAST IF BLOCK BY ROW/CORNER REFERENCES, IS THERE EASIER WAY?
                        v.setData(1); //R and L column owned by P1
                        if(row!=1){ //don't want to connect to top L or top R corners
                            Vertex topNeighbor = graph.get(new Coordinate(row-1, col)); //CAN WE MAKE ALL NEIGHBORS NEEDED EARLIER/NOT REPEAT?
                            v.addNeighbor(topNeighbor, 0);
                        }
                        if(col==2*dim && row%2==1){
                            Vertex leftNeighbor = graph.get(new Coordinate(row, col-1));
                            v.addNeighbor(leftNeighbor, 1);
                        }

                    }
                    else{ //not in top/bottom row, or right/left column = inner vertices
                        Vertex topNeighbor = graph.get(new Coordinate(row-1, col));
                        Vertex leftNeighbor = graph.get(new Coordinate(row, col-1));
                        if(row%2==1 && col%2==0){ //odd rows+even columns = P1 owned vertex
                            v.setData(1);
                        }
                        else if(row%2==0 && col%2==1){ //even rows+odd columns = P2 owned vertex
                            v.setData(2);
                        }
                        else{
                            v.setData(0); //unowned vertex
                        }

                        int vertexOwner = (Integer) v.getData();
                        if(row==1 && vertexOwner !=0){ //vertex is in row 1, and it is owned (P1)
                            v.addNeighbor(leftNeighbor, 1);
                        }
                        else if(col==1 && vertexOwner!=0){
                            v.addNeighbor(topNeighbor, 1);
                        }
                        else{ //gets top and left neighbor
                            v.addNeighbor(topNeighbor, 1);
                            v.addNeighbor(leftNeighbor, 1);
                        }
                    }
                }
                graph.put(coordinate, v);  //put into graph- coord, vertex
            }
        }

















        //OLD initPlayer
//        for (int r = 0; r < 2 * dim + 1; r++) {
//            for (int c = 0; c < 2 * dim + 1; c++) {
//                Vertex v = new Vertex(0);
//                Coordinate co = new Coordinate(r, c);
//                graph.put(co, v);
//            }
//        }
//        for (int r = 0; r < 2 * dim + 1; r++) {
//            for (int c = 0; c < 2 * dim + 1; c++) {
//                Coordinate co = new Coordinate(r, c);
//                if (r % 2 == 1 && c % 2 == 0) {
//                    graph.get(co).setData(1);
//                } else if (r % 2 == 0 && c % 2 == 1) {
//                    graph.get(co).setData(2);
//                }
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
        Vertex v = graph.get(m.getCoordinate());
        v.setData(m.getPlayerId());
        graph.put(m.getCoordinate(), v);
        System.out.println("Player: " + Integer.toString(m.getPlayerId()) + ", " + m.getCoordinate());
    }

    @Override
    public void otherPlayerInvalidated() {

    }

    @Override
    public PlayerMove move() {
        return null;
    }
}
