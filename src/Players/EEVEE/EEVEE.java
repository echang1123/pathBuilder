/*
TO DO: make separate Dijkstra function- use in move method and segments to victory method

for each move, check your segments to victory and the opponents, and if there is overlap go there- otherwise if you have
fewer segments to victory pick one and move, otherwise if they have fewer segments to victory pick one and block them instead

 */

package Players.EEVEE;

import Interface.Coordinate;
import Interface.PlayerModulePart1;
import Interface.PlayerModulePart2;
import Interface.PlayerMove;

import java.util.*;

/**
 * Created by Eugene on 7/11/2017.
 *
 * Team EEVEE- Emily Wesson and Eugene Chang
 *
 */
public class EEVEE implements PlayerModulePart1, PlayerModulePart2 {

    //FIELDS

    //graph represents game board
    private Map<Coordinate, Vertex<Integer>> graph;

    //board dimension
    private int dimension;

    //current number of moves made
    private int numMovesMade;

    private static int PLAYER_ID;
    private static int OPPONENT_ID;

    //constructor
    public EEVEE() {
    }

    /**
     * Has the player won?
     *
     * @param playerId - player Id to test for winning path.
     * @return - true or false.
     */
    @Override
    public boolean hasWonGame(int playerId) {
        HashMap<Vertex, Tuple> paths = runDijkstra(graph, playerId);
        Vertex end;
        Tuple endTuple;

        if (playerId == 1) {
            end = graph.get(new Coordinate(2 * dimension - 1, 2 * dimension));
            endTuple = (Tuple) paths.get(end);
            return endTuple.getWeight() == 0;
        }
        //if Player2 won
        else {
            end = graph.get(new Coordinate(2 * dimension, 2 * dimension - 1));
            endTuple = (Tuple) paths.get(end);
            return endTuple.getWeight() == 0;
        }
    }

    private HashMap<Vertex, Tuple> runDijkstra(Map<Coordinate, Vertex<Integer>> graph, int playerID) {
        int otherPlayer = 3-playerID;

        //Hashmap --> (key [vertex], value [tuple: prev vertex, weight from origin])
        HashMap paths = new HashMap<Vertex, Tuple>();
        Set<Coordinate> graphKeys = graph.keySet();
        List vertices = new ArrayList<Vertex>();
        Vertex start = new Vertex(0);

        //make a hashmap with all of the vertices and set initial prevs & weights & create all vertices
        for (Coordinate c : graphKeys) {
            Vertex v = graph.get(c);
            Tuple tuple = new Tuple(null, Integer.MAX_VALUE, c);
            if (playerID == 1 && c.getRow() == 1 && c.getCol() == 0) {
                //for start coordinate for P1, set prev to self & weight to 0
                tuple = new Tuple(v, 0, c);
                start = v;
            }
            else if (playerID == 2 && c.getRow() == 0 && c.getCol() == 1) {
                tuple = new Tuple(v, 0, c);
                start = v;
            }
            paths.put(v, tuple);
            //add playerID and un-owned vertices, excluding vertices belonging to the other player
            if((Integer)v.getData() != otherPlayer) {
                vertices.add(v);
            }
        }
        boolean first = true;
        while (!vertices.isEmpty()) {
            Vertex currentVertex;
            if (first) {
                currentVertex = start;
                first = false;
            }
            else {
                currentVertex = closestVertex(vertices, paths);
            }

            vertices.remove(currentVertex); //remove/finalize currentVertex
            List<Edge> currentNeighbors = currentVertex.getNeighbors();
            Tuple currentVertexTuple = (Tuple) paths.get(currentVertex);

            for (Edge e : currentNeighbors) {
                Vertex neighbor = e.getTo();
                Tuple neighborTuple = (Tuple) paths.get(neighbor);
                int neighborCurrentWeight = neighborTuple.getWeight();
                int weightViaCurrentVertex = currentVertexTuple.getWeight() + e.getWeight();

                if (weightViaCurrentVertex < neighborCurrentWeight) {
                    Tuple neighborShorterPath = new Tuple(currentVertex, weightViaCurrentVertex, neighborTuple.getCoordinate());
                    paths.put(neighbor, neighborShorterPath);
                }
            }
        }
        return paths;
    }

