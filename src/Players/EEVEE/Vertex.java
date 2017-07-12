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

    public List<Vertex<T>> getNeighbors(){
        List<Vertex<T>> vertices = new ArrayList<>(neighbors.size());
        for(Edge e:neighbors){
            vertices.add((Vertex<T>)e.getTo());
        }
        return vertices;
    }

    public void addNeighbor(Vertex<T> neighbor, int weight){
        neighbors.add(new Edge(this, neighbor, weight));
    }

    public static void main(String[] args) {
        Vertex v = new Vertex("A");
        v.addNeighbor(new Vertex("B"), 2);
        System.out.println(v.getNeighbors());
    }
}
