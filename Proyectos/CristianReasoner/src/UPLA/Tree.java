package src.UPLA;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.LinkedList;

public class Tree {
	private Node root;
	public HashMap<String, Node> existing;
	public ArrayList<Node> deadFeatures;
	
	/////
	
	public Boolean voidFM = false;
	public Integer numChildren;

	////////////////////////
	private LinkedList<Node> alreadyRevR; 
	private LinkedList<Node> alreadyRevE;
	
	public Tree(){
		root=null;
		existing = new HashMap<String, Node>();
	    deadFeatures= new ArrayList<Node>();
	    numChildren=0;
	    
	    alreadyRevR = new LinkedList<Node>(); 
	    alreadyRevE = new LinkedList<Node>();
	}
	
	private void setMandatoryFlow(Node rr, Node nn, int op){
		///Child Node needs to know its Mandatory-flow
		if (nn!=null){
			if (op==2){  //mandatory
				if (rr.getMandatoryFlow().size()>0){
		            nn.setMandatoryFlow(rr.mandatoryFlow);
		            nn.addMandatoryFlow(nn);
				}
				else{
					nn.addMandatoryFlow(rr);
					nn.addMandatoryFlow(nn);
				}
			}
			else{
		        nn.addMandatoryFlow(nn); 
				}			
		}
		else
			 rr.addMandatoryFlow(rr);
	}

	public void setPredecessorsMF(){
	   Node rr = getRoot();
	   propagatePredecessorsMF(rr);	   
	}
	
	private void propagatePredecessorsMF(Node cc){
	    //Base case
		if (cc.getChildren().size()==0)
			return;
		
		for(Node ch: cc.getChildren()){
			if (ch != cc){ //root contains itself!!!!
				/*Predecessors*/
				ch.setPredecessors(cc.getPredecesors());
				ch.addPredecesors(ch);
				
				/*MF*/
				setMandatoryFlow(cc, ch, ch.getFeatureState());
				/////////////////////
				
				propagatePredecessorsMF(ch);
			}		
		}
	}
 
	/*r is the father, n is the child*/
	public void addNode1(String r, String n, Integer op){
		Node rr = existing.get(r);
		Node nn=null;
		
		if (op==0){
     	   rr.setFather(rr);
  		   rr.addMandatoryFlow(rr);
  		   rr.addPredecesors(rr);
     	   root = rr;  		   
		}
		
		if (!n.equals("")){
	        ////////////////////////////////
			nn = existing.get(n);		
  		    nn.setFather(rr);
			nn.setFeatureState(op);
		
		    ///add node
			rr.addChild(nn);
			
			numChildren++;
		}				
	}	
	
	public void addAlt(String r, String n, String bs){
		Node rr = existing.get(r);
		Node nn = existing.get(n);
        nn.setFeatureState(3);
        
		rr.addChild(nn);
		nn.setFather(rr);
		
		//1st children of the set
		if (!n.equals(bs)){
			//ss is SuperSibling = 1st Sibling of the set...
			Node ss = existing.get(bs);
			
			//add nn to all siblings of ss and to ss and vice-versa		
			for(Node s1: ss.getXORsiblings()){
			   if (!s1.getXORsiblings().contains(nn)){	
			      s1.addXORsiblings(nn);			   
   			      nn.addXORsiblings(s1);
			   }
			}
			
			if (!ss.getXORsiblings().contains(nn)){
			   ss.addXORsiblings(nn);
			   nn.addXORsiblings(ss);
			}
		}			
	}
	
	public void addOpt(String r, String n, String bs){
		Node rr = existing.get(r);
		Node nn = existing.get(n);
        nn.setFeatureState(4);
        
		rr.addChild(nn);
		nn.setFather(rr);
				
		//1st children of the set
		if (!n.equals(bs)){
			//ss is SuperSibling = 1st Sibling of the set...
			Node ss = existing.get(bs);
			
			//add nn to all siblings of ss and to ss and vice-versa		
			for(Node s1: ss.getORsiblings()){
			   if (!s1.getORsiblings().contains(nn)){
			       s1.addORsiblings(nn);		  
			       nn.addORsiblings(s1);
			   }
			}
			
			if (!ss.getORsiblings().contains(nn)){
			   ss.addORsiblings(nn);
			   nn.addORsiblings(ss);
			}		
		}		
	}

