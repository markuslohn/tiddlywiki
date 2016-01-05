package de.bimalo.haushaltsbuch.transformator.delete;

import de.bimalo.common.Assert;
import java.util.ArrayList;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * It processes a single line with values read from CSV file.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 */
public final class CSVLineProcessorResult extends ArrayList<CSVLineProcessorResult.Message> {

    /**
     * Logger instance.
     */
    private static Logger logger = LoggerFactory.getLogger(CSVLineProcessorResult.class);

    /**
     * The object represents the result of a CSVLineProcessor applied to a
     * single line read from a CSV file.
     */
    private Object result = null;
    
    /**
     * The line number this result belongs to in the CSV file.
     */
    private long lineNumber = 0;

    /**
     * Indicates whether the overall result is faulted or not. It is faulted
     * when a one of the Message objects defining a fault.
     */
    private boolean faulted = false;

    /**
     * Creates a new <code>CSVLineProcessorResult</code> with a given result
     * Object.
     *
     * @param result the result object from the CSVLineProcessor. NULL is
     * allowed!
     */
    public CSVLineProcessorResult(Object result) {
        super();
        if (result != null) {
            this.result = result;
        }
    }

       /**
     * Creates a new <code>CSVLineProcessorResult</code> with a given result
     * Object.
     *
     * @param lineNumber the number of the processed line
     * @param result the result object from the CSVLineProcessor. NULL is
     * allowed!
     * @exception IllegalArgumentException if lineNumber < 0
     */
    public CSVLineProcessorResult(long lineNumber, Object result) {
        super();
        Assert.isTrue(lineNumber >= 0);
        this.lineNumber = lineNumber;
        if (result != null) {
            this.result = result;
        }
    }

    /**
     * Adds a new <code>CSVLineProcessorResult.Message</code> based on the given
     * input parameters.
     *
     * @param message the message
     * @return true if this collection changed as a result of the call
     */
    public boolean add(String message) {
        boolean result = false;
        try {
            CSVLineProcessorResult.Message msg = new CSVLineProcessorResult.Message(lineNumber, message);
            faulted = msg.isFault();
            result = super.add(msg);
        } catch (IllegalArgumentException ex) {
            result = false;
            if (logger.isTraceEnabled()) {
                logger.error(ex.getMessage(), ex);
            }
        }
        return result;
    }

    /**
     * Adds a new <code>CSVLineProcessorResult.Message</code> based on the given
     * input parameters.
     *
     * @param message the message
     * @param columnName the column reference (NULL is allowed!)
     * @return true if this collection changed as a result of the call
     */
    public boolean add(String message, String columnName) {
        boolean result = false;
        try {
            CSVLineProcessorResult.Message msg = new CSVLineProcessorResult.Message(lineNumber, message, columnName);
            faulted = msg.isFault();
            result = super.add(msg);
        } catch (IllegalArgumentException ex) {
            result = false;
            if (logger.isTraceEnabled()) {
                logger.error(ex.getMessage(), ex);
            }
        }
        return result;
    }

    /**
     * Adds a new <code>CSVLineProcessorResult.Message</code> based on the given
     * input parameters.
     *
     * @param th the java.lang.Throwable object
     * @return true if this collection changed as a result of the call
     */
    public boolean add(Throwable th) {
        boolean result = false;
        try {
            CSVLineProcessorResult.Message msg = new CSVLineProcessorResult.Message(lineNumber, th);
            faulted = msg.isFault();
            result = super.add(msg);
        } catch (IllegalArgumentException ex) {
            result = false;
            if (logger.isTraceEnabled()) {
                logger.error(ex.getMessage(), ex);
            }
        }
        return result;
    }

    /**
     * Adds a new <code>CSVLineProcessorResult.Message</code> based on the given
     * input parameters.
     *
     * @param th the java.lang.Throwable object
     * @param columnName the column reference (NULL is allowed!)
     * @return true if this collection changed as a result of the call
     */
    public boolean add(Throwable th, String columnName) {
        boolean result = false;
        try {
            CSVLineProcessorResult.Message msg = new CSVLineProcessorResult.Message(lineNumber, th, columnName);
            faulted = msg.isFault();
            result = super.add(msg);
        } catch (IllegalArgumentException ex) {
            result = false;
            if (logger.isTraceEnabled()) {
                logger.error(ex.getMessage(), ex);
            }
        }
        return result;
    }

