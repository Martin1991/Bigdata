import java.io.IOException;
import java.util.HashSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MutualFriend {

	public static class FriendsMapper extends Mapper<Object, Text, Text, Text> {
        private Text m_id = new Text();
        private Text m_others = new Text();
        
    	Text keyUser = new Text();
    	Text suggTuple = new Text();
    	Text existingFriend = new Text();
    	String [] userRow,friendList;
    	String otherFriends;
    	int i,j;

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            //create key as the friend-user pair who is smaller who is first
        	//create value as all of the user's friends
        	Configuration conf = context.getConfiguration();
        	String user1 = conf.get("user1");
        	String user2 = conf.get("user2");
        	String user12 = user1.compareTo(user2) < 0 ? user1+","+user2 : user2+","+user1;
        	userRow = value.toString().split("\\s");
    		if (userRow.length==1){
    			userRow = null;
    			return;
    		}
    		friendList = userRow[1].split(",");
    		m_others.set(userRow[1]);
    		if((userRow[0].equals(user1))||(userRow[0].equals(user2))){
    			m_id.set(user12);
    			context.write(m_id, m_others);
    		}
    		/*for (String friend : friendList) 
    		{
    			String id = userRow[0].compareTo(friend) < 0 ? userRow[0]+","+friend : friend+","+userRow[0];
                m_id.set(id);
                if(m_id.toString().equals(user12))
                {
                	context.write(m_id, m_others);
                }
    		}*/
    		
       
        }
    }

    public static class FriendsReducer
            extends Reducer<Text, Text, Text, Text> {
        private Text m_result = new Text();

        // Calculates intersection of two given Strings, i.e. friends lists
        private String intersection(String s1, String s2) {
            HashSet<String> h1 = new HashSet<String>();
            HashSet<String> h2 = new HashSet<String>();
            
            String[] n1 = s1.split(",");
            String[] n2 = s2.split(",");
            for(int i = 0; i < n1.length; i++) {
                h1.add(n1[i]);
            }
            for(int i = 0; i < n2.length; i++) {
                h2.add(n2[i]);
            }

            h1.retainAll(h2);
            String[] res = h1.toArray(new String[0]);
            String intersect = new String("");
            for (int i = 0; i < res.length; i++) {
                intersect += res[i]+",";
            }
            return intersect.substring(0,intersect.length()-1);
        }

        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            // Prepare a 2-String-Array to hold the values, i.e. the friends lists of
            // our current friends pair.
            String[] combined = new String[2];
            int cur = 0;
            for(Text value : values) {
                combined[cur++] = value.toString();
            }

            // Calculate the intersection of these lists and write result in the form (UserAUserB, MutualFriends).
            m_result.set(intersection(combined[0], combined[1]));
            context.write(key, m_result);
        }
    }

    public static void main(String args[]) throws Exception {
        // Standard Job setup procedure.
    	
        Configuration conf = new Configuration();
        conf.set("user1", args[2]);
        conf.set("user2", args[3]);
        Job job = Job.getInstance(conf, "Mutual Friends");
        
        job.setJarByClass(MutualFriend.class);
        job.setMapperClass(FriendsMapper.class);
        job.setReducerClass(FriendsReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}