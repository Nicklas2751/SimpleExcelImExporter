package eu.wiegandt.nicklas.simpleexcelimexporter;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.wiegandt.nicklas.simpleexcelimexporter.exceptions.ExcelImExporterError;
import eu.wiegandt.nicklas.simpleexcelimexporter.exceptions.ExcelImExporterException;
import eu.wiegandt.nicklas.simpleexcelimexporter.exceptions.ExcelImExporterWarning;
import eu.wiegandt.nicklas.simpleexcelimexporter.utils.LocalFileLoaderUtil;

/**
 * Tests the multi table import of the {@link ExcelImporter}.
 *
 * @author Nicklas Wiegandt (Nicklas2751)<br>
 *         <b>Mail:</b> nicklas@wiegandt.eu<br>
 *         <b>Jabber:</b> nicklas2751@elaon.de<br>
 *         <b>Skype:</b> Nicklas2751<br>
 *
 */
public class ExcelExtendedMultiImporterTest implements ExcelImExportObserver
{
    private static final String EXTENDED_MULTI_EXCEL_IMPORT_TEST_FILE_PATH =
            "importTest/testdataclass_erweitert_multitableimport.xlsx";
    private static final Logger LOG = LogManager.getLogger(ExcelExtendedMultiImporterTest.class);

    @BeforeClass
    public static final void beforeClass()
    {
        AbstractExcelImExporter.addTableManager(TestDataClasses.TEST.getExcelTableManager());
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
    public void testExtendedMultiImport() throws EncryptedDocumentException, InvalidFormatException, IOException,
            ExcelImExporterException, InterruptedException, ExecutionException
    {
        final File extendedMultiImportTestFile =
                LocalFileLoaderUtil.getLocalFile(EXTENDED_MULTI_EXCEL_IMPORT_TEST_FILE_PATH);
        final ExcelImporter importer = new ExcelImporter(extendedMultiImportTestFile.getAbsolutePath());
        importer.setMultiTableMode(true);
        importer.addObserver(this);
        importer.importExcel();
    }

    @Override
    public void updateProgress(final float aPercentage)
    {
        LOG.info("Progress [" + Thread.currentThread().getName() + "]: " + aPercentage + "%");
    }

}
