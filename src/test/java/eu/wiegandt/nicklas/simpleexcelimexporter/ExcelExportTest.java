package eu.wiegandt.nicklas.simpleexcelimexporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import eu.wiegandt.nicklas.simpleexcelimexporter.exceptions.ExcelImExporterError;
import eu.wiegandt.nicklas.simpleexcelimexporter.exceptions.ExcelImExporterException;
import eu.wiegandt.nicklas.simpleexcelimexporter.exceptions.ExcelImExporterWarning;
import eu.wiegandt.nicklas.simpleexcelimexporter.utils.LocalFileLoaderUtil;

/**
 * Tests the {@link ExcelExporter}.
 *
 * @author Nicklas Wiegandt (Nicklas2751)<br>
 *         <b>Mail:</b> nicklas@wiegandt.eu<br>
 *         <b>Jabber:</b> nicklas2751@elaon.de<br>
 *         <b>Skype:</b> Nicklas2751<br>
 *
 */
public class ExcelExportTest implements ExcelImExportObserver
{
    private static final String ERROR_WRONG_COLUMN_NAMES_PATTERN =
            "The excel table \"%s\" doesn't contain the expected columns.";
    private static final String ERROR_WRONG_VALUES_PATTERN =
            "The excel table \"%s\" doesn't contain the expected values.";
    private static final String EXTENDED_TEST_TABLE_NAME = "TestDataClass";
    private static final Logger LOG = LogManager.getLogger(ExcelExportTest.class);
    private static final String SIMPLE_TEST_TABLE_NAME = "TestTableSimple";
    private static final String TEST_EXCEL_FILE_NAME_PATTERN = "ExcelExportTest_%d.xlsx";
    private static final String TEST_FIELD_NAME_EXTENDED_TEST_FIELD_DATE_FIELD = "dateField";
    private static final String TEST_FIELD_NAME_EXTENDED_TEST_FIELD_EXPORT = "export";
    private static final String TEST_FIELD_NAME_EXTENDED_TEST_FIELD_IM_EXPORT = "imexport";
    private static final String TEST_FIELD_NAME_SIMPLE_TEST_FIELD_EXPORT = "testFieldExport";
    private static final String TEST_FIELD_NAME_SIMPLE_TEST_FIELD_IM_EXPORT = "testFieldImExport";
    private static Sheet testSheet;
    private List<String> columnNames;
    private Path excelFilePath;

    private List<String> values;

    /**
     * {@see ExcelImExportObserver#newError(ExcelImExporterError)}
     */
    @Override
    public void newError(final ExcelImExporterError aError)
    {
        Assert.fail(aError.getMessage());
    }

    /**
     * {@see ExcelImExportObserver#newWarning(ExcelImExporterWarning)
     */
    @Override
    public void newWarning(final ExcelImExporterWarning aWarning)
    {
        LOG.warn(aWarning.getMessage());
    }

    @Before
    public void setUp() throws InvalidFormatException, IOException, InterruptedException, ExecutionException,
            InstantiationException, IllegalAccessException, ExcelImExporterException
    {
        excelFilePath =
                LocalFileLoaderUtil.getLocalFile(String.format(TEST_EXCEL_FILE_NAME_PATTERN, new Date().getTime()));

        AbstractExcelImExporter.addTableManager(TestDataClasses.TEST.getExcelTableManager());
        AbstractExcelImExporter.addTableManager(TestDataClasses.TEST_NO_MAPPING.getExcelTableManager());

        final ExcelExporter excelExporter = new ExcelExporter();
        final Collection<String> tableNames = new ArrayList<>();
        tableNames.add(SIMPLE_TEST_TABLE_NAME);
        tableNames.add(EXTENDED_TEST_TABLE_NAME);
        excelExporter.addObserver(this);
        excelExporter.exportToExcel(tableNames, excelFilePath);
    }

    @After
    public void tearDown() throws IOException
    {
        Files.delete(excelFilePath);
    }

    @Test
    public void testExtendedMappingExport() throws InvalidFormatException, IOException
    {
        readResults(EXTENDED_TEST_TABLE_NAME);
        Assert.assertThat(String.format(ERROR_WRONG_COLUMN_NAMES_PATTERN, EXTENDED_TEST_TABLE_NAME), columnNames,
                IsCollectionContaining.hasItems(TEST_FIELD_NAME_EXTENDED_TEST_FIELD_EXPORT,
                        TEST_FIELD_NAME_EXTENDED_TEST_FIELD_IM_EXPORT, TEST_FIELD_NAME_EXTENDED_TEST_FIELD_DATE_FIELD));

        final String expectedDateFieldValue = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY)
                .format(TestDataController.TEST_VALUE_DATE_FIELD);

        Assert.assertThat(String.format(ERROR_WRONG_VALUES_PATTERN, EXTENDED_TEST_TABLE_NAME), values,
                IsCollectionContaining.hasItems(TestDataController.TEST_VALUE_EXPORT_FIELD,
                        TestDataController.TEST_VALUE_IMEXPORT_FIELD, expectedDateFieldValue));
    }

    @Test
    public void testSimpleExport() throws InvalidFormatException, IOException
    {
        readResults(SIMPLE_TEST_TABLE_NAME);
        Assert.assertThat(String.format(ERROR_WRONG_COLUMN_NAMES_PATTERN, SIMPLE_TEST_TABLE_NAME), columnNames,
                IsCollectionContaining.hasItems(TEST_FIELD_NAME_SIMPLE_TEST_FIELD_EXPORT,
                        TEST_FIELD_NAME_SIMPLE_TEST_FIELD_IM_EXPORT));

        Assert.assertThat(String.format(ERROR_WRONG_VALUES_PATTERN, SIMPLE_TEST_TABLE_NAME), values,
                IsCollectionContaining.hasItems(TestDataController.TEST_VALUE_EXPORT_FIELD,
                        TestDataController.TEST_VALUE_IMEXPORT_FIELD));
    }

    @Override
    public void updateProgress(final float aPercentage)
    {
        LOG.info("Progress [" + Thread.currentThread().getName() + "]: " + aPercentage + "%");
    }

    private void readResults(final String aTableName) throws IOException, InvalidFormatException
    {
        final Workbook workbook = new XSSFWorkbook(Files.newInputStream(excelFilePath));
        testSheet = workbook.getSheet(aTableName);
        columnNames = new ArrayList<>();
        testSheet.getRow(0).forEach(cell -> columnNames.add(cell.getStringCellValue()));

        values = new ArrayList<>();
        testSheet.getRow(1).forEach(cell -> values.add(cell.getStringCellValue()));
        workbook.close();
    }
}
