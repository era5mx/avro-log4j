package com.avrolog.log4j.layout;

import org.apache.logging.log4j.LogManager;

/* @see info-convert-version.txt */
// import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import org.junit.Test;

/* @see info-convert-version.txt */
// import org.apache.log4j.MDC;

public class AvroLogTest {

	/* @see info-convert-version.txt */
    // static Logger logger = Log.getLogger(Test.class);
    static Logger logger = LogManager.getLogger(AvroLogTest.class);

    @Test
	public void testDebug(){
		logger.debug("This is a debug message");
	}

    @Test
    public void testInfo(){
		logger.info("This is a info message");
	}

    @Test
    public void testWarn(){
		logger.info("This is a warn message");
	}

    @Test
    public void testMDC(){
		/* @see info-convert-version.txt */
		// MDC.put("mdcKey", "mdcVal");
		ThreadContext.put("mdcKey", "mdcVal");
		logger.info("This is a warn message");
	}

}
