import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

public class CandidateFeature {

	public PriorityQueue<TreeNode> run(TreeNode g, HashMap<Integer, MyGraph> Database ){

		HashMap<GrowEdge, TreeNode> H = new HashMap<GrowEdge, TreeNode>()  ;
		EdgeType t;
		MyGraph featureGraph = g.graph;
		TreeNode gPlus;

		/** First half of the algorithm */
		for (Integer G_index : g.Sstar) {
			MyGraph G = (MyGraph) Database.get(G_index).clone();
			Matches matches = g.M_G.get(G.Id);
			int len = matches.size();
			for (int i = 0; i < len; i++) {
				HashMap<Integer, Integer> match = matches.get(i);
				int f = match.size();
				for (Map.Entry<Integer, Integer> entry : match.entrySet())
				{
					int ui = entry.getKey();
					int fui = entry.getValue();

					Vertex mappedVertex = G.get(fui);
					int v;
					int uj;

					String l1,l2;
					
					for (Edge  e: mappedVertex.edges) {

						v = e.v1;
						if(match.containsValue(v)){
							uj = getKeyByValue(match, v);
							t = EdgeType.CLOSE;
						}
						else{
							uj = f;
							t = EdgeType.OPEN;
						}
						Vertex u = featureGraph.get(ui);

						if( uj > ui &&  !containsEdge(u, uj) ){
							
							l1 = u.label;
							l2 = G.get(v).label; /** Label of the vertex that you want to grow in g+ */
							GrowEdge grEdge = new GrowEdge(ui, uj, l1, l2, e.label);

							Set<Integer> s = new HashSet<Integer>();
							s.add(G.Id);

							if(H.containsKey(grEdge)){
								gPlus = H.get(grEdge);
								gPlus.Sstar.addAll(s);
							}
							else{
								/** This edge was not found in the Heap and therefore we will create a 
								 * new tree node and add it to the heap */
								gPlus = new TreeNode();
								gPlus.growEdge = grEdge;
								gPlus.Sstar = new HashSet<Integer>();
								gPlus.Sstar.addAll(s);
								gPlus.score = 0;
								gPlus.edge_type = t;
								gPlus.graph = (MyGraph) g.graph.clone();
								GrowGraph(gPlus.graph, gPlus.growEdge);
								H.put(grEdge, gPlus);
							}
						}
					}
				}

			}
		}
		
		/** Second half of the algorithm */
		for (Integer G_index : g.S) {
			MyGraph G = (MyGraph) Database.get(G_index).clone();
			Matches matches = g.M_G.get(G.Id);
			int len = matches.size();
			for (int i = 0; i < len; i++) {
				HashMap<Integer, Integer> match = matches.get(i);
				int f = match.size();

				for (Map.Entry<Integer, Integer> entry : match.entrySet())
				{

					int ui = entry.getKey();
					int fui = entry.getValue();

					Vertex mappedVertex = G.get(fui);
					int v;
					int uj;

					String l1,l2;

					for (Edge e: mappedVertex.edges) {

						v = e.v1;
						if(match.containsValue(v)){
							uj = getKeyByValue(match, v);
						}
						else{
							uj = f;
						}

						Vertex u = featureGraph.get(ui);

						if( uj > ui &&  !containsEdge(u, uj) ){

							l1 = u.label;
							l2 = G.get(v).label; /** Label of the vertex that you want to grow in g+ */
							GrowEdge grEdge = new GrowEdge(ui, uj, l1, l2, e.label);

							Set<Integer> s = new HashSet<Integer>();
							s.add(G.Id);
							
							if(H.containsKey(grEdge)){
								gPlus = H.get(grEdge);
								gPlus.S.addAll(s);
								
								Matches m;
								HashMap<Integer, Integer> newMatch = new HashMap<Integer,Integer>(match);

								if(gPlus.M_G.containsKey(G.Id)){
									m = gPlus.M_G.get(G.Id);
								}
								else{
									m = new Matches();
								}

								if(gPlus.edge_type == EdgeType.OPEN){
									newMatch.put(uj, v);
								}
								m.add(newMatch);
								gPlus.M_G.put(G.Id, m);
							}
						}
					}
				}
			}
		}
		
		PriorityQueue<TreeNode>  queue;
		if(H.isEmpty()){
			queue = new PriorityQueue<TreeNode>();
		}
		else{
			Comparator<TreeNode> comparator = new NodeComparator();
			queue = new PriorityQueue<TreeNode>(H.size(),comparator);
			for (Entry<GrowEdge, TreeNode> entry : H.entrySet())
			{
				TreeNode node = entry.getValue();
				ComputeScore(node,Database);
				queue.add(node);
			}	
		}

		return queue;
	}

	public void ComputeScore(TreeNode node, HashMap<Integer, MyGraph> Database){
		
		double score1 = node.Sstar.size();	
		double score2 = 0;
		
		for (Integer graph_index : node.S) {
			MyGraph graph = (MyGraph) Database.get(graph_index).clone();
			Matches ms = node.M_G.get(graph.Id);
			score2 += ms.size();
		}
		score2 /= node.S.size();

		double score = (score1/score2);
		int MAX = 100; 
		
		if(node.edge_type == EdgeType.OPEN){
			node.score = score;
		}
		if(node.edge_type == EdgeType.CLOSE){
			node.score = score + MAX;
		}
	}
	
	public TreeNode BestFeature(PriorityQueue<TreeNode> H, Set<Integer> C, HashMap<Integer, MyGraph> Database ){
		
		TreeNode gPlus = H.poll(); 

		while( !C.containsAll(gPlus.Sstar) ){
			
			// Intersection 
			gPlus.Sstar.retainAll(C);
			
			if(!gPlus.Sstar.isEmpty()){
				ComputeScore(gPlus,Database);
				H.add(gPlus);
			}
			
			gPlus = H.poll();
			if(gPlus == null){
				//System.out.println("O M G !!");
				break; 
			}
		}
		return gPlus;
	}
	
	public boolean containsEdge(Vertex v, int u){

		ArrayList<Edge> edges = v.edges;
		for (Edge edge : edges) {
			if(edge.v1 == u){
				return true;
			}
		}
		return false;
	}

	public static Integer getKeyByValue(Map<Integer, Integer> map, Integer value) {
		for (Entry<Integer, Integer> entry : map.entrySet()) {
			if (value == entry.getValue()) {
				return entry.getKey();
			}
		}
		return null;
	}

	public static void GrowGraph ( MyGraph G, GrowEdge growEdge){
		
		int v1 = growEdge.v1;
		int v2 = growEdge.v2; 
		
		Vertex u1,u2;
		if(G.containsKey(v1)){
			u1 = G.get(v1);
		}
		else{
			u1 = new Vertex(v1, growEdge.v1label);
		}
		u1.edges.add(new Edge(v2, growEdge.elabel));
		G.put(v1, u1);
	
		if(G.containsKey(v2)){
			u2 = G.get(v2);
		}
		else{
			u2 = new Vertex(v2, growEdge.v2label);
		}
		u2.edges.add(new Edge(v1, growEdge.elabel));
		G.put(v2, u2);
	}
	
}
