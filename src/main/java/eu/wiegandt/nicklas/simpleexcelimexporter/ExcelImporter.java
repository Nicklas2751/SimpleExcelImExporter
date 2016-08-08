package eu.wiegandt.nicklas.simpleexcelimexporter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import eu.wiegandt.nicklas.simpleexcelimexporter.api.DataClass;
import eu.wiegandt.nicklas.simpleexcelimexporter.exceptions.ExcelImExportErrorTypes;
import eu.wiegandt.nicklas.simpleexcelimexporter.exceptions.ExcelImExportWarningTypes;
import eu.wiegandt.nicklas.simpleexcelimexporter.exceptions.ExcelImExporterError;
import eu.wiegandt.nicklas.simpleexcelimexporter.exceptions.ExcelImExporterException;
import eu.wiegandt.nicklas.simpleexcelimexporter.exceptions.ExcelImExporterWarning;

/**
 * The Excel Importer.
 *
 * @author Nicklas Wiegandt (Nicklas2751)<br>
 *         <b>Mail:</b> nicklas@wiegandt.eu<br>
 *         <b>Jabber:</b> nicklas2751@elaon.de<br>
 *         <b>Skype:</b> Nicklas2751<br>
 *
 */
public class ExcelImporter extends AbstractExcelImExporter
{
    private static final Logger LOG = LogManager.getLogger(ExcelImporter.class);
    private final Path excelFilePath;
    private boolean multiTableMode;

    private String tableName;

    /**
     *
     * @param aExcelFilePath
     *            The path to the excel file which should be imported.
     * @deprecated Use {@link #ExcelImporter(Path)} instead.
     */
    @Deprecated
    public ExcelImporter(final String aExcelFilePath)
    {
        this(Paths.get(aExcelFilePath));
    }

    /**
     *
     * @param aExcelFilePath
     *            The path to the excel file which should be imported.
     */
    public ExcelImporter(final Path aExcelFilePath)
    {
        super();
        multiTableMode = false;
        tableName = "";
        checkPath(aExcelFilePath);
        excelFilePath = aExcelFilePath;
    }

    /**
     * The same as
     * {@link AbstractExcelImExporter#addTableManager(ExcelTableManager)}. But
     * here will be checked if the table manager has importable fields.
     *
     * @see AbstractExcelImExporter#addTableManager(ExcelTableManager)
     * @param aTableManager
     *            A Table manager.
     * @return true if adding was successful.
     */
    public static boolean addTableManager(final ExcelTableManager aTableManager)
    {
        if (aTableManager.getImportableExcelFields().isEmpty())
        {
            throw new IllegalArgumentException("The table has no importable fields.");
        }
        return tableManagers.add(aTableManager);
    }

    public String getTableName()
    {
        return tableName;
    }

    /**
     * Starts the import.
     *
     * @throws ExcelImExporterException
     *             Will be thrown if a exception occurred during import.
     */
    public void importExcel() throws ExcelImExporterException
    {
        try
        {
            updateProgress();
            if (isMultiTableMode())
            {
                importExcelTables();

            }
            else
            {
                importExcelTable(getTableName());
            }
        }
        catch (final EncryptedDocumentException exception)
        {
            LOG.fatal(ExcelImExportErrorTypes.ENCRYPTED_DOCUMENT.getMessageTemplate(), exception);
            final ExcelImExporterError errorMessage =
                    new ExcelImExporterError(ExcelImExportErrorTypes.ENCRYPTED_DOCUMENT);
            throw new ExcelImExporterException(errorMessage);
        }
    }

    public boolean isMultiTableMode()
    {
        return multiTableMode;
    }

    public void setMultiTableMode(final boolean aMultiTableMode)
    {
        multiTableMode = aMultiTableMode;
    }

    public void setTableName(final String aTableName)
    {
        tableName = aTableName;
    }

    private void checkHasRequiredFields(final ExcelTableManager aTableManager,
            final Collection<ExcelImExporterField> aFields) throws ExcelImExporterException
    {
        final Collection<String> requiredFieldNames = aTableManager.getImportableRequiredExcelFields().parallelStream()
                .map(Field::getName).collect(Collectors.toList());
        final boolean hasRequiredFields = aFields.parallelStream().map(ExcelImExporterField::getFieldName)
                .collect(Collectors.toList()).containsAll(requiredFieldNames);
        if (!hasRequiredFields)
        {
            final ExcelImExporterError notAllRequiredFieldsError =
                    new ExcelImExporterError(ExcelImExportErrorTypes.NOT_ALL_REQUIRED_FIELDS,
                            String.format(ExcelImExportErrorTypes.NOT_ALL_REQUIRED_FIELDS.getMessageTemplate(),
                                    String.join(", ", requiredFieldNames)));
            throw new ExcelImExporterException(notAllRequiredFieldsError);
        }
    }

