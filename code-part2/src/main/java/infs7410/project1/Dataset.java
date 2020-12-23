package infs7410.project1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Dataset {
	
	
	
	private int numFeatures;
	private List<double[]> featureMatrix = new ArrayList<double[]>();
	private List<Integer> labelVector = new ArrayList<Integer>();
	
	private HashMap<String,ArrayList<double[]>> qidGetAllFeatures = new HashMap<>();
	private HashMap<String,ArrayList<String>> qidGetAllDocids = new HashMap<>();
	private HashMap<String,ArrayList<Integer>> qidGetRelevenceSet = new HashMap<>();
	private HashMap<String,HashMap<String,Integer>> qidDocidGetRelevence = new HashMap<>();
	

	public Dataset(String filePath, int numFeatures) {
		this.numFeatures = numFeatures;
	
        		
        try (FileReader reader = new FileReader(filePath);
    
	            BufferedReader br = new BufferedReader(reader)) {
        			
	            // read line by line
	            String line;
	            String currentQid = null;
	            ArrayList<String> docIds = new ArrayList<>();
	            ArrayList<double[]> queryFeatures = new ArrayList<>();
	            ArrayList<Integer> revelenceSet = new ArrayList<>();
	            HashMap<String,Integer> docidRel = new HashMap<>();
	            
	            while ((line = br.readLine()) != null) {
	            	//System.out.println(line);
	                String[] tokens = line.split(" ");
	                
	                String qid = tokens[1].split(":")[1];
	                
	                
	                
	                if (qid.equals(currentQid) == false && currentQid != null) {

	                	this.qidGetAllDocids.put(currentQid,(ArrayList)docIds.clone());
	                	this.qidGetAllFeatures.put(currentQid, (ArrayList)queryFeatures.clone());
	                	this.qidGetRelevenceSet.put(currentQid,(ArrayList)revelenceSet.clone());
	                	this.qidDocidGetRelevence.put(currentQid, (HashMap<String, Integer>) docidRel.clone());
	                	docIds.clear();
	                	queryFeatures.clear();
	                	revelenceSet.clear();
	                	docidRel.clear();
	                	
	                	
	                }
	                currentQid = qid;

	                String docid = tokens[10].split(":")[1];
	                
	                //int relevence = Integer.parseInt(tokens[9].split(":")[1]);
					int relevence = Integer.parseInt(tokens[0]);
	                if (relevence>0) {
	                	relevence = 1;
	                }
	                this.labelVector.add(relevence);
	                
	                double[] features = new double[this.numFeatures];
	                for(int i=0; i<this.numFeatures; i++) {
	                	double feature = Double.parseDouble(tokens[i+2].split(":")[1]);
	                	features[i] = feature;
	                }
	                
	                this.featureMatrix.add(features);
	                
	                docIds.add(docid);
	                queryFeatures.add(features);
	                revelenceSet.add(relevence);
	                docidRel.put(docid, relevence);
	                

	            }

	        } catch (IOException e) {
	            System.err.format("IOException: %s%n", e);
	        }
    }
	
	public List<double[]> getFeatureMarix(){
		return this.featureMatrix;
	}
	
	public List<Integer> getLabelVector(){
		return this.labelVector;
	}
	
	public List<String> queryGetDocIds(String qid){
		return (List<String>) this.qidGetAllDocids.get(qid).clone();
	}
	
	public ArrayList<double[]> queryGetFeatures(String qid){
		return this.qidGetAllFeatures.get(qid);
	}
	
	public Set<String> getAllQid(){
		return this.qidGetAllDocids.keySet();
	}
	
	public ArrayList<Integer> queryGetRelevenceSet(String qid){
		return (ArrayList<Integer>) this.qidGetRelevenceSet.get(qid).clone();
	}
	
	public Integer queryDocidGetRel(String qid, String docid) {
		return this.qidDocidGetRelevence.get(qid).get(docid);
	}
}
