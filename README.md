# SimpleExcelImExporter
A framework based on Apache POI to smilpify the im- and export of excel files. This framework adds two Annotations to deklare the im- and exportable classes and their fields.

## Status
* Jenkins: [![Build Status](http://jenkins.wiegandt.eu/job/SimpleExcelImExporter/badge/icon)](http://jenkins.wiegandt.eu/job/SimpleExcelImExporter/)

## How to use

1. Install the framework as libary. (Maven artifact in central coming soon.)
2. Let all classes which should be im-/exportet implement the interface "DataClass"
3. Create a data controller for each data class
  * A class implementing the interface "DataController<[DataClass Type]>"
4. Annotate all data classes for im-/export with @ExcelTable
  * The parameter "datacontroller" must get a class of a data controller for these class.
  * The parameter "mappingfile" is optional but if you fill it, it must be filled with a path to a mapping file for this data class.
  *  * You can use the Method "generateCleanMappingFile(String, String)" to generate a clean mapping file for your class.
5. Annotate all fields which should be im-/exportable with @ExcelField
  * The parameter "exportable" has to be true if the field sould be exportable.
  * The parameter "importable" has to be true if the field sould be importable.
  * The optional parameter "required" field can be set to true if the, importable, field must exist when importing a excel table using this class.
6. Create an "ExcelTableManager" for each class and add them to the ExcelImporter and ExcelExporter class.

You can find some example code in the src/test/java folders.
