package infs7410.project1.configuration;

import org.terrier.utility.ApplicationSetup;

@SuppressWarnings("Duplicates")
public class NoProcessing extends TextProcessingPipeline {

    public NoProcessing() {
        super("no_processing");
    }

    @Override
    public void Configure() {
        ApplicationSetup.setProperty("querying.processes", "terrierql:TerrierQLParser,"
                + "parsecontrols:TerrierQLToControls,"
                + "parseql:TerrierQLToMatchingQueryTerms,"
                + "matchopql:MatchingOpQLParser,"
                + "localmatching:LocalManager$ApplyLocalMatching,"
                + "filters:LocalManager$PostFilterProcess");
        ApplicationSetup.setProperty("querying.default.controls", "terrierql:on," +
                "parsecontrols:on," +
                "parseql:on," +
                "localmatching:on," +
                "filters:on," +
                "decorate:on");
        ApplicationSetup.setProperty("termpipelines", "");
    }
}
