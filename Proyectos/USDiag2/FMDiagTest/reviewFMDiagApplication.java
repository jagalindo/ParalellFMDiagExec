package FMDiagTest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import es.us.isa.Choco.fmdiag.ChocoExplainErrorFMDIAG;
import es.us.isa.Choco.fmdiag.ChocoExplainErrorFMDIAGParalell;
import es.us.isa.ChocoReasoner.ChocoReasoner;
import es.us.isa.ChocoReasoner.questions.ChocoDetectErrorsQuestion;
import es.us.isa.FAMA.errors.Error;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.XMLReader;

public class reviewFMDiagApplication {
	private String algorithm;
	private String pathInput;
	private static String pathOutput;
	private int threadsNumber;
	
	private static Double [] time_start = new Double[4];
	private static Double [] time_end = new Double[4];
	private static Double [] time_diff = new Double[4];
	
	private static BufferedWriter output1 = null;
	private static BufferedWriter output2 = null;
	/////////////////////////////
	private static ChocoReasoner reasoner;
	private static FAMAFeatureModel parseFile;
	
	private static ChocoExplainErrorFMDIAG fmdiag;
	private static ChocoExplainErrorFMDIAG flexdiag;
	private static ChocoExplainErrorFMDIAGParalell fmdiagP; 
	private static ChocoExplainErrorFMDIAGParalell flexdiagP; 
	///
	static String fileName="";
	private String EOL = System.getProperty("line.separator");
	
	public reviewFMDiagApplication(String algorithm, String pathInput, String pathOutput){
		this.algorithm = algorithm;
		this.pathInput= pathInput;
		this.pathOutput= pathOutput;
	}
	
	public reviewFMDiagApplication(String algorithm, String pathInput, String pathOutput, int threadsNumber){
		this.algorithm = algorithm;
		this.pathInput= pathInput;
		this.pathOutput= pathOutput;
		this.threadsNumber = threadsNumber;		
	}
	
