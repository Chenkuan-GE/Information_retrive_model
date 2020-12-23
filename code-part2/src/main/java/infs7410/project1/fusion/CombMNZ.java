package infs7410.project1.fusion;

import infs7410.project1.TrecResult;
import infs7410.project1.TrecResults;

import java.util.HashMap;
import java.util.List;

public class CombMNZ extends Fusion {
    @Override
    public TrecResults Fuse(List<TrecResults> resultsLists) {
        HashMap<String, TrecResult> seen = new HashMap<>();
		HashMap<String, Integer> count = new HashMap<>();

        for (TrecResults trecResults : resultsLists) {
            for (TrecResult result : trecResults.getTrecResults()) {
                if (!seen.containsKey(result.getDocID())) {
                    seen.put(result.getDocID(), result);
					incrementCount(count, result);
                } else {
					incrementCount(count, result);
                    double score = seen.get(result.getDocID()).getScore();
                    result.setScore(result.getScore() + score);
                    seen.put(result.getDocID(), result);
                }
            }
        }
		for (String entryKey : seen.keySet()) {
			double adjustedScore = seen.get(entryKey).getScore() * count.get(entryKey);
			seen.get(entryKey).setScore(adjustedScore);
		}
        return flatten(seen);
    }
	
	public void incrementCount(HashMap<String, Integer> countList, TrecResult result) {
		if (!countList.containsKey(result.getDocID())) {
			countList.put(result.getDocID(), 0);
		}
		if (result.getScore() > 0) {
			countList.put(result.getDocID(), countList.get(result.getDocID()) + 1);
		}
	}
}