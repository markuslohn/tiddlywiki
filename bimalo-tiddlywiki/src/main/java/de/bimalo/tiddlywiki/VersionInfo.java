package de.bimalo.tiddlywiki;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Manages version information about this application. The version information
 * are retrieved by reading the manifest.mf file in the jar-File where the
 * requested package is located. Consult <code>java.lang.Package</code> for more
 * details on how to provide this information in manifest.mf.
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 * @see java.lang.Package
 */
public final class VersionInfo {

    /**
     * The default name of the package where this VersionInfo class belongs to.
     */
    private static String defaultPackageName
            = VersionInfo.class.getPackage().getName();

    /**
     * A name of a package for which to retrieve version information.
     */
    private String customPackageName;

    /**
     * The implementation title.
     */
    private String implementationTitle;

    /**
     * The implementation vendor.
     */
    private String implementationVendor;

    /**
     * The implementation version.
     */
    private String implementationVersion;

    /**
     * The specification title.
     */
    private String specificationTitle;

    /**
     * The specification vendor.
     */
    private String specificationVendor;

    /**
     * The specification version.
     */
    private String specificationVersion;

    /**
     * The name of the operating system.
     */
    private String operatingSystem;

    /**
     * The version of the operating system.
     */
    private String operationSystemVersion;

    /**
     * The version of JAVA.
     */
    private String javaVersion;

    /**
     * The name of the vendor of the JVM.
     */
    private String javaVendor;

    /**
     * Constructs a new <code>VersionInfo</code> object. The version information
     * will be retrieved for the package of this VersionInfo class.
     *
     * @throws NullPointerException if the package could not be found in the
     * classpath
     */
    public VersionInfo() {
        customPackageName = defaultPackageName;
        Package p = Package.getPackage(customPackageName);
        if (p == null) {
            throw new NullPointerException(getPackageNotFoundErrorMessage(customPackageName));
        }
        loadVersionInformationForPackage(p);
    }

    /**
     * Constructs a new <code>VersionInfo</code> object with given package name.
     * If the the packageName is null the default package name will be used
     * instead. This means it uses the package where this Versioninfo class
     * belongs to.
     *
     * @throws NullPointerException if the package could not be found in the
     * classpath
     */
    public VersionInfo(String packageName) {
        if (packageName == null || packageName.length() == 0) {
            customPackageName = defaultPackageName;
        } else {
            customPackageName = String.valueOf(packageName);
        }

        Package p = Package.getPackage(customPackageName);
        if (p == null) {
            throw new NullPointerException(getPackageNotFoundErrorMessage(customPackageName));
        }
        loadVersionInformationForPackage(p);
    }

    /**
     * Gets the version number as <code>String</code> object.
     *
     * @return version number, like "1.4.0.2"
     */
    public String getVersionNumberString() {
        String version = "0.0.0.0";

        if (implementationVersion != null) {
            version = implementationVersion;
        } else if (specificationVersion != null) {
            version = specificationVersion;
        }
        return version;
    }

    /**
     * Prints versioning information to the standard output device.
     */
    public void printToStdout() {
        System.out.print(getVersionInformation());
    }

    /**
     * Prints the versioning information to a AWT frame window.
     */
    public void printToWindow() {
        Frame frame = new Frame(customPackageName);
        frame.setLayout(null);
        frame.setSize(new Dimension(400, 300));
        frame.setBackground(SystemColor.control);
        frame.setResizable(false);
        frame.setSize(frame.getPreferredSize());
        Button button1 = new Button();
        button1.setLabel("OK");
        button1.setBounds(new Rectangle(163, 240, 68, 23));
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        TextArea textArea1 = new TextArea();
        textArea1.setBounds(new Rectangle(10, 40, 375, 185));
        textArea1.setEditable(false);
        textArea1.setText(getVersionInformation());
        frame.add(textArea1, null);
        frame.add(button1, null);

        frame.setVisible(true);
    }

    /**
     * Povides the versioning information as String.
     *
     * @return versioning information
     */
    public String toString() {
        return getVersionInformation();
    }

    private String getVersionInformation() {
        StringBuffer sbuf = new StringBuffer();

        sbuf.append("Package Name: ").append(customPackageName);
        sbuf.append("\n");
        sbuf.append("Implementation Title: ");
        if (this.implementationTitle != null) {
            sbuf.append(this.implementationTitle);
        } else {
            sbuf.append("n/a");
        }
        sbuf.append("\n");

        sbuf.append("Implementation Vendor: ");
        if (this.implementationVendor != null) {
            sbuf.append(this.implementationVendor);
        } else {
            sbuf.append("n/a");
        }
        sbuf.append("\n");

        sbuf.append("Implementation Version: ");
        if (this.implementationVersion != null) {
            sbuf.append(this.implementationVersion);
        } else {
            sbuf.append("n/a");
        }
        sbuf.append("\n");

        sbuf.append("Specification Title: ");
        if (this.specificationTitle != null) {
            sbuf.append(this.specificationTitle);
        } else {
            sbuf.append("n/a");
        }
        sbuf.append("\n");

        sbuf.append("Specification Vendor: ");
        if (this.specificationVendor != null) {
            sbuf.append(this.specificationVendor);
        } else {
            sbuf.append("n/a");
        }
        sbuf.append("\n");

        sbuf.append("Specification Version: ");
        if (this.specificationVersion != null) {
            sbuf.append(this.specificationVersion);
        } else {
            sbuf.append("n/a");
        }
        sbuf.append("\n");

        sbuf.append("Operating System: ");
        sbuf.append(this.operatingSystem);
        sbuf.append(" Version: ");
        sbuf.append(this.operationSystemVersion);
        sbuf.append("\n");

        sbuf.append("Java: ");
        sbuf.append(this.javaVendor);
        sbuf.append(" Version: ");
        sbuf.append(this.javaVersion);
        sbuf.append("\n");

        return sbuf.toString();
    }

    /**
     * Displays the versioning information.
     *
     * @param as the arguments
     */
    public void show(String[] as) {
        if (as.length > 0 && as[0].equalsIgnoreCase("-stdout")) {
            printToStdout();
        } else {
            printToWindow();
        }
    }

    /**
     * The main function for this object.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new VersionInfo().show(args);
    }

    /**
     * Loads the version information for the provided package.l
     *
     * @param p the package used to retrieve version informations
     */
    private void loadVersionInformationForPackage(Package p) {
        implementationTitle = p.getImplementationTitle();
        implementationVendor = p.getImplementationVendor();
        implementationVersion = p.getImplementationVersion();

        specificationTitle = p.getImplementationTitle();
        specificationVendor = p.getSpecificationVendor();
        specificationVersion = p.getSpecificationVersion();

        operatingSystem = System.getProperty("os.name");
        operationSystemVersion = System.getProperty("os.version");
        javaVersion = System.getProperty("java.version");
        javaVendor = System.getProperty("java.vendor");
    }

    private String getPackageNotFoundErrorMessage(String packageName) {
        StringBuffer sb = new StringBuffer();
        sb.append("Package ");
        sb.append(packageName);
        sb.append(" could not be found in the classpath.");
        return sb.toString();
    }
}
