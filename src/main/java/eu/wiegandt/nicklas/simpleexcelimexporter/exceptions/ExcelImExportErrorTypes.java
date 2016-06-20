package eu.wiegandt.nicklas.simpleexcelimexporter.exceptions;

/**
 * A enumeration with the possible error types.
 *
 * @author Nicklas Wiegandt (Nicklas2751)<br>
 *         <b>Mail:</b> nicklas@wiegandt.eu<br>
 *         <b>Jabber:</b> nicklas2751@elaon.de<br>
 *         <b>Skype:</b> Nicklas2751<br>
 *
 */
public enum ExcelImExportErrorTypes
{
    ENCRYPTED_DOCUMENT("A excel sheet or the whole file is encrypted."),
    EXPORT_FAILED_SYSTEM_ERROR("The export failed during a fatal exception. Please report to a developer."),
    IMPORT_FAILED_SYSTEM_ERROR("The import failed during a fatal exception. Please report to a developer."),
    MAPPING_NO_FALID_JSON_FILE(
            "The mapping file ist not a valid json file. Please checke the json syntax. You could generate a valid mapping json file an fill it with your mapping."),
    NOT_ALL_REQUIRED_FIELDS("The table don't contains all required fields. Follwoing fields are required: [%s]"),
    TABLE_IS_NOT_EXPORTABLE("The table with the name \"%s\" is not exportable."),
    TABLE_NAME_INVALID("Can't find a table with the name \"%s\".");
    private String messageTemplate;

    private ExcelImExportErrorTypes(final String aMessageTemplate)
    {
        messageTemplate = aMessageTemplate;
    }

    public String getMessageTemplate()
    {
        return messageTemplate;
    }

}