    /**
     * Given a list of vertices, which are keys in hashamp paths, use the list of vertices to
     * compare each entry in the hashmap and return the vertex with the lowest weight
     *
     * @param vertices list of vertices to search
     * @param paths    hashmap with [Key-vertex, Value- tuple of(prev vertex, weight, coordinate)]
     * @return next closest vertex
     */
    private Vertex closestVertex(List<Vertex> vertices, Map<Vertex, Tuple> paths) {
        Vertex min = vertices.get(0);
        int currentMinWeight = paths.get(min).getWeight();
        for (Vertex v : vertices) {
            int currentWeight = paths.get(v).getWeight();
            if (currentMinWeight > currentWeight) {
                min = v;
                currentMinWeight = currentWeight;
            }
        }
        return min;
    }

    /**
     * Method called at the beginning of each game to initialize the player module or reset.
     * For multiple games, only ONE instance of each PlayerModule is made.
     *
     * @param dim - the number of nodes at an edge for a given player. the grid of nodes is of size dim (dim + 1).
     *            The board's dimensions are (2 * dim + 1)(2 * dim + 1).
     * @param playerId - player id (1 or 2)
     */
    @Override
    public void initPlayer(int dim, int playerId) {
        graph = new HashMap<>();
        dimension = dim;
        PLAYER_ID = playerId;
        OPPONENT_ID = 3-playerId;
        for (int row = 0; row < 2 * dim + 1; row++) {
            for (int col = 0; col < 2 * dim + 1; col++) {
                Coordinate coordinate = new Coordinate(row, col);
                //corners will be -1, P1/P2 vertices will be 1 or 2, & unowned vertices will be 0
                Vertex v = new Vertex(-1);
                if (!atCorner(row, col)) {
                    //top or bottom rows
                    if (row == 0 || row == 2 * dim) {
                        //top and bottoms rows all belong to P2
                        v.setData(2);
                        if (col != 1) {
                            //get the vertex left of the current vertex
                            Vertex leftNeighbor = graph.get(new Coordinate(row, col - 1));
                            v.addNeighbor(leftNeighbor, 0);
                        }
                        if (row == 2 * dim && col % 2 == 1) {
                            Vertex topNeighbor = graph.get(new Coordinate(row - 1, col));

                            //topNeighbors are unowned at start
                            v.addNeighbor(topNeighbor, 1);
                        }
                    }
                    else if (col == 0 || col == 2 * dim) { //LEFT OR RIGHT COLUMNS
                        v.setData(1); //R and L column owned by P1
                        // we don't want to connect to top L or top R corners
                        if (row != 1) {
                            Vertex topNeighbor = graph.get(new Coordinate(row - 1, col));
                            v.addNeighbor(topNeighbor, 0);
                        }
                        if (col == 2 * dim && row % 2 == 1) {
                            Vertex leftNeighbor = graph.get(new Coordinate(row, col - 1));
                            v.addNeighbor(leftNeighbor, 1);
                        }
                    }
                    //not in top/bottom row, or right/left column (inner vertices)
                    else {
                        Vertex topNeighbor = graph.get(new Coordinate(row - 1, col));
                        Vertex leftNeighbor = graph.get(new Coordinate(row, col - 1));

                        //odd rows & even columns (P1 owned vertex)
                        if (row % 2 == 1 && col % 2 == 0) {
                            v.setData(1);
                        }
                        //even rows & odd columns (P2 owned vertex)
                        else if (row % 2 == 0 && col % 2 == 1) {
                            v.setData(2);
                        }
                        //unowned vertex
                        else {
                            v.setData(0);
                        }

                        int vertexOwner = (Integer) v.getData();
                        //vertex is in row 1, and it is owned (P1)
                        if (row == 1 && vertexOwner != 0) {
                            v.addNeighbor(leftNeighbor, 1);
                        }
                        else if (col == 1 && vertexOwner != 0) {
                            v.addNeighbor(topNeighbor, 1);
                        }
                        else {
                            v.addNeighbor(topNeighbor, 1);
                            v.addNeighbor(leftNeighbor, 1);
                        }
                    }
                }
                graph.put(coordinate, v);
            }
        }
    }

