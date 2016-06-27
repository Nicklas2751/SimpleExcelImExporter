package eu.wiegandt.nicklas.simpleexcelimexporter;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.wiegandt.nicklas.simpleexcelimexporter.annotations.ExcelField;
import eu.wiegandt.nicklas.simpleexcelimexporter.annotations.ExcelTable;
import eu.wiegandt.nicklas.simpleexcelimexporter.api.DataClass;
import eu.wiegandt.nicklas.simpleexcelimexporter.api.DataController;
import eu.wiegandt.nicklas.simpleexcelimexporter.exceptions.ExcelImExportErrorTypes;
import eu.wiegandt.nicklas.simpleexcelimexporter.exceptions.ExcelImExporterError;
import eu.wiegandt.nicklas.simpleexcelimexporter.exceptions.ExcelImExporterException;
import eu.wiegandt.nicklas.simpleexcelimexporter.utils.LocalFileLoaderUtil;

/**
 * A Class to manage a class which is annotated with {@link ExcelTable}.
 *
 * @author Nicklas Wiegandt (Nicklas2751)<br>
 *         <b>Mail:</b> nicklas@wiegandt.eu<br>
 *         <b>Jabber:</b> nicklas2751@elaon.de<br>
 *         <b>Skype:</b> Nicklas2751<br>
 *
 */
public class ExcelTableManager
{
    private static final String ERROR_TEXT_CANT_FIND_MAPPING_FILE = "Can't find the given mapping File.";
    private static final String ERROR_TEXT_EXCE_TABLE_IS_NULL = "The DataClass can't be null!";
    private static final String ERROR_TEXT_NOT_A_EXCEL_TABLE =
            "The given class isn't aviable for the excel im- export. It needs the annotation @ExcelTable.";
    private static final String ERROR_TEXT_WRONG_DATACONTROLLER =
            "The data controller is wrong. It cant use this data class.";
    private static final Logger LOG = LogManager.getLogger(ExcelTableManager.class);
    private static final String NOT_EVERY_NEEDED_GETTER_EXISTS_PATTERN =
            "Not every needed getter exists. Following getters are needed: [%s]";
    private static final String NOT_EVERY_NEEDED_SETTER_EXISTS_PATTERN =
            "Not every needed setter exists. Following setters are needed: [%s]";
    private final Class<? extends DataClass> excelTableClass;
    private String tableName;

    /**
     * A constructor which calls some methods to check if the given class is
     * valid for im-/exporting.<br>
     * <b>Warning: </b>The validation methods will call
     * {@link IllegalStateException}s if the class is not valid.<br>
     * <br>
     * <u>The class must accomplish the following conditions:</u><br>
     * <ul>
     * <li>The class can't be null.</li>
     * <li>The class must be annotated with {@link ExcelTable}</li>
     * <li>If a mapping file is listed it must exist.</li>
     * <li>The listed {@link DataController} must be able to handle the
     * class.</li>
     * <li>The class must contain a setter method for every importable field (a
     * field with {@link ExcelField#importable()} == true).</li>
     * <li>The class must contain a getter method for every exportable field (a
     * field with {@link ExcelField#exportable()} == true).</li>
     * </ul>
     * The setter methods must look like this:<br>
     * <code>public void set[FieldName]([Type] ...)</code> <br>
     * <br>
     * The getter methods must look like this:<br>
     * <code>public [Type] get[FieldName]</code>
     *
     * @param aExcelTable
     *            The with {@link ExcelTable} annotated class.
     */
    public ExcelTableManager(final Class<? extends DataClass> aExcelTable)
    {
        checkExcelTableNotNull(aExcelTable);
        excelTableClass = aExcelTable;
        checkIsAnnotatedWithExcelTable(aExcelTable);
        checkIfMappingFileExistsIfGiven(aExcelTable);
        checkIfDataControllerCanHandleDataClass(aExcelTable);
        checkIfHasSetterMethodsForImportFields(aExcelTable);
        checkIfHasGetterMethodsForExportFields(aExcelTable);
    }

    public Class<? extends DataController<? extends DataClass>> getDataController()
    {
        return excelTableClass.getAnnotation(ExcelTable.class).datacontroller();
    }

