package binf8380_finalProject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class AbundanceSample 
{
	private String id;
	private HashMap<String, BigDecimal> values;
	private HashMap<String, BigDecimal> proportions;
	
	// in case of non-terminal decimals, round to 4 places, .*5 rounds up
	private final MathContext MC = new MathContext(4, RoundingMode.HALF_UP); 
	
	public AbundanceSample(String id, HashMap<String, BigDecimal> values)
	{
		this.id = id;
		this.values = values;
		this.proportions = getAbundanceProportions();
	}
	
	// getter: sample ID as a String
	public String getID()
	{
		return id;
	}
	
	// getter: single abundance value given desired taxa
	public BigDecimal getTaxaAbundance(String taxa)
	{
		return values.get(taxa);
	}
	
	public BigDecimal getTaxaProportion(String taxa)
	{
		return proportions.get(taxa);
	}
	
	public static List<AbundanceSample> readAbundanceTable(File file) throws Exception
	{
		BufferedReader reader = new BufferedReader(new FileReader(file)); // file reader
		
		List<AbundanceSample> sampleList = new ArrayList<AbundanceSample>(); // initialize AS holder list
		List<String> keys = new ArrayList<String>();
		
		boolean firstLine = true;
		
		for (String nextLine = reader.readLine(); nextLine != null; nextLine = reader.readLine())
		{
			String tmp_id = "";
			HashMap<String, BigDecimal> tmp_map = new HashMap<String, BigDecimal>();
			Scanner s = new Scanner(nextLine).useDelimiter("\t"); // splits each line by tab
			String currentLine = s.next();
			
			if (firstLine) // return header as keys
			{
				keys = Arrays.asList(currentLine.split(","));
				//System.out.println(keys.get(0).toString());
				
				firstLine = false; // only do this once
			}
				
			else
			{
				List<String> tmp_values = Arrays.asList(currentLine.split(",")); // tmp values as string split by comma
				tmp_id = tmp_values.get(0); //first value is the sample ID
				
				for(int i=1; i<tmp_values.size(); i++) // subsequent values are abundance data
				{
					BigDecimal abundanceValue = new BigDecimal(tmp_values.get(i));
					tmp_map.put(keys.get(i), abundanceValue);
				}
				
				sampleList.add(new AbundanceSample(tmp_id, tmp_map)); // append current sample to the overall list
			}
		}
		
		reader.close();
		
		return sampleList;
	}
	
	public HashMap<String, BigDecimal> getAbundanceProportions(){
		HashMap<String, BigDecimal> proportionMap = new HashMap<String, BigDecimal>();
		BigDecimal ttlAbundance = new BigDecimal(0);
		
		// get total abundance value
		for (BigDecimal value : values.values())
			ttlAbundance = ttlAbundance.add(value);
		

		// populate abundance map
		for (String key : values.keySet()) {
			BigDecimal tmp_value = values.get(key);
			tmp_value = tmp_value.divide(ttlAbundance, MC);
			
			proportionMap.put(key, tmp_value);
		}
		
		return proportionMap;
	}
	
	public static void main(String[] args) throws Exception
	{
		String path = "/Users/dfrybrum/advanced_bioinformatics_programming/finalProject/java_toySet.csv";
		File file = new File(path);
		List<AbundanceSample> sampleList = AbundanceSample.readAbundanceTable(file);
		
		for(AbundanceSample s : sampleList)
		{
			System.out.print("Sample ID: " + s.getID() + " ");
			System.out.println("Euryarchaeota Abundance: " + s.getTaxaAbundance("Euryarchaeota"));
			System.out.println("Euryarchaeota Proportion: " + s.getTaxaProportion("Euryarchaeota"));;
		}
	}
}

