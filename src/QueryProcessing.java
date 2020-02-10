import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class QueryProcessing {

	public Set<Integer> SuperGraphSearch(MyGraph Q, TreeNode gr, HashMap<Integer, MyGraph> Database ){

		int EdgeCardinality = 0;
		int VertexCardinality = Q.size();
		for (Map.Entry<Integer, Vertex> entry : Q.entrySet()) {
			Vertex v = entry.getValue();
			EdgeCardinality += v.edges.size();
		}
		Set<Integer> C = new HashSet<Integer>();
		for (Integer index : gr.S) {
			MyGraph G = Database.get(index);
			if(NumberOfEdges(G) <= EdgeCardinality){
				if(G.size() <= VertexCardinality ){
					C.add(G.Id);		
				}
			}
		}
		//System.out.println( "C " +  C.size());

		Comparator<QueryNode> comparator = new QueryNodeComparator(); 
		PriorityQueue<QueryNode> H = new PriorityQueue<QueryNode>(10,comparator) ;

		Set<Integer> A_Q = new HashSet<Integer>();

		QueryNode q = new QueryNode() ;
		q.treeNode = gr;
		q.Sstar = new HashSet<Integer>(C) ;

		GraphMatches graphMatches = new GraphMatches(q.treeNode.graph, Q);
		q.M_Q = graphMatches.Match();

		ComputeScore(q);
		H.add(q);

		while(!C.isEmpty()){
			if(!H.isEmpty()){
				q = BestFeature(H, C);
				if(q==null){
					break;
				}
				TreeNode g = q.treeNode;
				for (TreeNode gPlus : g.children) {
					if(gPlus.children.isEmpty()){

						/* Remember that we are not storing the Database graph
						 * but only the mapping for that Database graph with
						 * this feature graph  */
						Matches f = SearchForMatch(q.M_Q, gPlus,Q,Database,false,g.graph);
						if(gPlus.actualGraph.Id == 35){
							f = SearchForMatch(q.M_Q, gPlus,Q,Database,false,g.graph); // Search a match f of gPlus by extending M_Q
//							System.out.println(q.M_Q);
//							System.out.println(g.graph);
//							System.out.println(gPlus.graph);
//							System.out.println(gPlus.actualGraph);
//							System.out.println( f.size() + " match" );
						}
						
						if(!f.isEmpty()){
							A_Q.addAll(gPlus.S);
						}
						C.removeAll(gPlus.S);
					}
					else{
						FeatureExpansion(Q, q, gPlus, H, C);
					}
				}	
			}
			else{
				break;
			}
		}
		
		return A_Q;
	}


	public void FeatureExpansion(MyGraph Q, QueryNode q, TreeNode gPlus
			,PriorityQueue<QueryNode> H, Set<Integer> C){

		QueryNode qPlus = new QueryNode();
		qPlus.treeNode = gPlus;
		qPlus.Sstar = new HashSet<Integer>(gPlus.S);
		qPlus.Sstar.retainAll(C);
		qPlus.M_Q = new Matches();

		GrowEdge gr = gPlus.growEdge;
		int ui,uj;
		ui = gr.v1;
		uj = gr.v2;

		String label = gr.elabel;
		String v2label = gr.v2label;

		for ( HashMap<Integer, Integer> f : q.M_Q) {

			if(gPlus.edge_type == EdgeType.OPEN){ 
				int fui = f.get(ui);
				Vertex v_fui = Q.get(fui);
				for (Edge e : v_fui.edges) {
					int v = e.v1;
					Vertex vNew = Q.get(v);
					/** We are matching e.v1 to a new vertex in q.graph but that 
					 * vertex is already present in g plus */
					/** This means that the open edge v's label
					 * should match with the second vertex of the 
					 * grow edge because that is the vertex that 
					 * was added */

					/** Only if the vertex and the edge labels match*/

					if(!f.containsValue(v) ){
						if( vNew.label.equals(v2label) && e.label.equals(label)){
							HashMap<Integer, Integer> m = new HashMap<Integer, Integer>(f);
							m.put(f.size(), v);
							qPlus.M_Q.add(m);
						}
					}
				}
			}
			else{
				int fui = f.get(ui);
				int fuj = f.get(uj);

				Vertex v_fui = Q.get(fui);
				for (Edge e : v_fui.edges){
					/** The vertices are already mapped */
					if( e.v1 == fuj && e.label.equals(label)){
						HashMap<Integer, Integer> m = new HashMap<Integer, Integer>(f);
						qPlus.M_Q.add(m);
					}
				}
			}
		}

		if(!qPlus.M_Q.isEmpty()){
			ComputeScore(qPlus);
			H.add(qPlus);
		}
		else{
			C.removeAll(qPlus.Sstar);
		}
	}

	/** The set of matches of the feature of q -> M_Q*/

	public Matches SearchForMatch( Matches M_Q, TreeNode gPlus, MyGraph Q, HashMap<Integer, MyGraph> Dataset, boolean print, MyGraph g ){

		MyGraph subGraph = gPlus.graph;
		MyGraph superGraph = Q;

		GraphMatches grmat = new GraphMatches(subGraph, superGraph);

		Set<Integer> subGraphIds = new HashSet<Integer>(subGraph.keySet()); 
		Set<Integer> superGraphIds = new HashSet<Integer>(superGraph.keySet());
		HashMap<Integer, Integer> currentMatch = new HashMap<Integer,Integer>(); 
		Matches allMatches = null;
		Matches ms = new Matches();
		if(print){
			System.out.println(M_Q);
			System.out.println(gPlus.graph);
			System.out.println(gPlus.growEdge);
			System.out.println(gPlus.edge_type);
		}
		for (HashMap<Integer, Integer> hashMap : M_Q){

			currentMatch = new HashMap<>(hashMap);
			subGraphIds = new HashSet<Integer>(subGraph.keySet()); 
			superGraphIds = new HashSet<Integer>(superGraph.keySet());
			allMatches = new Matches();

			subGraphIds.removeAll(hashMap.keySet());
			superGraphIds.removeAll(hashMap.values());

			if(gPlus.edge_type == EdgeType.CLOSE){
				int u = gPlus.growEdge.v1;
				int v = gPlus.growEdge.v2;
				int fu = currentMatch.get(u);
				int fv = currentMatch.get(v);
				
				Vertex v1 = Q.get(fu);
				
				if(grmat.contains(v1.edges, fv, gPlus.growEdge.elabel)){
					grmat.RecursiveMatch(allMatches, currentMatch, subGraphIds, superGraphIds, 0);
					if(print){
						System.out.println(currentMatch);
						System.out.println(allMatches);
					}
					ms.addAll(allMatches);		
				}
				else{
					// Not correct  
				}
			}
			else{
				grmat.RecursiveMatch(allMatches, currentMatch, subGraphIds, superGraphIds, 0);
				if(print){
					System.out.println(currentMatch);
					System.out.println(allMatches);
				}
				ms.addAll(allMatches);
			}
		}

		if(print){
			System.out.println(subGraph);
			System.out.println(superGraph);
			System.out.println(ms);	
		}
		
		subGraph = gPlus.actualGraph;
		grmat = new GraphMatches(subGraph, superGraph);
		Matches FinalMatches = new Matches();

		int numOfVertices = gPlus.graph.size();
		
		if(print){
			System.out.println(ms.size() + "  " + gPlus.matches.size() );
			System.out.println(gPlus.matches);
		}
		
		for (HashMap<Integer, Integer> hashMap : ms){ // These are the keys 
			for (HashMap<Integer, Integer> hashMap1 : gPlus.matches) { // These are the values
				
				currentMatch = new HashMap<>();
				int key,value;
				for (int i = 0; i < numOfVertices; i++) {
					key = hashMap1.get(i);
					value = hashMap.get(i);	
					currentMatch.put(key, value);
				}
				if(print){
					System.out.println( "CM " + currentMatch);
				}
				subGraphIds = new HashSet<Integer>(subGraph.keySet());  
				superGraphIds = new HashSet<Integer>(superGraph.keySet());
				allMatches = new Matches();

				subGraphIds.removeAll(hashMap1.values()); 
				superGraphIds.removeAll(hashMap.values());

				grmat.RecursiveMatch(allMatches, currentMatch, subGraphIds, superGraphIds, 0);
				FinalMatches.addAll(allMatches);
			}
		}
		if(print){
			System.out.println(subGraph);
			System.out.println(superGraph);
			System.out.println(FinalMatches);	
		}
		
		
		return FinalMatches ;
	}

	public void ComputeScore(QueryNode q){
		double score = 0;
		double score1,score2;
		score1 = q.Sstar.size();
		score2 = (double)1/(double)q.M_Q.size();
		score = score1*score2;
		q.score = score;
	}

	public QueryNode BestFeature(PriorityQueue<QueryNode> H, Set<Integer> C ){

		QueryNode q = H.poll(); 

		while( !C.containsAll(q.Sstar) ){
			// Intersection 
			q.Sstar.retainAll(C);
			if(!q.Sstar.isEmpty()){
				ComputeScore(q);
				H.add(q);
			}
			q = H.poll();
			if(q == null){
				break; 
			}
		}
		return q;
	}

	public int NumberOfEdges(MyGraph G){
		int num = 0;
		for (Map.Entry<Integer, Vertex> entry : G.entrySet()) {
			Vertex v = entry.getValue();
			num += v.edges.size();
		}
		return num;
	}

}