    /**
     * Searches for a {@link ExcelField} with the given field name.
     *
     * @param aFieldName
     *            The field name.
     * @return A {@link Optional} with the search result.
     */
    public Optional<Field> getExcelField(final String aFieldName)
    {
        return findExcelField(aFieldName, getExcelFields());
    }

    public Collection<Field> getExcelFields()
    {
        return Arrays.asList(excelTableClass.getDeclaredFields()).parallelStream()
                .filter(field -> field.isAnnotationPresent(ExcelField.class)).collect(Collectors.toList());
    }

    public Class<? extends DataClass> getExcelTableClass()
    {
        return excelTableClass;
    }

    public Collection<Field> getExportableExcelFields()
    {
        return getExcelFields().parallelStream().filter(field -> field.getAnnotation(ExcelField.class).exportable())
                .collect(Collectors.toList());
    }

    public Collection<Field> getImportableExcelFields()
    {
        return getExcelFields().stream().filter(field -> field.getAnnotation(ExcelField.class).importable())
                .collect(Collectors.toList());
    }

    public Collection<Field> getImportableRequiredExcelFields()
    {
        return getImportableExcelFields().parallelStream()
                .filter(field -> field.getAnnotation(ExcelField.class).requiredField()).collect(Collectors.toList());
    }

    public String getMappingFilePath()
    {
        return LocalFileLoaderUtil.getLocalFile(excelTableClass.getAnnotation(ExcelTable.class).mappingFile())
                .getAbsolutePath();
    }

    /**
     * Determines the table name. Uses the data class name as table name if the
     * optional annotation parameter {@link ExcelTable#tableName()} is not set.
     *
     * @return The determined table name.
     */
    public String getTableName()
    {
        if (tableName == null)
        {
            final String annotatedTableName = excelTableClass.getAnnotation(ExcelTable.class).tableName();
            if (annotatedTableName.isEmpty())
            {
                tableName = excelTableClass.getSimpleName();
            }
            else
            {
                tableName = annotatedTableName;
            }
        }
        return tableName;
    }

    /**
     * Checks if a {@link ExcelField} with the given name exists for the
     * dataclass of this manager.
     *
     * @param aFieldName
     *            The field name.
     * @return true if it exists.
     */
    public boolean hasImportableField(final String aFieldName)
    {
        return getImportableExcelField(aFieldName).isPresent();
    }

    /**
     * Checks if a mapping file path is listed.
     *
     * @return true if a mapping file path is listed.
     */
    public boolean hasMappingFilePath()
    {
        return !(getMappingFilePath().isEmpty() || new File(getMappingFilePath()).isDirectory());
    }

    private void checkExcelTableNotNull(final Class<? extends DataClass> aExcelTable)
    {
        if (aExcelTable == null)
        {
            throw new IllegalArgumentException(ERROR_TEXT_EXCE_TABLE_IS_NULL);
        }
    }

    private void checkIfDataControllerCanHandleDataClass(final Class<? extends DataClass> aExcelTable)
    {
        try
        {
            if (!aExcelTable.getAnnotation(ExcelTable.class).datacontroller().getMethod("getDataClass")
                    .invoke(aExcelTable.getAnnotation(ExcelTable.class).datacontroller().newInstance())
                    .equals(aExcelTable))
            {
                throw new IllegalArgumentException(ERROR_TEXT_WRONG_DATACONTROLLER);
            }
        }
        catch (final IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException | InstantiationException exception)
        {
            LOG.debug(ERROR_TEXT_WRONG_DATACONTROLLER, exception);
            throw new IllegalArgumentException(ERROR_TEXT_WRONG_DATACONTROLLER);
        }
    }

