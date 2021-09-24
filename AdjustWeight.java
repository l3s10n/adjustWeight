import java.io.*;
import java.util.*;

public class AdjustWeight{
	private String stage_location;	
	private String FP_location;
	private Map<String, Double> judgeMode2Score = new HashMap<>();
	private Map<String, Integer> judgeMode2Count = new HashMap<>();
	private ArrayList<String> FPs = new ArrayList<String>();
	
	public AdjustWeight(String stage_location, String FP_location) {
		this.stage_location = stage_location;
		this.FP_location = FP_location;
	}
	
	private void initInfo() {	
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
		
		try {
			File FPdir = new File(FP_location);
	        File[] FPfiles = FPdir.listFiles();
	        for (int i = 0; i < FPfiles.length; i++) {
	        	FPs.add(FPfiles[i].getName());
	        }
		}catch (IOException e) { 
            e.printStackTrace(); 
        }
		
		for(int i = 0; i < FPs.size(); i++) {
			String[] slices = FPs[i].split("_");
			for(int j = 0; j < slices.length; j++) {
				if(slices[i] == "result") {
					countinue;
				}
				if(judgeMode2Count.containsKey(slices[i])) {
					judgeMode2Count.put(slices[i], judgeMode2Count.get(slices[i])+1);
				}
				else {
					judgeMode2Count.put(slices[i], 1);
				}
			}
		}
	}
	
	public void adjust() {
		initInfo();
		
	}
}

public static void main(String[] args) {
    System.out.print(1);
}