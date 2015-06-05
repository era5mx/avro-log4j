### How to use

  En proceso de investigación como realizar ésta configuración en el log4j.xml:

  log4j.appender.logger_name.layout=com.avrolog.log4j.layout.AvroLogLayout
  log4j.appender.logger_name.layout.Type=json
  log4j.appender.logger_name.layout.MDCKeys=mdcKey


  Provide the MDC keys as comma seperated values

  Se incorporó el manejo de dependencias con gradle
  <s>add the required libraries to class path</s>

  Use Avro's DataFileWriteTool to read new-line delimited JSON records in the log and writes an Avro data file.


### TODO

	- supporting binary serialization
	- custom appenders



  *[more details] (http://bytescrolls.blogspot.com/2011/05/using-avro-to-serialize-logs-in-log4j.html)