    private void checkPath(final Path aExcelFilePath)
    {
        if (!Files.exists(aExcelFilePath))
        {
            throw new InvalidParameterException("The excel file don't exists.");
        }
    }

    private void importExcelTable(final Sheet aSheet, final String aTableName) throws ExcelImExporterException
    {
        final Optional<ExcelTableManager> tableManagerSearchResult = searchTableManager(aTableName);
        if (tableManagerSearchResult.isPresent())
        {
            final ExcelTableManager tableManager = tableManagerSearchResult.get();
            final Collection<Cell> columns = readColumns(aSheet);

            final Collection<ExcelImExporterField> fields = mappColumnsToExcelFields(tableManager, columns);

            final Collection<ExcelImExporterField> importableFields = removeNotImportableFields(tableManager, fields);

            if (!tableManager.getImportableRequiredExcelFields().isEmpty())
            {
                checkHasRequiredFields(tableManager, importableFields);
            }

            final Collection<? extends DataClass> dataClasses =
                    mappToDataclasses(tableManager, aSheet, importableFields);
            for (final DataClass dataClass : dataClasses)
            {
                try
                {
                    finishDataSetProcess();
                    tableManager.getDataController().getMethod("pushData", dataClass.getClass())
                            .invoke(tableManager.getDataController().newInstance(), dataClass);
                }
                catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                        | NoSuchMethodException | SecurityException | InstantiationException exception)
                {
                    LOG.fatal(ExcelImExportErrorTypes.IMPORT_FAILED_SYSTEM_ERROR.getMessageTemplate(), exception);
                    final ExcelImExporterError errorMessage =
                            new ExcelImExporterError(ExcelImExportErrorTypes.IMPORT_FAILED_SYSTEM_ERROR);
                    throw new ExcelImExporterException(errorMessage);
                }
            }
        }
        else

