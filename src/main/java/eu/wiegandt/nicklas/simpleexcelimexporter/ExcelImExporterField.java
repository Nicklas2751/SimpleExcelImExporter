package eu.wiegandt.nicklas.simpleexcelimexporter;

import eu.wiegandt.nicklas.simpleexcelimexporter.annotations.ExcelField;
import eu.wiegandt.nicklas.simpleexcelimexporter.api.DataClass;

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
    private static final String GETTER_METHOD_START = "get";
    private static final String SETTER_METHOD_START = "set";
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

    /**
     * Generates a getter method name for the name of the field.
     *
     * @param aField
     *            The field which name should be used.
     * @return A getter method name.
     */
    private static String fieldNameToGetterName(final ExcelImExporterField aField)
    {
        return fieldNameToMethodName(aField, GETTER_METHOD_START);
    }

    /**
     * Converts a field name to a method name with the given method prefix.
     *
     * @param aField
     *            The {@link ExcelImExporterField} with the field name which
     *            should be converted.
     * @param aMethodPrefix
     *            The method prefix.
     * @return A method name with the prefix and the field name.<br>
     *         Example: field name: <i>"importField"</i> and method prefix:
     *         <i>"get"</i> will be converted to: <i>"getImportField"</i>.
     */
    private static String fieldNameToMethodName(final ExcelImExporterField aField, final String aMethodPrefix)
    {
        final String fieldName = aField.getFieldName();

        final StringBuilder getterMethodNameBuilder = new StringBuilder();
        getterMethodNameBuilder.append(aMethodPrefix);
        getterMethodNameBuilder.append(fieldName.substring(0, 1).toUpperCase());
        getterMethodNameBuilder.append(fieldName.substring(1));
        return getterMethodNameBuilder.toString();
    }

    /**
     * Generates a setter method name for the name of the field.
     *
     * @param aField
     *            The field which name should be used.
     * @return A setter method name.
     */
    private static String fieldNameToSetterName(final ExcelImExporterField aField)
    {
        return fieldNameToMethodName(aField, SETTER_METHOD_START);
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

    /**
     * Determines the name of the getter method.
     *
     * @param aDataClass
     *            The data class which field is used.
     * @return The getter method name.
     * @throws NoSuchFieldException
     *             Will be thrown if there is no field with the given field
     *             name.
     */
    public String getGetterMethodName(final Class<? extends DataClass> aDataClass) throws NoSuchFieldException
    {
        String getterMethodName;
        final String annotatedGetterMethodName =
                aDataClass.getDeclaredField(fieldName).getAnnotation(ExcelField.class).getterMethod();
        if (annotatedGetterMethodName.isEmpty())
        {
            getterMethodName = fieldNameToGetterName(this);
        }
        else
        {
            getterMethodName = annotatedGetterMethodName;
        }
        return getterMethodName;
    }

    /**
     * Determines the name of the setter method.
     *
     * @param aDataClass
     *            The data class which field is used.
     * @return The setter method name.
     * @throws NoSuchFieldException
     *             Will be thrown if there is no field with the given field
     *             name.
     */
    public String getSetterMethodName(final Class<? extends DataClass> aDataClass) throws NoSuchFieldException
    {
        String setterMethodName;
        final String annotatedSetterMethodName =
                aDataClass.getDeclaredField(fieldName).getAnnotation(ExcelField.class).setterMethod();
        if (annotatedSetterMethodName.isEmpty())
        {
            setterMethodName = fieldNameToSetterName(this);
        }
        else
        {
            setterMethodName = annotatedSetterMethodName;
        }
        return setterMethodName;
    }

}
