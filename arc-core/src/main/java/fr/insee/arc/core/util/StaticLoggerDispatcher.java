package fr.insee.arc.core.util;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/** Transition class. Try to use a LoggerDispatcher as an @Autowired attribute instead. */
public class StaticLoggerDispatcher {
	
//	private static LoggerDispatcher loggerDispatcher;
//	
//	public StaticLoggerDispatcher(@Autowired @Qualifier("activeLoggerDispatcher") LoggerDispatcher loggerDispatcherInstance) {
//		loggerDispatcher = loggerDispatcherInstance;
//	}

	/** Transition class. Use a LoggerDispatcher as an @Autowired attribute instead. */
	public static void error(Object message, Logger logger) {
		System.out.println(message);//error(message, logger);		
	}

	/** Transition class. Use a LoggerDispatcher as an @Autowired attribute instead. */
	public static void error(Object message, Exception ex, Logger logger) {
		System.out.println(message);//error(message, ex, logger);
	}

	/** Transition class. Use a LoggerDispatcher as an @Autowired attribute instead. */
	public static void warn(Object message, Logger logger) {
		System.out.println(message);//warn(message, logger);		
	}

	/** Transition class. Use a LoggerDispatcher as an @Autowired attribute instead. */
	public static void warn(Object message, Exception ex, Logger logger) {
		System.out.println(message);//warn(message, ex, logger);
	}

	/** Transition class. Use a LoggerDispatcher as an @Autowired attribute instead. */
	public static void info(Object message, Logger logger) {
		System.out.println(message);//info(message, logger);
	}

	/** Transition class. Use a LoggerDispatcher as an @Autowired attribute instead. */
	public static void info(Object message, Exception ex, Logger logger) {
		System.out.println(message);//info(message, ex, logger);
	}

	/** Transition class. Use a LoggerDispatcher as an @Autowired attribute instead. */
	public static void debug(Object message, Logger logger) {
		System.out.println(message);//debug(message, logger);
	}

	/** Transition class. Use a LoggerDispatcher as an @Autowired attribute instead. */
	public static void debug(Object message, Exception ex, Logger logger) {
		System.out.println(message);//debug(message, ex, logger);
	}

	/** Transition class. Use a LoggerDispatcher as an @Autowired attribute instead. */
	public static void trace(Object message, Logger logger) {
		System.out.println(message);//trace(message, logger);
	}

	/** Transition class. Use a LoggerDispatcher as an @Autowired attribute instead. */
	public static void trace(Object message, Exception ex, Logger logger) {
		System.out.println(message);//trace(message, ex, logger);
	}
	
}