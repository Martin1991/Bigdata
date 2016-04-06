CS6350HW#1 Gaodan Fang

Dataset: soc-LiveJournal1Adj.txt	userdata.txt

1.source files & jar files
Q1:
HW1\Q1\PeopleYouMayKnow
HW1\Q1\peopleyoumayknow.jar

Q2:
HW1\Q2\MutualFriend
HW1\Q2\mutualfriend.jar

Q3:
HW1\Q3\InMemoryJoin
HW1\Q3\InMemoryJoin.jar

Q4:
HW1\Q4\ReduceSideJoin
HW1\Q4\AgeAvgInfo.jar


2.commands

run command
Q1:
hdfs dfs -rmr output
hadoop jar peopleyoumayknow.jar PeopleYouMayKnow /gxf140330/input /gxf140330_out

Q2:
hdfs dfs -rmr output
hadoop jar mutualfriend.jar MutualFriend /gxf140330/input /gxf140330_out user1 user2

Q3:
hdfs dfs -rmr output
hadoop jar InMemoryJoin.jar Driver /gxf140330/input /gxf140330_out user1 user2 /gxf140330/cache/userdata.txt

Q4:
%%%delete intermediate files
hdfs dfs -rmr /gxf140330/first_job_output
hdfs dfs -rmr /gxf140330_out
hadoop jar ReduceSideJoin.jar AgeAvgInfo /gxf140330/input /gxf140330/cache/userdata.txt /gxf140330_out



3. output 
Q1:
HW1\Q1\part-r-00000


Q2:
HW1\Q2\part-r-00000


Q3:
HW1\Q3\part-r-00000


Q4:
HW1\Q4\part-r-00000
