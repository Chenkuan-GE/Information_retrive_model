package infs7410.project1;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LTR {

    private String trainFIle;
    public void getLTRscore(String train, String test, String out_f) throws IOException
    {
        String trainFile = train;
        String testFile = test;
        // String num_fea = "6";
        int numFeatures = 5;
        int numEpoch = 200;


        Dataset trainingSet = new Dataset(trainFile, numFeatures);
        Dataset testSet = new Dataset(testFile, numFeatures);
        LinearRanker ranker = new LinearRanker(numFeatures);


        List<double[]> trainingSetFeatureMatrix = trainingSet.getFeatureMarix();
        List<Integer> trainingSetLabelVector = trainingSet.getLabelVector();

        //ranker.getQueryReusltList(testSet);
        double last_ndcg = 0;

        int epoch = 0;
        while(epoch < numEpoch) {
            ranker.updateWeights(0.1, trainingSetFeatureMatrix, trainingSetLabelVector);
            double ndcgScore = Evaluator.ndcgAtK(testSet, ranker.getQueryReusltList(testSet), 3);
            System.out.printf("ndcg@3: %f after %d epoch %n", ndcgScore, epoch);
            if(last_ndcg == ndcgScore)
            {
                System.out.print("Converge");
                break;
            }
            else
            {
                last_ndcg = ndcgScore;
                epoch ++;
            }

        }

        // ===================================
        // TODO: 得出score

        TrecResults result_LTR = new TrecResults();
        TrecResults results = ranker.gettrecresultlist(testSet);
        results.setRunName("LTR_score");
        results.write(out_f);
    }

}
