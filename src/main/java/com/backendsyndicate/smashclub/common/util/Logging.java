package com.backendsyndicate.smashclub.common.util;

import com.backendsyndicate.smashclub.admin.service.log.LogService;
import com.backendsyndicate.smashclub.common.config.LogConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;

public class Logging {
    private static StringBuilder sBuild = new StringBuilder();
    private static Logger logger = LogManager.getLogger(Logging.class);

    public static void handleException(String strClass, String strMethod, int line, String errorCode, String message) {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        sBuild.setLength(0);
        logger.error(
//                    sBuild
//                    .append(System.getProperty("line.separator"))
//                    .append("Class: ").append(strClass).append(System.getProperty("line.separator"))
//                    .append("Method: ").append(strMethod).append(System.getProperty("line.separator"))
//                    .append("Error: ").append(e.getMessage())
                sBuild.append(System.getProperty("line.separator"))
                        .append(String.format("%tY-%<tm-%<td %<tH:%<tM:%<tS ", now))
                        .append(String.format("[APPLICATION] ERROR on %s@%s Line %d [%s]: %s", strClass, strMethod, line, errorCode, message))
        );
    }

    public static void printConsole(String message) {
        if( LogConfig.isEnableLog() ) {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            System.out.println(String.format("%tY-%<tm-%<td %<tH:%<tM:%<tS [APPLICATION] INFO: %s", now, message));
        }
    }
}
