import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class DGTree {

	HashMap<Integer, MyGraph> Database;
	ArrayList<GrowEdge> RootEdges;
	Set<Integer> UncoveredIndices;

	public DGTree(HashMap<Integer, MyGraph> database) {
		Database = database; 
		RootEdges = new ArrayList<GrowEdge>();
	}

	public ArrayList<TreeNode> DGTreeConstruct() throws CloneNotSupportedException{
		ArrayList<TreeNode> Forest = new ArrayList<TreeNode>();
		TreeNode gr = new TreeNode();
		gr.graph = Helpers.singleEdgeGraph(Database, RootEdges);
		gr.S =  new HashSet<Integer>(Database.keySet()) ;
		gr.Sstar = new HashSet<Integer>(Database.keySet()) ;

		/** Grow edge type of gr is phi */
		/** For each graph which is present in the database 
		 * find the matches between the single edge graph that we just created
		 * and the graph and union it with the matches which are 
		 * already present in the matches */

		for (Integer Gi_index : gr.S){
			MyGraph Gi = (MyGraph) Database.get(Gi_index).clone();
			int id = Gi.Id;
			GraphMatches graphMatches = new GraphMatches(gr.graph, Gi);
			Matches m = graphMatches.Match();
			gr.M_G.put(id,m);
		}

		UncoveredIndices = new HashSet<Integer>(Database.keySet());
		Set<Integer> C = TreeGrow(gr);
		//System.out.println("The number of graphs not covered by this root node are " + C.size() );

		Forest.add(gr);

		while(!C.isEmpty()){
			HashMap<Integer, MyGraph> newDatabase = new HashMap<Integer,MyGraph>();
			for (Integer index : C) {
				newDatabase.put(index, Database.get(index));
			}
			Database = new HashMap<Integer, MyGraph>(newDatabase);
			gr = new TreeNode();
			gr.graph = Helpers.singleEdgeGraph(Database, RootEdges);
			gr.S =  new HashSet<Integer>(Database.keySet()) ;
			gr.Sstar = new HashSet<Integer>(Database.keySet()) ;

			for (Integer Gi_index : gr.S){
				MyGraph Gi = (MyGraph) Database.get(Gi_index).clone();
				int id = Gi.Id;
				GraphMatches graphMatches = new GraphMatches(gr.graph, Gi);
				Matches m = graphMatches.Match();
				gr.M_G.put(id,m);
			}

			UncoveredIndices = new HashSet<Integer>(Database.keySet());
			C = TreeGrow(gr);
			//System.out.println("The number of graphs not covered by this root node are " + C.size() );
			Forest.add(gr);
		}
		return Forest;
	}

	public Set<Integer> TreeGrow(TreeNode gr) throws CloneNotSupportedException{

		CandidateFeature candy = new CandidateFeature();
		PriorityQueue<TreeNode> H = candy.run(gr,Database);

		/** The set of uncovered graphs*/
		Set<Integer> C =  new HashSet<Integer>(gr.Sstar);
//		if(C.contains(35)){
//			System.out.println(C.size());
//		}
		while(!C.isEmpty()){
			if(!H.isEmpty()){
				TreeNode gPlus = candy.BestFeature(H, C, Database);
				if(gPlus == null){
					break;
				}
				if(gPlus.Sstar.size()>1){
					gPlus.graph = Helpers.AddGrowEdge(gr.graph, gPlus.growEdge);
					Set<Integer> c = TreeGrow(gPlus);
					C.addAll(c);
					if(!c.isEmpty()){
//						System.out.println("Adding uncovered graphs " + c.size() );
//						System.out.println(c);	
//						System.out.println(gPlus.Sstar.size());
						gPlus.Sstar.removeAll(c);
//						System.out.println(gPlus.Sstar.size());
					}
					
					/** gPlus.Sstar graphs were deleted from the uncovered 
					vertices list C in the BestFeature method but there might be some graphs
					in it which are not covered by the root gPlus because this is a 
					labeled graph. Hence return all the uncovered vertices from that method 
					and add them to C. So that they can be covered by something else. */
				}
				else{
					if(gPlus.Sstar.size()==1){
						gPlus.actualGraph = (MyGraph)Database.get(gPlus.Sstar.toArray()[0]);
						if(gPlus.actualGraph.Id == 71){
						//	System.out.println("Ofcourse it is present in the DGTree " + gPlus.actualGraph.Id);
						}
						gPlus.matches = gPlus.M_G.get(gPlus.actualGraph.Id) ;
						gPlus.S = new HashSet<Integer>();
						gPlus.S.addAll(gPlus.Sstar);
					}
				}
				gr.children.add(gPlus);
				UncoveredIndices.removeAll(gPlus.Sstar);
				C.removeAll(gPlus.Sstar);
				gPlus.Sstar = null;
				gPlus.M_G = null;	
			}
			else{
				break;

			}
		}
//		if(!C.isEmpty()){
//			System.out.println(C.size());	
//		}
		return C;
	}

}

