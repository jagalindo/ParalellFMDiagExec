package src.UPLA;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.FeatureModelReasoner;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.GenericRelation;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

public class VidalGalingoBenavidesReasoner extends FeatureModelReasoner {

	public Tree miGrafo = new Tree();
    public Map<Node, GenericFeature> listF;

	public Tree getGrafo(){
		return miGrafo;
	}
	
	public VidalGalingoBenavidesReasoner(){
        super();
		reset();
	}
	
	public void Mostrar(){
	   miGrafo.mostrarDF();
	   miGrafo.mostrar();	
	}
	
	@Override
	public void reset() {
		miGrafo =  new Tree();
       listF = new HashMap<Node, GenericFeature>();
	}
     
	@Override
	public void addFeature(GenericFeature feature, Collection<Cardinality> cardIt) {
	    //Add Node
		Node nn = new Node(feature.getName(), 0, null, null, null, null, null, null);				
		miGrafo.existing.put(feature.getName(), nn);	
		
		listF.put(nn, feature);
	}

	public GenericFeature getFeature(Node n){
		return listF.get(n);
	}
	
	@Override
	public void addRoot(GenericFeature feature) {
		miGrafo.addNode1(feature.getName(), "", 0);
		
//		System.out.println(miGrafo.getRoot().getId());
	}

	@Override
	public void addMandatory(GenericRelation rel, GenericFeature child, GenericFeature parent) {
		String cnf_parent = parent.getName();
		String cnf_child = child.getName();

		// Clauses
        miGrafo.addNode1(cnf_parent, cnf_child, 2);	       
	}

	@Override
	public void addOptional(GenericRelation rel, GenericFeature child, GenericFeature parent) {
		String cnf_parent = parent.getName();
		String cnf_child = child.getName();

		// Clauses
        miGrafo.addNode1(cnf_parent, cnf_child, 1);	       
	}

	@Override
	public void addCardinality(GenericRelation rel, GenericFeature child, GenericFeature parent,
			Iterator<Cardinality> cardinalities) {
		// TODO Auto-generated method stub
		
	}
		
	@Override
	public void addSet(GenericRelation rel, GenericFeature parent,
			Collection<GenericFeature> children,
			Collection<Cardinality> cardinalities) {

		GenericFeature feature;
		Iterator<Cardinality> iter = cardinalities.iterator();
		Cardinality card = iter.next();

		Integer minCard = card.getMin();
		Integer maxCard = card.getMax();
		
		String father = parent.getName();
		
		if (minCard ==1){
			if (card.getMax() != 1) {
	
				// =============
				// OR Relation
				// =============
				Iterator<GenericFeature> it = children.iterator();
				String firstSibling="";
	
				while (it.hasNext()) {
					feature = it.next();
					String child = feature.getName();
				
					if (firstSibling.equals(""))
						firstSibling = child;
	
					/*
					if (child.contains("F58"))
						System.out.println("AAAA");
				*/
					miGrafo.addOpt(father, child, firstSibling);
				   }
			}
			else{
				// =============
				// XOR Relation
	   		    // =============
				Iterator<GenericFeature> it = children.iterator();
				String firstSibling="";
				
				while (it.hasNext()) {
					feature = it.next();
					String child = feature.getName();
				
					if (firstSibling.equals(""))
						firstSibling = child;
					
					miGrafo.addAlt(father, child, firstSibling);
				   }			
				
			   miGrafo.numChildren++;
			}
		}
	}

	@Override
	public void addExcludes(GenericRelation rel, GenericFeature origin, GenericFeature destination) {
		// TODO Auto-generated method stub
		String cnf_parent = origin.getName();
		String cnf_child = destination.getName();

		if (cnf_parent.contains("46") && cnf_child.contains("78"))
			System.out.println("-----------");
		
		// Clauses
        miGrafo.addNode2(cnf_parent, cnf_child, 3);	       
	}

	@Override
	public void addRequires(GenericRelation rel, GenericFeature origin, GenericFeature destination) {
        //Tree arbol 
		String cnf_parent = origin.getName();
		String cnf_child = destination.getName();

  	 	// Clauses
        miGrafo.addNode2(cnf_parent, cnf_child, 4);	       
	}

	
	@Override
	public PerformanceResult ask(Question q) {		
		if (q.getClass().toString().contains("DeadFeatures")){
		   miGrafo.setPredecessorsMF();	
//		   miGrafo.mostrar();
		   
		   miGrafo.review();			
		}
		
	    return null;	
	}

	@Override
	public void unapplyStagedConfigurations() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applyStagedConfiguration(Configuration conf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, Object> getHeusistics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setHeuristic(Object obj) {
		// TODO Auto-generated method stub
		
	}	
}