        {
            final ExcelImExporterWarning warning = new ExcelImExporterWarning(
                    ExcelImExportWarningTypes.TABLE_INVALID_SKIP,
                    String.format(ExcelImExportWarningTypes.TABLE_INVALID_SKIP.getMessageTemplate(), aTableName));
            postWarning(warning);
        }

    }

    private void importExcelTable(final String aTableName) throws ExcelImExporterException
    {
        try (Workbook workbook = WorkbookFactory.create(Files.newInputStream(excelFilePath)))
        {
            Sheet tableSheet;
            if (isMultiTableMode())
            {
                tableSheet = workbook.getSheet(aTableName);
                if (tableSheet == null)
                {
                    final ExcelImExporterError error =
                            new ExcelImExporterError(ExcelImExportErrorTypes.TABLE_NAME_INVALID);
                    throw new ExcelImExporterException(error);
                }
            }
            else
            {
                tableSheet = workbook.getSheetAt(0);
            }

            importExcelTable(tableSheet, aTableName);
        }
        catch (EncryptedDocumentException | InvalidFormatException | IOException exception)
        {
            LOG.fatal(ExcelImExportErrorTypes.IMPORT_FAILED_SYSTEM_ERROR.getMessageTemplate(), exception);
            final ExcelImExporterError errorMessage =
                    new ExcelImExporterError(ExcelImExportErrorTypes.IMPORT_FAILED_SYSTEM_ERROR);
            throw new ExcelImExporterException(errorMessage);
        }
    }

    private void importExcelTables() throws ExcelImExporterException
    {
        try (Workbook workbook = WorkbookFactory.create(Files.newInputStream(excelFilePath)))
        {

            setSubRuns(workbook.getNumberOfSheets());

            final ExecutorService executor = Executors.newCachedThreadPool();
            final Collection<Callable<Void>> importTasks = new ArrayList<>();

            for (final Sheet sheet : workbook)
            {
                final Callable<Void> importTask = () -> {
                    importExcelTable(sheet, sheet.getSheetName());
                    finishSubRun();
                    return null;
                };
                importTasks.add(importTask);
            }
            final List<Future<Void>> executionResults = executor.invokeAll(importTasks);
            for (final Future<Void> result : executionResults)
            {
                result.get();
            }
        }
        catch (EncryptedDocumentException | InvalidFormatException | IOException | InterruptedException
                | ExecutionException exception)
        {
            LOG.fatal(ExcelImExportErrorTypes.IMPORT_FAILED_SYSTEM_ERROR.getMessageTemplate(), exception);
            final ExcelImExporterError errorMessage =
                    new ExcelImExporterError(ExcelImExportErrorTypes.IMPORT_FAILED_SYSTEM_ERROR);
            throw new ExcelImExporterException(errorMessage);
        }
    }

    private Collection<ExcelImExporterField> mappColumnsToExcelFields(final ExcelTableManager aTableManager,
            final Collection<Cell> aColumns) throws ExcelImExporterException
    {
        final Collection<ExcelImExporterField> fields = new ArrayList<>();
        for (final Cell column : aColumns)
        {
            final String columnName = column.getStringCellValue();
            if (aTableManager.hasMappingFilePath())
            {
                final Map<String, String> mapping = loadMapping(aTableManager.getMappingFilePath());
                if (mapping.containsKey(columnName))
                {
                    fields.add(new ExcelImExporterField(column.getColumnIndex(), columnName, mapping.get(columnName)));
                }
                else
                {
                    final ExcelImExporterWarning columnNotInMappingWarning =
                            new ExcelImExporterWarning(ExcelImExportWarningTypes.COLUMN_NOT_IN_MAPPING, String.format(
                                    ExcelImExportWarningTypes.COLUMN_NOT_IN_MAPPING.getMessageTemplate(), columnName));
                    postWarning(columnNotInMappingWarning);
                }

            }
            else
            {
                fields.add(new ExcelImExporterField(column.getColumnIndex(), columnName, columnName));
            }
        }
        return fields;
    }

    private DataClass mappToDataclass(final Row aRow, final ExcelTableManager aTableManager,
            final Collection<ExcelImExporterField> aFields)
    {

        final DataClass data;
        try
        {
            data = aTableManager.getExcelTableClass().cast(aTableManager.getExcelTableClass().newInstance());

            for (final ExcelImExporterField excelImExporterField : aFields)
            {
                aTableManager.getExcelTableClass()
                        .getMethod(excelImExporterField.getSetterMethodName(aTableManager.getExcelTableClass()),
                                String.class)
                        .invoke(data, new DataFormatter()
                                .formatCellValue(aRow.getCell(excelImExporterField.getColumnIndex())));

            }
            return data;
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException | InstantiationException | NoSuchFieldException exception)
        {
            LOG.fatal(ExcelImExportErrorTypes.IMPORT_FAILED_SYSTEM_ERROR.getMessageTemplate(), exception);
            final ExcelImExporterError errorMessage =
                    new ExcelImExporterError(ExcelImExportErrorTypes.IMPORT_FAILED_SYSTEM_ERROR);
            throw new IllegalStateException(new ExcelImExporterException(errorMessage));
        }
    }

    private Collection<? extends DataClass> mappToDataclasses(final ExcelTableManager aTableManager, final Sheet aSheet,
            final Collection<ExcelImExporterField> aFields)
    {
        List<Row> rows = StreamSupport.stream(aSheet.spliterator(), true).collect(Collectors.toList());
        rows = rows.stream().filter(row -> row.getFirstCellNum() != -1).collect(Collectors.toList());
        rows.remove(0);// The column name row
        addDataSetsToProcess(rows.size());

        return rows.parallelStream().map(row -> mappToDataclass(row, aTableManager, aFields))
                .collect(Collectors.toList());
    }

    private Collection<Cell> readColumns(final Sheet aSheet)
    {
        return StreamSupport.stream(aSheet.getRow(0).spliterator(), true).collect(Collectors.toList());
    }

    private Collection<ExcelImExporterField> removeNotImportableFields(final ExcelTableManager aTableManager,
            final Collection<ExcelImExporterField> aFields)
    {
        final Collection<ExcelImExporterField> importableFields = new ArrayList<>();
        for (final ExcelImExporterField field : aFields)
        {
            if (aTableManager.hasImportableField(field.getFieldName()))
            {
                importableFields.add(field);
            }
            else
            {
                final ExcelImExporterWarning fieldNotImportableWarning =
                        new ExcelImExporterWarning(ExcelImExportWarningTypes.FIELD_NOT_IMPORTABLE,
                                String.format(ExcelImExportWarningTypes.FIELD_NOT_IMPORTABLE.getMessageTemplate(),
                                        field.getFieldName()));
                postWarning(fieldNotImportableWarning);
            }
        }
        return importableFields;

    }

}
