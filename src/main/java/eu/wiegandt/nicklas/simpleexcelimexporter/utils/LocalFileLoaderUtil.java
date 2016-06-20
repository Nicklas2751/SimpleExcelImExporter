package eu.wiegandt.nicklas.simpleexcelimexporter.utils;

import java.io.File;
import java.net.URISyntaxException;

/**
 * A utility class to load a local file.
 *
 * @author Nicklas Wiegandt (Nicklas2751)<br>
 *         <b>Mail:</b> nicklas@wiegandt.eu<br>
 *         <b>Jabber:</b> nicklas2751@elaon.de<br>
 *         <b>Skype:</b> Nicklas2751<br>
 *
 */
public class LocalFileLoaderUtil
{
    private LocalFileLoaderUtil()
    {
        super();
    }

    /**
     * @param aLocalPath
     *            The path to the file which should be loaded.
     * @return The local file.
     */
    public static File getLocalFile(final String aLocalPath)
    {
        try
        {
            return new File(ClassLoader.getSystemResource("").toURI().getPath(), aLocalPath);
        }
        catch (final URISyntaxException uiUriSyntaxException)
        {
            throw new RuntimeException("This is a critical error. Please report it.", uiUriSyntaxException);
        }
    }

}
