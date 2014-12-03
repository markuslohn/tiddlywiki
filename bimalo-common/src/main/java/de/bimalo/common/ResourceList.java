package de.bimalo.common;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * A ResourceList can be used to find resources in the current class path by
 * using a regular expression.</p>
 * <p>
 * After creation of the ResourceList the pattern cannot be modified. However
 * when calling getResources the processing starts again! The result is not
 * cached internally!</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 */
public final class ResourceList {

    /**
     * The name of the classpath property.
     */
    private static String CLASSPATH_PROPERTY_NAME = "java.class.path";
    /**
     * Logger for this class.
     */
    final Logger logger = LoggerFactory.getLogger(ResourceList.class);
    /**
     * The pattern used to search for resources in the classpath.
     */
    private Pattern pattern;

    /**
     * Creates a new <code>ResourceList</code> with a specified regular
     * expression.
     *
     * @param regexp the regular expression used to search for resources in the
     * classpath
     * @exception IllegalArgumentException if regexp is null
     * @throws PatternSyntaxException if the regexp's syntax is invalid
     */
    public ResourceList(String regexp) {
        Assert.notNull(regexp);
        pattern = Pattern.compile(regexp);
    }

    /**
     * <p>
     * Finds all resources matching the specified pattern. A resource is some
     * data (images, audio, text, etc.) that can be accessed by class code in a
     * way that is independent of the location of the code.</p>
     *
     * @return An enumeration of URL objects for the resource. If no resources
     * could be found, the enumeration will be empty.
     * @throws IOException if I/O errors occur
     * @see java.lang.ClassLoader#getResources(String)
     */
    public Enumeration getResources() throws IOException {
        List resources = new ArrayList();
        List classpathElements = getCurrentClasspath();
        for (int i = 0; i < classpathElements.size(); i++) {
            resources.addAll(getResources((File) classpathElements.get(i)));
        }
        return Collections.enumeration(resources);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return pattern.toString();
    }

    /**
     * <p>
     * Finds all resources for an element in the classpath matching the
     * specified pattern.</p>
     * <p>
     * The classpath element is specified by <code>file</code>.</p>
     *
     * @param file the classpath element, like a jar-file or a directory with
     * class-files
     * @return a List containing URL object's to resources matching the
     * specified pattern.
     * @throws IOException if path could not be opened for read
     */
    private List getResources(File file) throws IOException {
        List resources = new ArrayList();
        if (file.isDirectory()) {
            resources.addAll(getResourcesFromDirectory(file));
        } else {
            resources.addAll(getResourcesFromJarFile(file));
        }
        return resources;
    }

    /**
     * <p>
     * Finds the resources in a jar-file represented by the provided File
     * object.</p>
     *
     * @param path the jar-file
     * @return a List containing URL object's to resources matching the
     * specified pattern.
     * @throws IOException if path could not be opened for read
     */
    private List getResourcesFromJarFile(File path) throws IOException {
        List resources = new ArrayList();

        if (path.canRead()) {
            JarFile jar = null;
            try {
                jar = new JarFile(path);
                for (Enumeration en = jar.entries(); en.hasMoreElements();) {
                    JarEntry resource = (JarEntry) en.nextElement();
                    String resourceName = resource.getName();
                    if (pattern.matcher(resourceName).matches()) {
                        resources.add(loadResource(resourceName));
                    }
                }
            } finally {
                if (jar != null) {
                    jar.close();
                }
            }
        }
        return resources;
    }

    /**
     * <p>
     * Finds the resources in a directory represented by the provided file
     * object.</p>
     *
     * @param path the jar-file
     * @return a List containing URL object's to resources matching the
     * specified pattern.
     * @throws IOException if path could not be opened for read
     */
    private List getResourcesFromDirectory(File path) throws IOException {
        List resources = new ArrayList();

        if (path.canRead()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isDirectory()) {
                    resources.addAll(getResourcesFromDirectory(file));
                } else {
                    String resourceName = file.getCanonicalPath();
                    if (pattern.matcher(resourceName).matches()) {
                        resources.add(file.toURL());
                    }
                }
            }
        }
        return resources;
    }

    /**
     * <p>
     * Gets the current configured classpath in the environment.</p>
     * <p>
     * The result is a List containing <code>File</code> objects. Every File
     * references an element in the classpath. An element can be a jar-archive,
     * a folder with class-files etc.</p>
     *
     * @return all classpath elements as File object's in a List
     */
    private List getCurrentClasspath() {
        List urls = new ArrayList();
        String classPath = System.getProperty(CLASSPATH_PROPERTY_NAME, ".");
        if (classPath != null) {
            String[] classPathElements = classPath.split(File.pathSeparator);
            for (int i = 0; i < classPathElements.length; i++) {
                urls.add(new File(classPathElements[i]));
            }
        } else {
            logger.warn("Could not determine classpath from system property {}.",
                    CLASSPATH_PROPERTY_NAME);
        }
        return urls;
    }

    /**
     * <p>
     * Finds the resource with the given name.</p>
     *
     * @param name the name of the resource
     * @return A URL object for reading the resource, or null if the resource
     * could not be found or the invoker doesn't have adequate privileges to get
     * the resource
     * @see java.lang.ClassLoader#getResource(String)
     * @throws IOException if the resource could not be found
     */
    private URL loadResource(String name) throws IOException {
        URL resource = getClass().getClassLoader().getResource(name);
        if (resource == null) {
            throw new IOException("Could not found resource " + name
                    + ". Could be a security issue?");
        }
        return resource;
    }
}
