package codeGenerator;
import java.io.File;
import java.util.Random;

import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.generator.FM.AbstractFMGenerator;
import es.us.isa.generator.FM.FMGenerator;
import es.us.isa.generator.FM.GeneratorCharacteristics;
import es.us.isa.utils.BettyException;
import es.us.isa.utils.FMWriter;

public class Generator {
	
	private static Integer numModels = 20;
	private static Integer minFeatures = 9000;
	private static Integer maxFeatures = 15000;
	
	private static String path = "E:\\Doctorado\\Experiments\\Base-Betty\\";

	public static void main(String[] args) throws Exception, BettyException {
	
		/////Cleaning Path 1st
		File file = new File(path);
        File[] files = file.listFiles(); 
    
        for (File f: files) 
             f.delete();        
        //////////////////////////////////////

		for(int i=0;i < numModels; i++){
			
			Random r = new Random();
			int featuresNumber = r.nextInt(maxFeatures - minFeatures) + minFeatures;
			float CTC = (float) (Math.random() * 100);
			
// STEP 1: Specify the user's preferences for the generation (so-called characteristics)
			GeneratorCharacteristics characteristics = new GeneratorCharacteristics();
			characteristics.setNumberOfFeatures(featuresNumber);		// Number of features
			characteristics.setPercentageCTC(CTC);			
			// Percentage of cross-tree constraints.
	 
// STEP 2: Generate the model with the specific characteristics (FaMa FM metamodel is used)
			AbstractFMGenerator generator = new FMGenerator();
			FAMAFeatureModel fm = (FAMAFeatureModel) generator.generateFM(characteristics);
	 
			/*
// OPTIONAL: Show detailed statistics of the feature model generated
			FMStatistics statistics = new FMStatistics(fm);
			System.out.println(statistics);
			 */
			
			// STEP 3: Save the model
			FMWriter writer = new FMWriter();
			writer.saveFM(fm,  path + "model" + ((Integer)i).toString() +  ".xml"); 
	 		// TODO Auto-generated method stub
			
			System.out.println("model" + ((Integer)i).toString() +  ".xml Created (" + featuresNumber + "," + CTC + "%)");
		}
		
		System.out.println("OK!!!!");
	}
}
