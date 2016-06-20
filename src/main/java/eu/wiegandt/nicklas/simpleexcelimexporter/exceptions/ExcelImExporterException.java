package eu.wiegandt.nicklas.simpleexcelimexporter.exceptions;

/**
 * A excecption for errors occurred during im-/exporting.
 *
 * @author Nicklas Wiegandt (Nicklas2751)<br>
 *         <b>Mail:</b> nicklas@wiegandt.eu<br>
 *         <b>Jabber:</b> nicklas2751@elaon.de<br>
 *         <b>Skype:</b> Nicklas2751<br>
 *
 */
public class ExcelImExporterException extends Exception
{
    private static final long serialVersionUID = -8800365225300479358L;
    private final ExcelImExporterError error;

    /**
     * @param aError
     *            The occurred error.
     */
    public ExcelImExporterException(final ExcelImExporterError aError)
    {
        super(aError.getMessage());
        error = aError;
    }

    public ExcelImExporterError getError()
    {
        return error;
    }

}
