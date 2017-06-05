package src.UPLA;

import java.util.ArrayList;
import java.util.LinkedList;


public class Node {
	private String Id;
	private Integer featureState;
	
	private ArrayList<Node> XORsiblings;
	private ArrayList<Node> ORsiblings;
	
	/**/
	
	/*To review each node, each one has its own message!!! So, crossMessages 
	 * are not necessary since  you already know the nodes, and each one has a message*/
    public ArrayList<Node> mandatoryFlow;
    public ArrayList<Node> predecessors;
	private ArrayList<Node> excludeNodes;
	private ArrayList<Node> requireNodes;
	private ArrayList<Node> requiredBy;
	private ArrayList<Node> children;
	
	private Node father;
    public Boolean DeadFeature;
    
   
    private Boolean revR;
    private Boolean revE;
    
	public LinkedList<Node> pendingRs; 	public LinkedList<Node> pendingRd;
	public LinkedList<Node> pendingEs;  public LinkedList<Node> pendingEd;

	public Node(String Id, Integer fs, ArrayList<Node> XORs, ArrayList<Node> ORs, 
			    ArrayList<Node> eNs, ArrayList<Node> rNs, ArrayList<Node> rB, Node f){
		
		DeadFeature=false; revE=false; revR=false;
		pendingRs= new LinkedList<Node>(); pendingRd= new LinkedList<Node>();
		pendingEs= new LinkedList<Node>(); pendingEd= new LinkedList<Node>();
		/////////////
		
		mandatoryFlow = new ArrayList<Node>(); predecessors = new ArrayList<Node>();
		
		////////////
		this.Id = Id;
		this.featureState = fs;
		
		///XORsiblings
		this.XORsiblings = new ArrayList<Node>();
		if (XORs!=null){
			for(Node nx: XORs){
				this.XORsiblings.add(nx);
			}
		}
		
		///ORsiblings
		this.ORsiblings = new ArrayList<Node>();
		if (ORs!=null){
			for(Node no: ORs){
				this.ORsiblings.add(no);
			}
		}
		
		///excludeNodes
		this.excludeNodes = new ArrayList<Node>();
		if (eNs!=null){
			for(Node ne: eNs){
				this.excludeNodes.add(ne);
			}
		}
		
		///requireNodes
		this.requireNodes = new ArrayList<Node>();
		if (rNs!=null){
			for(Node nr: rNs){
				this.XORsiblings.add(nr);
			}
		}
		///requiredBy
		this.requiredBy = new ArrayList<Node>();
		if (rB!=null){
			for(Node nrb: rB){
				this.requiredBy.add(nrb);
			}
		}
		
		this.children = new ArrayList<Node>();
		this.father = f;
	}

	//////////////////////////////////
    public ArrayList<Node> getPredecesors() {
		return predecessors;
	}
	
    public void cleanPredecesor(){
    	predecessors.clear();
    }
    
    public void addPredecesors(Node pred) {
		this.predecessors.add(pred);
	}

    public void setPredecessors(ArrayList<Node> prev){
    	if (prev.size()>0){
       	   for(Node n: prev){
               predecessors.add(n);     
       	   }
       	}
    }

    /// 
    public ArrayList<Node> getMandatoryFlow() {
		return mandatoryFlow;
	}

    public void cleanMandatoryFlow(){
    	mandatoryFlow.clear();
    }

    public Node getTopMF(){
    	return mandatoryFlow.get(0);
    }
    
    public Boolean containsMFNode(Node x){
    	for(Node n: mandatoryFlow){
    		if (x.mandatoryFlow.contains(n))
    			return true;
    	}
    	
       	return false;    	
    }
    
    public void addMandatoryFlow(Node m) {
		this.mandatoryFlow.add(m);
	}
    
    public void setMandatoryFlow(ArrayList<Node> mand){
    	if (mand.size()>0){
       	   for(Node n: mand){
               mandatoryFlow.add(n);     
       	   }
       	}
    }


    ///
    public String getMessageMF(){
    	String M="";
    	if (mandatoryFlow.size()>0){
    		M="[";
    		for(Node n: mandatoryFlow){
    			M += n.getId() + ",";
    		}
    		
    		M = M.substring(0, M.length()-1);
    		M += "]";
    	}
    	
    	return M;
    }
    
    public String getMessagePred(){
    	String M="";
    	
    	if (predecessors.size()>0){
    		M="[";
    		for(Node n: predecessors){
    			M += n.getId() + "," ;
    		}
    		M = M.substring(0, M.length()-1);
    		M += "]";
    	}
    	
    	return M;    	
    }
    
	///
	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public Integer getFeatureState() {
		return featureState;
	}

	public void setFeatureState(Integer featureState) {
		this.featureState = featureState;
	}

	public Node getFather() {
		return father;
	}

	public void setFather(Node f) {
		this.father = f;
	}

	//Obtener Hijo 
	public Node getChild(int i){
		return children.get(i);
	}
	
	//Crear Hijo
	public void addChild(Node n){
		this.children.add(n);
	}
	
	public ArrayList<Node> getXORsiblings() {
		return XORsiblings;
	}

    //Add XOR    
	public void addXORsiblings(Node n) {
		XORsiblings.add(n);
	}

	public ArrayList<Node> getORsiblings() {
		return ORsiblings;
	}

    //Add OR    
	public void addORsiblings(Node n) {
		ORsiblings.add(n);
	}

	public ArrayList<Node> getExcludeNodes() {
		return excludeNodes;
	}

    //Add exclude Node   
	public void addExcludeNodes(Node n) {
		excludeNodes.add(n);
	}

	public ArrayList<Node> getRequireNodes() {
		return requireNodes;
	}

	//Add require 
	public void addRequireNodes(Node n) {
		requireNodes.add(n);
	}

	
	public ArrayList<Node> getRequiredBy() {
		return requiredBy;
	}

	//Add requiredBy 
	public void addRequiredBy(Node n) {
		requiredBy.add(n);
	}
	
	public ArrayList<Node> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<Node> children) {
		this.children = children;
	}

	public Boolean getRevR() {
		return revR;
	}

	public void setRevR(Boolean revR) {
		this.revR = revR;
	}

	public Boolean getRevE() {
		return revE;
	}

	public void setRevE(Boolean revE) {
		this.revE = revE;
	}
}
