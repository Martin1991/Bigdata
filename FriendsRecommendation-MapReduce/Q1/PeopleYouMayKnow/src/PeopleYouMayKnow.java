import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class PeopleYouMayKnow {
	
	public static void main(String ar[]) throws IOException, InterruptedException, ClassNotFoundException {
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println("Job started: " + dateFormat.format(date)); //2014/08/06 15:59:48
		
		Configuration conf = new Configuration();
	    Job job = Job.getInstance(conf, "People_You_May_Know");
	    
	    job.setJarByClass(PeopleYouMayKnow.class);
	    job.setMapperClass(FriendMapper.class);
	    job.setReducerClass(FriendReducer.class);
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(Text.class);
	    
	    FileInputFormat.addInputPath(job, new Path(ar[0]));
	    FileOutputFormat.setOutputPath(job, new Path(ar[1]));
	    System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
