package eu.wiegandt.nicklas.simpleexcelimexporter.utils;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    public static Path getLocalFile(final String aLocalPath)
    {
        try
        {
            return Paths.get(ClassLoader.getSystemResource("").toURI()).resolve(aLocalPath);
        }
        catch (final URISyntaxException uiUriSyntaxException)
        {
            throw new IllegalStateException("This is a critical error. Please report it.", uiUriSyntaxException);
        }
    }

}