	/*For requires messages, r is the source and n us the target: r requires n*/
	public void addNode2(String r, String n, Integer cross){
 		Node rr = existing.get(r);		
		Node nn = existing.get(n);
		
		if (cross==3){ //excludes
			addExcludeNodesOn(rr, nn);
		}
		
		else if (cross==4){ //requires
		    addRequireNodesOn(rr, nn);		 
		}		
	}
	
	private void addExcludeNodesOn(Node rr, Node nn)
	{
		if (!rr.getExcludeNodes().contains(nn))
		    rr.addExcludeNodes(nn);
			
		if (!nn.getExcludeNodes().contains(rr))
			nn.addExcludeNodes(rr);
	 }
		
	private void addRequireNodesOn(Node rr, Node nn){
	   if (rr == nn)	
		  return;
	   
 	   if (!rr.getRequireNodes().contains(nn))
   	      rr.addRequireNodes(nn);
   	      	       
 	   if (!nn.getRequiredBy().contains(rr))
 		   nn.addRequiredBy(rr);
	}
	
	public HashMap<String, Node> getExisting() {
		return existing;
	}

	public Node getRoot() {
		return root;
	}
	
	void propagateRequires(){
		for(Node n: existing.values()){
			ArrayList<Node> nodesR = n.getRequireNodes();
			Integer requiredN = nodesR.size();
			
			if (requiredN > 0){
			    alreadyRevR.add(n); //Already Looked for...

			   for(int i=0; i <requiredN; i++){			
				   Node ch = nodesR.get(i);
	   			   propagate1(ch, n);
	   			}
			   
				n.setRevR(true); alreadyRevR.remove(n); /*Node is already reviewed and not more in progress*/
				
				//////////Are there pending nodes? (waiting for the n requires information)					
				propagatePendingR(n);
			}
		}
	}
	
	void propagate1(Node n, Node father){
		System.out.println("(" + n.getId() + ", " + father.getId() + ")");

		if (!n.getRevR()){	
			alreadyRevR.add(n);			
			ArrayList<Node> nodes = n.getRequireNodes(); Integer nodesN = nodes.size(); //n requires nodes (nodesN)
			
			if (nodesN > 0){
				//Looking for additional requires for nn
				for(int i=0; i< nodesN; i++){
					Node ch = nodes.get(i);
					
					if (!alreadyRevR.contains(ch) && ch!=n){ //is ch previously reviewed?
  					   propagate1(ch, n);
  					   
  					   //is ch pending now? --> n is pending of  ch as well
  					   if (ch.pendingRs.size()>0){
  						  n.pendingRs.add(ch); ch.pendingRd.add(n);  
  					   }

						///Updating n to require nodes required by ch...
						ArrayList<Node> oldChildren = ch.getRequireNodes(); Integer oldN = oldChildren.size();
						 
						for(int j=0; j< oldN; j++){
							Node oldCh = oldChildren.get(j);
									
							if (!nodes.contains(oldCh) && oldCh != n) 
								n.addRequireNodes(oldCh);
							
							if (!oldCh.getRequiredBy().contains(n) && oldCh != n) 
								oldCh.addRequiredBy(n);
						}
					}
					else{
						 if (alreadyRevR.contains(ch)){
							 System.out.println("Node c" + n.getId() + " is pending of " + ch.getId());
						     n.pendingRs.add(ch); ch.pendingRd.add(n); //n needs to know requires nodes of ch
						    }
					    }
				}
			}
		}
				
		//All requires of n are requires of father
		ArrayList<Node> oldChildren2 = n.getRequireNodes();
		for(Node oldCh2: oldChildren2){
			if (!father.getRequireNodes().contains(oldCh2) && oldCh2!=father) 
				father.addRequireNodes(oldCh2);
				
			if (!oldCh2.getRequiredBy().contains(father)  && oldCh2!=father) 
				oldCh2.addRequiredBy(father);						
		}								
		
		//////////Are there pending nodes? (waiting for the n requires information)	
		propagatePendingR(n);
		n.setRevR(true);
		alreadyRevR.remove(n);
	}


