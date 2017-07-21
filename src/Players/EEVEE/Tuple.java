package Players.EEVEE;

/**
 * Created by eaw on 7/21/2017.
 */
public class Tuple {

    private Vertex vertex;
    private int weight;

    public Tuple(Vertex v, int weight){
        vertex = v;
        this.weight = weight;
    }

    public Vertex getVertex(){
        return vertex;
    }

    public int getWeight(){
        return weight;
    }

    @Override
    public String toString(){
        return "Prev Vertex: " + vertex.toString() + ", Weight: "+ weight;
    }

}
