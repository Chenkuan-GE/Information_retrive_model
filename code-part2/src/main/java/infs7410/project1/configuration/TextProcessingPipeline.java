package infs7410.project1.configuration;

import org.terrier.utility.ApplicationSetup;

public abstract class TextProcessingPipeline {

    private String index;

    /**
     * This is the constructor to this abstract class. It configures standard indexing and ranking
     * parameters which do not change across the classes that extend this.
     * Note that also this is where the name of the index is configured.
     *
     * @param index The name of the index on disk.
     */
    TextProcessingPipeline(String index) {
        this.index = index;
        ApplicationSetup.setProperty("terrier.index.prefix", index);

        // The first three properties configure which XML fields Terrier will use for indexing.
        // http://terrier.org/docs/current/javadoc/org/terrier/indexing/SimpleXMLCollection.html
        ApplicationSetup.setProperty("xml.doctag", "PubmedArticle");
        ApplicationSetup.setProperty("xml.idtag", "PMID");
        ApplicationSetup.setProperty("xml.terms", "ArticleTitle,AbstractText,Keyword");

        // http://terrier.org/docs/v5.1/quickstart-integratedsearchdisk.html#configuringsearchresultenhancementtransformations
        ApplicationSetup.setProperty("querying.postfilters", "decorate:org.terrier.querying.SimpleDecorate");

        ApplicationSetup.setProperty("matching.retrieved_set_size", "1000");
        ApplicationSetup.setProperty("trec.output.format.length", "1000");

        ApplicationSetup.setProperty("trec.topics", "./QUERIES.txt");
        ApplicationSetup.setProperty("trec.topics.parser", "SingleLineTRECQuery");
    }

    public abstract void Configure();

    public String index() {
        return this.index;
    }
}
