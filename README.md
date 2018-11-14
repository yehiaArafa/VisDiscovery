# VisDiscovery
**B.Sc. Graduation Project**


Data analysts often build visualizations as the first step in their analytical workflow. However, when working with high-dimensional datasets, identifying visualizations that show relevant or desired trends in data can be laborious and needs a lot of efforts. Our graduation project introduces VisDiscovery, a visualization recommendation engine, that recommends interesting visualizations to facilitate fast visual analysis: given a subset of data to be studied, VisDiscovery intelligently explores the space of visualizations, evaluates promising visualizations for trends and recommends those it deems most useful or interesting, helping data scientists and analysts connecting things in interesting ways and look at data from different angles.

Our engine operates with huge data sizes and takes any semi-structured data as its source and operate on it.
In order to make our system more interactive and reduce the latency of the response we applied two kind of optimizations; Sharing-based Optimizations and Parallel SQL Execution.   
VisDiscovery runs on top of Cloudera Impala and Hadoop which allows parallel SQL queries execution and big data storage.   
We developed a tool https://github.com/OmarKRostom/PESTO to make the connection to Impala server is easy as writing one line of code.  

In conclusion, this is an important first step in exploring of visualization recommendation tools, paving the way towards rapid visual data analysis.  
**Please check Thesis for more info**


