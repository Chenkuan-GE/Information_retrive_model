package infs7410.project1;

import infs7410.project1.ranking.*;
import org.terrier.querying.IndexRef;
import org.terrier.structures.*;
import org.terrier.structures.postings.IterablePosting;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GetDataset {

    private String topic;

    public void getdata(String bm25_res, String txtfile, String output_filename) throws IOException
    {
        InputStream res_bm25 = new FileInputStream(bm25_res);
        BufferedReader br_bm25 = new BufferedReader(new InputStreamReader(res_bm25));
        String new_line;
        HashMap<String, String> docidSet = new HashMap<>();

        while((new_line = br_bm25.readLine()) != null ){

            String[] tokens_new = new_line.split(" ");

            String new_qid = tokens_new[0];
            String new_docid = tokens_new[2];
            docidSet.put(new_docid, new_qid);
        }
        res_bm25.close();


        BM25 weightingModel = new BM25();
        TF_IDF tf_idf_wm = new TF_IDF();
        DirichletLM dl_wm = new DirichletLM();
        Jelinek_Mercer jm_wm = new Jelinek_Mercer();
        BM25_RSJ rsj_wm = new BM25_RSJ();

        Index index = IndexFactory.of(IndexRef.of("./var/index/data.properties"));
        PostingIndex invertedIndex = index.getInvertedIndex();
        Lexicon<String> lexicon = index.getLexicon();
        MetaIndex meta = index.getMetaIndex();

        String train_path = txtfile;
        InputStream train_txt = new FileInputStream(train_path);
        BufferedReader buf_train = new BufferedReader((new InputStreamReader(train_txt)));

        File dataset_out = new File(output_filename);
        OutputStream output = new FileOutputStream(dataset_out);

        List<Topic> topics = new ArrayList<>();
        String read_line = buf_train.readLine();
        HashMap<String, Integer> query_dict = new HashMap<>();

        while (read_line != null)
        {
            String[] parts = read_line.split("\\s+");
            topics.add(new Topic(parts[0], String.join(" ", Arrays.copyOfRange(parts, 1, parts.length))));
            if (!query_dict.containsKey(parts[0]))
            {
                query_dict.put(parts[0], parts.length - 1);
            }
            read_line = buf_train.readLine();
        }

        for (Topic t: topics)
        {
            // TODO: Get the information of QID, LENGTH OF QUERY
            String qid = t.getTopic();
            String query = t.getQuery();
            String[] query_terms = query.split(" ");
            int len_query = query_terms.length;

            weightingModel.setCollectionStatistics(index.getCollectionStatistics());
            tf_idf_wm.setCollectionStatistics(index.getCollectionStatistics());
            dl_wm.setCollectionStatistics(index.getCollectionStatistics());
            jm_wm.setCollectionStatistics(index.getCollectionStatistics());
            rsj_wm.setCollectionStatistics(index.getCollectionStatistics());

            int stop = 0;

            for (String q: query_terms)
            {
                LexiconEntry entry = lexicon.getLexiconEntry(q);



                if (entry == null) {
                    continue; // This term is not in the index, go to next document.
                }

                weightingModel.setEntryStatistics(entry.getWritableEntryStatistics());
                tf_idf_wm.setEntryStatistics(entry.getWritableEntryStatistics());
                dl_wm.setEntryStatistics(entry.getWritableEntryStatistics());
                jm_wm.setEntryStatistics(entry.getWritableEntryStatistics());
                rsj_wm.setEntryStatistics(entry.getWritableEntryStatistics());

                // Set the number of times the query term appears in the query.
                double kf = 0.0;
                for (String otherTerm : query_terms) {
                    if (otherTerm.equals(q)) {
                        kf++;
                    }
                }
                weightingModel.setKeyFrequency(kf);
                tf_idf_wm.setKeyFrequency(kf);
                dl_wm.setKeyFrequency(kf);
                jm_wm.setKeyFrequency(kf);
                rsj_wm.setKeyFrequency(kf);

                // Prepare the weighting model for scoring.
                weightingModel.prepare();
                tf_idf_wm.prepare();
                dl_wm.prepare();
                jm_wm.prepare();
                rsj_wm.prepare();

                IterablePosting ip = invertedIndex.getPostings(entry);
                // double score = 0.0;
                // int count = 0;
                while (ip.next() != IterablePosting.EOL)
                {
                    String docId = meta.getItem("docno", ip.getId());
                    if (docidSet.containsKey(docId)) {

//                        System.out.print("DocNO: " + docId + " ------- with doc length: " + ip.getDocumentLength() + "\n");
//                        System.out.print("The score for BM25" + weightingModel.score(ip) + "\n");
//                        System.out.print("The score for TFIDF" + tf_idf_wm.score(ip) + "\n");
//                        System.out.print("The score for DL" + dl_wm.score(ip) + "\n");
//                        System.out.print("The score for JM" + jm_wm.score(ip) + "\n");
//                        System.out.print("qid: "+docidSet.get(docId) + " ----- with the length: " + query_dict.get(docidSet.get(docId)) + " \n");
                        // System.out.print(qid + ": " + len_query + " \n");
                        // Set Parameter
                        // String QID = docidSet.get(docId);
                        if (qid.equals(docidSet.get(docId)))
                        {
                            String QID = qid;
                            double bm25_score = weightingModel.score(ip);
                            double tfidf_score = tf_idf_wm.score(ip);
                            double dl_score = dl_wm.score(ip);
                            double jm_score = jm_wm.score(ip);
                            double rsj_score = rsj_wm.score(ip);
                            String DocNo = docId;
                            int doc_length = ip.getDocumentLength();
                            int query_length = len_query;
                            // int query_length = query_dict.get(docidSet.get(docId));
                            int unique_terms = ip.getFrequency();
                            stop++;
                            // count++;
                            output.write(String.format("0 qid:%s 1:%s 2:%s 3:%s 4:%s 5:%s 6:%d 7:%d 8:%d #docid:%s\n", QID, bm25_score,tfidf_score, dl_score, jm_score, rsj_score, query_length, doc_length, unique_terms, DocNo).getBytes());
                        }
                    }

                }
            }

        }

        output.close();
    }

}
