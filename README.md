Log Helper Plugin
===

The Log Helper plugin for Pentaho Data Integration provides several steps and job entries to assist with common logging tasks.

* Write Variables to Log Step
* Write Variables to Log Job Entry
* Write Result Rows to Log Job Entry
* Write Result Files to Log Job Entry
* Send Log to Variable Job Entry

Developed by [Inquidia Consulting](www.inquidia.com).

System Requirements
---
**Version 1.0 for 5.0-3**

-Pentaho Data Integration 5.0 to 5.3

**Version 1.0**

-Pentaho Data Integration 5.4 or above

Installation
---
**Using Pentaho Marketplace**

1. In the Pentaho Marketplace find the Log Helper plugin and click Install
2. Restart Spoon

**Manual Install**

1. Place the LogHelper folder in the ${DI\_HOME}/plugins/ directory
2. Restart Spoon

Write Variables to Log Step
---
Instead of having to manually list every parameter you want to log, this transformation step will write the parameters configured in the transformation or all variables set to the log.  This step can be completely outside of the general data flow of the transformation with no input or output hops, or in the stream.

* **Log level** - The logging level the variables should be logged at.  For example: if you specify debug level and run the transformation at basic level the variables will not be written to the log.
* **Variable Type** - Parameters or All variables - Choose to write only the parameters specified in the transformation, or write all variables to the log.
* **Regex filter** - Allows for a regex to be specified to filter the variables to log by name.  Only variables with names that match the regex will be logged.
* **Only log once?** - Only log for the first row.

Write Variables to Log Job Entry
---
Instead of having to manually list every parameter you want to log, this job entry will write the parameters configured in the job or all variables set to the log.

* **Log level** - The logging level the variables should be logged at.  For example: if you specify debug level and run the job at basic level the variables will not be written to the log.
* **Variable Type** - Parameters or All variables - Choose to write only the parameters specified in the transformation, or write all variables to the log.
* **Regex filter** - Allows for a regex to be specified to filter the variables to log by name.  Only variables with names that match the regex will be logged.

Write Result Rows to Log Job Entry
---
This job will write all of the rows in the result rows to the log.  This is useful because otherwise there is no way to see what the result rows look like.

* **Log level** - The logging level the result rows should be logged at.  For example: if you specify debug level and run the job at basic level the result rows will not be written to the log.
* **Limit** - Only log the first n result rows to the log.  0 means no limit.

Write Result Files to Log Job Entry
---
This job will write all of the files in the result files to the log.  This is useful because otherwise there is no way to see what the result files look like.

* **Log level** - The logging level the result files should be logged at.  For example: if you specify debug level and run the job at basic level the result files will not be written to the log.
* **Limit** - Only log the first n result files to the log.  0 means no limit.

Send Log to Variable Job Entry
---
Often at the end of a job, especially if the job fails you want to do something with it.  If you use database logging you can get the log from the database, if you specified a log file for the job you can get the log file from result rows, but what if you do neither of these.  This job entry gets the job log up to the point of this job entry and puts it into a variable that you can then do something with, like include the log in the body of the failure notification email.

* **Job level** - Current, Parent, or Root job - What job to get the log for.
* **Variable name** - The name of the variable to contain the log.
* **Variable type** - Valid in the JVM, Valid in the current job, Valid in the parent job, Valid in the root job
* **Line limit** - Limit to only getting the last n events from the log.  0 means all events.
* **Filter log level** - Filter to only include events that were logged above a certain log level.  For example if you ran the job with DEBUG logging, but set this to Error only then the variable would only contain the events logged at the error level.

**Usage Notes:**

* The maximum number of log lines that can be returned is configured by the KETTLE_MAX_LOGGING_REGISTRY_SIZE variable.  It simply is not possible to get logging events older than this limit.
* This job entry must be able to fit the entire log for the job into memory.  If you have a very large log you may run into Out of Memory issues.

Building from Source
---
The Log Helper Plugin is built using Ant.  Since I do not want to deal with the complexities of Ivy the following instructions must be followed before building this plugin.

1. Edit the build.properties file.
2. Set pentahoclasspath to the data-integration/lib directory on your machine.
3. Set the pentahoswtclasspath to the data-integration/libswt directory on your machine.
4. Set the pentahobigdataclasspath to the data-integration/plugins/pentaho-big-data-plugin/lib directory on your machine.
5. Run "ant dist" to build the plugin.

**Build Note:**
In Pentaho 5.4 a backward incompatibility was introduced for job entry plugins.  Although the same code can be used for both PDI versions above and below 5.4, the version of the PDI libraries used when building this plugin matter.

If you plan to use this plugin with PDI 5.3 or below you must build using PDI 5.3 or below libraries.  If you plan to use this plugin with PDI 5.4 and above, you must build using PDI 5.4 or above libraries.