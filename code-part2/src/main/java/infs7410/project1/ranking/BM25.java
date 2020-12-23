package infs7410.project1.ranking;

import org.terrier.matching.models.WeightingModel;

public class BM25 extends WeightingModel {

    private double k1 = 1.1;
    private double b = 0.5;

    public BM25() {
        super();
    }

    @Override
    public String getInfo() {
        return "BM25";
    }

    @Override
    public double score(double tf, double docLength) {
        double numerator = tf * (k1 + 1);
		double denominator = tf + (k1 * (1 - b + (b * docLength / averageDocumentLength)));
		double invDocFreq = Math.log(numberOfDocuments / (documentFrequency + 1));
        return invDocFreq * numerator / denominator;
    }
}
