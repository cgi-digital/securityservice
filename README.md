# Slack Security Service

Micro service to integrate with slack, this service interacts with slack a slash command to audit channels within a workspace 
the audit process concentrates on file uploads to channels and produces a HTML output file into a location specified in the 
environment variables 


These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.


What things you need to install the software and how to install them

```
git clone //TODO 
maven 
Java JDK 8+ (GraalVM) 
Suitable IDE 
```

How to get the service running 

```
1. Clone the repo 
2. Set environment variables 
   ADMIN_USERS="firstname.lastname"
   EMAIL_ADDR=<comma separated list of email addresses> 
   FILE_PATH=<output path where to write the file> 
3. import into chosen IDE or use the maven clean install command 
4. To run the system from an IDE choose a run config or with java use the java command 

```

Unit tests have not been included in this project as they are not providing any value to the project, due in part to the 
service being so small and because of the heavy use of the jslack library which would require a new slack workspace and a slack 
test account. It would also require heavy use of mocking which would then increase the class count in the project. 

As yet the service does not do enough to warrant the increased class count to require mocking frameworks to be included. 

#To Deploy 

The project includes a dockerfile, use that and deploy the docker image
