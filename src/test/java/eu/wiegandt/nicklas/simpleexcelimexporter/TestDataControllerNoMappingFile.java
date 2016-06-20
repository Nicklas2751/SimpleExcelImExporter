package eu.wiegandt.nicklas.simpleexcelimexporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.wiegandt.nicklas.simpleexcelimexporter.api.DataController;

public class TestDataControllerNoMappingFile implements DataController<TestDataClassNoMappingFile>
{
    private static final Logger LOG = LogManager.getLogger(TestDataControllerNoMappingFile.class);
    private final Collection<TestDataClassNoMappingFile> pushedData;

    public TestDataControllerNoMappingFile()
    {
        pushedData = new ArrayList<>();
    }

    @Override
    public Class<TestDataClassNoMappingFile> getDataClass()
    {
        return TestDataClassNoMappingFile.class;
    }

    public Collection<TestDataClassNoMappingFile> getPushedData()
    {
        return pushedData;
    }

    @Override
    public Collection<TestDataClassNoMappingFile> pullData()
    {
        final TestDataClassNoMappingFile testDataClass = new TestDataClassNoMappingFile();
        testDataClass.setTestFieldExport("TestExportWert1");
        testDataClass.setTestFieldImExport("TestExportWert2");
        return Stream.of(testDataClass).collect(Collectors.toList());
    }

    @Override
    public void pushData(final TestDataClassNoMappingFile aDataClass)
    {
        pushedData.add(aDataClass);
        LOG.info("Imported: " + aDataClass.toString());
    }

}