    private boolean atCorner(int row, int col){
        return (row == 0 && col == 0) || //top left
                (row == 0 && col == 2 * dimension) || //top right
                (row == 2 * dimension && col == 0) || //bottom left
                (row == 2 * dimension && col == 2 * dimension); //bottom right
    }

    /**
     * Method called after every move is made.  The engine will only call this after it verifies the validity of
     * the move, so we do not need to verify the move provided.  It is guaranteed to be valid.
     *
     * @param m - most recent move
     */
    @Override
    public void lastMove(PlayerMove m) {
        numMovesMade++;
        int player = m.getPlayerId();

        //get move vertex to update
        Vertex move = graph.get(m.getCoordinate());

        //update owner
        move.setData(player);
        updateEdges(move);

        //put newly updated vertex back into graph/board representation
        graph.put(m.getCoordinate(), move);
        if(hasWonGame(1)||hasWonGame(2)){ //if game is over, reset numMovesMade
            numMovesMade = 0;
        }
    }

    /**
     * Given a vertex, update all edges to vertex from neighbors and all edges from vertex to neighbors- if vertex
     * and neighbor vertex have same player owner, update weight to 0, otherwise update weight to 100000
     *
     * @param v Vertex being updated
     */
    private void updateEdges(Vertex v) {
        List<Edge> vNeighbors = v.getNeighbors();
        //loop through neighbors and update edge weights
        for (Edge e : vNeighbors) {
            Vertex neighbor = e.getTo();
            List<Edge> neighborNeighbors = neighbor.getNeighbors();
            if (neighbor.getData() == v.getData()) {
                e.setWeight(0);
                for (Edge neighborEdge : neighborNeighbors) {
                    Vertex n = neighborEdge.getTo();
                    if (n.equals(v)) {
                        neighborEdge.setWeight(0);
                    }
                }
            }
            else {
                e.setWeight(100000); //if neighbor belongs to other player, weight to max
                for (Edge neighborEdge : neighborNeighbors) {
                    Vertex n = neighborEdge.getTo();
                    if (n.equals(v)) {
                        neighborEdge.setWeight(10000);
                    }
                }
            }
        }
    }

    /**May or may not need to implement.  Indicates that the other player has been invalidated.*/
    @Override
    public void otherPlayerInvalidated() {

    }

    /** Generates the next move for this player.
     * @return - a PlayerMove object representing the next move.
     */
    @Override
    public PlayerMove move() {
        HashMap<Vertex, Tuple> paths = runDijkstra(graph, PLAYER_ID);
        ArrayList<PlayerMove> myPathToVictory = pathToVictory(PLAYER_ID, paths);

        //IF OTHER PLAYER INVALIDATED, JUST PICK NEXT AVAILABLE MOVE ON OWN PATH TO VICTORY
        HashMap<Vertex, Tuple> opponentPaths = runDijkstra(graph, OPPONENT_ID);
        ArrayList<PlayerMove> opponentPathToVictory = pathToVictory(OPPONENT_ID, opponentPaths);

        List legalMoves = allLegalMoves();
        ArrayList<PlayerMove> availableMoves = new ArrayList<>();

        for(int i=0; i<myPathToVictory.size(); i++){ //if pathToVictory overlap exists make that move
            PlayerMove move = myPathToVictory.get(i);
            Vertex v = graph.get(move.getCoordinate());
            if(((Integer) v.getData() == 0) && legalMoves.contains(move)){ //Vertex is unowned and is a legal move

                //opponent's list of PlayerMoves contain their ID
                PlayerMove oppMove = new PlayerMove(move.getCoordinate(), OPPONENT_ID);
                if(opponentPathToVictory.contains(oppMove)){
                    return move;
                }
                availableMoves.add(move); //avoid re-checking owner = 0 & legal move if no overlap
            }
        }
        PlayerMove myMove = availableMoves.get(availableMoves.size()-1); //this is an available/legal move from myPathToVictory

        // no overlap in pathsToVictory and opponent is closer to winning - move to block them
        if(fewestSegmentsToVictory(PLAYER_ID) > fewestSegmentsToVictory(OPPONENT_ID)){
            for(int i = 0; i<opponentPathToVictory.size(); i++){
                PlayerMove oppMove = opponentPathToVictory.get(i);
                PlayerMove move = new PlayerMove(oppMove.getCoordinate(), PLAYER_ID);
                Vertex v = graph.get(oppMove.getCoordinate());
                if(((Integer) v.getData() == 0) && legalMoves.contains(move)){
                    return move; //block first available/legal move on opponent's path
                }
            }
        }
        return myMove;
    }

