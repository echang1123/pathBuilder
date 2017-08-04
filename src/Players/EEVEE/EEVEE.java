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
    //hashmap graph represents game board
    private Map<Coordinate, Vertex<Integer>> graph;
    //dimension of board
    private int dimension;
    //current number of moves made
    private int numMovesMade;

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
            /* debug helper print statments- prints out Player 1 final path weight + board at game end
            System.out.println("END WEIGHT PLAYER 1: " + endTuple.getWeight()); //REMOVE
            System.out.println("------------");
            for(Coordinate c : graphKeys){
                System.out.println("Coordinate: " + c.toString());
                Vertex v = graph.get(c);
                List<Edge> edges = v.getNeighbors();
                for(Edge e : edges) {
                    System.out.println("----------Coordinate " + c.toString() + "Vertex " + v + "Edge weight: " + e.getWeight()); //REMOVE
                }
            }
            */
            return endTuple.getWeight() == 0;
        } else { //checking if Player2 won
            end = graph.get(new Coordinate(2 * dimension, 2 * dimension - 1));
            endTuple = (Tuple) paths.get(end);
            /* debug help- prints Player 2 final path weight at game end
            System.out.println("END WEIGHT PLAYER 2: " + endTuple.getWeight()); //REMOVE
            System.out.println("------------");
            */
            return endTuple.getWeight() == 0;

        }
    }

    private HashMap<Vertex, Tuple> runDijkstra(Map<Coordinate, Vertex<Integer>> graph, int playerID) {
        int otherPlayer = 3-playerID;
        HashMap paths = new HashMap<Vertex, Tuple>(); //new Hashmap holds (key[vertex], value[tuple: prev vertex, weight from origin])
        Set<Coordinate> graphKeys = graph.keySet(); //set of all the keys (Coordinates) in graph
        List vertices = new ArrayList<Vertex>(); //will contain a list of every vertex in graph
        Vertex start = new Vertex(0);
        for (Coordinate c : graphKeys) { //make a hashmap with all of the vertices and set initial prevs+weights, make list of all vertices
            Vertex v = graph.get(c);
            Tuple tuple = new Tuple(null, Integer.MAX_VALUE, c);
            if (playerID == 1 && c.getRow() == 1 && c.getCol() == 0) { //player1 starts at coordinate (1,0)
                tuple = new Tuple(v, 0, c); //if at start coordinate for P1, set prev to self, weight to 0
                start = v;
            } else if (playerID == 2 && c.getRow() == 0 && c.getCol() == 1) { //player 2 starts at coordinate (0,1)
                tuple = new Tuple(v, 0, c);
                start = v;
            }
            paths.put(v, tuple);
            if((Integer)v.getData() != otherPlayer) { //don't add vertices belonging to the other player, just add playerID and un-owned vertices
                vertices.add(v);
            }
        }
        boolean first = true;
        while (!vertices.isEmpty()) {
            Vertex currentVertex;
            if (first) {
                currentVertex = start;
                first = false;
            } else {
                currentVertex = closestVertex(vertices, paths); //method to return the next closest vertex from list of all vertices not yet visited
            }
            vertices.remove(currentVertex); //remove/finalize currentVertex
            List<Edge> currentNeighbors = currentVertex.getNeighbors();
            Tuple currentVertexTuple = (Tuple) paths.get(currentVertex);
            for (Edge e : currentNeighbors) {
                Vertex neighbor = e.getTo();
                Tuple neighborTuple = (Tuple) paths.get(neighbor); //WHY DOES THIS NEED TO BE CAST TO WORK?
                int neighborCurrentWeight = neighborTuple.getWeight(); //weight of neighbor vertex currently in paths hashmap
                int weightViaCurrentVertex = currentVertexTuple.getWeight() + e.getWeight(); //weight of the current vertex plus weight of edge from current to neighbor
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
     * @param dim      - the number of nodes at an edge for a given player. the grid of nodes is of size dim (dim + 1).
     *                 The board's dimensions are (2 * dim + 1)(2 * dim + 1).
     * @param playerId - player id (1 or 2)
     */
    @Override
    public void initPlayer(int dim, int playerId) {
        graph = new HashMap<>();
        dimension = dim;
        for (int row = 0; row < 2 * dim + 1; row++) {
            for (int col = 0; col < 2 * dim + 1; col++) {
                Coordinate coordinate = new Coordinate(row, col);
                Vertex v = new Vertex(-1); //default vertex data- corners will have -1, P1/P2 vertices will be 1 or 2, unowned will be 0
                //DO CORNERS EVEN NEED A VERTEX? maybe to avoid null pointer? use try/catch?
//       MADE FUNCTION FOR THIS         boolean atCorner = (row == 0 && col == 0) || //top left
//                        (row == 0 && col == 2 * dimension) || //top right
//                        (row == 2 * dimension && col == 0) || //bottom left
//                        (row == 2 * dimension && col == 2 * dimension); //bottom right
                if (!atCorner(row, col)) {
                    if (row == 0 || row == 2 * dim) { //TOP OR BOTTOM ROWS
                        v.setData(2); //top and bottoms rows all belong to P2
                        if (col != 1) { //add left neighbors but not 2 left corners
                            Vertex leftNeighbor = graph.get(new Coordinate(row, col - 1)); //get vertex to left of current (v)
                            v.addNeighbor(leftNeighbor, 0); //connect w/ weight of 0
                        }
                        if (row == 2 * dim && col % 2 == 1) {//also need to add top neighbor for bottom row, but only for unowned spots (don't connect to P1 owned vertices in second to last row)
                            Vertex topNeighbor = graph.get(new Coordinate(row - 1, col));
                            v.addNeighbor(topNeighbor, 1); //topNeighbors are unowned at start
                        }
                    } else if (col == 0 || col == 2 * dim) { //LEFT OR RIGHT COLUMNS
                        v.setData(1); //R and L column owned by P1
                        if (row != 1) { //don't want to connect to top L or top R corners
                            Vertex topNeighbor = graph.get(new Coordinate(row - 1, col));
                            v.addNeighbor(topNeighbor, 0);
                        }
                        if (col == 2 * dim && row % 2 == 1) {
                            Vertex leftNeighbor = graph.get(new Coordinate(row, col - 1));
                            v.addNeighbor(leftNeighbor, 1);
                        }
                    } else { //not in top/bottom row, or right/left column = inner vertices
                        Vertex topNeighbor = graph.get(new Coordinate(row - 1, col));
                        Vertex leftNeighbor = graph.get(new Coordinate(row, col - 1));
                        if (row % 2 == 1 && col % 2 == 0) { //odd rows+even columns = P1 owned vertex
                            v.setData(1);
                        } else if (row % 2 == 0 && col % 2 == 1) { //even rows+odd columns = P2 owned vertex
                            v.setData(2);
                        } else {
                            v.setData(0); //unowned vertex
                        }
                        int vertexOwner = (Integer) v.getData();
                        if (row == 1 && vertexOwner != 0) { //vertex is in row 1, and it is owned (P1)
                            v.addNeighbor(leftNeighbor, 1);
                        } else if (col == 1 && vertexOwner != 0) {
                            v.addNeighbor(topNeighbor, 1);
                        } else { //gets both a top and a left neighbor
                            v.addNeighbor(topNeighbor, 1);
                            v.addNeighbor(leftNeighbor, 1);
                        }
                    }
                }
                graph.put(coordinate, v);  //put into graph- coord, vertex
            }
        }
    }

        private boolean atCorner(int row, int col){
            return (row == 0 && col == 0) || //top left
                (row == 0 && col == 2 * dimension) || //top right
                (row == 2 * dimension && col == 0) || //bottom left
                (row == 2 * dimension && col == 2 * dimension); //bottom right
    }

        //EUGENE'S IMPLEMENTATION
        /*graph = new HashMap<>();
        //set entire graph to 0
        for (int r = 0; r < 2 * dim + 1; r++) {
            for (int c = 0; c < 2 * dim + 1; c++) {
                Vertex v = new Vertex(0);
                Coordinate co = new Coordinate(r, c);
                graph.put(co, v);
            }
        }
        //set up player nodes for BOTH player 1 and player 2
        for (int r = 0; r < 2 * dim + 1; r++) {
            for (int c = 0; c < 2 * dim + 1; c++) {
                Coordinate co = new Coordinate(r, c);
                //odd row and even column
                if (r % 2 == 1 && c % 2 == 0) {
                    graph.get(co).setData(1);
                }
                //even row and odd column
                else if (r % 2 == 0 && c % 2 == 1) {
                    graph.get(co).setData(2);
                }
                //check to see if it's not a corner node
                if(!(((r == 0 && c == 0) || (r == 2*dim && c == 0)) || (r == 0 && c == 2*dim) || (r == 2*dim && c < 2*dim))){
                    Vertex curr = graph.get(co);
                    Vertex north = graph.get(new Coordinate(r-2, c));
                    Vertex east = graph.get(new Coordinate(r, c+2));
                    Vertex south = graph.get(new Coordinate(r+2, c));
                    Vertex west = graph.get(new Coordinate(r, c-2));
                    if(r == 0){
                        //top left blue node
                        if(c == 1){
                            curr.addNeighbor(south, 1);
                            curr.addNeighbor(east, 0);
                        }
                        //top right blue node
                        else if(c == 2*dim-1){
                            curr.addNeighbor(south, 1);
                            curr.addNeighbor(west, 0);
                        }
                        //rest of top blue nodes
                        else{
                            if(c % 2 == 1){
                                curr.addNeighbor(east, 0);
                                curr.addNeighbor(south, 1);
                                curr.addNeighbor(west, 0);
                            }
                        }
                    }
                    else if(r == 1){
                        //top left red node
                        if(c == 0){
                            curr.addNeighbor(east, 1);
                            curr.addNeighbor(south, 0);
                        }
                        //top right red node
                        else if(c == 2*dim){
                            curr.addNeighbor(west, 1);
                            curr.addNeighbor(south, 0);
                        }
                        else{
                            if(c % 2 == 0){
                                curr.addNeighbor(east, 1);
                                curr.addNeighbor(south, 1);
                                curr.addNeighbor(west, 1);
                            }
                        }
                    }

                }
            }
        }*/

    /**
     * Method called after every move is made.  The engine will only call this after it verifies the validity of
     * the move, so we do not need to verify the move provided.  It is guaranteed to be valid.
     *
     * @param m - most recent move
     */
    @Override
    public void lastMove(PlayerMove m) {
        numMovesMade++; //increase every time a move is made
        int player = m.getPlayerId();
        Vertex move = graph.get(m.getCoordinate()); //get move vertex to update
        move.setData(player); //update owner
        updateEdges(move);
        graph.put(m.getCoordinate(), move); //put newly updated vertex back into graph/board representation
        System.out.println("Player: " + Integer.toString(m.getPlayerId()) + ", " + m.getCoordinate());
        if(hasWonGame(1)||hasWonGame(2)){
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
        for (Edge e : vNeighbors) { //loop through neighbors and update edge weights
            Vertex neighbor = e.getTo(); //for each of these neighbor vertices need to update the edge where it is the from vertex and move is the to vertex also
            List<Edge> neighborNeighbors = neighbor.getNeighbors();
            if (neighbor.getData() == v.getData()) {
                e.setWeight(0); //if neighbor is same player, weight of edge with move as from, neighbor as to set to 0
                for (Edge neighborEdge : neighborNeighbors) {
                    Vertex n = neighborEdge.getTo();
                    if (n.equals(v)) {
                        neighborEdge.setWeight(0);
                    }
                }
            } else {
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


    @Override
    public void otherPlayerInvalidated() {

    }

    @Override
    public PlayerMove move() {

        // start the clock
        double start = System.currentTimeMillis();

//        if(numMovesMade == 0){
//            Coordinate c1 = new Coordinate(dimension*2-1, 1);
//            return(new PlayerMove(c1, 1));
//        }
//        if(numMovesMade == 1){
//            Coordinate c2 = new Coordinate(1, dimension*2-1);
//            return(new PlayerMove(c2, 2));
//        }


        //need two implementations based on if player 1 or player 2- don't make invalid moves!!
        int myPlayerID = myPlayerID();
        int opponentID = 3-myPlayerID;
        System.out.println("numMovesMade "+ numMovesMade);
        System.out.println("I AM PLAYER " + myPlayerID + " and OPPONENT IS PLAYER " + opponentID);
        HashMap<Vertex, Tuple> paths = runDijkstra(graph, myPlayerID);
        ArrayList<PlayerMove> myPathToVictory = pathToVictory(myPlayerID, paths);

        //IF OTHER PLAYER INVALIDATED, JUST PICK NEXT AVAILABLE MOVE ON OWN PATH TO VICTORY HERE


        HashMap<Vertex, Tuple> opponentPaths = runDijkstra(graph, opponentID);
        ArrayList<PlayerMove> opponentPathToVictory = pathToVictory(opponentID, opponentPaths);

        List legalMoves = allLegalMoves();
        ArrayList<PlayerMove> availableMoves = new ArrayList<>();

        for(int i=0; i<myPathToVictory.size(); i++){ //if pathToVictory overlap exists make that move
            PlayerMove move = myPathToVictory.get(i);
            Vertex v = graph.get(move.getCoordinate());
            if(((Integer) v.getData() == 0) && legalMoves.contains(move)){ //Vertex is unowned and is a legal move

                PlayerMove oppMove = new PlayerMove(move.getCoordinate(), opponentID); //opponent's list of PlayerMoves contain their ID
                if(opponentPathToVictory.contains(oppMove)){
                    System.out.println("\n BLOCKED OVERLAP \n"); //REMOVE
                    return move;
                }
                availableMoves.add(move); //avoid re-checking owner = 0 & legal move if no overlap
            }
        }
        PlayerMove myMove = availableMoves.get(availableMoves.size()-1); //this is an available/legal move from myPathToVictory

       // for(int i=0; i<availableMoves.size(); i++){// no overlap in pathsToVictory
        if(fewestSegmentsToVictory(myPlayerID) > fewestSegmentsToVictory(opponentID)){ //Opponent is closer to winning , make move to block
            for(int i = 0; i<opponentPathToVictory.size(); i++){
                PlayerMove oppMove = opponentPathToVictory.get(i);
                PlayerMove move = new PlayerMove(oppMove.getCoordinate(), myPlayerID);
                Vertex v = graph.get(oppMove.getCoordinate());
                if(((Integer) v.getData() == 0) && legalMoves.contains(move)){ //Vertex is unowned and is a legal move
                    System.out.println("\n BLOCK DEFENSIVE MOVE \n");
                    return move; //block first available/legal move on opponent's path
                }
            }
        }
        else if(fewestSegmentsToVictory(myPlayerID) < fewestSegmentsToVictory(opponentID)){
            System.out.println("OFFENSIVE MOVE, I'M CLOSER TO WINNING");
            //return myMove;
        }

        // compute the elapsed time
        System.out.println("Elapsed time: " +
                (System.currentTimeMillis() - start)/1000.0 + " seconds.");

        return myMove; //no overlap in pathsToVictory, I am closer to winning
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

        /* debug help
        System.out.println("\n PATH TO VICTORY for Player " + PlayerID + "\n"); //REMOVE
        for(int i = 0; i< roadToVictory.size(); i++){
            System.out.println(roadToVictory.get(i).toString());
        }
        */

        return roadToVictory;
    }


    @Override
    public List allLegalMoves() {
        List legalMoves = new ArrayList<PlayerMove>();
        Set<Coordinate> graphKeys = graph.keySet(); //set of all the keys (Coordinates) in graph
        boolean player1 = true;
        for (Coordinate c : graphKeys) { //for each entry in the graph,
            Vertex v = graph.get(c);
            if (((Integer)v.getData() == 0) ){ //if vertex owner is 0, then move is available
                PlayerMove move = new PlayerMove(c, myPlayerID());
                legalMoves.add(move);
            }
        }
        return legalMoves;
    }


    @Override
    public int fewestSegmentsToVictory(int i) {
        /*
        run dijkstra based on player
        based on end vertex for player, get final edge weight and divide by 2
         */
        HashMap<Vertex, Tuple> paths = runDijkstra(graph, i);
        Vertex end;
        Tuple endTuple;
        if (i == 1) {
            endTuple = getEndTuple(1, paths);
            // debug helper print statments- prints out Player 1 final path weight + board at game end
            System.out.println("END WEIGHT PLAYER 1: " + endTuple.getWeight()); //REMOVE
            System.out.println("MOVES FTW PLAYER 1: " + endTuple.getWeight() / 2);
            System.out.println("------------");

/*debug help
            Set<Coordinate> graphKeys = graph.keySet();
            for(Coordinate c : graphKeys){
                System.out.println("Coordinate: " + c.toString());
                Vertex v = graph.get(c);
                List<Edge> edges = v.getNeighbors();
                for(Edge e : edges) {
                    System.out.println("----------Neighbor Edge Coordinate " + c.toString() + "Vertex " + v + "Edge weight: " + e.getWeight()); //REMOVE
                }
            }
            Coordinate start = new Coordinate(1,0);
            System.out.println("End Tuple: " + endTuple.toString() + "\n previous tuples: ");
            Tuple last = endTuple;
            while(!(last.getCoordinate().equals(start))){
                System.out.println(last.toString());
                last = getPrev(paths, last);
            }
            System.out.println("_____________________");
*/


            return endTuple.getWeight() / 2;
        } else { //checking for Player2
            endTuple = getEndTuple(2, paths);
            // debug help- prints Player 2 final path weight at game end
            System.out.println("END WEIGHT PLAYER 2: " + endTuple.getWeight()); //REMOVE
            System.out.println("MOVES FTW PLAYER 2: " + endTuple.getWeight() / 2); //make integer
            System.out.println("------------");
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