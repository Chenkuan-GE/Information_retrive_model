package infs7410.project1;


import infs7410.project1.ranking.TF_IDF;
import infs7410.project1.ranking.BM25;
import infs7410.project1.ranking.Jelinek_Mercer;
import infs7410.project1.ranking.DirichletLM;
import infs7410.project1.ranking.BM25_RSJ;

import infs7410.project1.fusion.Borda;
import infs7410.project1.fusion.CombMNZ;
import infs7410.project1.fusion.CombSUM;
import infs7410.project1.fusion.Fusion;

import infs7410.project1.normalisation.*;



import org.apache.log4j.Logger;
import org.terrier.applications.batchquerying.TRECQuerying;
import org.terrier.indexing.Collection;
import org.terrier.indexing.SimpleFileCollection;
import org.terrier.indexing.TRECCollection;
import org.terrier.matching.models.WeightingModel;
import org.terrier.querying.IndexRef;
import org.terrier.structures.Index;


import java.io.*;
import java.nio.file.Paths;
import java.util.*;


import org.terrier.structures.IndexFactory;
import org.terrier.structures.Lexicon;
import org.terrier.structures.LexiconEntry;
import org.terrier.structures.indexing.Indexer;
import org.terrier.structures.indexing.classical.BasicIndexer;
import org.terrier.utility.ApplicationSetup;


public class Project1 {

    static Logger logger = Logger.getLogger(infs7410.project1.Project1.class);

    public static void main(String[] args) throws IOException, Exception {
	/* Takes up to one argument indicating whether to use fusion methods.
	 * If no argument is given, or the argument is false, then only normal retrievals are run.
	 * All retrieval methods are run, regardless of files existing.
	 *
	 * If the argument is true, fusion methods are run on the .res files from each retrieval methods.
	 * Existing .res files are used as-is and not recomputed, however any missing files will be computed.
	 * .res files for fusion should be named the same as normal output files i.e. weightingModel.getClass().getSimpleName() + ".res"
	 * and found in the resultData location.
	 */
		// the index part
		// we did it by command line and the text processing was used PorterStemmer
		// You could check more information on our github [https://github.com/UQ-infs7410-2020/team-4/tree/master/project]


	 
		// Modify these to pick query list and .res file location
		// the train and test files were located at the folder which named resources
		String queryData = "./resources/train_set.small.txt";
		String resultData = "./var/results/";
		
		
		boolean doFusion;
		if (args.length == 0) {
			doFusion = false;
		} else if (args[0].equals("true")) {
			doFusion = true;
		} else if(args[0].equals("false")) {
			doFusion = false;
		} else {
			throw new Exception("Only accepts \"true\" or \"false\" as an argument.");
		}
		

        ApplicationSetup.setProperty("TrecDocTags.doctag", "DOC");
        ApplicationSetup.setProperty("TrecDocTags.idtag", "DOCNO");
        ApplicationSetup.setProperty("TrecDocTags.skip", "DOCHDR");

        // https://github.com/terrier-org/terrier-core/blob/5.x/doc/quickstart-integratedsearchdisk.md#configuring-search-result-enhancementtransformations
        ApplicationSetup.setProperty("querying.postfilters", "decorate:org.terrier.querying.SimpleDecorate");
        // https://github.com/terrier-org/terrier-core/blob/5.x/doc/quickstart-integratedsearchdisk.md#configuring-the-query-process
        ApplicationSetup.setProperty("querying.processes", "terrierql:TerrierQLParser,"
                + "parsecontrols:TerrierQLToControls,"
                + "parseql:TerrierQLToMatchingQueryTerms,"
                + "matchopql:MatchingOpQLParser,"
                + "applypipeline:ApplyTermPipeline,"
                + "localmatching:LocalManager$ApplyLocalMatching,"
                + "filters:LocalManager$PostFilterProcess");
        // https://github.com/terrier-org/terrier-core/blob/5.x/doc/configure_general.md#configuring-overview
        ApplicationSetup.setProperty("querying.default.controls", "terrierql:on," +
                "parsecontrols:on," +
                "parseql:on," +
                "applypipeline:on," +
                "localmatching:on," +
                "filters:on," +
                "decorate:on");

		
        
		List<WeightingModel> models = new ArrayList<>();
		// Add or comment out whichever models you want to run
		//models.add(new TF_IDF());
		models.add(new BM25());
		// TODO: Add pseudo-relevance feedback
		//models.add(new Jelinek_Mercer());
		//models.add(new DirichletLM());

		// we have created the model of BM25_RSJ but not sure to use
		// it could be able to check it in ranking folder
		

		List<Fusion> fusions = new ArrayList<>();
		//Add or comment out whichever fusion methods you want. Fusion methods will only run if "true" is given as an argument to main.
		fusions.add(new Borda());
		fusions.add(new CombSUM());
		fusions.add(new CombMNZ());
		
		
		System.out.println("----- Start Working --------");
		
		List<String> resultsFiles = new ArrayList<>();
		for (WeightingModel model : models) {
			// Runs each model in the list			
			String resDir = resultData+model.getClass().getSimpleName()+".res";
			resultsFiles.add(resDir);
			File resFile = new File(resDir);
			if (!(resFile.exists() && doFusion)) {
			batchRetrieval(model, queryData, resDir);
			System.out.println(model.getClass().getSimpleName()+" completed successfully.");
			} else {
				System.out.println(model.getClass().getSimpleName()+".res found, skipping computation.");
			}
		}
		System.out.println("All batch retrievals successfully finished.");
		
		// Return if not doing fusion methods
		if (!doFusion) {
			return;
		}
		
		System.out.println("Beginning fusion methods.");

		String fusResDir = "var/results/BM25.res";
		for (Fusion fusion : fusions) {
			// Runs each fusion method in the list
			fusResDir = resultData + fusion.getClass().getSimpleName() + ".res";
			//File fusResFile = new File(fusResDir);
			start_fusion(fusion, resultsFiles, fusResDir);
			System.out.println(fusion.getClass().getSimpleName()+" completed successfully.");
		}
		System.out.println("All fusion methods successfully finished.");


		// =====================================================================
		System.out.println("Start to get the dataset for learning to rank");

		GetDataset dataset = new GetDataset();
		String input_txt = "resource/dev_set.txt";
		String bm25_res = fusResDir;
		String output_file = "output_dev.txt";

		dataset.getdata(bm25_res, input_txt, output_file);

		System.out.println("The needed dataset has been created successfully");


		// ======================================================================
		System.out.println("Start to get result file for learning to rank");

		LTR ltr_score = new LTR();
		String trainfile = "output_dev.txt";	// could change to output train file if get the dataset
		String testfile = output_file;
		String outputfile = "ltr_dev.res";
		ltr_score.getLTRscore(trainfile, testfile, outputfile);

		System.out.println("The LTR result file has been created successfully");




	}