	void propagatePendingR(Node base){
		if (base.pendingRs.size() > 0) //base still needs to know about requires nodes of previous nodes
		   return;
		
		LinkedList<Node> pending = base.pendingRd; Integer pendingN = pending.size();
		
		for(int i=0; i<pendingN; i++){ //for each pending node that requires base!!!
			Node pen = base.pendingRd.get(i);
			
			for(Node r: base.getRequireNodes()){ //add nodes of base in r...
				if (!pen.getRequireNodes().contains(r) && pen!=r){
					pen.addRequireNodes(r);
				}
				if (!r.getRequiredBy().contains(pen) && pen!=r){
					r.addRequiredBy(pen);
				}
			}
			
			pen.pendingRs.remove(base); //pen is not waiting for base anymore 
			propagatePendingR(pen); //pending node inform to the nodes that require its!!!			
		}
		
		base.pendingRd.clear(); //base no present more pending nodes!!!!		
	}
	
		
	void copyLinkedList(LinkedList<Node> source, LinkedList<Node> destiny){
	    for(Node n: source){
	    	destiny.add(n);
	    }	
	}
	
	public void review(){
	    mostrar();
		  
	   propagateRequires();
       
	   System.out.println("");	  System.out.println("");

	   mostrar();
		
	   globalRequires();
			
	   for(Node nn: existing.values()){
		   if (deadFeatures.size()==numChildren)
			   voidFM=true;
		   
		   if (voidFM)
			   break;

		   /*
		   if (nn.getId().contains("F24") || nn.getId().contains("F26") || nn.getId().contains("F58"))
			   System.out.println("xq");
		   */
		  if (nn.getRequireNodes().size()>0)
			 reviewRequires(nn);
		 
		  if (nn.getExcludeNodes().size()>0)
			 reviewExcludes(nn);
		   }	   	 
	   
	}
			
	private void globalRequires(){  
		/*For each node nn, 
		 * 1. if nn is in a XOR association
		 * 2. if nn and siblings of nn are required by a node mm in an XOR association, mm is Dead!!!!  
		 * */
		
		 for(Node nn: existing.values()){
		    if (nn.getFeatureState()==3){ /*Is nn in a XOR?*/
		       
		       for (Node mm: nn.getRequiredBy()){
			       Integer count_XOR1=0; 
			       /*Count_XOR1:  Number of required XORsiblings of n*/
			       
			       if (mm.getFeatureState()==3){ /*Is mm in a XOR?*/
			    	   count_XOR1++;
			    	   
				       /*Review if XOR siblings of nn are required by mm*/
			    	   for(Node ss: nn.getXORsiblings()){
			    		   if (ss.getRequiredBy().contains(mm)){
			    			  /*If ss (XOR sibling of nn) requires mm*/
			    			   count_XOR1++;
			    		   }
			    		   
			    		if (count_XOR1>=2) /*mm is in a XOR and requires more than one XOR sibling*/
			    			addRequiredByDF(mm);
			    	   }
				      
		    	   } 			      
		       }
		    }		    
		 }	 
		 
		 /*If nodes of a XOR set present only one live node, that node is mandatory!!!!*/
		 for(Node nn: existing.values()){
		    if (nn.getFeatureState()==3){ /*Node nn is in a XOR*/
		    	Node live = null;
		    	
		    	if (!nn.DeadFeature)
		    	   live = nn;
		    	
		    	 /*To look in nn siblings for another live node*/
		    	 for(Node ss: nn.getXORsiblings()){
		    	    if (!ss.DeadFeature){  /*ss is dead*/
		    		    if (live!=null){ /*More than 1 live node*/
		    	  	        live=null;
		    		    	break;
		    		    	}
		    		 }    
		    	 }
		       
		    	 if (live!=null){  /*There is only one live node in the XOR set of nn*/
		    	     live.setFeatureState(2); /*MANDATORY!!!!!*/
		    	     live.setMandatoryFlow(live.getFather().getMandatoryFlow());
		    	     live.addMandatoryFlow(live);
		    	 }
		    }
		 }
		 
	}
	
	
	void requireRule0_1(Node A, Node B){
		if (A.getXORsiblings().contains(B))
			addRequiredByDF(A);		
 	}

