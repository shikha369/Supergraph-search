import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class MyGraph extends HashMap<Integer,Vertex> implements Cloneable, Comparable<MyGraph>, Serializable { // , Comparator<MyGraph>{
	private static final long serialVersionUID = 1L;
	public int Id; 
	public double score;

	@Override
	public int compareTo(MyGraph other) {
		if(score < other.score){
			return -1;
		}
		if(score > other.score){
			return 1;
		}
		return 0;
	}

	@Override /** Returns only -1 or 0. There is no order. Only equal or not */
	//	public int compare(MyGraph g0, MyGraph g1) {
	//
	////		if(!g0.values().contains(g1.values())){
	////			System.out.println(" Haha ");
	////			System.out.println(g0.values());
	////			System.out.println(g1.values());
	////			return -1;
	////		}
	////		if(!g1.values().contains(g0.values())){
	////			System.out.println(" Haha ");
	////			System.out.println(g0.values());
	////			System.out.println(g1.values());
	////			return -1;
	////		}
	//		for (Map.Entry<Integer, Vertex> entry : g0.entrySet()) {
	//			if(entry.getValue().compare(entry.getValue(), g1.get(entry.getKey())) != 0){
	//				System.out.println("Failed here");
	//				System.out.println(entry.getValue());
	//				System.out.println(g1.get(entry.getKey()));
	//				return -1;
	//			}
	//		}
	//		return 0;
	//	}
	//	@Override
	public Object clone() {
		MyGraph cloned = (MyGraph)super.clone();
		for (Map.Entry<Integer, Vertex> entry : cloned.entrySet()) {
			try {
				entry.setValue((Vertex) entry.getValue().clone());
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		return cloned;
	}

	@Override
	public boolean equals(Object arg0) {

		if(arg0 == this ){
			return true;
		}
		if(!(arg0 instanceof MyGraph) ){
			return false;
		}
		MyGraph mg = (MyGraph) arg0;
		if(mg.Id != Id){
			return false;
		}
		if(!mg.keySet().containsAll(this.keySet())){
			return false;
		}
		if(!this.keySet().containsAll(mg.keySet())){
			return false;
		}
		if(!mg.values().containsAll(this.values())){
			return false;
		}
		if(!this.values().containsAll(mg.values())){
			return false;
		}
		return super.equals(arg0);
	}
}; 

class Vertex implements Cloneable, Serializable { // , Comparator<Vertex>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int index;
	String label;
	ArrayList<Edge> edges; 

	Vertex(int i, String l){
		index = i;
		label = String.valueOf(l);
		edges = new ArrayList<Edge>();
	}
	//Modified clone() method in Employee class
	@Override
	protected Object clone() throws CloneNotSupportedException {
		Vertex cloned = (Vertex) super.clone();
		cloned.edges = (ArrayList<Edge>) (cloned.edges.clone());
		return cloned;
	}
	@Override
	public String toString()
	{
		return index + " " + label + " " + edges.toString() + "\n";
	}

	//	@Override
	//	public int compare(Vertex v0, Vertex v1) {
	//
	//		if(v0.index != v1.index){
	//			System.out.println(" f " + v0.index + " " + v1.index );
	//			return -1;
	//		}
	//		if(!v0.label.equals(v1.label)){
	//			System.out.println(" f " + v0.label + " " + v1.label );
	//			return -1;
	//		}
	//		if(v0.edges.size() != v1.edges.size()){
	//			System.out.println(" f " + v0.edges.size() + " " + v1.edges.size() );
	//			return -1; 
	//		}
	//
	//		for (int i = 0; i < v0.edges.size(); i++) {
	//			for (int j = 0; j < v1.edges.size(); j++) {
	//				if(v0.edges.get(i).compare(v0.edges.get(i), v1.edges.get(i)) != 0){
	//					System.out.println(" f " + v0.edges.get(i) + " " + v1.edges.get(i) );
	//					return -1; 
	//				}
	//			}
	//		}
	//		
	//		return 0;
	//	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj){
			return true;
		}
		if( !(obj instanceof Vertex) ){
			return false;
		}
		Vertex v = (Vertex)obj;
		if((v.index != index) || !(v.label.equals(label))  ){
			return false;
		}
		if(v.edges.size() != edges.size()){
			return false;
		}
		for (Edge e : edges) {
			if(!v.edges.contains(e)){
				return false;
			}
		}
		for (Edge e : v.edges) {
			if(!edges.contains(e)){
				return false;
			}
		}
		return true;
	}

}
class Edge implements Serializable, Cloneable { // implements Comparator<Edge>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int v1;
	String label; 
	Edge(int index1, String l){
		v1 = index1;
		label = l; 
	}

	@Override
	public String toString(){
		return v1 + " " + label; 
	}

	//	@Override
	//	public int compare(Edge e0, Edge e1) {
	//		if(e0.v1 != e1.v1){
	//			System.out.println( " F " + e0.v1 + " " + e1.v1 );
	//			return -1;
	//		}
	//		if(!e0.label.equals(e1.label)){
	//			System.out.println( " F " + e0.label + " " + e1.label );
	//			return -1;
	//		}
	//		return 0;
	//	}

	@Override
	public boolean equals(Object obj) {
		if(obj == this ){
			return true;
		}
		if(!(obj instanceof Edge)){
			return false;
		}
		Edge e = (Edge)obj;
		return (v1== e.v1) && e.label.equals(label) ;
	}

//	@Override
//	protected Object clone() throws CloneNotSupportedException {
//		Edge e = new Edge(v1, label);
//		return super.clone();
//	}
}

public class ReadGraph {

	public void read(Scanner in, HashMap<Integer, MyGraph> graphDatabase){
		String line,label; 
		int id, numOfNodes, numOfEdges,i,v1,v2; 
		String[] tokens; 

		while(in.hasNext()){
			line = in.nextLine();
			line = line.substring(1);
			id = Integer.parseInt(line);

			MyGraph graph = new MyGraph();

			numOfNodes = Integer.parseInt(in.nextLine());
			Vertex[] vertices = new Vertex[numOfNodes];
			i= 0; 
			while(i < numOfNodes){
				vertices[i] = new Vertex(i, in.nextLine());
				i++; 
			}

			/** Read all the edges one by one*/	
			numOfEdges = Integer.parseInt(in.nextLine()); 
			i=0;
			tokens = line.split(" ");
			while(i < numOfEdges){
				line = in.nextLine();
				tokens = line.split(" ");
				v1 = Integer.parseInt(tokens[0]);
				v2 = Integer.parseInt(tokens[1]);
				label = tokens[2]; 

				/** Always add the edge only to the vertex
				 * with the lower number */

				//if( v1 < v2 ){
				AddEdge(new Edge(v2, label), vertices, v1);	
				//}
				//else{
				AddEdge(new Edge(v1, label), vertices, v2);	
				//}
				i++; 
			}

			for(i=0;i<numOfNodes;i++){
				graph.put(i,vertices[i] ); 
			}
			graph.Id = id; 
			graphDatabase.put(id, graph);
		}

	}

	public static void AddEdge(Edge e, Vertex[] vs, int index){
		vs[index].edges.add(e);
		return; 
	}

}
