//eaw changes- deleted extraneous code in constructor


package Players.EEVEE;

import Interface.Coordinate;
import Interface.PlayerModulePart1;
import Interface.PlayerMove;

import java.util.*;

/**
 * Created by Eugene on 7/11/2017.
 */
public class EEVEE implements PlayerModulePart1{

    //FIELDS
    Map<Coordinate, Vertex<Integer>> graph;
    int dimension;

    //constructor
    public EEVEE(){}


    /**
     * Has the player won?
     * @param playerId - player Id to test for winning path.
     * @return - true or false.
     */
    @Override
    public boolean hasWonGame(int playerId) {
        //boolean hasWon = false;


        //will need to check for a path from one side to the other based on the player's ID- start w/ row 0 vs. col 0
        //for player 1- Left to Right, player 2 Top to Bottom
        //need to use back-tracking? follow a given edge path of weight 0 until all of the surrounding edges are 1,
        // then no more moves connected to that vertex so then check if at the last row or column

        Map paths = new HashMap<Vertex, Tuple>(); //new Hashmap holds (key[vertex], value[tuple: prev vertex, weight from origin])

        Set<Coordinate> graphKeys = graph.keySet(); //set of all the keys (Coordinates) in graph
        List vertices = new ArrayList<Vertex>(); //will contain list of all vertices in graph
        Vertex start = new Vertex(0);

        for(Coordinate c:graphKeys){ //make a hashmap with all of the vertices and set initial prevs+weights, make list of all vertices
            Vertex v = graph.get(c);
            Tuple tuple = new Tuple(null, Integer.MAX_VALUE);
            if(playerId == 1 && c.getRow() == 1 && c.getCol() ==0){ //player1 starts at coordinate (1,0)
                    tuple = new Tuple(v, 0); //at start coordinate for P1, set prev to self, weight to 0
                    start = v;
            }
            else if(playerId==2 && c.getRow()==0 && c.getCol()==1){ //player 2 starts at coordinate (0,1)
                    tuple = new Tuple(v, 0);
                    start = v;
            }
            paths.put(v, tuple);
            if(tuple.getVertex() != null) {
                System.out.println("VERTEX: " + v.toString() + " TUPLE: " + tuple.toString());
            }//REMOVE
            vertices.add(v);
        }
        boolean first = true;
        while(!vertices.isEmpty()){ //make this a helper method?
            Vertex currentVertex;
            if(first){
                currentVertex = start;
                first = false;
            }
            else{
                currentVertex = closestVertex(vertices, paths); //method to return the minimum distance vertex in the list
            }
            vertices.remove(currentVertex);
            List<Edge> currentNeighbors = currentVertex.getNeighbors();
            Tuple currentVertexTuple = (Tuple) paths.get(currentVertex);
            for(Edge e:currentNeighbors){
                Vertex neighbor = e.getTo();
                Tuple neighborTuple = (Tuple) paths.get(neighbor); //WHY DOES THIS NEED TO BE CAST TO WORK?
                int neighborCurrentWeight = neighborTuple.getWeight(); //weight of neighbor vertex currently in paths hashmap
                int weightViaCurrentVertex = currentVertexTuple.getWeight() + e.getWeight(); //weight of the current vertex plus weight of edge from current to neighbor
                if(weightViaCurrentVertex < neighborCurrentWeight){
                    Tuple neighborShorterPath = new Tuple(currentVertex, weightViaCurrentVertex);
                    paths.put(neighbor, neighborShorterPath);
                }
            }
        }
        Vertex end;
        Tuple endTuple;
        if(playerId == 1){
            end = graph.get(new Coordinate(2*dimension-1, 2*dimension));
            endTuple = (Tuple) paths.get(end);
            System.out.println("END WEIGHT PLAYER 1: " + endTuple.getWeight()); //REMOVE
            return endTuple.getWeight() == 0;
        }
        else{
            end = graph.get(new Coordinate(2*dimension, 2*dimension-1));
            endTuple = (Tuple) paths.get(end);
            System.out.println("END WEIGHT PLAYER 2: " + endTuple.getWeight()); //REMOVE
            return endTuple.getWeight() == 0;
        }


        /*PSEUDOCODE
        make new hashmap [name it paths?] to hold
        get keys from graph/board's underlying hashmap

        for loop:
            use the keys from graph to get vertices and then make the paths hashmap w/ only the origin vertex initialized, all others have value tuple[null, max_int]
            create a list of all of the vertices in graph aka all of the keys for new paths hashmap
       first = true
       while the list is not empty:
            if first, use the start vertex for first go around- avoid searching entire list for it, already know where to start
            else:
            find the vertex in the list with the next lowest distance
            assign it to a variable, then remove it from the list

            now have next closest vertex, use to adjust paths info for its neighbors if necessary

        */


       /* PROB JUST DELETE
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

        */

       //will return (value of one of the end points == 0) if true, then did win, if false, then player did not win
        //return hasWon;
    }

    public Vertex closestVertex(List<Vertex> vertices, Map<Vertex, Tuple> paths){
        Vertex min = vertices.get(0);
        int currentMinWeight = paths.get(min).getWeight();
        for(Vertex v : vertices){
            int currentWeight = paths.get(v).getWeight();
            if(currentMinWeight>currentWeight){
                min = v;
                currentMinWeight = currentWeight;
            }
        }
        System.out.println("Closest Vertex method: Next closest vertex: " + min.toString() + " , weight: "+ currentMinWeight); //REMOVE
        return min;
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
                        (row==0 && col==2*dimension) || //top right
                        (row==2*dimension && col==0)|| //bottom left
                        (row==2*dimension && col==2*dimension); //bottom right
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
                        // THIS IS JUST FLIPPING LAST IF BLOCK BY ROW/COL REFERENCES, IS THERE EASIER WAY?
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
    }

    /**
     * Method called after every move is made.  The engine will only call this after it verifies the validity of
     * the move, so we do not need to verify the move provided.  It is guaranteed to be valid.
     * @param m - most recent move
     */
    @Override
    public void lastMove(PlayerMove m) {
        Vertex move = graph.get(m.getCoordinate()); //get move vertex to update
        move.setData(m.getPlayerId()); //update owner
        List<Edge> neighbors = move.getNeighbors(); //get all of this vertex's neighbor edges

        for(Edge e:neighbors){ //loop through neighbors and update edge weights
            Vertex neighbor = e.getTo();
            if(neighbor.getData() == move.getData()){
                e.setWeight(0); //if neighbor is same player, weight to zero
            }
            else{
                e.setWeight(10); //if neighbor other player, weight to max
            }
        }
        graph.put(m.getCoordinate(), move); //put newly updated vertex back into graph/board representation

//        Vertex v = graph.get(m.getCoordinate()); rewrote same line tho
//        v.setData(m.getPlayerId()); rewrote same line tho
//        graph.put(m.getCoordinate(), v);
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
