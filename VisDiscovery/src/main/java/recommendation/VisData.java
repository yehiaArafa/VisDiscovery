package recommendation;


import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author ninja
 */
public class VisData implements Comparable<VisData> {

    public double totalDeviation = 0;
    public String visType;
    public String xAxisTitle;
    public String yAxisTitle; 
    public HashMap<String, String> recomQuery1 = new HashMap<String, String>();
    public HashMap<String, String> recomQuery2 = new HashMap<String, String>();
    
    public HashMap<String, String> recomQuery1_normalized = new HashMap<String, String>();
    public HashMap<String, String> recomQuery2_normalized = new HashMap<String, String>();
    
    public ArrayList<VisObject> values = new ArrayList<VisObject>();
    
    
    @Override
     public int compareTo(VisData t) {
     	if(t.totalDeviation>totalDeviation) return 1;
     	else if(t.totalDeviation<totalDeviation) return -1;
     	return 0;
     }
}
