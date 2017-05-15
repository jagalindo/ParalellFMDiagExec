package FMDiagTest;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

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


public class InconsistentFM {
	private String pathOutput;
	private String pathInput;
	private int NC;
	private boolean graph;
    private boolean strict;
    private boolean cleanDirectory;
    	
    ///////////changes1 for cross tree changes - changes2 for multiplicity changes
    private int changes1=0;
    private int changes2=0;
    
	/////////////////////////////
	private FAMAFeatureModel parseFile;
	
	public void setPaths(String pathInput, String pathOutput){
		this.pathInput = pathInput;
		this.pathOutput = pathOutput;
	}
	
	public InconsistentFM(String pathInput, String pathOutput, int NC, boolean graph, boolean strict, boolean clean){
		this.pathInput = pathInput;
		this.pathOutput = pathOutput;
		this.NC = NC;
		this.graph = graph;
		this.strict = strict;
		cleanDirectory=clean;
	}
	
	public void setStrict(boolean strict){
		this.strict = strict;
	}
			
	private void graphicGeneration(String name, FAMAFeatureModel parseFile) throws Exception{
		VariabilityModel vm = parseFile;
		GraphVizWriter gvW = new GraphVizWriter();
		gvW.writeFile(name, vm);
	}
	
	public static boolean ReviewExistenceConstraint(ArrayList<Dependency> dependencySet, ArrayList<Feature> Flist, 
		     int f1, int f2, int cross){	
		for(Dependency dAux: dependencySet){
	       Feature source = dAux.getOrigin();
	       Feature destiny = dAux.getDestination();
	       
	       if (Flist.get(f1)==source && Flist.get(f2)==destiny){
	       	if (cross==0 && (dAux instanceof RequiresDependency)){ //same source and target of a requires association
	               return true; 
	       	}
	       	else if (cross==1 && (dAux instanceof ExcludesDependency)){
	       		return true;
	       	}
	       }
	       else if (Flist.get(f2)==source && Flist.get(f1)==destiny){
	       	 if (cross==1 && (dAux instanceof ExcludesDependency)){
	       		return true;
	       	 }
	       }
	   }
		
		return false;
	}
	
	public static void CopyFeatureModel(FAMAFeatureModel destiny, FAMAFeatureModel source){
		Iterator<Dependency> dependencies = source.getDependencies();
		
		while(dependencies.hasNext()){
			destiny.addDependency(dependencies.next());
		}
	}
	
	private int applyChange(int change, FAMAFeatureModel parseFile2, int previousNMistakes){		
    	int Res=0;
    	
		////////////////Current Dependencies and Features
    	int nFeatures = parseFile.getFeaturesNumber();
		
    	Iterator<Dependency> dependencySet= parseFile2.getDependencies();
		ArrayList<Dependency> Dlist = new ArrayList<>();
		dependencySet.forEachRemaining(Dlist::add);
		
		Collection<Feature> features = parseFile2.getFeatures();
		ArrayList<Feature> Flist = new ArrayList<Feature>(features);
		
   	    Collection<GenericRelation> relations = parseFile2.getRelations();
   	    ArrayList<GenericRelation> Rlist = new ArrayList<GenericRelation>(relations);
		
   	    ///////1: requieres, 2: excludes
   	    if (change == 1 || change == 2){
	    	///Feature Source    		
	 	    int s1 = ThreadLocalRandom.current().nextInt(0, nFeatures);
			
		    ///Feature Target
		    int s2=0;
		    do{
			    s2 = ThreadLocalRandom.current().nextInt(0, nFeatures);
		    }while(s2==s1);
			
		    /////////
		    RequiresDependency rc; ExcludesDependency ec;
	   	    
		    if (change==1){
		       if (!ReviewExistenceConstraint(Dlist, Flist, s1, s2, 0)){
	 	          rc = new RequiresDependency("requires" + Dlist.size()  ,((Feature) Flist.get(s1)), ((Feature) Flist.get(s2)));
		          parseFile2.addDependency(rc);
		          Res = 1;
		       }
			 }
		    else{
		         if (!ReviewExistenceConstraint(Dlist, Flist, s1, s2,1)){
		        	ec = new ExcludesDependency("excludes" + Dlist.size(),((Feature) Flist.get(s1)), ((Feature) Flist.get(s2)));
		        	parseFile2.addDependency(ec);
		        	Res = 1;			         
		        }
		     }
		    
		    if (Res > 0){  //If cross-cutting constraint was added, it is necessary to verify if really it is an error
		    	if (this.strict){
		    		int nNMistakes = reviewError(parseFile2);
		    		
			    	if (nNMistakes == previousNMistakes){
			    		Res = 0;
			    	}
			    	
			   	}
		    }
   	    }
   	    else{ //relation
   	    	int nRelation = relations.size();
   	    	
   	    	if (nRelation > 0){
   	    	   int r = ThreadLocalRandom.current().nextInt(0, nRelation);
   	    	   Relation rel = (Relation) Rlist.get(r);
   	    	   
   	    	   Iterator<Cardinality> cardinalities = rel.getCardinalities();
	   	       ArrayList<Cardinality> Clist = new ArrayList<>();
	   		   cardinalities.forEachRemaining(Clist::add);
	   				   
   	    	   for(Cardinality cc: Clist){
   	    		   int min = ThreadLocalRandom.current().nextInt(0, cc.getMax()+1);
   	    		   int max = ThreadLocalRandom.current().nextInt(min, cc.getMax()+1);

   	    		   cc.setMin(min);
   	    		   cc.setMax(max);
   	     	   }
   	    	   
   	   	       Res = 1;
   	    	}
   	    }
   	    
		return Res;
	}
	
