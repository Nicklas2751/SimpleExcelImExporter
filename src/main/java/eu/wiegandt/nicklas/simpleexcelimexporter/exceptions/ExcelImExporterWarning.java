package eu.wiegandt.nicklas.simpleexcelimexporter.exceptions;

/**
 * A class which represents warnings which give extra informations for the user.
 *
 * @author Nicklas Wiegandt (Nicklas2751)<br>
 *         <b>Mail:</b> nicklas@wiegandt.eu<br>
 *         <b>Jabber:</b> nicklas2751@elaon.de<br>
 *         <b>Skype:</b> Nicklas2751<br>
 *
 */
public class ExcelImExporterWarning
{

    private final String message;
    private final ExcelImExportWarningTypes type;

    /**
     * The message template of the type will be used as message.
     *
     * @param aType
     *            The warning type.
     */
    public ExcelImExporterWarning(final ExcelImExportWarningTypes aType)
    {
        this(aType, aType.getMessageTemplate());
    }

    /**
     * @param aType
     *            The warning type.
     * @param aMessage
     *            A message which describes what's happened.
     */
    public ExcelImExporterWarning(final ExcelImExportWarningTypes aType, final String aMessage)
    {
        super();
        type = aType;
        message = aMessage;
    }

    public String getMessage()
    {
        return message;
    }

    public ExcelImExportWarningTypes getType()
    {
        return type;
    }

}
