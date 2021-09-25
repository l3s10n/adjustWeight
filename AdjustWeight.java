import java.io.*;
import java.util.*;

public class AdjustWeight{
	private String stage_location;	
	private String FP_location;
	private Map<String, Double> judgeMode2Score = new HashMap<>();
	private Map<String, Integer> judgeMode2Count = new HashMap<>();
	private ArrayList<String> FPs = new ArrayList<String>();
	private double threshold;
	
	public AdjustWeight(String stage_location, String FP_location) {
		this.stage_location = stage_location;
		this.FP_location = FP_location;
	}
	
	private void prepareInfo() {	
		try {
			String line;
			BufferedReader stageReader = new BufferedReader(new FileReader(stage_location));
			while((line = stageReader.readLine()) != null) {
				String[] slices = line.split(",");
				if(slices.length == 2) {
					judgeMode2Score.put(slices[0], Double.parseDouble(slices[1]));
				}
			}
		}catch (IOException e) { 
            e.printStackTrace(); 
        } 
		
		File FPdir = new File(FP_location);
        File[] FPfiles = FPdir.listFiles();
        for (int i = 0; i < FPfiles.length; i++) {
        	FPs.add(FPfiles[i].getName());
        }
		
		for(int i = 0; i < FPs.size(); i++) {
			String[] slices = FPs.get(i).substring(0, FPs.get(i).length()-4).split("_");
			for(int j = 0; j < slices.length; j++) {
				if(slices[j].equals("result")) {
					continue;
				}
				if(judgeMode2Count.containsKey(slices[j])) {
					judgeMode2Count.put(slices[j], judgeMode2Count.get(slices[j])+1);
				}
				else {
					judgeMode2Count.put(slices[j], 1);
				}
			}
		}
	}
	
	private void writeTofile() {
		// remain to be code
		for (String key : judgeMode2Score.keySet()) {
			System.out.println(key);
			System.out.println(judgeMode2Score.get(key));
		}
		System.out.println("\nthreshold:\n"+threshold);
	}
	
	public void adjust() {
		prepareInfo();
		
		// misunderstanding. Another policy?
		/*
		Double v;
		for (String key : judgeMode2Count.keySet()) {
			v = Double.valueOf(judgeMode2Count.get(key))/Double.valueOf(FPs.size());
			judgeMode2Score.put(key, judgeMode2Score.get(key)-v);
		}
		*/
		
		Double v;
		for (String key : judgeMode2Count.keySet()) {
			v = Double.valueOf(FPs.size())/Double.valueOf(judgeMode2Count.get(key));
			judgeMode2Score.put(key, v);
		}
		
		threshold = -1.0;
		double tScore = 0;
		for(int i = 0; i < FPs.size(); i++) {
			String[] slices = FPs.get(i).substring(0, FPs.get(i).length()-4).split("_");
			for(int j = 0; j < slices.length; j++) {
				if(slices[j].equals("result")) {
					continue;
				}
				tScore = tScore + judgeMode2Score.get(slices[j]);
			}
			if(tScore > threshold) {
				threshold = tScore;
			}
			tScore = 0;
		}
		
		
		writeTofile();
	}
	
	public static void main(String[] args) {
	    // for test
		AdjustWeight adjustWeight = new AdjustWeight("/home/lesion/Desktop/detector/stage.txt","/home/lesion/detector/txt/123");
		adjustWeight.adjust();
	}
}
