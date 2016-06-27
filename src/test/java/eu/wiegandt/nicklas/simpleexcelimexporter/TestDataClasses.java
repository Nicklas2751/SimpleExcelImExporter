package eu.wiegandt.nicklas.simpleexcelimexporter;

import eu.wiegandt.nicklas.simpleexcelimexporter.api.DataClass;

public enum TestDataClasses
{

    TEST(TestDataClass.class), TEST_NO_MAPPING(TestDataClassNoMappingFile.class);

    private Class<? extends DataClass> dataClass;
    private ExcelTableManager excelTableManager;

    private TestDataClasses(final Class<? extends DataClass> aDataClass)
    {
        dataClass = aDataClass;
        try
        {
            excelTableManager = new ExcelTableManager(aDataClass);
        }
        catch (final Exception exception)
        {
            throw new IllegalStateException(exception);
        }
    }

    public ExcelTableManager getExcelTableManager()
    {
        return excelTableManager;
    }

    public Class<? extends DataClass> getTableClass()
    {
        return dataClass;
    }
}
