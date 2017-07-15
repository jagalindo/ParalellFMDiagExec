package FMDiagTest;

public class Aplicacion {
	public static void main(String[] args) throws Exception {
		String pathInput = "";
		String pathOutput = "";
		
		
		System.out.println("Args " + args.length);
		
		if (args.length == 0){
			System.out.println("You need to add parameters!");
			System.out.println("1st, you must indicate an operation [Change, Redundancy, Redundancy-Para, FMDiag, FMDiag-Para, FlexDiag, or FlexDiag-Para");
		    System.exit(0);
		}
		
		else if (args.length<2){
			if (args[0].equalsIgnoreCase("Change")){
				System.out.println("You need to indicate the path for input and output directories along with the number of changes (NC)!");				
				System.out.println("(by default, NC = 15)!");
			}
			else if (args[0].equalsIgnoreCase("FMDiag") || args[0].equalsIgnoreCase("FlexDiag"))
				System.out.println("You need to indicate the input file path at Least!");				
			
			else if (args[0].equalsIgnoreCase("FMDiag-Para") || args[0].equalsIgnoreCase("FlexDiag-Para")){
				System.out.println("You need to indicate the input file path and the number of Threads (NT)");
				System.out.println("(by default, NT = 4)!");
			}
			else if (args[0].equalsIgnoreCase("Redundancy")){
				System.out.println("You need to indicate the path for input and output directories!");				
				System.out.println("(by default, NC = 15)!");
			}
			else if (args[0].equalsIgnoreCase("Redundancy-Para")){
				System.out.println("You need to indicate the path for input and output directories along with the number of Threads (NT)!");				
				System.out.println("(by default, NT = 4)!");
			}				
		}
		else{				 
	 		  if (args[0].equalsIgnoreCase("FMDiag") || args[0].equalsIgnoreCase("FlexDiag")){
	 			  String out = (args.length > 2) ? args[2] : "";
				  
	 			  reviewFMDiagApplication review = new reviewFMDiagApplication(args[0], args[1], out);
				  review.main();
				 }
			  else if (args[0].equalsIgnoreCase("FMDiag-Para") || args[0].equalsIgnoreCase("FlexDiag-Para")){
				  String out = (args.length > 2) ? args[2] : "";
				  
				  int NT = (args.length > 3) ? Integer.parseInt(args[3]) : 4;
				  NT = (NT <= 0) ? 4 : NT;
				  
					
				  reviewFMDiagApplication review2 = new reviewFMDiagApplication(args[0], args[1], out, NT);
				  review2.main();
			  }				
			  else if (args[0].equalsIgnoreCase("Change")){
				  pathInput = args[1];
				  pathOutput = args[2];

				  int NC = (args.length > 3) ? Integer.parseInt(args[3]) : 15;
				  NC = (NC <= 0) ? 15 : NC;
							  
				  boolean graph = (args.length > 4) ? Boolean.parseBoolean(args[4]) : false;					
				  boolean strict = (args.length > 5) ? Boolean.parseBoolean(args[5]) : false;
				  boolean clean = (args.length > 6) ? Boolean.parseBoolean(args[6]) : true;
		
			//	  System.out.println("In " + pathInput + ", Out " + pathOutput);
				  
				  InconsistentFM2 model = new InconsistentFM2(pathInput, pathOutput, NC, graph, strict, clean);
				  model.main();				
				}    
			  else if (args[0].equalsIgnoreCase("Redundancy")){
				  String out = (args.length > 2) ? args[2] : "";
				  
	 			  reviewFMRedundancy review3 = new reviewFMRedundancy(args[0], args[1], out);
				  review3.main();
				 
			  }
			  else if (args[0].equalsIgnoreCase("Redundancy-Par")){
				  String out = (args.length > 2) ? args[2] : "";
				  
				  int NT = (args.length > 3) ? Integer.parseInt(args[3]) : 4;
				  NT = (NT <= 0) ? 4 : NT;
				  
					
				  reviewFMRedundancy review4 = new reviewFMRedundancy(args[0], args[1], out, NT);
				  review4.main();					
			  }
			  else{
					  System.out.println("Incorrent 1st argsument!");
				}					
			}				
	}     
}