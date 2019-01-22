package eu.wiegandt.nicklas.simpleexcelimexporter;

import java.io.IOException;
import java.lang.Thread;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import eu.wiegandt.nicklas.simpleexcelimexporter.api.DataClass;
import eu.wiegandt.nicklas.simpleexcelimexporter.api.DataController;
import eu.wiegandt.nicklas.simpleexcelimexporter.exceptions.ExcelImExportErrorTypes;
import eu.wiegandt.nicklas.simpleexcelimexporter.exceptions.ExcelImExporterError;
import eu.wiegandt.nicklas.simpleexcelimexporter.exceptions.ExcelImExporterException;

/**
 * A class to export data of annotated data classes to excel.
 *
 * @author Nicklas Wiegandt (Nicklas2751)<br>
 *         <b>Mail:</b> nicklas@wiegandt.eu<br>
 *         <b>Jabber:</b> nicklas2751@elaon.de<br>
 *         <b>Skype:</b> Nicklas2751<br>
 *
 */
public class ExcelExporter extends AbstractExcelImExporter
{
    private static final Logger LOG = LogManager.getLogger(ExcelExporter.class);
    private XSSFWorkbook workbook;

    /**
     * Exports the tables with the given tables names into a excel file to the
     * given path.
     *
     * @param aTableNames
     *            The table names form the tables which should be exported.
     * @param aExcelPath
     *            The path where the excel file should be saved. Must include
     *            the file name.
     * @throws ExcelImExporterException
     *             if there was a error while exporting the tables.
     * @deprecated Use {@link #exportToExcel(Collection, Path)} instead.
     */
    @Deprecated
    public void exportToExcel(final Collection<String> aTableNames, final String aExcelPath)
            throws ExcelImExporterException
    {
        exportToExcel(aTableNames, Paths.get(aExcelPath));
    }

    /**
     * Exports the tables with the given tables names into a excel file to the
     * given path.
     *
     * @param aTableNames
     *            The table names form the tables which should be exported.
     * @param aExcelPath
     *            The path where the excel file should be saved. Must include
     *            the file name.
     * @throws ExcelImExporterException
     *             if there was a error while exporting the tables.
     *
     */
    public void exportToExcel(final Collection<String> aTableNames, final Path aExcelPath)
            throws ExcelImExporterException

    {
        updateProgress();
        workbook = new XSSFWorkbook();

        setSubRuns(aTableNames.size());

        final ExecutorService executor = Executors.newCachedThreadPool();
        final Collection<Callable<Void>> exportTasks = new ArrayList<>();

        for (final String tableName : aTableNames)
        {
            final Callable<Void> exportTask = () -> {
                exportTable(tableName);
                finishSubRun();
                return null;
            };
            exportTasks.add(exportTask);
        }
        try
        {
            final List<Future<Void>> executionResults = executor.invokeAll(exportTasks);
            for (final Future<Void> result : executionResults)
            {
                result.get();
            }
            executor.shutdown();
            executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
            writeToFile(aExcelPath);
        }
        catch (final InterruptedException | ExecutionException exception)
        {
            LOG.fatal(ExcelImExportErrorTypes.EXPORT_FAILED_SYSTEM_ERROR.getMessageTemplate(), exception);
            final ExcelImExporterError errorMessage =
                    new ExcelImExporterError(ExcelImExportErrorTypes.EXPORT_FAILED_SYSTEM_ERROR);
            throw new ExcelImExporterException(errorMessage);
        }
    }

    private void exportTable(final String aTableName)
            throws InstantiationException, IllegalAccessException, ExcelImExporterException
    {
        final Optional<ExcelTableManager> tableManagerOptional = searchTableManager(aTableName);
        if (tableManagerOptional.isPresent())
        {
            final BidiMap<String, String> mapping = getMapping(tableManagerOptional.get());
            linkColumnsWithData(aTableName, mapping, pullData(tableManagerOptional.get()));
        }
        else
        {
            final ExcelImExporterError tableIsNotExportableError = new ExcelImExporterError(
                    ExcelImExportErrorTypes.TABLE_IS_NOT_EXPORTABLE,
                    String.format(ExcelImExportErrorTypes.TABLE_IS_NOT_EXPORTABLE.getMessageTemplate(), aTableName));
            throw new ExcelImExporterException(tableIsNotExportableError);
        }
    }

    private void fillRowWithData(final Row aRow, final Collection<String> aRowData)
    {
        final Iterator<String> rowIterator = aRowData.iterator();
        for (int cellNum = 0; cellNum < aRowData.size(); cellNum++)
        {
            aRow.createCell(cellNum).setCellValue(rowIterator.next());
        }

    }