    public Object getResult() {
        return result;
    }

    public long getLineNumber() {
        return lineNumber;
    }

    
    public boolean isFaulted() {
        return faulted;
    }

    /**
     * Represents a message for information or faults.
     */
    public class Message {

        /**
         * The lineNumber of the processed record.
         */
        private long lineNumber = 0;

        /**
         * A positive or negative message.
         */
        private String message = null;
        /**
         * The column reference.
         */
        private String columnName = null;
        /**
         * Is the message an information or a fault.
         */
        private boolean fault = false;

        private int hashCode = -1;

        /**
         * Creates a new <code>Message</code> with a message.
         *
         * @param lineNumber the number of the processed line
         * @param message the message
         * @exception IllegalArgumentException if lineNumber is negative or
         * message is null
         */
        public Message(long lineNumber, String message) {
            Assert.isTrue(lineNumber >= 0);
            Assert.notNull(message);
            this.lineNumber = lineNumber;
            this.message = message;
        }

        /**
         * Creates a new <code>Message</code> with a message.
         *
         * @param lineNumber the number of the processed line
         * @param message the message
         * @param columnName the column reference (NULL is allowed!)
         * @exception IllegalArgumentException if message is null
         */
        public Message(long lineNumber, String message, String columnName) {
            Assert.isTrue(lineNumber >= 0);
            Assert.notNull(message);
            this.lineNumber = lineNumber;
            this.message = message;
            if (columnName != null) {
                this.columnName = columnName;
            }
        }

        /**
         * Creates a new <code>Message</code> based on a given
         * java.lang.Throwable object.
         *
         * @param lineNumber the number of the processed line
         * @param th the java.lang.Throwable object
         * @exception IllegalArgumentException if th is null
         */
        public Message(long lineNumber, Throwable th) {
            Assert.isTrue(lineNumber >= 0);
            Assert.notNull(th);
            this.lineNumber = lineNumber;
            this.message = th.getMessage();
            this.fault = true;
        }

        /**
         * Creates a new <code>Message</code> based on a given
         * java.lang.Throwable object.
         *
         * @param lineNumber the number of the processed line
         * @param th the java.lang.Throwable object
         * @param columnName the column reference (NULL is allowed!)
         * @exception IllegalArgumentException if th is null
         */
        public Message(long lineNumber, Throwable th, String columnName) {
            Assert.isTrue(lineNumber >= 0);
            Assert.notNull(th);
            this.lineNumber = lineNumber;
            this.message = th.getMessage();
            if (columnName != null) {
                this.columnName = columnName;
            }
            this.fault = true;
        }

        public long getLineNumber() {
            return lineNumber;
        }

        public String getMessage() {
            return message;
        }

        public String getColumnName() {
            return columnName;
        }

        public boolean isFault() {
            return fault;
        }

        @Override
        public int hashCode() {
            if (hashCode == -1) {
                hashCode = 5;
                hashCode = 97 * hashCode + Objects.hashCode(Long.valueOf(lineNumber));
                hashCode = 97 * hashCode + Objects.hashCode(this.message);
                hashCode = 97 * hashCode + Objects.hashCode(this.columnName);
            }
            return hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            boolean equal = false;
            if (obj != null) {
                if (getClass() == obj.getClass()) {
                    final Message other = (Message) obj;
                    if (this.lineNumber == other.lineNumber && Objects.equals(this.message, other.message) && Objects.equals(this.columnName, other.columnName)) {
                        equal = true;
                    }

                }
            }
            return equal;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Message");
            sb.append("{");
            sb.append("lineNumber=").append(lineNumber).append(", ");
            sb.append("message=").append(message).append(", ");
            sb.append("columnName=").append(columnName).append(", ");
            sb.append("fault=").append(fault);
            sb.append("}");
            return sb.toString();
        }

    }
}
