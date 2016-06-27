package eu.wiegandt.nicklas.simpleexcelimexporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.wiegandt.nicklas.simpleexcelimexporter.api.DataController;

public class TestDataController implements DataController<TestDataClass>
{
    private static final Logger LOG = LogManager.getLogger(TestDataController.class);
    static final Date TEST_VALUE_DATE_FIELD = new Date();
    static final String TEST_VALUE_EXPORT_FIELD = "TestExportWert1";
    static final String TEST_VALUE_IMEXPORT_FIELD = "TestExportWert2";
    private final Collection<TestDataClass> pushedData;

    public TestDataController()
    {
        pushedData = new ArrayList<>();
    }

    @Override
    public Class<TestDataClass> getDataClass()
    {
        return TestDataClass.class;
    }

    public Collection<TestDataClass> getPushedData()
    {
        return pushedData;
    }

    @Override
    public Collection<TestDataClass> pullData()
    {
        final TestDataClass testDataClass = new TestDataClass();
        testDataClass.setTestFieldExport(TEST_VALUE_EXPORT_FIELD);
        testDataClass.setTestFieldImExport(TEST_VALUE_IMEXPORT_FIELD);
        testDataClass.setTestOptionalGetterSetter(TEST_VALUE_DATE_FIELD);
        return Stream.of(testDataClass).collect(Collectors.toList());
    }

    @Override
    public void pushData(final TestDataClass aDataClass)
    {
        pushedData.add(aDataClass);
        LOG.info("Imported: " + aDataClass.toString());
    }

}