	void requireRule0_2(Node A, Node B){
		if (A.getExcludeNodes().contains(B))
			addRequiredByDF(A);

		for(Node r: A.getRequireNodes()){
			if (A.getExcludeNodes().contains(B))
				addRequiredByDF(A);				
		}
	}

	void requireRule0_3(Node A, Node B){
		for(Node p1: B.getPredecesors()){
			for (Node p2: A.getPredecesors()){
				if (p1.getXORsiblings().contains(p2)) /*p1 Pred(B) is a XORSibling of p2 Pred(B) ==> A is a DeadFeature */ {
					addRequiredByDF(A);
				}
			}
		}
	}
	
	void requireRule0_4(Node A, Node B){
		if (B.getXORsiblings().size() > 0){
		    int cc=1;
			for(Node ss: B.getXORsiblings()){
				if (A.getRequireNodes().contains(ss)) {
				  /*A requires ss which is in XOR association and A requires a XORsibling of*/
				   cc++;
				   if (cc >= 2)
					   break;
				}
			}
			
			if (cc>=2)
     	       addRequiredByDF(A);
		}
	}
	
	void requireRule0_5(Node A, Node B){
	   //nn obtains its MandatoryFlow           
       if (A.mandatoryFlow.size()>0){ // Does the Predecessors of mm list contains one node of the MandatoryFlow?
    	   for(Node ss: A.mandatoryFlow){		   
	           if (B.predecessors.contains(ss)){   
	        	   //XORSiblings of nn are Dead Feature!!!
	           	   ArrayList<Node> siblings = B.getXORsiblings();

	           	   if (siblings.size()>0){
		           	   for(Node dd: siblings){				                   
		           		   if (!deadFeatures.contains(dd))
				     	       addRequiredByDF(dd);
		           	   }
	           	   }
        	   }   
    	   }    		   
       }
		
	}
	
    /*For transitivity A requires C as well!!! Thus, previous rule is also valid!!!*/
	//////////////////////////////// A req B req C
   	/* If Pred(nn) is a XORSibling of a Pred(rr) --> A is dead!!!!*/		
/*			for(Node rr: mm.getRequireNodes()){				
		for(Node p1: nn.getPredecesors()){
			for(Node p2: rr.getPredecesors()){
				if (p1.getFather() == p2.getFather() && p1.getFeatureState() == 3 && p2.getFeatureState() == 3)
					addRequiredByDF(nn);
				}
		}
*/			
		/*Next rule is also part of requireRule0_3 since rr is a required node of nn
		 and nn and rr are predecessor of themselves. So, If nn requires rr, a Pred(nn) 
		 is in a XOR with Pred(rr), then nn is dead.   */
    
		/*Is a XOR sibling of nn in the the Predecessors list of rr?*/
      	/*for(Node ss: nn.getXORsiblings()){
      		if (rr.getPredecesors().contains(ss))
      			addRequiredByDF(nn);
      		}
		*/
    
    /*Next rule is also present for transitivity: Since nn requires rr, and if rr is in a XOR, nn is dead (requireRule0_1)*/
        
      	/*Does mm requires a XORsibling of nn???*/
	/*	if (nn.getXORsiblings().contains(rr)){
			addRequiredByDF(nn);
		}
      	
	}*/					
	
