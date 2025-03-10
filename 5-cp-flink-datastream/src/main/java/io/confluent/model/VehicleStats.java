/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package io.confluent.model;

import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.SchemaStore;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;

@org.apache.avro.specific.AvroGenerated
public class VehicleStats extends org.apache.avro.specific.SpecificRecordBase
        implements org.apache.avro.specific.SpecificRecord {
    private static final long serialVersionUID = 372883042547573847L;

    public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser()
            .parse(
                    "{\"type\":\"record\",\"name\":\"VehicleStats\",\"namespace\":\"io.confluent.model\",\"fields\":[{\"name\":\"usage_category\",\"type\":\"string\"},{\"name\":\"vehicle_count\",\"type\":[\"null\",\"long\"],\"default\":null},{\"name\":\"window_start\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"window_end\",\"type\":[\"null\",\"string\"],\"default\":null}]}");

    public static org.apache.avro.Schema getClassSchema() {
        return SCHEMA$;
    }

    private static final SpecificData MODEL$ = new SpecificData();

    private static final BinaryMessageEncoder<VehicleStats> ENCODER = new BinaryMessageEncoder<>(MODEL$, SCHEMA$);

    private static final BinaryMessageDecoder<VehicleStats> DECODER = new BinaryMessageDecoder<>(MODEL$, SCHEMA$);

    /**
     * Return the BinaryMessageEncoder instance used by this class.
     * @return the message encoder used by this class
     */
    public static BinaryMessageEncoder<VehicleStats> getEncoder() {
        return ENCODER;
    }

    /**
     * Return the BinaryMessageDecoder instance used by this class.
     * @return the message decoder used by this class
     */
    public static BinaryMessageDecoder<VehicleStats> getDecoder() {
        return DECODER;
    }

    /**
     * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
     * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
     * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
     */
    public static BinaryMessageDecoder<VehicleStats> createDecoder(SchemaStore resolver) {
        return new BinaryMessageDecoder<>(MODEL$, SCHEMA$, resolver);
    }

    /**
     * Serializes this VehicleStats to a ByteBuffer.
     * @return a buffer holding the serialized data for this instance
     * @throws java.io.IOException if this instance could not be serialized
     */
    public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
        return ENCODER.encode(this);
    }

    /**
     * Deserializes a VehicleStats from a ByteBuffer.
     * @param b a byte buffer holding serialized data for an instance of this class
     * @return a VehicleStats instance decoded from the given buffer
     * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
     */
    public static VehicleStats fromByteBuffer(java.nio.ByteBuffer b) throws java.io.IOException {
        return DECODER.decode(b);
    }

    private java.lang.CharSequence usage_category;
    private java.lang.Long vehicle_count;
    private java.lang.CharSequence window_start;
    private java.lang.CharSequence window_end;

    /**
     * Default constructor.  Note that this does not initialize fields
     * to their default values from the schema.  If that is desired then
     * one should use <code>newBuilder()</code>.
     */
    public VehicleStats() {}

    /**
     * All-args constructor.
     * @param usage_category The new value for usage_category
     * @param vehicle_count The new value for vehicle_count
     * @param window_start The new value for window_start
     * @param window_end The new value for window_end
     */
    public VehicleStats(
            java.lang.CharSequence usage_category,
            java.lang.Long vehicle_count,
            java.lang.CharSequence window_start,
            java.lang.CharSequence window_end) {
        this.usage_category = usage_category;
        this.vehicle_count = vehicle_count;
        this.window_start = window_start;
        this.window_end = window_end;
    }

    @Override
    public org.apache.avro.specific.SpecificData getSpecificData() {
        return MODEL$;
    }

    @Override
    public org.apache.avro.Schema getSchema() {
        return SCHEMA$;
    }

    // Used by DatumWriter.  Applications should not call.
    @Override
    public java.lang.Object get(int field$) {
        switch (field$) {
            case 0:
                return usage_category;
            case 1:
                return vehicle_count;
            case 2:
                return window_start;
            case 3:
                return window_end;
            default:
                throw new IndexOutOfBoundsException("Invalid index: " + field$);
        }
    }

    // Used by DatumReader.  Applications should not call.
    @Override
    @SuppressWarnings(value = "unchecked")
    public void put(int field$, java.lang.Object value$) {
        switch (field$) {
            case 0:
                usage_category = (java.lang.CharSequence) value$;
                break;
            case 1:
                vehicle_count = (java.lang.Long) value$;
                break;
            case 2:
                window_start = (java.lang.CharSequence) value$;
                break;
            case 3:
                window_end = (java.lang.CharSequence) value$;
                break;
            default:
                throw new IndexOutOfBoundsException("Invalid index: " + field$);
        }
    }

    /**
     * Gets the value of the 'usage_category' field.
     * @return The value of the 'usage_category' field.
     */
    public java.lang.CharSequence getUsageCategory() {
        return usage_category;
    }

    /**
     * Sets the value of the 'usage_category' field.
     * @param value the value to set.
     */
    public void setUsageCategory(java.lang.CharSequence value) {
        this.usage_category = value;
    }

    /**
     * Gets the value of the 'vehicle_count' field.
     * @return The value of the 'vehicle_count' field.
     */
    public java.lang.Long getVehicleCount() {
        return vehicle_count;
    }

    /**
     * Sets the value of the 'vehicle_count' field.
     * @param value the value to set.
     */
    public void setVehicleCount(java.lang.Long value) {
        this.vehicle_count = value;
    }

    /**
     * Gets the value of the 'window_start' field.
     * @return The value of the 'window_start' field.
     */
    public java.lang.CharSequence getWindowStart() {
        return window_start;
    }

    /**
     * Sets the value of the 'window_start' field.
     * @param value the value to set.
     */
    public void setWindowStart(java.lang.CharSequence value) {
        this.window_start = value;
    }

    /**
     * Gets the value of the 'window_end' field.
     * @return The value of the 'window_end' field.
     */
    public java.lang.CharSequence getWindowEnd() {
        return window_end;
    }

    /**
     * Sets the value of the 'window_end' field.
     * @param value the value to set.
     */
    public void setWindowEnd(java.lang.CharSequence value) {
        this.window_end = value;
    }

    /**
     * Creates a new VehicleStats RecordBuilder.
     * @return A new VehicleStats RecordBuilder
     */
    public static io.confluent.model.VehicleStats.Builder newBuilder() {
        return new io.confluent.model.VehicleStats.Builder();
    }

    /**
     * Creates a new VehicleStats RecordBuilder by copying an existing Builder.
     * @param other The existing builder to copy.
     * @return A new VehicleStats RecordBuilder
     */
    public static io.confluent.model.VehicleStats.Builder newBuilder(io.confluent.model.VehicleStats.Builder other) {
        if (other == null) {
            return new io.confluent.model.VehicleStats.Builder();
        } else {
            return new io.confluent.model.VehicleStats.Builder(other);
        }
    }

    /**
     * Creates a new VehicleStats RecordBuilder by copying an existing VehicleStats instance.
     * @param other The existing instance to copy.
     * @return A new VehicleStats RecordBuilder
     */
    public static io.confluent.model.VehicleStats.Builder newBuilder(io.confluent.model.VehicleStats other) {
        if (other == null) {
            return new io.confluent.model.VehicleStats.Builder();
        } else {
            return new io.confluent.model.VehicleStats.Builder(other);
        }
    }

    /**
     * RecordBuilder for VehicleStats instances.
     */
    @org.apache.avro.specific.AvroGenerated
    public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<VehicleStats>
            implements org.apache.avro.data.RecordBuilder<VehicleStats> {

        private java.lang.CharSequence usage_category;
        private java.lang.Long vehicle_count;
        private java.lang.CharSequence window_start;
        private java.lang.CharSequence window_end;

        /** Creates a new Builder */
        private Builder() {
            super(SCHEMA$, MODEL$);
        }

        /**
         * Creates a Builder by copying an existing Builder.
         * @param other The existing Builder to copy.
         */
        private Builder(io.confluent.model.VehicleStats.Builder other) {
            super(other);
            if (isValidValue(fields()[0], other.usage_category)) {
                this.usage_category = data().deepCopy(fields()[0].schema(), other.usage_category);
                fieldSetFlags()[0] = other.fieldSetFlags()[0];
            }
            if (isValidValue(fields()[1], other.vehicle_count)) {
                this.vehicle_count = data().deepCopy(fields()[1].schema(), other.vehicle_count);
                fieldSetFlags()[1] = other.fieldSetFlags()[1];
            }
            if (isValidValue(fields()[2], other.window_start)) {
                this.window_start = data().deepCopy(fields()[2].schema(), other.window_start);
                fieldSetFlags()[2] = other.fieldSetFlags()[2];
            }
            if (isValidValue(fields()[3], other.window_end)) {
                this.window_end = data().deepCopy(fields()[3].schema(), other.window_end);
                fieldSetFlags()[3] = other.fieldSetFlags()[3];
            }
        }

        /**
         * Creates a Builder by copying an existing VehicleStats instance
         * @param other The existing instance to copy.
         */
        private Builder(io.confluent.model.VehicleStats other) {
            super(SCHEMA$, MODEL$);
            if (isValidValue(fields()[0], other.usage_category)) {
                this.usage_category = data().deepCopy(fields()[0].schema(), other.usage_category);
                fieldSetFlags()[0] = true;
            }
            if (isValidValue(fields()[1], other.vehicle_count)) {
                this.vehicle_count = data().deepCopy(fields()[1].schema(), other.vehicle_count);
                fieldSetFlags()[1] = true;
            }
            if (isValidValue(fields()[2], other.window_start)) {
                this.window_start = data().deepCopy(fields()[2].schema(), other.window_start);
                fieldSetFlags()[2] = true;
            }
            if (isValidValue(fields()[3], other.window_end)) {
                this.window_end = data().deepCopy(fields()[3].schema(), other.window_end);
                fieldSetFlags()[3] = true;
            }
        }

        /**
         * Gets the value of the 'usage_category' field.
         * @return The value.
         */
        public java.lang.CharSequence getUsageCategory() {
            return usage_category;
        }

        /**
         * Sets the value of the 'usage_category' field.
         * @param value The value of 'usage_category'.
         * @return This builder.
         */
        public io.confluent.model.VehicleStats.Builder setUsageCategory(java.lang.CharSequence value) {
            validate(fields()[0], value);
            this.usage_category = value;
            fieldSetFlags()[0] = true;
            return this;
        }

        /**
         * Checks whether the 'usage_category' field has been set.
         * @return True if the 'usage_category' field has been set, false otherwise.
         */
        public boolean hasUsageCategory() {
            return fieldSetFlags()[0];
        }

        /**
         * Clears the value of the 'usage_category' field.
         * @return This builder.
         */
        public io.confluent.model.VehicleStats.Builder clearUsageCategory() {
            usage_category = null;
            fieldSetFlags()[0] = false;
            return this;
        }

        /**
         * Gets the value of the 'vehicle_count' field.
         * @return The value.
         */
        public java.lang.Long getVehicleCount() {
            return vehicle_count;
        }

        /**
         * Sets the value of the 'vehicle_count' field.
         * @param value The value of 'vehicle_count'.
         * @return This builder.
         */
        public io.confluent.model.VehicleStats.Builder setVehicleCount(java.lang.Long value) {
            validate(fields()[1], value);
            this.vehicle_count = value;
            fieldSetFlags()[1] = true;
            return this;
        }

        /**
         * Checks whether the 'vehicle_count' field has been set.
         * @return True if the 'vehicle_count' field has been set, false otherwise.
         */
        public boolean hasVehicleCount() {
            return fieldSetFlags()[1];
        }

        /**
         * Clears the value of the 'vehicle_count' field.
         * @return This builder.
         */
        public io.confluent.model.VehicleStats.Builder clearVehicleCount() {
            vehicle_count = null;
            fieldSetFlags()[1] = false;
            return this;
        }

        /**
         * Gets the value of the 'window_start' field.
         * @return The value.
         */
        public java.lang.CharSequence getWindowStart() {
            return window_start;
        }

        /**
         * Sets the value of the 'window_start' field.
         * @param value The value of 'window_start'.
         * @return This builder.
         */
        public io.confluent.model.VehicleStats.Builder setWindowStart(java.lang.CharSequence value) {
            validate(fields()[2], value);
            this.window_start = value;
            fieldSetFlags()[2] = true;
            return this;
        }

        /**
         * Checks whether the 'window_start' field has been set.
         * @return True if the 'window_start' field has been set, false otherwise.
         */
        public boolean hasWindowStart() {
            return fieldSetFlags()[2];
        }

        /**
         * Clears the value of the 'window_start' field.
         * @return This builder.
         */
        public io.confluent.model.VehicleStats.Builder clearWindowStart() {
            window_start = null;
            fieldSetFlags()[2] = false;
            return this;
        }

        /**
         * Gets the value of the 'window_end' field.
         * @return The value.
         */
        public java.lang.CharSequence getWindowEnd() {
            return window_end;
        }

        /**
         * Sets the value of the 'window_end' field.
         * @param value The value of 'window_end'.
         * @return This builder.
         */
        public io.confluent.model.VehicleStats.Builder setWindowEnd(java.lang.CharSequence value) {
            validate(fields()[3], value);
            this.window_end = value;
            fieldSetFlags()[3] = true;
            return this;
        }

        /**
         * Checks whether the 'window_end' field has been set.
         * @return True if the 'window_end' field has been set, false otherwise.
         */
        public boolean hasWindowEnd() {
            return fieldSetFlags()[3];
        }

        /**
         * Clears the value of the 'window_end' field.
         * @return This builder.
         */
        public io.confluent.model.VehicleStats.Builder clearWindowEnd() {
            window_end = null;
            fieldSetFlags()[3] = false;
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public VehicleStats build() {
            try {
                VehicleStats record = new VehicleStats();
                record.usage_category =
                        fieldSetFlags()[0] ? this.usage_category : (java.lang.CharSequence) defaultValue(fields()[0]);
                record.vehicle_count =
                        fieldSetFlags()[1] ? this.vehicle_count : (java.lang.Long) defaultValue(fields()[1]);
                record.window_start =
                        fieldSetFlags()[2] ? this.window_start : (java.lang.CharSequence) defaultValue(fields()[2]);
                record.window_end =
                        fieldSetFlags()[3] ? this.window_end : (java.lang.CharSequence) defaultValue(fields()[3]);
                return record;
            } catch (org.apache.avro.AvroMissingFieldException e) {
                throw e;
            } catch (java.lang.Exception e) {
                throw new org.apache.avro.AvroRuntimeException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static final org.apache.avro.io.DatumWriter<VehicleStats> WRITER$ =
            (org.apache.avro.io.DatumWriter<VehicleStats>) MODEL$.createDatumWriter(SCHEMA$);

    @Override
    public void writeExternal(java.io.ObjectOutput out) throws java.io.IOException {
        WRITER$.write(this, SpecificData.getEncoder(out));
    }

    @SuppressWarnings("unchecked")
    private static final org.apache.avro.io.DatumReader<VehicleStats> READER$ =
            (org.apache.avro.io.DatumReader<VehicleStats>) MODEL$.createDatumReader(SCHEMA$);

    @Override
    public void readExternal(java.io.ObjectInput in) throws java.io.IOException {
        READER$.read(this, SpecificData.getDecoder(in));
    }

    @Override
    protected boolean hasCustomCoders() {
        return true;
    }

    @Override
    public void customEncode(org.apache.avro.io.Encoder out) throws java.io.IOException {
        out.writeString(this.usage_category);

        if (this.vehicle_count == null) {
            out.writeIndex(0);
            out.writeNull();
        } else {
            out.writeIndex(1);
            out.writeLong(this.vehicle_count);
        }

        if (this.window_start == null) {
            out.writeIndex(0);
            out.writeNull();
        } else {
            out.writeIndex(1);
            out.writeString(this.window_start);
        }

        if (this.window_end == null) {
            out.writeIndex(0);
            out.writeNull();
        } else {
            out.writeIndex(1);
            out.writeString(this.window_end);
        }
    }

    @Override
    public void customDecode(org.apache.avro.io.ResolvingDecoder in) throws java.io.IOException {
        org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
        if (fieldOrder == null) {
            this.usage_category =
                    in.readString(this.usage_category instanceof Utf8 ? (Utf8) this.usage_category : null);

            if (in.readIndex() != 1) {
                in.readNull();
                this.vehicle_count = null;
            } else {
                this.vehicle_count = in.readLong();
            }

            if (in.readIndex() != 1) {
                in.readNull();
                this.window_start = null;
            } else {
                this.window_start = in.readString(this.window_start instanceof Utf8 ? (Utf8) this.window_start : null);
            }

            if (in.readIndex() != 1) {
                in.readNull();
                this.window_end = null;
            } else {
                this.window_end = in.readString(this.window_end instanceof Utf8 ? (Utf8) this.window_end : null);
            }

        } else {
            for (int i = 0; i < 4; i++) {
                switch (fieldOrder[i].pos()) {
                    case 0:
                        this.usage_category =
                                in.readString(this.usage_category instanceof Utf8 ? (Utf8) this.usage_category : null);
                        break;

                    case 1:
                        if (in.readIndex() != 1) {
                            in.readNull();
                            this.vehicle_count = null;
                        } else {
                            this.vehicle_count = in.readLong();
                        }
                        break;

                    case 2:
                        if (in.readIndex() != 1) {
                            in.readNull();
                            this.window_start = null;
                        } else {
                            this.window_start =
                                    in.readString(this.window_start instanceof Utf8 ? (Utf8) this.window_start : null);
                        }
                        break;

                    case 3:
                        if (in.readIndex() != 1) {
                            in.readNull();
                            this.window_end = null;
                        } else {
                            this.window_end =
                                    in.readString(this.window_end instanceof Utf8 ? (Utf8) this.window_end : null);
                        }
                        break;

                    default:
                        throw new java.io.IOException("Corrupt ResolvingDecoder.");
                }
            }
        }
    }
}
