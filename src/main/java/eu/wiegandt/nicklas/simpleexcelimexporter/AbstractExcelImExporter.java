package eu.wiegandt.nicklas.simpleexcelimexporter;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import eu.wiegandt.nicklas.simpleexcelimexporter.exceptions.ExcelImExportErrorTypes;
import eu.wiegandt.nicklas.simpleexcelimexporter.exceptions.ExcelImExporterError;
import eu.wiegandt.nicklas.simpleexcelimexporter.exceptions.ExcelImExporterException;
import eu.wiegandt.nicklas.simpleexcelimexporter.exceptions.ExcelImExporterWarning;

/**
 * The abstract base for the im- and exporter.
 *
 * @author Nicklas Wiegandt (Nicklas2751)<br>
 *         <b>Mail:</b> nicklas@wiegandt.eu<br>
 *         <b>Jabber:</b> nicklas2751@elaon.de<br>
 *         <b>Skype:</b> Nicklas2751<br>
 *
 */
public abstract class AbstractExcelImExporter
{
    private static final String ERROR_TEXT_NO_TABLE_MANAGER = "There are no table managers!";
    private static final String ERROR_TEXT_PATTERN_TABLE_NOT_EXISTS =
            "The table with the name \"%s\" is doesn't exist for im- or export.";
    private static final String ERROR_TEXT_TABLE_MANAGER_IS_NULL =
            "The given table manager is null. The table manager can't be null!";
    private static final Logger LOG = LogManager.getLogger(AbstractExcelImExporter.class);
    private static final Collection<ExcelImExportObserver> observers = new ArrayList<>();

    protected static final Collection<ExcelTableManager> tableManagers = new ArrayList<>();

    private int dataSetsToProcess;
    private int finishedSubRuns;
    private float percentage;
    private int processedDataSets;
    private int subRuns;

    /**
     * The default constructor. It checks if one or more table managers are set
     * and initializes the statistics.
     */
    public AbstractExcelImExporter()
    {
        super();
        if (tableManagers.isEmpty())
        {
            throw new IllegalStateException(ERROR_TEXT_NO_TABLE_MANAGER);
        }
        dataSetsToProcess = 0;
        subRuns = 0;
        finishedSubRuns = 0;
        processedDataSets = 0;
    }

    /**
     * Adds a table manager to the list of supported table managers for the im-
     * / exporter.
     *
     * @param aTableManager
     *            The table manager which should be added.
     * @return true if adding was successful.
     * @see Collection#add(Object)
     */
    public static boolean addTableManager(final ExcelTableManager aTableManager)
    {
        if (aTableManager == null)
        {
            throw new IllegalArgumentException(ERROR_TEXT_TABLE_MANAGER_IS_NULL);
        }
        return tableManagers.add(aTableManager);
    }

    /**
     * * A helper method to generate a mapping file for a table.<br>
     * The mapping format is: "column name": "field name".
     *
     * @param aFilePath
     *            The path where the mapping file should be saved.
     * @param aTableName
     *            The table name for which the file should be generated.
     * @throws IOException
     *             if the file can't be written.
     * @throws IllegalArgumentException
     *             if no table with the given table name is available for im- or
     *             export.
     * @deprecated Use {@link #generateCleanMappingFile(Path, String)} instead.
     */
    @Deprecated
    public static void generateCleanMappingFile(final String aFilePath, final String aTableName) throws IOException
    {
        generateCleanMappingFile(Paths.get(aFilePath), aTableName);
    }

    /**
     * A helper method to generate a mapping file for a table.<br>
     * The mapping format is: "column name": "field name".
     *
     * @param aFilePath
     *            The path where the mapping file should be saved.
     * @param aTableName
     *            The table name for which the file should be generated.
     * @throws IOException
     *             if the file can't be written.
     * @throws IllegalArgumentException
     *             if no table with the given table name is available for im- or
     *             export.
     */
    public static void generateCleanMappingFile(final Path aFilePath, final String aTableName) throws IOException
    {
        final Optional<ExcelTableManager> tableManagerOptional = searchTableManager(aTableName);
        if (tableManagerOptional.isPresent())
        {
            final Map<String, String> mapping = tableManagerOptional.get().getExcelFields().parallelStream()
                    .collect(Collectors.toMap(Field::getName, Field::getName));
            final Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try
            {
                try (Writer writer = Files.newBufferedWriter(aFilePath))
                {
                    gson.toJson(mapping, writer);
                }
            }
            catch (final IOException ioException)
            {
                throw ioException;
            }
        }
        else
        {
            throw new IllegalArgumentException(String.format(ERROR_TEXT_PATTERN_TABLE_NOT_EXISTS, aTableName));
        }
    }

    /**
     * Removes a table manager.
     *
     * @param aTableManager
     *            The table manager which should be removed.
     * @return true if the table manager was successful removed.
     * @see Collection#remove(Object)
     */
    public static boolean removeTableManager(final ExcelTableManager aTableManager)
    {
        return tableManagers.remove(aTableManager);
    }

    /**
     * Searches for a table manager for a table with the given table name.
     *
     * @param aTableName
     *            The table name for which a table manager should be searched.
     * @return Returns a {@link Optional} with the search result.
     */
    protected static Optional<ExcelTableManager> searchTableManager(final String aTableName)
    {
        if (tableManagers.isEmpty())
        {
            throw new IllegalStateException(ERROR_TEXT_NO_TABLE_MANAGER);
        }
        return tableManagers.parallelStream()
                .filter(excelTableManager -> excelTableManager.getTableName().equalsIgnoreCase(aTableName)).findAny();
    }

