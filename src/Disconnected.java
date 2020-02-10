import java.util.ArrayList;
import java.util.LinkedList;

public class Disconnected {

	int V;
	MyGraph G;

	public ArrayList<MyGraph> FindComponents( MyGraph g){
		
		V = g.size();
		G = g;

		ArrayList<MyGraph> toBeReturned = new ArrayList<MyGraph>();
		int src = 0;
		MyGraph g0 = new MyGraph();
		ArrayList<Integer> notDone = new ArrayList<Integer>() ;

		g0 = new MyGraph();
		for (int i = 1; i < V; i++){
			if(isReachable(src, i)){
				g0.put(i, g.get(i));
			}else{
				notDone.add(i);
			}
		}
		toBeReturned.add(g0);

		while(!notDone.isEmpty()){
			ArrayList<Integer> temp = new ArrayList<Integer>();
			g0 = new MyGraph();
			src = notDone.get(0); 
			for (int i = 1; i < notDone.size(); i++) {
				int dest = notDone.get(i);
				
				if(isReachable(src, dest)){
					g0.put(dest, g.get(dest));
				}
				else{
					temp.add(dest);
				}
			}
			toBeReturned.add(g0);
			notDone = temp;
		}
		
		return toBeReturned;
	}


	Boolean isReachable(int s, int d)
	{

		boolean visited[] = new boolean[V];
		LinkedList<Integer> queue = new LinkedList<Integer>();
		visited[s]=true;
		queue.add(s);

		while (queue.size()!=0)
		{
			s = queue.poll();
			int n;
			Vertex u = G.get(s);

			for (Edge e : u.edges) {
				n = e.v1;
				if (n==d)
					return true;
				if (!visited[n])
				{
					visited[n] = true;
					queue.add(n);
				}
			}
		}
		return false;
	}
}
