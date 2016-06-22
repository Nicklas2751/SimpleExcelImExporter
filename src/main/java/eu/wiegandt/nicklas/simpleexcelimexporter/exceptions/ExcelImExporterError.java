package eu.wiegandt.nicklas.simpleexcelimexporter.exceptions;

import java.io.Serializable;

/**
 * A class which represents an error while im-/exporting.
 *
 * @author Nicklas Wiegandt (Nicklas2751)<br>
 *         <b>Mail:</b> nicklas@wiegandt.eu<br>
 *         <b>Jabber:</b> nicklas2751@elaon.de<br>
 *         <b>Skype:</b> Nicklas2751<br>
 *
 */
public class ExcelImExporterError implements Serializable
{
    private static final long serialVersionUID = -1679175633391151510L;
    private final String message;
    private final ExcelImExportErrorTypes type;

    /**
     * The message template of the type will be used as message.
     *
     * @param aType
     *            The type of error which occurred.
     * @see ExcelImExportErrorTypes
     */
    public ExcelImExporterError(final ExcelImExportErrorTypes aType)
    {
        this(aType, aType.getMessageTemplate());
    }

    /**
     * @param aType
     *            The type of error which occurred.
     * @param aMessage
     *            A message which describes the occurred error. A message
     *            template or a complete message can be get from the message
     *            type: {@link ExcelImExportErrorTypes#getMessageTemplate}.
     * @see ExcelImExportErrorTypes
     */
    public ExcelImExporterError(final ExcelImExportErrorTypes aType, final String aMessage)
    {
        super();
        type = aType;
        message = aMessage;
    }

    public String getMessage()
    {
        return message;
    }

    public ExcelImExportErrorTypes getType()
    {
        return type;
    }
}