	public int addMistakes(File fileEntry, int nchanges) throws Exception{
		FAMAFeatureModel parseFile2;
		int totalMistakes = 0, totalChanges = 0;
		changes1 = changes2 = 0;
		
    	while(totalChanges <  nchanges){
    		int resApplyChanges = 0;
	    	int previousNMistakes = reviewError(parseFile);
	    	
		    do{

			    System.out.println("Trying Change");
			    
		    	parseFile2 = new FAMAFeatureModel(parseFile);
		    	CopyFeatureModel(parseFile2, parseFile);
		    			    
		    	int c1 = ThreadLocalRandom.current().nextInt(1, 4);   
		    	int r1 = applyChange(c1, parseFile2, previousNMistakes);
		    		
		    	if  (c1 < 3)
		    		changes1 += r1;
		    	else
		    		changes2 += r1;
		    					
		    	resApplyChanges = r1;
				////////////////
				
		    }while(resApplyChanges == 0);
		    
		    totalChanges+=resApplyChanges;
		    parseFile = parseFile2;		   
		    
		    System.out.println("Applied Change");
		    /*
		    ////////////For each new Change, we can generate new Files!!!
		    totalMistakes = reviewError(parseFile);
	    	writingFiles(fileEntry, totalChanges);
			*/
    	}
	    totalMistakes = reviewError(parseFile);
	    return totalMistakes;
	}
	
	private int reviewError(FAMAFeatureModel parseFile){
		ChocoReasoner reasoner = new ChocoReasoner();
		parseFile.transformTo(reasoner);
	
		ChocoDetectErrorsQuestion Q1 = new ChocoDetectErrorsQuestion();
		Q1.setObservations(parseFile.getObservations());
		reasoner.ask(Q1);
	    
  		return Q1.getErrors().size();
	}

	private void cleanOutputDirectory(File file){
		if (this.cleanDirectory){
		    File[] contents = file.listFiles();
		 
		    if (contents != null) {
		        for (File f : contents) {
		            cleanOutputDirectory(f);
		        }
		    }
		    file.delete();			
		}
	}
	
	private void writingFiles(File fileEntry, int explanations) throws Exception{
    	//File name + AppliedChanges + CrossTree Changes + Multiplicity Changes + Explanations
		String name = fileEntry.getName().substring(0,fileEntry.getName().length()-4) + 
			      "_" + (changes1+changes2) + "_" + changes1 + "_" + changes2 + "_" + explanations;	

		////Folder for Number of Changes Changes and Number of Explanations
		String pathOutputAux1 = pathOutput+(changes1+changes2) + "/";
		
		int expN = (explanations/5);
		
		String pathOutputAux2 = pathOutputAux1 +  (5*expN) + "-" + (5*(expN+1)-1) + "/";
		
		try{
			new File(pathOutputAux1).mkdir();
			new File(pathOutputAux2).mkdir();
		}
		catch(Exception ex){}
		
		///Writing Graph
		if (this.graph)
     	    graphicGeneration(pathOutputAux2 + name, parseFile);    		
	    	
    	///Writing new XML file
    	XMLWriter x = new XMLWriter();
    	x.writeFile(pathOutputAux2 +  name + ".xml", parseFile);
	}
	
	public void main(){
		Path path1 = Paths.get(pathInput);
		Path path2 = Paths.get(pathOutput);

		final File folder = new File(path1.toString());		
		final File folder2 = new File(path2.toString());
		
		cleanOutputDirectory(folder2);
		new File(pathOutput).mkdir();

	////	System.out.println("11");
		Integer genModels = 1;
		
		for (final File fileEntry : folder.listFiles()) {
			String extension = fileEntry.getName().substring(fileEntry.getName().length()-3);
			

//			System.out.println("22");
			if (extension.toUpperCase().equals("XML")){			    	        	
				XMLReader reader  = new XMLReader();	
	    		try{
		    		 parseFile = (FAMAFeatureModel) reader.parseFile(fileEntry.getAbsolutePath());	    				
		//	    	 System.out.println("Generating Models for " + fileEntry + "(" + genModels + ")");
		    		
		    		 
		    		 //i represents the number of changes in the model
		    	    for (int i = 1; i <= this.NC; i++){
			    		///////////////Add Mistakes on the Model
		    	    	int explanations = addMistakes(fileEntry, i);
		    	
		    	    	writingFiles(fileEntry, explanations);
		    	    	
		    	    }
		    	    
		    	    //System.out.println("Models for " + fileEntry + " Generated");
		    	    genModels++;
	    		}
	    		catch(Exception ex){
	    			System.out.println(fileEntry + " - " + ex.toString());
	    		}
	 
	    		//////////////
				}
        }

	}
}
