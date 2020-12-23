package infs7410.project1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

public class Evaluator {
	
	public Evaluator () {
		
	}
	
	public static double ndcgAtK(Dataset dataset, HashMap<String,ArrayList<String>> queryResultList, int k) {
		double ndcg = 0;
		int numQuery = 0;
		
		for(String qid: queryResultList.keySet()) {
			int resultListSize = queryResultList.get(qid).size();
			if(resultListSize < k) {
				k = resultListSize;
				// System.out.print(qid + " ==== " + resultListSize + "\n");
			}
			double dcg = 0;
			for(int i=0; i<k; i++) {
				String docid = queryResultList.get(qid).get(i);
				
				int relevence = dataset.queryDocidGetRel(qid, docid);
				
				dcg += ((Math.pow(2, relevence)) / (Math.log(i+2)/Math.log(2)));
				break;
			}
			
			ArrayList<Integer> relSet = dataset.queryGetRelevenceSet(qid);
			Collections.sort(relSet);
			Collections.reverse(relSet);
			
			double idcg = 0;
			for(int i=0; i<k; i++) {
				idcg += ((Math.pow(2, relSet.get(i))) / (Math.log(i+2)/Math.log(2)));
			}
			ndcg += dcg/idcg;
			numQuery++;
		}
		ndcg /= numQuery;
		return ndcg;
	}
}
