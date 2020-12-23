package infs7410.project1.ranking;

import org.terrier.matching.models.WeightingModel;

public class Jelinek_Mercer extends WeightingModel {
    private double lambda = 0.6;

    @Override
    public String getInfo() {
        return "Jelinek_Mercer";
    }

    @Override
    public double score(double tf, double docLength) {

        return Math.log((1-lambda)*tf /docLength + lambda * termFrequency/numberOfTokens);
    }
}