	public static void listFilesForFolder(final File folder) {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) 
	            listFilesForFolder(fileEntry);
	        else 
	            System.out.println(fileEntry.getName());
	        
	    }
	}
	
	public void FMDiagExplanations(ChocoDetectErrorsQuestion Q1, File fileEntry, ChocoReasoner reasoner) throws IOException{       
		fmdiag = new ChocoExplainErrorFMDIAG();
		fmdiag.setErrors(Q1.getErrors());
		fmdiag.returnAllPossibeExplanations=true;
		fmdiag.flexactive = false;
		
		time_start[0] = new Double(System.currentTimeMillis());
		reasoner.ask(fmdiag);
		time_end[0] = new Double(System.currentTimeMillis());

  		////////////////////////////////
		File fileOutputFinal;
		if (pathOutput.equals(""))
  		   fileOutputFinal = new File(fileName.substring(0,fileName.length()-3) + "FMDiag");
		else
		   fileOutputFinal = new File(pathInput + ".FMDiag");
		////////////
		
		BufferedWriter outputFinal = new BufferedWriter(new FileWriter(fileOutputFinal));

		time_diff[0] = (time_end[0]-time_start[0]);
		System.out.println(fileEntry.getName() + " | FMDiag " + time_diff[0] + "ms");
		outputFinal.write(fileEntry.getName() + " | FMDiag " + time_diff[0] + "ms");
		outputFinal.newLine();
		
		String messageFinal="";
		
		for (Error ex: fmdiag.errors_explanations.keySet()) {
			//messageFinal="";
			///1st line --> to print the error
			messageFinal += ex.toString() +  EOL;
			
			///2nd line --> to print the observation (error type)
			messageFinal += ex.getObservation() +  EOL;
			
			///3rd line --> to print the explanations
			LinkedList<String> explanations = fmdiag.errors_explanations.get(ex);
			messageFinal += explanations +  EOL;
		}
		
		outputFinal.write(messageFinal);
		outputFinal.close();  	
	}

	private void ParallelFMDiagExplanations(ChocoDetectErrorsQuestion Q1, File fileEntry, ChocoReasoner reasoner) throws IOException{		  		
		fmdiagP = new ChocoExplainErrorFMDIAGParalell(threadsNumber);
		fmdiagP.setErrors(Q1.getErrors());
		fmdiagP.returnAllPossibeExplanations=true;
			
		time_start[1] = new Double(System.currentTimeMillis());
		reasoner.ask(fmdiagP);
  		time_end[1] = new Double(System.currentTimeMillis());
	
		////////////////////////////////
		File fileOutputFinal;
		if (pathOutput.equals(""))
   		   fileOutputFinal = new File(fileName.substring(0,fileName.length()-3) + "ParaFMDiag");
		else
		   fileOutputFinal = new File(pathInput + ".ParaFMDiag");
		////////////

		BufferedWriter outputFinal = new BufferedWriter(new FileWriter(fileOutputFinal));

		time_diff[1] = (time_end[1]-time_start[1]);
		System.out.println(fileEntry.getName() + " | ParallelFMDiag " + time_diff[1] + "ms");
		outputFinal.write(fileEntry.getName() + " | ParallelFMDiag " + time_diff[1] + "ms");
		outputFinal.newLine();
		
		String messageFinal="";
		
		for (Error ex: fmdiagP.errors_explanations.keySet()) {
			//messageFinal="";
			///1st line --> to print the error
			messageFinal += ex.toString() +  EOL;
			
			///2nd line --> to print the observation (error type)
			messageFinal += ex.getObservation() +  EOL;
			
			///3rd line --> to print the explanations
			LinkedList<String> explanations = fmdiagP.errors_explanations.get(ex);
			messageFinal += explanations +  EOL;
		}
		
		outputFinal.write(messageFinal);
		outputFinal.close();  	

    }

	private  void FlexDiagExplanations(ChocoDetectErrorsQuestion Q1, File fileEntry, int m, ChocoReasoner reasoner) throws IOException{       	
		flexdiag = new ChocoExplainErrorFMDIAG();
		flexdiag.setErrors(Q1.getErrors());
		flexdiag.returnAllPossibeExplanations=true;
		
		flexdiag.flexactive=true;
		flexdiag.m = m;

		time_start[2] = new Double(System.currentTimeMillis());
		reasoner.ask(flexdiag);
  		time_end[2] = new Double(System.currentTimeMillis());

		////////////////////////////////
		File fileOutputFinal;
		if (pathOutput.equals(""))
   		   fileOutputFinal = new File(fileName.substring(0,fileName.length()-3) + m + "_FlexDiag");
		else
		   fileOutputFinal = new File(pathInput + "." + m + "_FlexDiag");
		////////////
		BufferedWriter outputFinal = new BufferedWriter(new FileWriter(fileOutputFinal));

		time_diff[2] = (time_end[2]-time_start[2]);
		System.out.println(fileEntry.getName() + " | FlexDiag m " + m  + " - " + time_diff[2] + "ms");
		outputFinal.write(fileEntry.getName() + " | FlexDiag m " + m + " - " + time_diff[2] + "ms");
		outputFinal.newLine();
		
		String messageFinal="";

		for (Error ex: flexdiag.errors_explanations.keySet()) {
			//messageFinal="";
			///1st line --> to print the error
			messageFinal += ex.toString() +  EOL;
			
			///2nd line --> to print the observation (error type)
			messageFinal += ex.getObservation() +  EOL;
			
			///3rd line --> to print the explanations
			LinkedList<String> explanations = flexdiag.errors_explanations.get(ex);
			messageFinal += explanations +  EOL;
		}

		outputFinal.write(messageFinal);
		outputFinal.close();  	
		
	}

	private  void ParallelFlexDiagExplanations(ChocoDetectErrorsQuestion Q1, File fileEntry, int m, ChocoReasoner reasoner) throws IOException{       	
		flexdiagP = new ChocoExplainErrorFMDIAGParalell(threadsNumber);
		flexdiagP.setErrors(Q1.getErrors());
		flexdiagP.returnAllPossibeExplanations=true;
		
		flexdiagP.flexactive=true;
		flexdiagP.m = m;

		time_start[3] = new Double(System.currentTimeMillis());
		reasoner.ask(flexdiagP);
  		time_end[3] = new Double(System.currentTimeMillis());

		////////////////////////////////
		File fileOutputFinal;
		if (pathOutput.equals(""))
   		   fileOutputFinal = new File(fileName.substring(0,fileName.length()-3) + m + "_ParaFlexDiag");
		else
		   fileOutputFinal = new File(pathInput + "." + m + "_ParaFlexDiag");
		////////////

		BufferedWriter outputFinal = new BufferedWriter(new FileWriter(fileOutputFinal));

		time_diff[3] = (time_end[3]-time_start[3]);
		System.out.println(fileEntry.getName() + " | ParallelFlexDiag m " + m  + " - " + time_diff[3] + "ms");
		outputFinal.write(fileEntry.getName() + " | ParallelFlexDiag m " + " - " + time_diff[3] + "ms");
		outputFinal.newLine();
		
		String messageFinal="";

		for (Error ex: flexdiagP.errors_explanations.keySet()) {
			///1st line --> to print the error
			messageFinal += ex.toString() +  EOL;
			
			///2nd line --> to print the observation (error type)
			messageFinal += ex.getObservation() +  EOL;
			
			///3rd line --> to print the explanations
			LinkedList<String> explanations = flexdiagP.errors_explanations.get(ex);
			messageFinal += explanations +  EOL;
		}

		outputFinal.write(messageFinal);
		outputFinal.close();  			
	}


	public boolean deleteDirectory(File directory) {
	    if(directory.exists()){
	        File[] files = directory.listFiles();
	        if(null!=files){
	            for(int i=0; i<files.length; i++) {
	                if(files[i].isDirectory()) {
	                    deleteDirectory(files[i]);
	                }
	                else {
	                    files[i].delete();
	                }
	            }
	        }
	    }
	    return(directory.delete());
	}
		
	public void main() throws Exception {
		final File fileEntry = new File(pathInput);
		reasoner = new ChocoReasoner();

		fileName = fileEntry.getName();
    	String extension = fileName.substring(fileName.length()-3);
    	
		if (extension.toUpperCase().equals("XML")){	
    		XMLReader reader  = new XMLReader();	
    		   		
    		try{
    		    parseFile = (FAMAFeatureModel) reader.parseFile(fileEntry.getAbsolutePath());	    					    			
    		   // System.out.println("Diagnosis of " + fileEntry.getAbsolutePath());
    		    
    		    ////////////////////////
	    		parseFile.transformTo(reasoner);
	    		ChocoDetectErrorsQuestion Q1 = new ChocoDetectErrorsQuestion();
	    		Q1.setObservations(parseFile.getObservations());
	    		reasoner.ask(Q1);
	    		
	    		//////////////////////1.  FMDiag    	
	    		if (this.algorithm.equalsIgnoreCase("FMDiag"))
    				FMDiagExplanations(Q1, fileEntry, reasoner);
	    		
	    		//////////////////////2. Parallel FMDiag    		
	    		if (this.algorithm.equalsIgnoreCase("FMDiag-para"))
	    			ParallelFMDiagExplanations(Q1, fileEntry, reasoner);
	       		
	       		//////////////////////3. FlexDiag and 4. Parallel FlexDiag...
	    		if (this.algorithm.equalsIgnoreCase("FlexDiag") || this.algorithm.equalsIgnoreCase("FlexDiag-para")){
	    			int[] ms = {1, 2, 4, 8};
	       		
	    			for(int i=0; i< 4; i++){	
	    				if (this.algorithm.equalsIgnoreCase("FlexDiag"))
	    				   FlexDiagExplanations(Q1, fileEntry,ms[i], reasoner);
	    				
	    				if (this.algorithm.equalsIgnoreCase("FlexDiag-para"))
		    			    ParallelFlexDiagExplanations(Q1, fileEntry, ms[i], reasoner);
	    			}
	    		}
	  	 	}catch(Exception Ex){
    			System.out.println("Error: " + fileEntry.getName() + " - " +  Ex.toString());
		  	}
	    }
//		System.out.println("OK!!!!");	    	
	}	
}