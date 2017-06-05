package src.UPLA.questions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import src.UPLA.VidalGalingoBenavidesReasoner;
import src.UPLA.Node;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.DeadFeaturesQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;

public class CristianDeadFeatures implements DeadFeaturesQuestion {

	Collection<GenericFeature> dead;
	
	public CristianDeadFeatures() {
		dead = new LinkedList<GenericFeature>();
	}
	
	@Override
	public Class<? extends Reasoner> getReasonerClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<GenericFeature> getDeadFeatures() {
		return dead;
	}

	public PerformanceResult answer(Reasoner r) {
		if(r==null){
			throw new FAMAParameterException("Reasoner :Not specified");
		}
		
		r.ask(this);
		VidalGalingoBenavidesReasoner reasoner = (VidalGalingoBenavidesReasoner) r; 
		
		ArrayList<Node> review = reasoner.getGrafo().deadFeatures;
	
		for(Node n: review){
			 GenericFeature feature = reasoner.getFeature(n);
			 if (feature == null){
				 throw new IllegalStateException();
			 }
				 
			 dead.add(reasoner.getFeature(n));
		}
		
		return null;
	}
}
