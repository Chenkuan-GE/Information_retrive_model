package infs7410.project1.ranking;

import org.terrier.matching.models.WeightingModel;

public class TF_IDF extends WeightingModel {

    public TF_IDF() {
        super();
    }

    @Override
    public String getInfo() {
        return "tf-idf";
    }

    @Override
    public double score(double tf, double docLength) {
		double invDocFreq = Math.log(numberOfDocuments / (1 + documentFrequency));
        return tf * invDocFreq;
    }
}