    /**
     * Adds an observer for the im- exporter.
     *
     * @param aExcelImExportObserver
     *            The observer which should be added.
     * @return true if the adding was successful.
     * @see Collection#add(Object)
     */
    public boolean addObserver(final ExcelImExportObserver aExcelImExportObserver)
    {
        return observers.add(aExcelImExportObserver);
    }

    /**
     * Removes an observer.
     *
     * @param aExcelImExportObserver
     *            The observer which should be removed.
     * @return true if the removing was successful.
     * @see Collection#remove(Object)
     */
    public boolean removeObserver(final ExcelImExportObserver aExcelImExportObserver)
    {
        return observers.remove(aExcelImExportObserver);
    }

    /**
     * Calculates how much sub runs are finished.
     *
     * @return The difference between {@link #subRuns} and
     *         {@link #finishedSubRuns}. If the result is 0 or less it returns
     *         1.
     */
    private synchronized int getUnfinishedSubRuns()
    {
        int unfinishedSubRuns = subRuns - finishedSubRuns;
        if (unfinishedSubRuns <= 0)
        {
            unfinishedSubRuns = 1;
        }
        return unfinishedSubRuns;
    }

    /**
     * Updates the percentage for each observer.
     *
     * @param aPercentage
     *            The new percentage.
     */
    private void updateProgress(final float aPercentage)
    {
        observers.parallelStream().forEach(observer -> observer.updateProgress(aPercentage));
    }

    protected synchronized void addDataSetsToProcess(final int aDataSetsToProcess)
    {
        dataSetsToProcess += aDataSetsToProcess;
    }

    protected synchronized void addProcessedDataSets(final int aProcessedDataSets)
    {
        processedDataSets += aProcessedDataSets;
        calculateProgress();
    }

    /**
     * Calculates the progress percentage. Uses the number of "sub runs" where
     * each sub run is a table so the result is for the complete im-/export.
     */
    protected synchronized void calculateProgress()
    {
        percentage = (float) processedDataSets / dataSetsToProcess * 100f / getUnfinishedSubRuns();
        if (Float.isNaN(percentage))
        {
            percentage = 0f;
        }
        updateProgress(percentage);
    }

    /**
     * Increases the number of processed data sets by one.
     */
    protected synchronized void finishDataSetProcess()
    {
        addProcessedDataSets(1);
    }

    /**
     * Increases the number of finished sub runs by one.
     */
    protected synchronized void finishSubRun()
    {
        finishedSubRuns++;
    }

    /**
     * Loads the mapping from a mapping file.
     *
     * @param aMappingFilePath
     *            The path to the mapping file.
     * @return The mapping.
     * @throws ExcelImExporterException
     *             will be thrown if the mapping file is invalid.
     * @deprecated Use {@link #loadMapping(Path)} instead.
     */
    @Deprecated
    protected BidiMap<String, String> loadMapping(final String aMappingFilePath) throws ExcelImExporterException
    {
        return loadMapping(Paths.get(aMappingFilePath));
    }

    /**
     * Loads the mapping from a mapping file.
     *
     * @param aMappingFilePath
     *            The path to the mapping file.
     * @return The mapping.
     * @throws ExcelImExporterException
     *             will be thrown if the mapping file is invalid.
     */
    protected BidiMap<String, String> loadMapping(final Path aMappingFilePath) throws ExcelImExporterException
    {
        final Gson gson = new Gson();
        final Type mapType = new TypeToken<DualHashBidiMap<String, String>>()
        {
        }.getType();
        try
        {
            return gson.fromJson(Files.newBufferedReader(aMappingFilePath), mapType);
        }
        catch (final JsonParseException jsonParseException)
        {
            LOG.debug(ExcelImExportErrorTypes.MAPPING_NO_FALID_JSON_FILE.getMessageTemplate(), jsonParseException);
            final ExcelImExporterError jsonParseError =
                    new ExcelImExporterError(ExcelImExportErrorTypes.MAPPING_NO_FALID_JSON_FILE);
            throw new ExcelImExporterException(jsonParseError);
        }
        catch (final IOException ioException)
        {
            final String errorMsg = "This error is a critical bug please report to the Autor.";
            LogManager.getLogger(getClass()).fatal(errorMsg, ioException);
            throw new IllegalStateException(errorMsg, ioException);
        }
    }

    /**
     * Posts an error for the observers.
     *
     * @param aError
     *            The error which should be posted.
     */
    protected void postError(final ExcelImExporterError aError)
    {
        observers.parallelStream().forEach(observer -> observer.newError(aError));
    }

    /**
     * Posts an warning for the observers.
     *
     * @param aWarning
     *            The warning which should be posted.
     */
    protected void postWarning(final ExcelImExporterWarning aWarning)
    {
        observers.parallelStream().forEach(observer -> observer.newWarning(aWarning));
    }

    protected synchronized void setSubRuns(final int aSubRuns)
    {
        subRuns = aSubRuns;
    }

    /**
     * Updates the percentage for each observer.
     *
     */
    protected void updateProgress()
    {
        updateProgress(percentage);
    }

}
