package darvin939.DarkDays.Utils.Debug;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

   private final MessageFormat messageFormat;
   private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");


   public LogFormatter() {
      String propName = this.getClass().getName() + ".format";
      String format = LogManager.getLogManager().getProperty(propName);
      if(format == null || format.trim().length() == 0) {
         format = "%t [%L] *%C.%M* : %m [Thread ¹%T] %S";
      }

      if(!format.contains("{") && !format.contains("}")) {
         format = format.replace("%L", "{0}").replace("%m", "{1}").replace("%M", "{2}").replace("%t", "{3}").replace("%c", "{4}").replace("%T", "{5}").replace("%n", "{6}").replace("%C", "{7}").replace("%S", "{8}") + "\n";
         this.messageFormat = new MessageFormat(format);
      } else {
         throw new IllegalArgumentException("curly braces not allowed");
      }
   }

   public String format(LogRecord record) {
      String[] arguments = new String[9];
      arguments[0] = record.getLevel().toString();
      arguments[1] = record.getMessage();
      if(arguments[1] == null || arguments[1].length() == 0) {
         Throwable date = record.getThrown();
         if(date != null) {
            arguments[1] = date.getMessage();
         }
      }

      arguments[1] = record.getMessage();
      if(record.getSourceMethodName() != null) {
         arguments[2] = record.getSourceMethodName();
      } else {
         arguments[2] = "?";
      }

      Date var12 = new Date(record.getMillis());
      synchronized(dateFormat) {
         arguments[3] = dateFormat.format(var12);
      }

      if(record.getSourceClassName() != null) {
         arguments[4] = record.getSourceClassName();
      } else {
         arguments[4] = "?";
      }

      arguments[5] = Integer.valueOf(record.getThreadID()).toString();
      arguments[6] = record.getLoggerName();
      int var13 = arguments[4].lastIndexOf(".") + 1;
      if(var13 > 0 && var13 < arguments[4].length()) {
         arguments[7] = arguments[4].substring(var13);
      } else {
         arguments[7] = arguments[4];
      }

      if(record.getThrown() == null) {
         arguments[8] = "";
      } else {
         if(record.getMessage() != null) {
            byte stackTrace = 1;
            arguments[stackTrace] = arguments[stackTrace] + " " + record.getThrown().toString();
         } else {
            arguments[1] = record.getThrown().toString();
         }

         String var14 = "\n";
         StackTraceElement[] var9;
         int var8 = (var9 = record.getThrown().getStackTrace()).length;

         for(int var7 = 0; var7 < var8; ++var7) {
            StackTraceElement st = var9[var7];
            var14 = var14 + "\t" + st.toString() + "\n";
         }

         arguments[8] = var14;
      }

      synchronized(messageFormat) {
         return messageFormat.format(arguments);
      }
   }
}
