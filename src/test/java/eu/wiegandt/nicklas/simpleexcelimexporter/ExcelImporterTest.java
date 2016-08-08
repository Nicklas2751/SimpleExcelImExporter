package eu.wiegandt.nicklas.simpleexcelimexporter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import eu.wiegandt.nicklas.simpleexcelimexporter.annotations.ExcelTable;
import eu.wiegandt.nicklas.simpleexcelimexporter.exceptions.ExcelImExporterError;
import eu.wiegandt.nicklas.simpleexcelimexporter.exceptions.ExcelImExporterException;
import eu.wiegandt.nicklas.simpleexcelimexporter.exceptions.ExcelImExporterWarning;
import eu.wiegandt.nicklas.simpleexcelimexporter.utils.LocalFileLoaderUtil;

/**
 * Tests the {@link ExcelImporter}.
 *
 * @author Nicklas Wiegandt (Nicklas2751)<br>
 *         <b>Mail:</b> nicklas@wiegandt.eu<br>
 *         <b>Jabber:</b> nicklas2751@elaon.de<br>
 *         <b>Skype:</b> Nicklas2751<br>
 *
 */
public class ExcelImporterTest implements ExcelImExportObserver
{
    private static final int NOT_EMPTY_PATIENT_ROW_COUNT = 3;
    private static final String CLEAN_MAPPING_FILE_FILENAME = "testCleanMapping.json";
    private static final Logger LOG = LogManager.getLogger(ExcelImporterTest.class);
    private static final String SIMPLE_EXCEL_IMPORT_TEST_FILE_PATH = "importTest/TestTableSimple.xlsx";
    private static final String EXCEL_PATIENT_IMPORT_TEST_FILE_PATH = "importTest/Gesamtliste - Prototyp.xlsx";
    private static final String TEST_CLASS_FOR_CLEAN_MAPPING_TEST = "testtablesimple";
    private float progressPercentage;

    @BeforeClass
    public static final void beforeClass()
    {
        AbstractExcelImExporter.addTableManager(TestDataClasses.TEST_NO_MAPPING.getExcelTableManager());
        AbstractExcelImExporter.addTableManager(TestDataClasses.TEST_PATIENT.getExcelTableManager());
    }

    @Before
    public final void beforeEachTest()
    {
        progressPercentage = 0;
    }

    @Override
    public void newError(final ExcelImExporterError aError)
    {
        Assert.fail(aError.getMessage());
    }

    @Override
    public void newWarning(final ExcelImExporterWarning aWarning)
    {
        LOG.warn(aWarning.getMessage());
    }

    @Test
    public void testCleanMappingFile() throws Exception
    {
        final Path cleanMappingFilePath = LocalFileLoaderUtil.getLocalFile(CLEAN_MAPPING_FILE_FILENAME);
        AbstractExcelImExporter.generateCleanMappingFile(cleanMappingFilePath, TEST_CLASS_FOR_CLEAN_MAPPING_TEST);
        final Gson gson = new Gson();
        final Type mappingType = new TypeToken<Map<String, String>>()
        {
        }.getType();
        final Map<String, String> actual = gson.fromJson(Files.newBufferedReader(cleanMappingFilePath), mappingType);
        final Set<String> expected = Arrays.stream(TestDataClassNoMappingFile.class.getDeclaredFields()).parallel()
                .map(Field::getName).collect(Collectors.toSet());

        Assert.assertThat(actual.keySet(), CoreMatchers.is(expected));
    }

    @Test
    public void testSimpleImport() throws EncryptedDocumentException, InvalidFormatException, IOException,
            ExcelImExporterException, InterruptedException, ExecutionException
    {
        final Path simpleImportTestFilePath = LocalFileLoaderUtil.getLocalFile(SIMPLE_EXCEL_IMPORT_TEST_FILE_PATH);
        final ExcelImporter importer = new ExcelImporter(simpleImportTestFilePath);
        importer.setTableName(simpleImportTestFilePath.getFileName().toString().split("\\.")[0]);
        importer.addObserver(this);
        importer.importExcel();
    }

    @Test
    public void testImportExcludesEmptyRows() throws EncryptedDocumentException, InvalidFormatException, IOException,
            ExcelImExporterException, InterruptedException, ExecutionException
    {
        final Path simpleImportTestFilePath = LocalFileLoaderUtil.getLocalFile(EXCEL_PATIENT_IMPORT_TEST_FILE_PATH);
        final ExcelImporter importer = new ExcelImporter(simpleImportTestFilePath);
        importer.setTableName(TestDataClasses.TEST_PATIENT.getTableClass().getAnnotation(ExcelTable.class).tableName());
        importer.addObserver(this);
        importer.importExcel();

        while (progressPercentage < 100)
        {
        }

        final Collection<PatientAdresseDataClass> pushedData = PatientAdresseDataController.getPushedData();
        Assert.assertEquals(NOT_EMPTY_PATIENT_ROW_COUNT, pushedData.size());
    }

    @Override
    public void updateProgress(final float aPercentage)
    {
        progressPercentage = aPercentage;
        LOG.info("Progress: " + aPercentage + "%");
    }
}
