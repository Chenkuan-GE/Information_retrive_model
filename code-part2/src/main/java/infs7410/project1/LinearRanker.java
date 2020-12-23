package infs7410.project1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class LinearRanker {
	private double[] weightVector;
	private int numFeatures;

	public LinearRanker(int numFeatures) {

		this.weightVector = new double[numFeatures];
		this.numFeatures = numFeatures;

		// random initialize weights
		Random r = new Random();
		for(int i=0; i<numFeatures; i++) {
			this.weightVector[i] = r.nextGaussian() * 0.1;
		}
	}



	/**
	* Compute sigmoid hypothesis function.
	*
	* @param a training sample vector, size: (1 * numFeatures).
	* @return a double between 0 and 1.
	*/
	public double computeHypothesis(double[] X) {
		double hx = 0;

		for(int i=0; i<this.numFeatures; i++) {
			hx += this.weightVector[i] * X[i];
		}
		hx = 1 / (1 + Math.pow(Math.E,-hx));

		return hx;
	}

	/**
	* Compute gradient for given feature weight index.
	*
	* @param List<double[]> featureMatrix, training set matrix, size: (numRows * numFeatures).
	* @param List<Integer> labelVector, truth label vector of training set, size: (1 * numRows).
	* @param int weightIndex, indicate which feature weight need to be update.
	* @return the gradient of given feature weight.
	*/
	public double computeGradient(List<double[]> featureMatrix, List<Integer> labelVector, int weightIndex) {
		double gradient = 0;
		int size = labelVector.size();
		for(int i=0; i<size; i++) {
			double[] x =featureMatrix.get(i);
			gradient += (this.computeHypothesis(x) - labelVector.get(i)) * x[weightIndex];
		}
		gradient /= size;
		return gradient;
	}

	/**
	* Update ranker's feature weights.
	*
	* @param double learningRate, learning rate of gradient descent.
	* @param List<double[]> featureMatrix, training set feature matrix, size: (numRows * numFeatures).
	* @param List<Integer> labelVector, truth label vector of training set, size: (1 * numRows).
	*/
	public void updateWeights(double learningRate, List<double[]> featureMatrix, List<Integer> labelVector) {
		double[] tempWeightVector = this.weightVector.clone();
		for(int i=0; i<this.numFeatures; i++) {
			tempWeightVector[i] -= learningRate * this.computeGradient(featureMatrix, labelVector, i);
		}

		this.weightVector = tempWeightVector;
	}



	public double computeCost(List<double[]> featureMatrix, List<Integer> labelVector) {
		double cost = 0;
		int size = labelVector.size();
		for(int i=0; i<size; i++) {
			if(labelVector.get(i)==0) {
				cost += -Math.log(1 - this.computeHypothesis(featureMatrix.get(i)));
			}else {
				cost += -Math.log(this.computeHypothesis(featureMatrix.get(i)));
			}
		}
		cost /= size;
		return cost;
	}

	public HashMap<String,ArrayList<String>> getQueryReusltList(Dataset dataset) {
		HashMap<String,ArrayList<String>> queryResultLists = new HashMap<String,ArrayList<String>>();
		//System.out.println("======================");
		for(String qid:dataset.getAllQid()) {
			List<String> docList = dataset.queryGetDocIds(qid);
			List<double[]> featureList = dataset.queryGetFeatures(qid);
			List<Double> scroes = this.computeScores(featureList);
			Map<String, Double> map = new HashMap<String, Double>();
			//System.out.println(scroes + "%n");

			for(int i=0; i<docList.size(); i++) {
				map.put(docList.get(i), scroes.get(i));
			}

			List<Entry<String, Double>> list = new ArrayList<>(map.entrySet());
	        list.sort(Entry.comparingByValue());
	        Collections.reverse(list);


	        ArrayList<String> resultList = new ArrayList<String>();
	        for(Entry<String, Double> entry: list) {
	        	resultList.add(entry.getKey());
	        }

	        queryResultLists.put(qid, resultList);
		}

		return queryResultLists;
	}

	private List<Double> computeScores(List<double[]> featureList){
		List<Double> scores = new ArrayList<Double>();
		for(double[] features : featureList ) {
			double score = 0;
			for(int i=0; i<this.numFeatures; i++) {
				score += features[i] * this.weightVector[i];
			}
			scores.add(score);
		}

		return scores;
	}

	private List<Double> computeScores2(List<double[]> featureList){
		List<Double> scores = new ArrayList<Double>();
		for(double[] features : featureList ) {
			double score = 0;
			for(int i=0; i<this.numFeatures; i++) {
				score += features[i] * this.weightVector[i];
			}
			scores.add(Math.abs(score));
		}

		return scores;
	}

//	public double compute_suit_score(List<double[]> featureList){
//		List<Double> scores = new ArrayList<Double>();
//		for(double[] features : featureList ) {
//			double score = 0;
//			for(int i=0; i<this.numFeatures; i++) {
//				score += features[i] * this.weightVector[i];
//			}
//			scores.add(score);
//		}
//
//		double total_score = 0;
//		int size = featureList.size();
//		for(double s : scores)
//		{
//			total_score += s;
//		}
//		total_score = total_score/size;
//
//		return total_score;
//	}

	public TrecResults gettrecresultlist(Dataset dataset) {
		TrecResults queryResultLists = new TrecResults();
		// HashMap<String, Double> map = new HashMap<>();
		//System.out.println("======================");
		for(String qid:dataset.getAllQid()) {
			List<String> docList = dataset.queryGetDocIds(qid);
			List<double[]> featureList = dataset.queryGetFeatures(qid);
			List<Double> scroes = this.computeScores2(featureList);
			HashMap<String, Double> map = new HashMap<String, Double>();
			//System.out.println(scroes + "%n");

			for(int i=0; i<docList.size(); i++) {
				map.put(docList.get(i), scroes.get(i));
			}

			List<Entry<String, Double>> list = new ArrayList<>(map.entrySet());
			list.sort(Entry.comparingByValue());
			Collections.reverse(list);
			// System.out.print(dataset.getAllQid().size());

			int count = 1;
			for(Entry<String, Double> entry: list) {
				queryResultLists.getTrecResults().add(new TrecResult(
						qid,
						entry.getKey(),
						count,
						entry.getValue(),
						null
				));

				count++;
			}
		}

		return queryResultLists;
	}

}
