package de.bimalo.common;

/**
 * <p>
 * The TimeRecorder is intended to use when there exists the need to trace
 * the execution time between a defined start and end point.</p>
 * <p>
 * The implementation is thread safe!<p>.
 * 
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 */
public final class TimeRecorder {

    /**
     * The human readable name of this TimeRecorder. Uniqueness will not be
     * guaranteed!
     */
    private String name = null;
    /**
     * Time in milliseconds when this timer was started.
     */
    private long startTime = 0;
    /**
     * Time in milliseconds when this timer was stopped.
     */
    private long stopTime = 0;
    /**
     * Computation of stopTime - startTime.
     */
    private long recordingTime = -1;

    /**
     * Creates a default <code>TimeRecorder</code>.
     */
    public TimeRecorder() {
        name = "TimeRecorder";
    }

    /**
     * Creates a <code>TimeRecorder</code> with a given name.
     * @param name the name for this TimeRecorder. If invalid a default name will 
     * be used instead
     */
    public TimeRecorder(String name) {
        if (name == null || name.isEmpty()) {
            this.name = "TimeRecorder";
        } else {
            this.name = name;
        }
    }

    /**
     * Starts the recording.
     */
    public synchronized void start() {
        startTime = System.currentTimeMillis();
        stopTime = startTime;
    }

    /**
     * <p>
     * Stops this TimeRecorder.</p>
     * <p>
     * Allowed only if the TimeRecorder has been previously started!</p>
     * @throws IllegalArgumentException if this TimeRecorder was not started (call to start() first).
     */
    public synchronized void stop() {
        if (startTime == 0) {
            throw new IllegalStateException("TimeRecorder was not started.");
        }
        stopTime = System.currentTimeMillis();
        recordingTime = stopTime - startTime;
    }

    /**
     * Gets the name of this TimeRecorder
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the recorded time in milliseconds between the start- and
     * stop time. If this TimeRecorder was not started it always returns -1.
     * @return the time in milliseconds between start and stop time.
     */
    public long getTime() {
        return recordingTime;
    }

    /**
     * Returns the result of this TimeRecorder.
     * @return the result of this Timer
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(name);
        buf.append(" finished in ");
        buf.append(recordingTime);
        buf.append(" ms.");
        return buf.toString();
    }
}
