package eu.wiegandt.nicklas.simpleexcelimexporter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
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
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import eu.wiegandt.nicklas.simpleexcelimexporter.AbstractExcelImExporter;
import eu.wiegandt.nicklas.simpleexcelimexporter.ExcelImExportObserver;
import eu.wiegandt.nicklas.simpleexcelimexporter.ExcelImporter;
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
    private static final String CLEAN_MAPPING_FILE_FILENAME = "testCleanMapping.json";
    private static final Logger LOG = LogManager.getLogger(ExcelImporterTest.class);
    private static final String SIMPLE_EXCEL_IMPORT_TEST_FILE_PATH = "importTest/testdataclassnomappingfile.xlsx";
    private static final String TEST_CLASS_FOR_CLEAN_MAPPING_TEST = "testdataclassnomappingfile";

    @BeforeClass
    public static final void beforeClass()
    {
        AbstractExcelImExporter.addTableManager(TestDataClasses.TEST_NO_MAPPING.getExcelTableManager());
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
        final File cleanMappingFile = LocalFileLoaderUtil.getLocalFile(CLEAN_MAPPING_FILE_FILENAME);
        AbstractExcelImExporter.generateCleanMappingFile(cleanMappingFile.getAbsolutePath(),
                TEST_CLASS_FOR_CLEAN_MAPPING_TEST);
        final Gson gson = new Gson();
        final Type mappingType = new TypeToken<Map<String, String>>()
        {
        }.getType();
        final Map<String, String> actual = gson.fromJson(new FileReader(cleanMappingFile), mappingType);
        final Set<String> expected = Arrays.stream(TestDataClassNoMappingFile.class.getDeclaredFields()).parallel()
                .map(Field::getName).collect(Collectors.toSet());

        Assert.assertThat(actual.keySet(), CoreMatchers.is(expected));
    }

    @Test
    public void testSimpleImport() throws EncryptedDocumentException, InvalidFormatException, IOException,
            ExcelImExporterException, InterruptedException, ExecutionException
    {
        final File simpleImportTestFile = LocalFileLoaderUtil.getLocalFile(SIMPLE_EXCEL_IMPORT_TEST_FILE_PATH);
        final ExcelImporter importer = new ExcelImporter(simpleImportTestFile.getAbsolutePath());
        importer.setTableName(simpleImportTestFile.getName().split("\\.")[0]);
        importer.addObserver(this);
        importer.importExcel();
    }

    @Override
    public void updateProgress(final float aPercentage)
    {
        LOG.info("Progress: " + aPercentage + "%");
    }

}
