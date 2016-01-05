package de.bimalo.common;

import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p> Represents the process identifier from the operating system for the JVM
 * running this class. Currently only supported on unix platforms!</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 */
public final class PID {

    /**
     * Logger for this class.
     */
    final Logger logger = LoggerFactory.getLogger(Localizer.class);
    /**
     * The singleton instance for this class.
     */
    private static PID instance = null;
    /**
     * The process identifier of the operating system thread running the JVM
     * where this class is being executed.
     */
    private String processId = null;
    /**
     * Indicates whether the JVM is running on windows operating system or not.
     */
    private boolean windows = false;
    /**
     * The hash code for for this PID.
     */
    private int hashCode = -1;

    /**
     * Private constructor. Detects the process identifier.
     */
    private PID() {
        //TODO: Implement better exception handling when needed!

        windows = System.getProperty("os.name").startsWith("Windows");

        if (!windows) {
            Runtime r = Runtime.getRuntime();
            InputStream in = null;
            try {
                String[] cmd = {"/bin/bash", "-c", "echo $PPID"};
                Process p = r.exec(cmd);
                if (p != null) {
                    byte[] buf = new byte[100];
                    in = p.getInputStream();
                    in.read(buf);
                    processId = new String(buf);
                    hashCode = 17 + processId.hashCode();
                }
            }
            catch (IOException ex) {
                logger.error("PID could not be determined.", ex);
                processId = "unknown";
            }
            finally {
                IOUtilities.closeInputStream(in);
            }
        } else {
            processId = "unknown";
        }
    }

    /**
     * Gets an instance of this class.
     *
     * @return the already constructed instance of PID
     */
    public static synchronized PID getInstance() {
        if (instance == null) {
            instance = new PID();
        }
        return instance;
    }

    /**
     * Gets the process identifier of the operating system process running the
     * JVM where this class is being executed.
     *
     * @return the process identifier
     */
    public String getProcessId() {
        return processId;
    }

    /**
     * Gets the process identifier of the operating system process running the
     * JVM where this class is being executed.
     *
     * @return the process identifier
     */
    public String toString() {
        return getProcessId();
    }

    /**
     * Returns a hash code value for this PID.
     *
     * @return the hash code
     */
    public int hashCode() {
        return hashCode;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false
     * otherwise.
     */
    public boolean equals(Object obj) {
        boolean result = true;
        
        if (obj == null || (!( obj instanceof PID ))) {
            result = false;
        }
        PID bean = (PID) obj;
        result =  ( this.getProcessId().equals(bean.getProcessId()) );
        return result;
    }
}