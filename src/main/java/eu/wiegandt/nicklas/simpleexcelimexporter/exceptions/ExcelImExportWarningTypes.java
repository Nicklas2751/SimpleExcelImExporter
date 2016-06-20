package eu.wiegandt.nicklas.simpleexcelimexporter.exceptions;

/**
 * A enumeration whit the possible warning types.
 *
 * @author Nicklas Wiegandt (Nicklas2751)<br>
 *         <b>Mail:</b> nicklas@wiegandt.eu<br>
 *         <b>Jabber:</b> nicklas2751@elaon.de<br>
 *         <b>Skype:</b> Nicklas2751<br>
 *
 */
public enum ExcelImExportWarningTypes
{
    COLUMN_NOT_IN_MAPPING("The column \"%s\" is not a part of the mapping file."),
    FIELD_NOT_IMPORTABLE("The field \"%s\" is not importable and will be skipped."),
    TABLE_INVALID_SKIP("The table \"%s\" is invalid and will be skipped");
    private String messageTemplate;

    private ExcelImExportWarningTypes(final String aMessageTemplate)
    {
        messageTemplate = aMessageTemplate;
    }

    public String getMessageTemplate()
    {
        return messageTemplate;
    }

}
