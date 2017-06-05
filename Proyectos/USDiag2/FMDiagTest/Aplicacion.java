package FMDiagTest;

public class Aplicacion {
	public static void main(String[] args) throws Exception {
		String pathInput = "";
	
	/*	
		System.out.println("1. To apply changes on FMs: ");
		System.out.println("java -jar USDiag.jar Change pathIn pathOut changesNumber [graphViz] [strict] [cleanOut]");
		System.out.println("--");
		System.out.println("2. To apply a Diag on a FM: ");
		System.out.println("java -jar USDiag.jar [FMDiag|FMDiag-para|FlexDiag|FlexDiagPara] fileIn [fileOut] [threadsNumber]\n");
		*/
//		System.out.println("Args " + args.length);
		
		/*
		if (args.length == 0){
			System.out.println("You need to add parameters!");
			System.out.println("1st, you must indicate the model for inconsistencies detection!");
		    System.exit(0);

		}
		
		else{	
		 */
		  pathInput = ".//ModelsErrorsFinal//Splot//models_out//";
		  InconsistentFM2 model = new InconsistentFM2(pathInput);
		  model.main();				
		  
		  pathInput = ".//ModelsErrorsFinal//Betty//models_out//";
		  InconsistentFM2 model2 = new InconsistentFM2(pathInput);
		  model2.main();				
		  

	//		}    
					
	}     
}

