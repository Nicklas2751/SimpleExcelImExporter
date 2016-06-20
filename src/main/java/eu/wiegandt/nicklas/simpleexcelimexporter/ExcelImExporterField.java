package eu.wiegandt.nicklas.simpleexcelimexporter;

/**
 * A POJO class to save the needed informations of a field during im- and
 * export.
 *
 * @author Nicklas Wiegandt (Nicklas2751)<br>
 *         <b>Mail:</b> nicklas@wiegandt.eu<br>
 *         <b>Jabber:</b> nicklas2751@elaon.de<br>
 *         <b>Skype:</b> Nicklas2751<br>
 *
 */
public class ExcelImExporterField
{

    private final Integer columnIndex;
    private final String columnName;
    private final String fieldName;

    /**
     * @param aColumnIndex
     *            The column index of the column which represents this field.
     * @param aColumnName
     *            The name of the column which represents this field.
     * @param aFieldName
     *            The name of the field.
     */
    public ExcelImExporterField(final Integer aColumnIndex, final String aColumnName, final String aFieldName)
    {
        super();
        columnIndex = aColumnIndex;
        columnName = aColumnName;
        fieldName = aFieldName;
    }

    public Integer getColumnIndex()
    {
        return columnIndex;
    }

    public String getColumnName()
    {
        return columnName;
    }

    public String getFieldName()
    {
        return fieldName;
    }

}
