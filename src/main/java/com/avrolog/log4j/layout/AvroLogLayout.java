package com.avrolog.log4j.layout;

import static com.avrolog.log4j.layout.LogConstants.LOGGER;
import static com.avrolog.log4j.layout.LogConstants.LEVEL;
import static com.avrolog.log4j.layout.LogConstants.MDC;
import static com.avrolog.log4j.layout.LogConstants.MESSAGE;
import static com.avrolog.log4j.layout.LogConstants.NDC;
import static com.avrolog.log4j.layout.LogConstants.THREADNAME;
import static com.avrolog.log4j.layout.LogConstants.THROWABLE;
import static com.avrolog.log4j.layout.LogConstants.TIMESTAMP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.util.Utf8;
import org.apache.logging.log4j.core.LogEvent;

//TODO DARR
/* @see info-convert-version.txt */
//import org.apache.log4j.spi.LoggingEvent;
//import org.apache.log4j.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Layout;


/****
 * A layout for log4j using avro
 *
 * @author harisgx
 *
 */
public class AvroLogLayout implements Layout {

	private String type;// binary or json? loaded from log4j.properties
	private List<String> mdcKeys = new ArrayList<String>();//mdc keys, user has to set these values

	protected AvroLogLayout(){
	}

	public String format(LogEvent event) {
		ByteArrayOutputStream bao = null;
		String logOutput = "";
		try{

			Schema avroSchema = SchemaUtil.instance().getSchema();
			if(avroSchema != null){
				GenericDatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(avroSchema);
				Encoder encoder = null;
				bao = new ByteArrayOutputStream();

				encoder = EncoderFactory.get().jsonEncoder(avroSchema, bao);

				GenericRecord record = new GenericData.Record(avroSchema);
				putBasicFields(event, record);
				putNDCValues(event, record);
				putThrowableEvents(event, record);
				putMDCValues(event, record);
				writer.write(record, encoder);
				encoder.flush();
				logOutput = new String(bao.toByteArray());
			}

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(bao != null){
				try {
					bao.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return logOutput + "\n";
	}

	private void putBasicFields(LogEvent event, GenericRecord record){
		record.put(LOGGER, event.getLoggerName());
		record.put(LEVEL, event.getLevel().toString());
		record.put(TIMESTAMP,event.getTimeMillis());
		record.put(THREADNAME, event.getThreadName());
		record.put(MESSAGE, new Utf8(event.getMessage().toString()));
	}

	private void putNDCValues(LogEvent event, GenericRecord record){
		  if (event.getContextStack() != null) {
			  record.put(NDC, event.getContextStack());
	      }
	}

	private void putThrowableEvents(LogEvent event, GenericRecord record){
		String throwableEvents = getThrowableEvents(event);
		 if(throwableEvents.length() > 0){
			 record.put(THROWABLE, throwableEvents);
		 }
	}

	private void putMDCValues(LogEvent event, GenericRecord record){

		Map<String, String> mdcMap = getMDCValues(event);
		if(mdcMap != null){
			record.put(MDC, mdcMap);
		}
	}

	private String getThrowableEvents(LogEvent event) {

		StringBuilder throwableStr = new StringBuilder();
        //String[] throwableStrRep = event.getThrowableStrRep();
		StackTraceElement[] ste = event.getThrown().getStackTrace();

        //throwable event will be a line seperated string -- stacktrace?
        if (ste != null) {
            for (StackTraceElement str : ste) {
            	throwableStr.append(ste.toString()).append("\n");
            }
        }

//      //throwable event will be a line seperated string -- stacktrace?
//      if (throwableStrRep != null) {
//          for (String str : throwableStrRep) {
//          	throwableStr.append(str).append("\n");
//          }
//      }

        return throwableStr.toString();

    }

	/***
	 *
	 * @param event
	 * @return
	 */
	private Map<String, String> getMDCValues(LogEvent event){
		Map<String, String> hashMap = null;
		if (mdcKeys != null && mdcKeys.size() > 0) {
			//Obtain a copy of MDC prior to serialization or asynchronous logging.
            event.getContextMap();
            hashMap = new HashMap<String,String>(mdcKeys.size());
            for (String s : mdcKeys) {
                Object mdc = event.getContextMap().get(s);
                if (mdc != null) {
                	hashMap.put(s, mdc.toString());
                }
            }
        }
		return hashMap;
	}

	/***
	 * Sets the value of log4j.appender.logger_name.layout.MDCKeys from log4j.properties
	 * The value should be comma separated values
	 *
	 * @param mDCKeys
	 */
	public void setMDCKeys(String mDCKeys){
        if (mDCKeys != null && mDCKeys.length() > 0){
            this.mdcKeys = Arrays.asList(mDCKeys.split(","));
        }
    }

	public String getType() {
		return type;
	}

	/***
	 * Sets the value of log4j.appender.logger_name.layout.Type from log4j.properties
	 *
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toSerializable(LogEvent event) {
		return null;
	}

	@Override
	public byte[] getFooter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getHeader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] toByteArray(LogEvent event) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map getContentFormat() {
		// TODO Auto-generated method stub
		return null;
	}

}
