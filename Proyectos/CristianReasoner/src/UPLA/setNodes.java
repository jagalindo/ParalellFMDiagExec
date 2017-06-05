package src.UPLA;

import java.util.LinkedList;

/*
 * if max < 0 ==> max = nodes.size()
 * */

public class setNodes {
	protected LinkedList<Node> nodes;
	protected int min, max;
	
	public setNodes(int min, int max) {
		nodes = new LinkedList<Node>();
		this.min = min;
		this.max = max;
	}
		
	public void addNode(Node n){
		nodes.add(n);
	}
	
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	
	
	
}
