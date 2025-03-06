package io.confluent.stream;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

public class Stateful extends KeyedProcessFunction<String, String, String> {

    private transient ValueState<Tuple2<Long, byte[]>> data;

    @Override
    public void open(Configuration config) {
        ValueStateDescriptor<Tuple2<Long, byte[]>> descriptor = new ValueStateDescriptor<>(
                "data", // the state name
                TypeInformation.of(new TypeHint<Tuple2<Long, byte[]>>() {}), // type information
                Tuple2.of(0L, new byte[50])); // default value of the state, if nothing was set
        data = getRuntimeContext().getState(descriptor);
    }

    @Override
    public void processElement(
            String value, KeyedProcessFunction<String, String, String>.Context ctx, Collector<String> out)
            throws Exception {
        data.update(new Tuple2<>(0L, new byte[50]));
    }
}
