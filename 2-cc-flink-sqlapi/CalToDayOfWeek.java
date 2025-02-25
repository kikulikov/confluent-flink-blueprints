package io.confluent.flink.aa.udfs;

import org.apache.flink.table.functions.ScalarFunction;

public class CalToDayOfWeek extends ScalarFunction {

  public String eval(java.sql.Timestamp timestamp, String format) {
    try {
      return DateTimeUtils.calToDayOfWeek(timestamp, format);
    } catch(Exception e) {
      return null;
    }
  }
}