   /* Next rule is also considered in requireRule0_3)
        
   ////nn req mm AND nn is a XORsibling of a Pred(mm) --> nn is dead!!!
	 for(Node pp: mm.getPredecesors()){
		 if (pp.getXORsiblings().contains(nn)){
     			addRequiredByDF(nn);			 
		 }
	 }
	 */

	
	private void reviewRequires(Node nn){
    	/*mm is a required node of nn*/  
		for(Node mm: nn.getRequireNodes()){ 		
			/*nn requires a XOR sibling? mm requires a nn XOR sibling?*/
	        requireRule0_1(nn, mm);					

	    	/*nn requires an excluded node by either nn or a required nodes of nn?*/
	        requireRule0_2(nn, mm);					

	        /*Does a Pred(mm) is a XORSibling of a Pred(nn)?*/
	        requireRule0_3(nn, mm);					
	
	        /*Does nn requires more than one node in a XOR association*/
	        requireRule0_4(nn, mm);
	        
	        /*Review predecessors of each required node*/
	        requireRule0_4(nn, mm);

	        requireRule0_5(nn, mm);
		} 		
    
    	/*transitividad - ss: nodes requires by nn
    	 *                hh: a node excluded by ss
    	 *                
    	 *                If a predecessor of nn is excluded by hh (like a circle)
    	 *                If nn requires a node that is excluded by one of its required nodes ss (like a circle)
    	 *                If a predecessor of nn is required by hh (like a circle)*/
    	for(Node ss: nn.getRequireNodes()){   	
	       	ArrayList<Node> preN = nn.getPredecesors();
	       	
	       	for(Node hh: ss.getExcludeNodes()){
	       		if (preN.contains(hh)) //one of the excluded vertices is a Predecessor of nn?
	       			//recursive dependency
	       			addRequiredByDF(nn);

	       		/*a Pred(nn) requires  hh (an excluded vertex of nn) --> nn is dead */
	    		for(Node pp: preN){
	         		if (pp.getRequireNodes().contains(hh)){
		       			addRequiredByDF(nn);	       				
	       			}
	       		}
	    		
	    		/* A req B, B ex C and a PRED(C) is in the MF(A) --> C is dead*/
				for(Node pp: hh.getPredecesors()){
					if (nn.mandatoryFlow.contains(pp)){
				     	  addRequiredByDF(pp);
					}
				}
				
	       	}

	       	/*
	       	for(Node hh: ss.getRequireNodes()){
	       		if (preN.contains(hh) && hh != nn) //one of the required vertices is a Predeecesor of nn?
	       			//recursive dependency
	       			addRequiredByDF(ss);
	       	}*/
    	}
    		
  }
		
	///////////////////////////////////
	void excludeRule0_1(Node A, Node B){
		
	     for(Node a1: A.getMandatoryFlow()){
        	if (B.getMandatoryFlow().contains(a1)){ //Both Nodes are Dead!!!!
        		 addRequiredByDF(A); /*If a predecessor died, all its successors died as well!!!!!*/
        		 addRequiredByDF(B); /*If a predecessor died, all its successors died as well!!!!!*/
        		 
        		 break;
        	}
        }
        
        for(Node a1: A.getMandatoryFlow()){
        	if (B.getPredecesors().contains(a1)){ //Predecessors of B contains a Mandatory node
        	   addRequiredByDF(B); /*If a predecessor died, all its successors died as well!!!!!*/
        	   break;
        	}
        }
	}
	
	void excludeRule0_2(Node A, Node B){
	   for(Node pp: A.getPredecesors()){
		   if (pp.getRequireNodes().contains(B)) 
			   addRequiredByDF(pp); /*If a predecessor died, all its successors died as well!!!!!*/
	   }
	}
		
	void excludeRule0_3(Node A, Node B){
        if (A.getRequireNodes().contains(B)){
   		   if (!deadFeatures.contains(A))
      	       addRequiredByDF(A);
        }	
	}
	
