import java.io.*;
import java.util.*;
import java.io.FileReader;

public class AdjustWeight{
	private String stage_location;	
	private String FP_location;
	private String TP_location;
	private Map<String, Double> judgeMode2Score = new HashMap<>();
	private Map<String, Integer> FPJudgeMode2Count = new HashMap<>();
	private Map<String, Integer> TPJudgeMode2Count = new HashMap<>();
	private Map<String, Double> FPJudgeMode2Percent = new HashMap<>();
	private Map<String, Double> TPJudgeMode2Percent = new HashMap<>();
    private Map<String, Integer> PT2Count = new HashMap<>();
	private ArrayList<String> FPs = new ArrayList<String>();
    private ArrayList<String> TPs = new ArrayList<String>();
	private double threshold;
	
	public AdjustWeight(String stage_location, String FP_location, String TP_location) {
		this.stage_location = stage_location;
		this.FP_location = FP_location;
		this.TP_location = TP_location;
	}
	
	public void prepareInfo() {
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

        func(FP_location, FPs, FPJudgeMode2Count);
		func(TP_location, TPs, TPJudgeMode2Count);

		int sumFP = 0, sumTP = 0;

		for(Map.Entry<String, Integer> entry : FPJudgeMode2Count.entrySet()){
			System.out.println("FP: " + entry.getKey() + " " + entry.getValue());
			sumFP += entry.getValue();
		}

		for(Map.Entry<String, Integer> entry : TPJudgeMode2Count.entrySet()){
			System.out.println("TP: " + entry.getKey() + " " + entry.getValue());
			sumTP += entry.getValue();
		}

		for(Map.Entry<String, Integer> entry : FPJudgeMode2Count.entrySet()){
			double percent = (double)entry.getValue() / sumFP;
			FPJudgeMode2Percent.put(entry.getKey(), percent);
			System.out.println("FP: " + entry.getKey() + " " + percent);
		}

		for(Map.Entry<String, Integer> entry : TPJudgeMode2Count.entrySet()){
			double percent = (double)entry.getValue() / sumTP;
			TPJudgeMode2Percent.put(entry.getKey(), percent);
			System.out.println("TP: " + entry.getKey() + " " + percent);
		}

	}

	private void func(String location, ArrayList<String> FPs, Map<String, Integer> judgeMode2Count) {
		System.out.println(location);
        File FPdir = new File(location);
        File[] FPfiles = FPdir.listFiles();
        for (int i = 0; i < FPfiles.length; i++) {
            FPs.add(FPfiles[i].getName());
        }

        for(int i = 0; i < FPs.size(); i++) {
        	int lines = 0;
        	try {
				FileReader in = new FileReader(FPfiles[i]);
				LineNumberReader reader = new LineNumberReader(in);
				reader.skip(Long.MAX_VALUE);
				lines = reader.getLineNumber();
			} catch (IOException e) {
				e.printStackTrace();
			}
            String[] slices = FPs.get(i).substring(0, FPs.get(i).length()-4).split("_");
            for(int j = 0; j < slices.length; j++) {
                if(slices[j].equals("result")) {
                    continue;
                }
                if(judgeMode2Count.containsKey(slices[j])) {
                    judgeMode2Count.put(slices[j], judgeMode2Count.get(slices[j]) + lines);
                }
                else {
                    judgeMode2Count.put(slices[j], lines);
                }
            }
        }
        System.out.println(judgeMode2Count.size());
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
		
		for(Map.Entry<String, Double> m : judgeMode2Score.entrySet()) {
			String point = m.getKey();
			double score = m.getValue();
			double newScore = score +  (TPJudgeMode2Percent.getOrDefault(point, 0.0) -
					FPJudgeMode2Percent.getOrDefault(point, 0.0)) * score * 2.5;
			judgeMode2Score.put(point, newScore);
		}

		for(Map.Entry<String, Double> m : judgeMode2Score.entrySet()) {
			if(TPJudgeMode2Percent.containsKey(m.getKey()) || FPJudgeMode2Percent.containsKey(m.getKey())) {
				System.out.println(m.getKey() + ": " + m.getValue());
			}
		}

		for(int i = 0; i < FPs.size(); i++) {
			double sum = 0;
			String[] slices = FPs.get(i).substring(0, FPs.get(i).length()-4).split("_");
			for(int j = 0; j < slices.length; j++) {
				if(slices[j].equals("result")) {
					continue;
				}
				sum += judgeMode2Score.get(slices[j]);
			}
			System.out.println("Sum of FP File" + i + " is: " + sum);
		}

		for(int i = 0; i < TPs.size(); i++) {
			double sum = 0;
			String[] slices = TPs.get(i).substring(0, TPs.get(i).length()-4).split("_");
			for(int j = 0; j < slices.length; j++) {
				if(slices[j].equals("result")) {
					continue;
				}
				sum += judgeMode2Score.get(slices[j]);
			}
			System.out.println("Sum of TP File" + i + " is: " + sum);
		}



		//writeTofile();
	}

	public static void main(String[] args) {
	    // for test
		AdjustWeight adjustWeight = new AdjustWeight(
		        "C:\\Users\\59391\\Desktop\\adjustWeight\\stage.txt",
                "C:\\Users\\59391\\Desktop\\adjustWeight\\123",
                "C:\\Users\\59391\\Desktop\\adjustWeight\\234");
		adjustWeight.adjust();
	}
}