    private void fillTableWithData(final List<Map<String, String>> aMappedDataForExport, final Sheet aTableSheet)
    {
        int rowNum = 0;
        for (final Map<String, String> dataMap : aMappedDataForExport)
        {
            Row newRow = aTableSheet.createRow(rowNum);
            if (rowNum == 0)
            {
                fillRowWithData(newRow, dataMap.keySet());
                finishDataSetProcess();
                rowNum++;
                newRow = aTableSheet.createRow(rowNum);
            }
            fillRowWithData(newRow, dataMap.values());
            rowNum++;
            finishDataSetProcess();
        }

    }

    private String getFieldData(final ExcelImExporterField aExcelImExporterField, final DataClass aDataClass)

    {
        try
        {
            return aDataClass.getClass().getMethod(aExcelImExporterField.getGetterMethodName(aDataClass.getClass()))
                    .invoke(aDataClass).toString();
        }
        catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException
                | SecurityException | NoSuchFieldException exception)
        {
            LOG.fatal(ExcelImExportErrorTypes.EXPORT_FAILED_SYSTEM_ERROR.getMessageTemplate(), exception);
            final ExcelImExporterError errorMessage =
                    new ExcelImExporterError(ExcelImExportErrorTypes.EXPORT_FAILED_SYSTEM_ERROR);
			Thread.currentThread().interrupt();
            throw new IllegalStateException(new ExcelImExporterException(errorMessage));
        }
    }

    private BidiMap<String, String> getMapping(final ExcelTableManager aExcelTableManager)
            throws ExcelImExporterException
    {
        final BidiMap<String, String> mapping = new DualHashBidiMap<>();
        if (aExcelTableManager.hasMappingFilePath())
        {
            final Collection<String> exportableFieldNames = aExcelTableManager.getExportableExcelFields()
                    .parallelStream().map(Field::getName).collect(Collectors.toList());

            final BidiMap<String, String> loadedMapping = loadMapping(aExcelTableManager.getMappingFilePath());
            mapping.putAll(loadedMapping.entrySet().parallelStream()
                    .filter(entry -> exportableFieldNames.contains(entry.getValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        }
        else
        {
            mapping.putAll(aExcelTableManager.getExportableExcelFields().parallelStream()
                    .collect(Collectors.toMap(Field::getName, Field::getName)));
        }

        return mapping;
    }

    private void linkColumnsWithData(final String aTableName, final BidiMap<String, String> aMapping,
            final Collection<? extends DataClass> aDataClasses)
    {
        final List<Map<String, String>> mappedDataForExport = mapDataClasses(aDataClasses, aMapping);
        addDataSetsToProcess(mappedDataForExport.size() + 1);
        writeMappedData(aTableName, mappedDataForExport);
    }

    private Map<String, String> mapDataClass(final DataClass aDataClass, final BidiMap<String, String> aMapping)
    {
        return aMapping.entrySet().parallelStream().collect(Collectors.toMap(Map.Entry::getKey,
                entry -> getFieldData(new ExcelImExporterField(null, entry.getKey(), entry.getValue()), aDataClass)));

    }

    private List<Map<String, String>> mapDataClasses(final Collection<? extends DataClass> aDataClasses,
            final BidiMap<String, String> aMapping)
    {
        return aDataClasses.parallelStream().map(dataClass -> mapDataClass(dataClass, aMapping))
                .collect(Collectors.toList());
    }

    private Collection<? extends DataClass> pullData(final ExcelTableManager aExcelTableManager)
            throws InstantiationException, IllegalAccessException
    {
        final DataController<? extends DataClass> dataController = aExcelTableManager.getDataController().newInstance();
        return dataController.pullData();
    }

    private synchronized void writeMappedData(final String aTableName,
            final List<Map<String, String>> aMappedDataForExport)
    {
        try
        {
            final XSSFSheet tableSheet = workbook.createSheet(aTableName);
            fillTableWithData(aMappedDataForExport, tableSheet);
        }
        catch (final EncryptedDocumentException encryptedDocumentException)
        {
            LOG.fatal(ExcelImExportErrorTypes.EXPORT_FAILED_SYSTEM_ERROR.getMessageTemplate(),
                    encryptedDocumentException);
            final ExcelImExporterError errorMessage =
                    new ExcelImExporterError(ExcelImExportErrorTypes.EXPORT_FAILED_SYSTEM_ERROR);
            throw new IllegalStateException(new ExcelImExporterException(errorMessage));
        }

    }

    private synchronized void writeToFile(final Path aExcelPath)
    {
        try
        {
            workbook.write(Files.newOutputStream(aExcelPath));
            workbook.close();
        }
        catch (final IOException exception)
        {
            LOG.fatal(ExcelImExportErrorTypes.EXPORT_FAILED_SYSTEM_ERROR.getMessageTemplate(), exception);
            final ExcelImExporterError errorMessage =
                    new ExcelImExporterError(ExcelImExportErrorTypes.EXPORT_FAILED_SYSTEM_ERROR);
            throw new IllegalStateException(new ExcelImExporterException(errorMessage));
        }
    }

}
