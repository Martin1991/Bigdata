
//import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class AgeAvgInfo {
	
	public static class DirectFriends extends Mapper<LongWritable, Text, Text, Text>{
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
			Text friend = new Text();
			String[] friendList;
			String[] userRow = value.toString().split("\\s");
			if (userRow.length==1){
				userRow = null;
				return;
			}
			friendList = userRow[1].split(",");
			for (int i = 0; i < friendList.length; i++) {
				friend.set(friendList[i]);
				context.write(new Text(userRow[0]), friend);
			}
			
			
		}
	}
	
	public static class Ageaddress extends Mapper<LongWritable, Text, Text, Text> {
		Text keyuser2 = new Text();
		Text information = new Text();
		
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
			String row = value.toString();
			keyuser2.set("sort");
			information.set(row);
			context.write(keyuser2, information);
		}
		
	}
	
	public static class JoinReducer extends Reducer<Text, Text, Text, Text> {
		HashMap<String, Integer> age = new HashMap<String, Integer>();
		HashMap<String, String> address = new HashMap<String,String>();
		private Text m_result = new Text();
		private Text keyUser = new Text();
		
		protected void setup(Context context) throws IOException, InterruptedException {
			super.setup(context);
			//read data to memory on the mapper.
			//myCenterList = new ArrayList<>();
			Configuration conf = context.getConfiguration();
			String myfilepath = conf.get("userdata");
			//e.g /user/hue/input/
			Path part=new Path("hdfs://cshadoop1"+myfilepath);//Location of file in HDFS
			
			
			FileSystem fs = FileSystem.get(conf);
			FileStatus[] fss = fs.listStatus(part);
		    for (FileStatus status : fss) {
		        Path pt = status.getPath();
		        
		        BufferedReader br=new BufferedReader(new InputStreamReader(fs.open(pt)));
		        String line;
		        line=br.readLine();
		        while (line != null){
		            System.out.println(line);
		            //do what you want with the line read
		            String strLineReadChanged = line.replaceFirst(",", "@");
					String[] deptFieldArray = strLineReadChanged.split("@");
					String[] detail = deptFieldArray[1].toString().trim().split(",");
					String[] date = detail[8].toString().split("/");
					int ag = 2016-Integer.parseInt(date[2]);
					String ad = detail[2].toString()+","+detail[3].toString()+","+detail[4].toString();
					age.put(deptFieldArray[0].trim().toString(), ag);
					address.put(deptFieldArray[0].trim().toString(), ad.trim());
					line=br.readLine();
		            
		        }
		    }
		}
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			int totalage = 0;
			int avgage;
			int number = 0;
			
			for (Text val:values) {
				int temp;
    			temp= age.get(val.toString());
    			//String[] splitedDetail = tempString.split(",");
    			totalage += temp;
    			//nameZip += splitedDetail[0]+",";	
    			number++;
    		}
			if (number==0) {
				avgage = 0;
			} else {
				avgage = totalage/number;
			}
			String tempstring;
			tempstring = address.get(key.toString());
			keyUser.set(key.toString()+","+tempstring);
			String avgagestring = Integer.toString(avgage);
			m_result.set(avgagestring);
			context.write(keyUser, m_result);
		}
		
	}
	
	public static class SortReducer extends Reducer<Text, Text, Text, Text> {
		HashMap<String, String> originallist = new HashMap<String, String>(); 
		Text keyuser3 = new Text();
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
			for (Text val : values) {
				String[] detail = val.toString().split("\\t");
				originallist.put(detail[0].trim(), detail[1].trim());
			}
			ArrayList<String> getlist = sortages(originallist);
			for (int i = 0; i < getlist.size(); i++) {
				String[] list2 = getlist.get(i).split("@");
				keyuser3.set(list2[0]);
				String value = list2[1];
				context.write(keyuser3, new Text(value));
			}
		}
		public ArrayList<String> sortages(HashMap a){
			Object[] keycount =  a.keySet().toArray();
			Object[] agecount = a.values().toArray();	
			for (int i = 0; i < agecount.length; i++) {
				for (int j = 0; j < agecount.length-1; j++) {
					
					if (Integer.parseInt(agecount[j].toString())<Integer.parseInt(agecount[j+1].toString())) {
						Object tempage = agecount[j];
						agecount[j] = agecount[j+1];
						agecount[j+1] = tempage; 
						
						Object temp = keycount[j];
						keycount[j] = keycount[j+1];
						keycount[j+1] = temp;
					}
				}
			}
			ArrayList<String> returnlist = new ArrayList<String>();
			for (int i = 0; i < 20&&i<keycount.length; i++) {
				String temp = keycount[i].toString()+"@"+agecount[i].toString();
				returnlist.add(temp);
			}
			return returnlist;
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		Configuration conf1 = new Configuration();
		conf1.set("userdata", args[1]);
		Job job1 = new Job(conf1, "Avg"); 
		job1.setJarByClass(AgeAvgInfo.class);
		job1.setMapperClass(DirectFriends.class);
		job1.setReducerClass(JoinReducer.class);
		job1.setOutputKeyClass(Text.class);
        job1.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job1, new Path(args[0]));
        Path p1 = new Path ("/gxf140330/first_job_output"); 
        FileOutputFormat.setOutputPath(job1, p1);
        job1.waitForCompletion(true);
       
        	Configuration conf2 = new Configuration();
        	Job job2 = new Job(conf2, "sort");  
            job2.setJarByClass(AgeAvgInfo.class);
            job2.setMapperClass(Ageaddress.class);
            job2.setReducerClass(SortReducer.class);
            job2.setOutputKeyClass(Text.class);
            job2.setOutputValueClass(Text.class);
            FileInputFormat.addInputPath(job2, p1);
            FileOutputFormat.setOutputPath(job2, new Path(args[2]));
            job2.waitForCompletion(true);
            System.exit(job2.waitForCompletion(true) ? 0 : 1);
		
        
		
	}
}
