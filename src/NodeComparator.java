import java.util.Comparator;


public class NodeComparator implements Comparator<TreeNode>{

	/** Max element is stored at the top of the 
	 * Heap..  */
	@Override
	public int compare(TreeNode o1, TreeNode o2) {
		if(o1.score > o2.score){
			return -1;
		}
		if(o1.score < o2.score){
			return 1;
		}
		return 0;
	}

}

class QueryNodeComparator implements Comparator<QueryNode>{
	@Override
	public int compare(QueryNode o1, QueryNode o2) {

		if(o1.score > o2.score){
			return -1;
		}
		if(o1.score < o2.score ){
			return 1;
		}
		return 0;
	}
}