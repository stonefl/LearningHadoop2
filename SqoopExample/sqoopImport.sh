# sqoop query to load data from MySQL to HDFS
sqoop import \
--connect jdbc:mysql://127.0.0.1:3306/fdxcorp \
--table Customers \
--username root -P \
--direct -m 1;
