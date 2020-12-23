package infs7410.project1;

import org.terrier.querying.*;
import org.terrier.structures.*;
import org.terrier.structures.postings.IterablePosting;
import org.terrier.utility.ApplicationSetup;

import java.io.IOException;
import java.util.*;

public class PrfExpansion {

    public String expand(IndexRef ref, Topic t, int topK, int numTerms) throws IOException {


        System.out.println("----- Start Working PRF--------");





        // Run a search request using the original query.
        Manager queryManager = ManagerFactory.from(ref);
        SearchRequest srq = queryManager.newSearchRequestFromQuery(t.getQuery());
        srq.setControl(SearchRequest.CONTROL_WMODEL, "BM25");
        queryManager.runSearchRequest(srq);
        ScoredDocList resultsList = srq.getResults();

        Index index = IndexFactory.of(ref);
        //Index index = Index.createIndex("./var/index","data");
        //if index == null:

        Lexicon<String> lexicon = index.getLexicon();
        PostingIndex direct = index.getDirectIndex();
        DocumentIndex document = index.getDocumentIndex();

        // This list will contain all of the potential query terms.
        Map<Integer, ExpansionTerm> expansionTerms = new HashMap<>();

        for (int i = 0; i < resultsList.size() && i < topK; i++) {
            ScoredDoc doc = resultsList.get(i);

            // Get the document from the document index.
            DocumentIndexEntry entry = document.getDocumentEntry(doc.getDocid());
            IterablePosting ip = direct.getPostings(entry);

            // Iterating over all terms in the document.
            while (ip.next() != IterablePosting.EOL) {
                // Get collection term statistics.
                Map.Entry<String, LexiconEntry> termEntry = lexicon.getLexiconEntry(ip.getId());

                // Add the term to the list of possible expansion terms.
                double documentTf = ip.getFrequency();
                if (!expansionTerms.containsKey(ip.getId())) {
                    expansionTerms.put(ip.getId(), new ExpansionTerm(termEntry.getKey(), documentTf, 1, topK));
                } else {
                    ExpansionTerm term = expansionTerms.get(ip.getId());
                    term.setDocumentFreq(term.getDocumentFreq() + 1);
                    expansionTerms.put(ip.getId(), term);
                }
            }
        }

        List<ExpansionTerm> terms = new ArrayList<>(expansionTerms.values());
        Collections.sort(terms);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numTerms; i++) {
            sb.append(terms.get(i).getTerm()).append(" ");
        }

        return t.getQuery() + " " + sb.toString();
    }


    public class ExpansionTerm implements Comparable<ExpansionTerm> {
        private String term;
        private double documentTf;
        private double documentFreq;
        private double N;

        public ExpansionTerm(String term, double documentTf, double documentFreq, double N) {
            this.term = term;
            this.documentTf = documentTf;
            this.documentFreq = documentFreq;
            this.N = N;
        }

        public String getTerm() {
            return term;
        }

        public double getDocumentTf() {
            return documentTf;
        }

        public double getDocumentFreq() {
            return documentFreq;
        }

        public void setDocumentTf(double documentTf) {
            this.documentTf = documentTf;
        }

        public void setDocumentFreq(double documentFreq) {
            this.documentFreq = documentFreq;
        }

        public double score() {
            return documentTf * Math.log(N / (documentFreq + 1));
        }

        @Override
        public int compareTo(ExpansionTerm o) {
            return Double.compare(score(), o.score());
        }
    }

}