    private void checkIfHasGetterMethodsForExportFields(final Class<? extends DataClass> aExcelTable)
    {

        final Collection<String> neededNotFoundGetterName = new ArrayList<>();
        Boolean hasGetterMethods = true;
        final List<String> existingMethodNames = Arrays.asList(aExcelTable.getMethods()).parallelStream()
                .map(Method::getName).collect(Collectors.toList());

        for (final Field field : getExportableExcelFields())
        {
            String neededGetterName;
            try
            {
                neededGetterName =
                        new ExcelImExporterField(null, null, field.getName()).getGetterMethodName(excelTableClass);
            }
            catch (NoSuchFieldException | SecurityException exception)
            {
                LOG.fatal(ExcelImExportErrorTypes.EXPORT_FAILED_SYSTEM_ERROR.getMessageTemplate(), exception);
                final ExcelImExporterError errorMessage =
                        new ExcelImExporterError(ExcelImExportErrorTypes.EXPORT_FAILED_SYSTEM_ERROR);
                throw new IllegalStateException(new ExcelImExporterException(errorMessage));

            }
            final boolean isExistNeededGetterName = existingMethodNames.contains(neededGetterName);
            if (!isExistNeededGetterName)
            {
                neededNotFoundGetterName.add(neededGetterName);
            }

            hasGetterMethods = isExistNeededGetterName && hasGetterMethods;

        }
        if (!hasGetterMethods)
        {
            throw new IllegalArgumentException(
                    String.format(NOT_EVERY_NEEDED_GETTER_EXISTS_PATTERN, String.join(", ", neededNotFoundGetterName)));
        }
    }

    private void checkIfHasSetterMethodsForImportFields(final Class<? extends DataClass> aExcelTable)
    {
        final Collection<String> neededNotFoundSetterName = new ArrayList<>();
        Boolean hasSetterMethods = true;
        final List<String> existingMethodNames = Arrays.asList(aExcelTable.getMethods()).parallelStream()
                .map(Method::getName).collect(Collectors.toList());

        for (final Field field : getImportableExcelFields())
        {
            String neededSetterName;
            try
            {
                neededSetterName =
                        new ExcelImExporterField(null, null, field.getName()).getSetterMethodName(excelTableClass);
            }
            catch (NoSuchFieldException | SecurityException exception)
            {
                LOG.fatal(ExcelImExportErrorTypes.EXPORT_FAILED_SYSTEM_ERROR.getMessageTemplate(), exception);
                final ExcelImExporterError errorMessage =
                        new ExcelImExporterError(ExcelImExportErrorTypes.EXPORT_FAILED_SYSTEM_ERROR);
                throw new IllegalStateException(new ExcelImExporterException(errorMessage));
            }
            final boolean isExistNeededSetterName = existingMethodNames.contains(neededSetterName);
            if (!isExistNeededSetterName)
            {
                neededNotFoundSetterName.add(neededSetterName);
            }

            hasSetterMethods = isExistNeededSetterName && hasSetterMethods;

        }
        if (!hasSetterMethods)
        {
            throw new IllegalArgumentException(
                    String.format(NOT_EVERY_NEEDED_SETTER_EXISTS_PATTERN, String.join(", ", neededNotFoundSetterName)));
        }
    }

    private void checkIfMappingFileExistsIfGiven(final Class<? extends DataClass> aExcelTable)
    {
        if (!aExcelTable.getAnnotation(ExcelTable.class).mappingFile().isEmpty() && !LocalFileLoaderUtil
                .getLocalFile(aExcelTable.getAnnotation(ExcelTable.class).mappingFile()).exists())
        {
            LOG.debug("Mapping file path: "
                    + LocalFileLoaderUtil.getLocalFile(aExcelTable.getAnnotation(ExcelTable.class).mappingFile()));

            throw new IllegalArgumentException(ERROR_TEXT_CANT_FIND_MAPPING_FILE);
        }
    }

    private void checkIsAnnotatedWithExcelTable(final Class<? extends DataClass> aExcelTable)
    {
        if (!aExcelTable.isAnnotationPresent(ExcelTable.class))
        {
            throw new IllegalArgumentException(ERROR_TEXT_NOT_A_EXCEL_TABLE);
        }
    }

    private Optional<Field> findExcelField(final String aFieldName, final Collection<Field> aFields)
    {
        return aFields.parallelStream().filter(field -> field.getName().equalsIgnoreCase(aFieldName)).findFirst();
    }

    private Optional<Field> getImportableExcelField(final String aFieldName)
    {
        return findExcelField(aFieldName, getImportableExcelFields());
    }

}
