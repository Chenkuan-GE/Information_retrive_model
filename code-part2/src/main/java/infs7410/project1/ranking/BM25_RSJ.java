package infs7410.project1.ranking;

//import com.sun.tools.sjavac.comp.dependencies.PublicApiCollector;
import org.terrier.matching.models.WeightingModel;
import org.terrier.structures.Index;
import org.terrier.structures.Lexicon;
import org.terrier.structures.LexiconEntry;

import java.util.List;

public class BM25_RSJ extends WeightingModel {
    private static double a = 0.75;
    private double r;//number of relevant documents that contains the term.
    private double R = 0;//number of relevant documents.
    private double N = numberOfDocuments;//number of documents in the collection.
    private double n = documentFrequency;//number of documents that contain the term.

    private double k1 = 1.2;
    private double b = 0.75;
    private double k_3 = 8.0D;

    public BM25_RSJ(){
        super();
        //this.r = r;
    }

    // to get the number of relevant documents
    // we might get the recall and precision based on
    // precision = |relevant & retrieved| / |retrieved|
    // recall  = |relevant & retrieved| / |relevant|

    // and based on these two formula we could get the relevant documents which contains terms(r)
    // and total number of relevant documents in collection we might able to use terrier api which called RelevantFeedbackDocument(R)



    @Override
    public void setParameter(double _c) {
        a =_c;
    }

    @Override
    public String getInfo() {
        return "BM25_RSJ";
    }

    @Override
    public double score(double docLength, double tf) {
        double b = a;
        R = 1482;
        r = (int)(1+Math.random()*(20-1+1));
        double rsj = Math.log((R - r + 0.5) / (r + 0.5));
        double numerator = tf * (k1 + 1);
        double denominator = tf + (k1 * (1 - b + (b * docLength / averageDocumentLength)));
        double last =  (this.k_3 + 1.0D) * this.keyFrequency / (this.k_3 + this.keyFrequency);

        return rsj * numerator / denominator * last;
    }
}