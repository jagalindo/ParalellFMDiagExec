package FMDiagTest;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import es.us.isa.ChocoReasoner.ChocoReasoner;
import es.us.isa.ChocoReasoner.questions.ChocoDetectErrorsQuestion;
import es.us.isa.FAMA.models.FAMAfeatureModel.Dependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.ExcludesDependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.FAMAfeatureModel.Relation;
import es.us.isa.FAMA.models.FAMAfeatureModel.RequiresDependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.GraphVizWriter;
import es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.XMLReader;
import es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.XMLWriter;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.featureModel.GenericRelation;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;


public class InconsistentFM2 {
	private String pathInput;
	
	/////////////////////////////
	private FAMAFeatureModel parseFile;
	
	////////////////////
	public void setPaths(String pathInput){
		this.pathInput = pathInput;
		}
	
	public InconsistentFM2(String pathInput){
		this.pathInput = pathInput;
	}
	
	
	public static void CopyFeatureModel(FAMAFeatureModel destiny, FAMAFeatureModel source){
		Iterator<Dependency> dependencies = source.getDependencies();
		
		while(dependencies.hasNext()){
			destiny.addDependency(dependencies.next());
		}
	}
	 	
	private void writingFiles(File fileEntry, String path2, int explanations) throws Exception{

		//File name + AppliedChanges + CrossTree Changes + Multiplicity Changes + Explanations
		String name = fileEntry.getName().substring(0,fileEntry.getName().length()-5) + explanations;        

		String OS = System.getProperty("os.name").toLowerCase();
	    String separator ="";

	    if (OS.indexOf("win") >= 0)
	    	separator = "\\\\";
	    else
	    	separator =  File.separator;
		
	    ///Writing new XML file
	    XMLWriter x = new XMLWriter();
	    x.writeFile(path2 + separator + name + ".xml", parseFile);
	    
	    fileEntry.delete();
	}
	
	private int reviewError(FAMAFeatureModel parseFile){
		ChocoReasoner reasoner = new ChocoReasoner();
		parseFile.transformTo(reasoner);
	
		ChocoDetectErrorsQuestion Q1 = new ChocoDetectErrorsQuestion();
		Q1.setObservations(parseFile.getObservations());
		reasoner.ask(Q1);
	    
  		return Q1.getErrors().size();
	}

	public void main(){
		Path path1 = Paths.get(pathInput);
		//String name = path1.getFileName().toString();
		
		final File folder = new File(path1.toString());		
		
		for (final File fileEntry : folder.listFiles()) {
			String extension = fileEntry.getName().substring(fileEntry.getName().length()-3);
			String path2 = fileEntry.getParent();
				
	
			if (extension.toUpperCase().equals("XML")){			    	        	
				XMLReader reader  = new XMLReader();	
	    		try{
		    		 parseFile = (FAMAFeatureModel) reader.parseFile(fileEntry.getAbsolutePath());	    				    	    
			    	 int incons = reviewError(parseFile);
			    	 
			    	 writingFiles(fileEntry, path2, incons);
			    	 
	    		}
	    		catch(Exception ex){
	    			System.out.println(fileEntry + " - " + ex.toString());
	    		}
	 
	    		//////////////
	        }
		}
	}
}