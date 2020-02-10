import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GraphMatches {
	
	static MyGraph superGraph;
	static MyGraph subGraph; 
	
	GraphMatches(MyGraph subG, MyGraph superG){
		subGraph = subG;
		superGraph = superG; 
	//	System.out.println(subGraph);
	//	System.out.println(superGraph);
	}

	public Matches Match(){
		
		/** Determine a series of matches of the subGraph in the 
		 * SuperGraph. Each match is an array of indices to which
		 * the subGraph matches in the SuperGraph.
		 * That is it returns the indices of the vertices in the
		 * superGraph. */
			
		/* Brute force method - 
		 * All possible combinations 
		 * Let subgraph have m vertices and SuperGraph have 
		 * n vertices. Each of these m vertices can be mapped
		 * to any of the n vertices. After the matching of one node,
		 * store the vertices available for matching. Then iterate 
		 * over the first node matched from 1 to N.
		 * Iteratively for all the nodes.
		 * It is a recursive problems with subproblems */ 
	
		Set<Integer> subGraphIds = subGraph.keySet(); 
		Set<Integer> superGraphIds = superGraph.keySet();
		HashMap<Integer, Integer> currentMatch = new HashMap<Integer,Integer>(); 
		Matches allMatches = new Matches();
		RecursiveMatch(allMatches, currentMatch, subGraphIds, superGraphIds,0);
		return allMatches; 

	}

	/* Match one node of the subgraph to one node of the super graph  
	 * and recursively call the same function till all the nodes are matched 
	 * Enters this function when the first match is already initialized 
	 * 0 th index of each element of the current match indicates the 
	 * */
	
	public static void RecursiveMatch( ArrayList<HashMap<Integer, Integer>> allMatches, HashMap<Integer, Integer> currentMatch, Set<Integer> subGraphIds, Set<Integer> superGraphIds ,int x){
		
		Iterator<Integer> it = subGraphIds.iterator();
		if(it.hasNext()){

			int subId; 
			int[] newEntry = new int[2]; 
			Set<Integer> newSuper, newSub; 
			HashMap<Integer, Integer> match; 
			
			newSub = new HashSet<Integer>(subGraphIds); 
			subId = it.next();	
			newSub.remove(subId);

			Vertex sub_vertex = subGraph.get(subId); 
			for (Integer superId : superGraphIds) {
				Vertex super_vertex = superGraph.get(superId); 
				newSuper = new HashSet<Integer>(superGraphIds); 
				if(isMatchPossible(sub_vertex,super_vertex)){
					newEntry[0] = subId;
					newEntry[1] = superId; 
					if(isStructurePreserved(currentMatch, newEntry)){
						/* create a new match and pass this one down recursively 
						 * otherwise only the reference is passed down the 
						 * recursive calls. 
						 * */
						match = new HashMap<Integer,Integer>(currentMatch);
						match.put(newEntry[0], newEntry[1]);
						newSuper.remove(superId);
						
						RecursiveMatch(allMatches, match, newSub, newSuper,x+1);
					}
				}
			}
		}
		else{
			allMatches.add(currentMatch); 
		}
		
	}
	
	public static boolean isMatchPossible(Vertex u, Vertex v){
		 
		if((u.label.equals(v.label)) ){
			return true; 
		}
		return false; 
	}

	/** Match is always from the subgraph to the super graph */
	/* Check if there is an edge between the matches of the nodes 
	 * for every edge in the subgraph */
	
	public static boolean isStructurePreserved( HashMap<Integer, Integer> match, int[] newMatch ){
		
		int u = newMatch[0],v;
		int f_u = newMatch[1], f_v; 
		String label_uv;
		/* Go through the adjacency list of u and for every edge with a vertex v 
		 * check if an edge exists between f_u and f_v */
		
		Vertex u_vertex = subGraph.get(u);
		ArrayList<Edge> edges = u_vertex.edges;
		
		if(match.isEmpty()){
			return true; 
		}
		for (int i = 0; i < u_vertex.edges.size(); i++) {
			Edge e = edges.get(i);
			v = e.v1; 
			label_uv = e.label; 
			/* If the neighbor has already been mapped then check */
			if(match.containsKey(v)){
				f_v = match.get(v);
				Vertex v_vertex = superGraph.get(f_v); 
				if(!contains(v_vertex.edges, f_u, label_uv)){ /* Checking for an edge between the corresponding matches with the same label */
					return false; 
				}
			}
		}
		
		return true; 
	}

	public static boolean contains(ArrayList<Edge> edges, int v, String label){
		
		Edge e;
		for (int i = 0; i < edges.size(); i++) {
			e = edges.get(i);
			if(e.v1 == v && e.label.equals(label)){
				return true;
			}
		}
		return false; 
	}
	
	public static void p(String x){
		System.out.println(x);
	}
}

class Matches extends ArrayList<HashMap<Integer, Integer>> implements Serializable {
	private static final long serialVersionUID = 1L;
}; 



