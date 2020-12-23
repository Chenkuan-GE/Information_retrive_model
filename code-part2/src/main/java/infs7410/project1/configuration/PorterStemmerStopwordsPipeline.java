package infs7410.project1.configuration;

import org.terrier.utility.ApplicationSetup;

@SuppressWarnings("Duplicates")
public class PorterStemmerStopwordsPipeline extends TextProcessingPipeline {

    public PorterStemmerStopwordsPipeline() {
        super("porter-stopwords");
    }

    @Override
    public void Configure() {
        ApplicationSetup.setProperty("querying.processes", "terrierql:TerrierQLParser,"
                + "parsecontrols:TerrierQLToControls,"
                + "parseql:TerrierQLToMatchingQueryTerms,"
                + "matchopql:MatchingOpQLParser,"
                + "applypipeline:ApplyTermPipeline,"
                + "localmatching:LocalManager$ApplyLocalMatching,"
                + "filters:LocalManager$PostFilterProcess");
        ApplicationSetup.setProperty("querying.default.controls", "terrierql:on," +
                "parsecontrols:on," +
                "parseql:on," +
                "applypipeline:on," +
                "localmatching:on," +
                "filters:on," +
                "decorate:on");
        ApplicationSetup.setProperty("termpipelines", "PorterStemmer,Stopwords");
    }
}
