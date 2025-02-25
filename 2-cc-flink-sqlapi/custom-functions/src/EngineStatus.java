package io.confluent.functions;

import org.apache.flink.table.functions.ScalarFunction;

public class EngineStatus extends ScalarFunction {

  private static final String CODE_DANGER = "DANGER";
  private static final String CODE_WARNING = "WARNING";
  private static final String CODE_OK = "OK";

  public String eval(Integer engineTemperature) {
    if (engineTemperature > 230) {
      return CODE_DANGER;
    }
    if (engineTemperature > 210) {
      return CODE_WARNING;
    }
    return CODE_OK;
  }
}
