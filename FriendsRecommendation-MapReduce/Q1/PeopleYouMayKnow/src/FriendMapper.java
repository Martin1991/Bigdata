import java.io.IOException;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;


public class FriendMapper extends Mapper<LongWritable, Text, Text, Text>{
	Text one = new Text("1");
	Text minusOne = new Text("-1");
	Text keyUser = new Text();
	Text suggTuple = new Text();
	Text existingFriend = new Text();
	String [] userRow,friendList;
	int i,j;
	
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		
		userRow = value.toString().split("\\s");
		if (userRow.length==1){
			userRow = null;
			return;
		}
		//setting up key-value map
		friendList = userRow[1].split(",");
		for (i=0; i<friendList.length; i++) {
			keyUser.set(new Text(friendList[i]));
			for(j=0; j<friendList.length; j++){
				if(j==i) {
					continue;
				}
				suggTuple.set(friendList[j] + ",1");
				context.write(keyUser, suggTuple);
				
			}
			existingFriend.set(userRow[0] + ",-1");
			context.write(keyUser, existingFriend);			
		}
		
	}

}