	void excludeRule0_4(Node A, Node B){
       /*Is mm or a predecessor of mm in the MF(nn)???*/
    	for(Node ss: A.mandatoryFlow){   	
	        if (B.predecessors.contains(ss)){       	
	     	    addRequiredByDF(B);
            }
    	}
    	
    	/*Is mm a predecessor of nn???*/
    	if (A.predecessors.contains(B)){
       	     addRequiredByDF(A); 	   
        }

	}
	
	private void reviewExcludes(Node nn){
 	   /*Review messages of required nodes*/
       //Node mm
	   Boolean band=false;
	   Integer nEquals =0;
	   
	   /*Basic: nn exc mm, and MF(nn)=MF(mm), nn y mm are dead
       nn exc mm, and a Pred(nn) in MF(mm), nn is dead
	    **/
 /*
	  for(Node n1: nn.mandatoryFlow){
		  band = false;
		  for(Node n2: mm.mandatoryFlow){
			  if (n1==n2){
				  band=true;
				  nEquals++;
			  }
		  }
		  
		  if (!band) //Cycle finishes without founding n1 in the list of mm..
			  break;
	  }  
	  
	  if (nEquals==nn.mandatoryFlow.size() && nEquals == mm.mandatoryFlow.size()){
		   addRequiredByDF(nn);
		   addRequiredByDF(mm);
	  }

	  for (Node pp: nn.getPredecesors()){
		  if (mm.mandatoryFlow.contains(pp)){
			   addRequiredByDF(pp);
		  }
	  }
	  */
       for(Node mm: nn.getExcludeNodes()){
    	   /*nn exc mm and Root(MF(nn))= Root(MF(mm))*/
    	   excludeRule0_1(nn, mm);

    	   //////////// nn exc mm AND Pred(nn) requires mm --> then Pred(nn) is dead //////////
    	   excludeRule0_2(nn, mm);
    	   
           //Does node nn that already excludes mm, it also requires mm?
    	   excludeRule0_3(nn, mm);

    	   
    	   excludeRule0_4(nn, mm);
       }
			
       if (nn.getId().contains("F3"))
    	   System.out.println("here!!!!");
       
    	/*transitividad - ss: nodes excluded by nn
     	 *                hh: a node of ss required by other node */
    	for(Node ss: nn.getExcludeNodes()){   	
	       	ArrayList<Node> preN = nn.getPredecesors();
	       	ArrayList<Node> reqN = nn.getRequireNodes();
	       	 	
	       	for(Node hh: ss.getRequiredBy()){
	       		if (preN.contains(hh) && hh!=nn) //A Predeecesor of nn requires that node?
	       			//recursive dependency
	       			addRequiredByDF(hh);  /*Predecessor died... Children also died!!!*/
              
	       		if (reqN.contains(hh) && hh!=nn) //A Predeecesor of nn requires that node?
	       			//recursive dependency
	       			addRequiredByDF(nn);  /*Predecessor died... Children also died!!!*/
	       	}
	    }
		      
	}

	///////Features that require a DeadFeature are also Dead...
	private void addRequiredByDF(Node d){
		
	   	if (!deadFeatures.contains(d)){
		    d.DeadFeature=true;
	   		deadFeatures.add(d);

	   		if (d.getFeatureState()==2) /*A Mandatory Feature*/{
	   			if (d.mandatoryFlow.contains(root)){
	   				voidFM=true;
	   				
	   				///Void Model
	   				
	   			}
	   			else
	   				addRequiredByDF(d.getFather());
	   		}
	   		
	   	}
	
	   	 /*
	   	 else
	      	AddExplanation!!!!
	   	  */	

	   	
		if (d.getRequiredBy().size()>0){
			for (Node nn2: d.getRequiredBy()){
			     if (nn2!=d)
			     {
			   	 if (!deadFeatures.contains(nn2))
			   		 addRequiredByDF(nn2);
	
		    	 /*
		     	 else
		        	AddExplanation!!!!
		    	  */
			     }
			}
		}

		if (d.getChildren().size()>0){
			
			for (Node nn2: d.getChildren()){
			  
			   	 if (!deadFeatures.contains(nn2))
			   		 addRequiredByDF(nn2);
	
		    	 /*
		     	 else
		        	AddExplanation!!!!
		    	  */
			}
		}
	}

