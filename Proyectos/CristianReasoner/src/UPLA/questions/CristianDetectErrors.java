package src.UPLA.questions;

import java.util.Collection;

import src.UPLA.*;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.DetectErrorsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultDetectErrorsQuestion;
import es.us.isa.FAMA.errors.Error;
import es.us.isa.FAMA.errors.Observation;

public class CristianDetectErrors implements DetectErrorsQuestion {

	private DefDetectErrorsQuestion dtq;

	public CristianDetectErrors() {
		super();
		dtq = new DefDetectErrorsQuestion();
	}


	public Collection<Error> getErrors() {
		return dtq.getErrors();
	}

	public void setObservations(Collection<Observation> observations) {
		dtq.setObservations(observations);
	}


	public PerformanceResult answer(Reasoner r) {
		if(r==null){
			throw new FAMAParameterException("Reasoner :Not specified");
		}
	
		return dtq.answer(r);
	}

	public String toString(){
		return this.dtq.toString();
	}

	class DefDetectErrorsQuestion extends DefaultDetectErrorsQuestion{
/*		public PerformanceResult performanceResultFactory() {
			return new Sat4jResult();
	}
*/


/*
		public ValidQuestion validQuestionFactory() {
			return new Sat4jValidQuestion();
}
*/


		public Class<? extends Reasoner> getReasonerClass() {
			return null;
		}

		@Override
		public ValidQuestion validQuestionFactory() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public PerformanceResult performanceResultFactory() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	@Override
	public Class<? extends Reasoner> getReasonerClass() {
		// TODO Auto-generated method stub
		return null;
	}

}
