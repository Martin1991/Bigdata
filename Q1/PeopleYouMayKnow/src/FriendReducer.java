import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Reducer;


public class FriendReducer extends Reducer<Text,Text,IntWritable,Text> {
	
	HashMap<String, Integer> recommendedList;
	String [] pairStr;
	int [] pair = new int [2];
	
	List<String> inputVals;
	StringBuffer suggestionList = new StringBuffer();
	StringBuffer tmp = new StringBuffer();
	String recList= new String();
	IntWritable k;
	
	public void reduce(Text key, Iterable<Text> values,  Context context) throws IOException, InterruptedException {
		recommendedList = new HashMap<String, Integer>();
		suggestionList = new StringBuffer();
		tmp = new StringBuffer();
		inputVals = new ArrayList<String>();
		int s = Integer.parseInt(key.toString());
		if (s== 924 ||s== 8941 
				|| s== 8942 || s==9019 || s==9020 || s==9021 || s==9022	|| s==9990 ||s==9992||s==9993) {
		for (Text val : values) {
			inputVals.add(val.toString());
			
		}
		for (String val : inputVals) {
			if(val.contains("-1")) {
				pairStr = val.toString().split(",");
				recommendedList.put(pairStr[0], new Integer(-1));
			} else {
				pairStr = val.toString().split(",");
				
				if(recommendedList.containsKey(pairStr[0])){
					if (recommendedList.get(pairStr[0]) != -1){
						recommendedList.put(pairStr[0],recommendedList.get(pairStr[0]) + 1);
					}
				}
				else {
					recommendedList.put(pairStr[0],1);
				}
				
			}
		}
		
	
		recList = sortFriends(recommendedList);
		System.out.println(key + "\t" + recList);
		k = new IntWritable(Integer.parseInt(key.toString()));
		context.write(k, new Text(recList.substring(0, recList.length()-1)));// create key-recommend pair
		
	}}
	
	//sroting
	public String sortFriends (HashMap a){
		Object[] recomm =  a.keySet().toArray();
		Object recommCount[] = a.values().toArray();
		int i, j;
		Object temp, tempcount;
		StringBuffer returnlist = new StringBuffer();
		for(i=0; i<recommCount.length; i++) {
			for(j=0; j<recommCount.length-1 ; j++){
				if(Integer.parseInt(recommCount[j].toString()) < Integer.parseInt(recommCount[j+1].toString()) ) {
					
					//swap counts
					tempcount = recommCount[j+1];
					recommCount [j+1] = recommCount[j];
					recommCount [j] = tempcount;
					
					//swap the freinds
					temp = recomm[j+1];
					recomm [j+1] = recomm[j];
					recomm [j] = temp;
 				}
			}
		}
		
		//put the friends in a string
		returnlist.append("\t");
		for(i=0; i<10 && i<recomm.length&&recommCount[i].toString()!="-1"; i++) {
			if (recommCount[i].toString().equals("-1")) {
                continue;
            }
            else {
                returnlist.append(recomm[i].toString()).append(",");
            }
		}
		return returnlist.toString();
	}
}
