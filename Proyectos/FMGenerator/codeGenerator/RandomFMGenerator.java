package codeGenerator;
import java.util.Random;

import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.generator.FM.FMGenerator;
import es.us.isa.generator.FM.GeneratorCharacteristics;
import es.us.isa.generator.FM.MetamorphicFMGenerator;
import es.us.isa.utils.BettyException;
import es.us.isa.utils.FMStatistics;
import es.us.isa.utils.FMWriter;

/* This example shows how to generate a feature model and its corresponding set of products. 
 * The set of products is generated at the same
 * time that the model and does not use any analysis tool. 
 * Both, the model and its set of products can be used for functional testing.
 * For more details, please go to the BeTTy Website.
 */

public class RandomFMGenerator {

	private static String path = "E:\\Doctorado\\Experiments\\Base-Betty\\";

	private static Integer numModels = 10;
	private static Integer minFeatures = 60;
	private static Integer maxFeatures = 100;

	public static void main(String[] args) throws Exception, BettyException {
		
		for(int i=0;i < numModels; i++){

			Random r = new Random();
			int featuresNumber = r.nextInt(maxFeatures - minFeatures) + minFeatures;

			// STEP 1: Specify the user's preferences for the generation (characteristics)
			GeneratorCharacteristics characteristics = new GeneratorCharacteristics();
			characteristics.setNumberOfFeatures(featuresNumber);			// Number of features.
			characteristics.setPercentageCTC(50);				// Percentage of constraints.
			characteristics.setProbabilityMandatory(25);		// Probability of a feature being mandatory
			characteristics.setProbabilityOptional(25);			// Probability of a feature being optional.
			characteristics.setProbabilityOr(25);				// Probability of a feature being in an or-relation.
			characteristics.setProbabilityAlternative(25);		// Probability of a feature being in an alternative relation.
			characteristics.setMaxBranchingFactor(20);			// Max branching factor (default value = 10)
			characteristics.setMaxSetChildren(20);				// Max number of children in a set relationship (default value = 5)
			
			// Max number of products of the feature model to be generated. Too large values could cause memory overflows or the program getting stuck.
			characteristics.setMaxProducts(10000);			
			
			// STEP 2: Generate the model with the specific characteristics (FaMa FM metamodel is used)
			FMGenerator fmGen = new FMGenerator();
			MetamorphicFMGenerator generator = new MetamorphicFMGenerator(fmGen);
			FAMAFeatureModel fm = (FAMAFeatureModel) generator.generateFM(characteristics);
			
			System.out.println("Features Number " + fm.getFeaturesNumber() + " - " + featuresNumber);
			 
			// OPTIONAL: Show detailed statistics of the feature model generated
			FMStatistics statistics = new FMStatistics(fm);
			System.out.println(statistics);
			System.out.println("Number of products of the feature model generated: " + generator.getNumberOfProducts());
			
			// STEP 3: Save the model and the products
			FMWriter writer = new FMWriter();
			writer.saveFM(fm,  path + "model" + ((Integer)i).toString() +  ".xml"); 
	
			
			writer.saveProducts(generator.getPoducts(), path + "products" + ((Integer)i).toString() + ".csv");
			
			///// Step 4: Dead Features
			
			
		}
	}

}