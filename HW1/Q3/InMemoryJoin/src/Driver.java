
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;

import org.apache.hadoop.fs.Path;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


public class Driver extends Configured implements Tool {

  @Override
	public int run(String[] args) throws Exception {
		Job job = new Job(getConf());
		Configuration conf = job.getConfiguration();
		conf.set("user1", args[2]);
	    conf.set("user2", args[3]);
	    conf.set("userdata", args[4]);
		job.setJobName("Map-side join with text lookup file in DCache");
		
		job.setJarByClass(Driver.class);
		job.setMapperClass(FriendsMapper.class);
		job.setReducerClass(FriendsReducer.class);
		job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		//job.setNumReduceTasks(0);

		boolean success = job.waitForCompletion(true);
		return success ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new Configuration(),
				new Driver(), args);
		System.exit(exitCode);
	}
}
