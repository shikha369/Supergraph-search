import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Main {

	public static void main(String[] args) throws CloneNotSupportedException, IOException, ClassNotFoundException{

		String infile = "FinalDataset.txt"; 
		Scanner in = new Scanner(new FileInputStream(infile));

		HashMap<Integer, MyGraph> inputGraphs = new HashMap<Integer,MyGraph>();
		ReadGraph r = new ReadGraph();
		r.read(in, inputGraphs);
		in.close(); 

		infile = "input.txt"; 
		in = new Scanner(new FileInputStream(infile));

		HashMap<Integer, MyGraph> queryGraphs = new HashMap<Integer,MyGraph>();
		r.read(in, queryGraphs);
		in.close();

		HashMap<Integer, MyGraph> Dataset = new HashMap<Integer, MyGraph>(inputGraphs);

		/** Construct the DGTree */
		DGTree dg = new DGTree(Dataset);
		ArrayList<TreeNode> Forest = dg.DGTreeConstruct();

		//System.out.println("Printing DGTree ");
//		for (TreeNode treeNode : Forest) {
//			System.out.println( "size " + treeNode.Sstar.size());
//		}

		//						FileOutputStream fileOut = new FileOutputStream("IndexStructure.ser");
		//						ObjectOutputStream out = new ObjectOutputStream(fileOut);
		//						out.writeObject(Forest);
		//						out.close();
		//						fileOut.close();
		//				
		//			System.out.println( "Forest of DGTrees has been written into IndexStructure.ser " );
		//
		//				FileInputStream fileIn = new FileInputStream("IndexStructure.ser");
		//				ObjectInputStream Objin = new ObjectInputStream(fileIn);
		//				ArrayList<TreeNode> Forest = (ArrayList<TreeNode>) Objin.readObject();
		//				Objin.close();
		//				fileIn.close();
		//				System.out.println(" Successfully read the Forest of DGTrees !");

		QueryProcessing qp = new QueryProcessing(); 

		Object[] indices = queryGraphs.keySet().toArray();
		for (int i = 0; i < indices.length; i++) {
			int index = (int)indices[i];	
			MyGraph graph = (MyGraph) queryGraphs.get(index).clone();
			
//			Disconnected dis = new Disconnected();
//			ArrayList<MyGraph> graphComponents = dis.FindComponents(graph);
//			System.out.println("No of Components  are " + graphComponents.size() );
//			for (int j = 0; j < graphComponents.size(); j++) {
//				System.out.println(graphComponents.get(j));
//			}
			
			Set<Integer> A = new HashSet<Integer>();
			for (TreeNode treeNode : Forest) {
				Set<Integer> A_Q = qp.SuperGraphSearch(graph, treeNode, Dataset);
				A.addAll(A_Q);
			}
			List sortedList = new ArrayList<Integer>(A);
			Collections.sort(sortedList);
			System.out.println( A.size() + "\n" +  sortedList );
		}


//		int[] test = {2, 9, 13, 16, 77, 521 };
//		/** Test case for the match function */
//		MyGraph g1 = queryGraphs.get(1);
//		for (int i = 0; i < test.length; i++) {
//			MyGraph g2 = Dataset.get(test[i]);
//			GraphMatches gMatch = new GraphMatches(g2,g1);
//			ArrayList<HashMap<Integer, Integer>> matches = gMatch.Match(); 
//			System.out.println(g2.Id + "\n" + g2 );
//			System.out.println(g1.Id + "\n" + g1 );
//			System.out.println(matches); 
//		}
		return;

	}

	public static void AddEdge(Edge e, Vertex[] vs, int index){
		vs[index].edges.add(e);
		return; 
	}

	public static void PrintDGtree(TreeNode tn){
		for (TreeNode tnChild : tn.children) {
			System.out.println("Parent Node");
			System.out.println(tn.graph);
			System.out.println("Grow Edge");
			System.out.println(tnChild.growEdge);
			System.out.println("This graph");
			System.out.println(tnChild.graph);
			PrintDGtree(tnChild);
		}
	}


}

