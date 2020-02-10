import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class Helpers {

	public static MyGraph singleEdgeGraph( HashMap<Integer, MyGraph> D, ArrayList<GrowEdge> RootEdges ){
		MyGraph singleEdgeGraph = new MyGraph();

		Set<Integer> keys =  D.keySet();

		int index = 0;
		
		Object[] indices = keys.toArray();
		index = (int)indices[0];
		
		MyGraph graph = (MyGraph) D.get(index).clone();

		/** Assuming that in a graph the vertices are always numbered from 0 to
		 * N-1 if N is the total number of nodes in the graph */
		/** Just as a back up */

		if(graph.size()>=1){
			Vertex v = graph.get(0);
			if(v.edges.size()>=1){
				index = 0;
				Edge e = v.edges.get(index);

				Vertex originalNbr = graph.get(e.v1);

				Vertex v1 = new Vertex(0, v.label);
				Vertex v2 =  new Vertex(1, originalNbr.label) ;


				v1.edges.add(new Edge(1,e.label));
				/** Add edge only to one vertex*/
				v2.edges.add(new Edge(0,e.label));

				singleEdgeGraph.put(0,v1); // First vertex    
				singleEdgeGraph.put(1,v2); // Second vertex 

				GrowEdge gr = new GrowEdge(0,1,v1.label,v2.label,e.label);
				RootEdges.add(gr);	
			}
			
			
		}
		
		return singleEdgeGraph; 
	}


	public static MyGraph AddGrowEdge( MyGraph g, GrowEdge grEd ) throws CloneNotSupportedException{

		Vertex v1 ,v2;

		MyGraph gPlus = new MyGraph();

		for (Map.Entry<Integer, Vertex> entry : g.entrySet()) {
			Vertex v = entry.getValue();
			gPlus.put(entry.getKey(), (Vertex) v.clone());
		}

		if(gPlus.containsKey(grEd.v1)){
			v1 = gPlus.get(grEd.v1);
			v1.edges.add(new Edge(grEd.v2, grEd.elabel));
			if(!gPlus.containsKey(grEd.v2)){
				v2 = new Vertex(grEd.v2, grEd.v2label);
				v2.edges.add(new Edge(grEd.v1,grEd.elabel));
			}
			else{
				v2 = gPlus.get(grEd.v2);
				v2.edges.add(new Edge(grEd.v1,grEd.elabel));
			}

		}
		else{
			v2 = gPlus.get(grEd.v2);
			v2.edges.add(new Edge(grEd.v2, grEd.elabel));

			v1 = new Vertex(grEd.v1, grEd.v1label);
			v1.edges.add(new Edge(grEd.v2,grEd.elabel));

		}

		gPlus.put(grEd.v1, v1);
		gPlus.put(grEd.v2, v2);

		return gPlus;
	}
	
	
}
