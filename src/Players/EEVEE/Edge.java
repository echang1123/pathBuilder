package Players.EEVEE;

/**
 * Created by Eugene on 7/12/2017.
 */
public class Edge<T> {
    //FIELDS
    private Vertex<T> from;
    private Vertex<T> to;
    private int weight;

    //CONSTRUCTOR
    public Edge(Vertex<T> from, Vertex<T> to, int weight){
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public Vertex<T> getFrom(){
        return from;
    }

    public Vertex<T>getTo(){
        return to;
    }

    public int getWeight(){
        return weight;
    }

    public void setFrom(Vertex<T> f){
        from = f;
    }

    public void setTo(Vertex<T> t){
        to = t;
    }

    public void setWeight(int w){
        weight = w;
    }

    public String toString(){
        return "Edge " + "from " + from + " to " + to + " [Weight" + ": " + weight + "]";
    }
}