	public void mostrarDF(){
    	String Message = "Deade Features " + deadFeatures.size() + " : ";
		
    	for(Node n: deadFeatures){
	    	Message += n.getId() + " - ";
	    }	
	
    	System.out.println(Message);
	}
	
	public void mostrar(){	
		mostrar2(root, 0);
	}
	
	private void mostrar2(Node rr, Integer level){
		
	    ArrayList<Node> Q = new ArrayList<Node>();	    
	    Q.add(rr);
	    
	    while(Q.size()>0){

	    	Node cc = Q.remove(0);
	    	ArrayList<Node> children = cc.getChildren();
	    	
	    	String message = "Feature " + cc.getId() + " " + cc.getFeatureState() + ", " + cc.getChildren().size() + " Children ";
	    	
	    	///////
	    	ArrayList<Node> childs = cc.getChildren();
	    	
	    	String maux= "";
	    	
	    	if (childs.size()>0)
	    	   {
	    		
	    		maux = "[";
	         	for(Node x: childs){
	    	    	maux = maux + x.getId() + ",";
	         	}
	         	maux = maux.substring(0, maux.length()-1) + "]";
	    	}
	    	                   
	    	 message += maux + ", " + cc.getXORsiblings().size() + " XOR Sib ";
	    	///////////////
	    	ArrayList<Node> siblings1 = cc.getXORsiblings();
	    	
	    	maux= "";
	    	
	    	if (siblings1.size()>0)
	    	   {
	    		
	    		maux = "[";
	         	for(Node x: siblings1){
	    	    	maux = maux + x.getId() + ",";
	         	}
	         	maux = maux.substring(0, maux.length()-1) + "]";
	    	}
	    	                   
	    	 message += maux + ", " + cc.getORsiblings().size() + " OR Sib, " + 
	    			           cc.getExcludeNodes().size() + " Ex F ";
	    	
	    	 ////////
	    	ArrayList<Node> exc = cc.getExcludeNodes();
	    	
	    	maux= "";
	    	
	    	if (exc.size()>0)
	    	   {
	    		
	    		maux = "[";
	         	for(Node xx: exc){
	    	    	maux = maux + xx.getId() + ",";
	         	}
	         	maux = maux.substring(0, maux.length()-1) + "]";
	    	}
		    	
	    	message += maux + ", " + cc.getRequireNodes().size() + " Req F ";
	       
	    	////////
	    	ArrayList<Node> req = cc.getRequireNodes();
	    	
	    	maux= "";
	    	
	    	if (req.size()>0)
	    	   {
	    		
	    		maux = "[";
	         	for(Node xx: req){
	    	    	maux = maux + xx.getId() + ",";
	         	}
	         	maux = maux.substring(0, maux.length()-1) + "]";
	    	}
	    	                   	    	
	    	
       	    message += maux + ", and  " + cc.getRequiredBy().size() + " Req By F ";
            ////////
  	    	ArrayList<Node> req2 = cc.getRequiredBy();
  	    	
  	    	maux= "";
  	    	
  	    	if (req2.size()>0)
  	    	   {
  	    		
  	    		maux = "[";
  	         	for(Node xx: req2){
  	    	    	maux = maux + xx.getId() + ",";
  	         	}
  	         	maux = maux.substring(0, maux.length()-1) + "]";
  	    	}
  	    	
       	    
       	    message += maux + "- Father " + cc.getFather().getId() + " and Mandatory Flow " + cc.getMessageMF() + 
			                  ", Predecessors " + cc.getMessagePred() + " - DF " + cc.DeadFeature.toString();
	    	

	    	 //////////////////////
	    	 System.out.println(message);
	    	 
	    	if (children.size() > 0){
		    	for(Node ch: children){
		    		if (ch.getFeatureState() < 5)
		               Q.add(ch);			          
		    	}
		    }
	    }				
	    
	    System.out.println("");
	}
	
}