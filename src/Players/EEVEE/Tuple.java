package Players.EEVEE;

import Interface.Coordinate;

/**
 * Created by eaw on 7/21/2017.
 */
public class Tuple {

    //FIELDS
    private Vertex vertex;
    private int weight;
    private Coordinate coordinate; //for debugging/printing

    //constructor
    public Tuple(Vertex v, int weight, Coordinate c){
        vertex = v;
        this.weight = weight;
        coordinate = c;

    }

    //getters
    public Vertex getVertex(){
        return vertex;
    }

    public int getWeight(){
        return weight;
    }

    public Coordinate getCoordinate(){
        return coordinate;
    }

    @Override
    public String toString(){
        return "Prev Vertex: " + vertex.toString() + ", Weight: "+ weight + ", Coordinate: " + coordinate.toString();
    }

}
