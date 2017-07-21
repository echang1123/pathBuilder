package Players.EEVEE;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eugene on 7/12/2017.
 */
public class Vertex<T> {
    //FIELDS
    private T data;
    private List<Edge> neighbors;

    //CONSTRUCTOR
    public Vertex(T data){
        this.data = data;
        neighbors = new ArrayList<>();
    }

    public T getData(){
        return data;
    }

    public void setData(T newdata){
        this.data = newdata;
    }

    //getter for Vertex's neighbors field
    public List<Edge> getNeighbors(){
        return neighbors;
    }

    //eaw 7.19 updated to have bi-directional edges when adding new neighbor vertex- setter for neighbors field
    public void addNeighbor(Vertex<T> neighbor, int weight){
        neighbors.add(new Edge(this, neighbor, weight));
        neighbor.getNeighbors().add(new Edge(neighbor, this, weight));
    }

    //returns list of adjacent vertices
    public List<Vertex<T>> getNeighborVertices(){
        List<Vertex<T>> vertices = new ArrayList<>(neighbors.size());
        for(Edge e:neighbors){
            vertices.add((Vertex<T>)e.getTo());
        }
        return vertices;
    }

    public String toString(){
        return "[Vertex pID: " + data + "] Neighbors: "; //+ this.getNeighbors(); //REMOVE
    }
    public static void main(String[] args) {
//        Vertex v = new Vertex(1);
//        v.addNeighbor(new Vertex(2), 2);
//        v.addNeighbor(new Vertex(1), 2);
//        System.out.println(v);
//        System.out.println(v.getNeighbors());
    }
}