	public static void batchRetrieval(WeightingModel weightingModel, String queryDir, String resultDir) throws IOException {
		// Method for running a weighting model on a query (topic) list
		// queryDir and resultDir are the directories of the query list and the result (output) file

        ApplicationSetup.setProperty("trec.topics", queryDir);
        ApplicationSetup.setProperty("trec.topics.parser", "SingleLineTRECQuery");
        ApplicationSetup.setProperty("trec.results.file", resultDir);
        ApplicationSetup.setProperty("trec.model", weightingModel.getClass().getName());
        System.out.println(weightingModel.getClass().getSimpleName());
        TRECQuerying querying = new TRECQuerying(IndexRef.of("./var/index/data.properties"));
        querying.intialise();
        querying.processQueries();
      
	}


    public static void start_fusion(Fusion fusion, List<String> resultsFiles, String outputFile) throws IOException {
        // Parse each of the results files and add them to a list.
        List<TrecResults> results = new ArrayList<>(resultsFiles.size());
        for (String filename : resultsFiles) {
            results.add(new TrecResults(filename));
        }

        // Set the normaliser and fusion method to use.
        Normaliser norm = new MinMax();


        // Normalise the scores of each run.
        for (TrecResults trecResults : results) {
            norm.init(trecResults);
            for (int j = 0; j < trecResults.getTrecResults().size(); j++) {
                double normScore = norm.normalise(trecResults.getTrecResults().get(j));
                trecResults.getTrecResults().get(j).setScore(normScore);
            }
        }

        logger.info("fusing results for each topic");

        Set<String> topics = results.get(0).getTopics();
        TrecResults fusedResults = new TrecResults();
        for (String topic : topics) {
            //logger.info(topic);
            List<TrecResults> topicResults = new ArrayList<>();
            for (TrecResults r : results) {
                topicResults.add(new TrecResults(r.getTrecResults(topic)));
            }

            // Fuse the results together and write the new results list to disk.
            fusedResults.getTrecResults().addAll(fusion.Fuse(topicResults).getTrecResults());
        }

        //logger.info("writing results to disk");

        fusedResults.setRunName("fused");
        fusedResults.write(outputFile);
    }
        //  ==============================================================

    
}
