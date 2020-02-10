import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class TreeNode implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<TreeNode> children;
	MyGraph graph;
	MyGraph actualGraph; /** Useful only for the leaf nodes */
	Matches matches;     /** Useful only for the leaf node as it stores the matches from the feature graph to the actual data graph */
	GrowEdge growEdge; 
	EdgeType edge_type; 
	Set<Integer> S ;
	HashMap<Integer, Matches> M_G ;
	Set<Integer> Sstar;

	public double score;

	TreeNode(){
		children = new ArrayList<TreeNode>();
		graph = new MyGraph();
		growEdge = new GrowEdge();
		S = new HashSet<Integer>();
		Sstar = new HashSet<Integer>();
		M_G = new HashMap<Integer, Matches>();
	}
	
	@Override
	public String toString() {
		return graph.toString() + "" +  S.size() + " " +   Sstar.size() + "\n"  ;
	}
}

class GrowEdge implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int v1;
	int v2;
	String v1label;
	String v2label;
	String elabel;	

	GrowEdge() {
	}

	GrowEdge(int u, int v, String l1, String l2, String l3) {
		v1 = u;
		v2 = v;
		v1label = l1;
		v2label = l2;
		elabel = l3;
	}

	@Override
	public int hashCode()
	{
		return v1 + v2 + v1label.hashCode() + v2label.hashCode() + elabel.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		GrowEdge g = (GrowEdge)o;
		// The Edge Label has to be the same 
		// Even though the order of vertices can be different

		if(elabel.equals(g.elabel)){
			if((v1==g.v1) && (v2==g.v2) && (v1label.equals(g.v1label)) && (v2label.equals(g.v2label))){
				return true;
			}
			if((v1==g.v2) && (v2==g.v1) && (v1label.equals(g.v2label)) && (v2label.equals(g.v1label))){
				return true;
			}
		}
		return false;
	}
	@Override
	public String toString(){
		return v1 + " " + v1label + " " + v2 + " " + v2label + " " + elabel + "\n" ; 
	}
}

enum EdgeType implements Serializable {
	OPEN(true),
	CLOSE(false);

	private boolean value;    

	private EdgeType(boolean value) {
		this.value = value;
	}

	public boolean getValue() {
		return value;
	}
}

class QueryNode{
	
	TreeNode treeNode;
	Set<Integer> Sstar;
	Matches M_Q;
	double score;
	
	QueryNode() {
		Sstar = new HashSet<Integer>();
	}
	
}