    private ArrayList<PlayerMove> pathToVictory(int PlayerID, HashMap<Vertex, Tuple> paths){
        ArrayList roadToVictory = new ArrayList<PlayerMove>();
        Tuple endTuple = getEndTuple(PlayerID, paths);
        Coordinate start;
        if(PlayerID == 1){
            start = new Coordinate(1,0);
        }
        else{
            start = new Coordinate(0,1);
        }

        Tuple last = endTuple;
        while(!(last.getCoordinate().equals(start))){
            PlayerMove m = new PlayerMove(last.getCoordinate(), PlayerID);
            roadToVictory.add(m);
            last = getPrev(paths, last);
        }
        return roadToVictory;
    }


    /** Generate all legal moves for the given player's turn given the current game configuration.
     * @return - a List of all legal PlayerMove objects. They do not have to be in any particular order.
     */
    @Override
    public List allLegalMoves() {
        List legalMoves = new ArrayList<PlayerMove>();
        //set of all the keys (Coordinates) in graph
        Set<Coordinate> graphKeys = graph.keySet();
        boolean player1 = true;
        for (Coordinate c : graphKeys) {
            Vertex v = graph.get(c);
            if (((Integer)v.getData() == 0) ){
                PlayerMove move = new PlayerMove(c, PLAYER_ID);
                legalMoves.add(move);
            }
        }
        return legalMoves;
    }


    /**Given that a winning path still exists, computes fewest number of segments needed to win.
     *
     * @param i - playerId
     * @return - the fewest number of segments to add to complete a path
     */
    @Override
    public int fewestSegmentsToVictory(int i) {
        //run dijkstra based on player
        //based on end vertex for player, get final edge weight and divide by 2
        HashMap<Vertex, Tuple> paths = runDijkstra(graph, i);
        Vertex end;
        Tuple endTuple;
        if (i == 1) {
            endTuple = getEndTuple(1, paths);
            return endTuple.getWeight() / 2;
        }
        else { //checking for Player2
            endTuple = getEndTuple(2, paths);
            return endTuple.getWeight() / 2;
        }
    }

    private Tuple getPrev(HashMap<Vertex, Tuple> paths, Tuple t){
        return paths.get(t.getVertex());
    }

    private Tuple getEndTuple(int PlayerID, HashMap<Vertex, Tuple> paths){
        Vertex end;
        Tuple endTuple;
        if(PlayerID == 1){
            end = graph.get(new Coordinate(2 * dimension - 1, 2 * dimension));
            endTuple = (Tuple) paths.get(end);
        }
        else{
            end = graph.get(new Coordinate(2 * dimension, 2 * dimension - 1));
            endTuple = (Tuple) paths.get(end);
        }
        return endTuple;
    }

    private int myPlayerID(){
        if(numMovesMade % 2 == 0){
            return 1;
        }
        else{
            return 2;
        }
    }
}