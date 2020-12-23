package infs7410.project1.ranking;

import org.terrier.matching.models.WeightingModel;

public class DirichletLM extends WeightingModel {

    public DirichletLM() {
        super();
    }

    private double mu = 2500;
//    @Override
//    public void setParameter(double _c) {
//        this.c = _c;
//    }

    @Override
    public String getInfo() {
        return "DirichletLM";
    }

    @Override
    public double score(double tf, double docLength) {
        return Math.log((tf + mu * termFrequency/numberOfTokens)/(docLength + mu));
    }
//
//    @Deprecated
//    public double score(double tf, double docLength,double n_t,
//                        double F_t,
//                        double keyFrequency) {
//
//        return 0;
//    }
}
