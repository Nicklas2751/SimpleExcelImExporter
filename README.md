# SimpleExcelImExporter
A framework based on Apache POI to smilpify the im- and export of excel files. This framework adds two Annotations to deklare the im- and exportable classes and their fields.

## Status / Additional informations
* Gitter: [![Join the chat at https://gitter.im/Nicklas2751/SimpleExcelImExporter](https://badges.gitter.im/Nicklas2751/SimpleExcelImExporter.svg)](https://gitter.im/Nicklas2751/SimpleExcelImExporter?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
* Travis-ci: [![Build Status](https://travis-ci.org/Nicklas2751/SimpleExcelImExporter.svg?branch=master)](https://travis-ci.org/Nicklas2751/SimpleExcelImExporter) | [![Build Status Development](https://travis-ci.org/Nicklas2751/SimpleExcelImExporter.svg?branch=development)](https://travis-ci.org/Nicklas2751/SimpleExcelImExporter)
* Sonarcloud: [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Nicklas2751_SimpleExcelImExporter&metric=alert_status)](https://sonarcloud.io/dashboard?id=Nicklas2751_SimpleExcelImExporter)
* JavaDoc: [![Javadocs](http://www.javadoc.io/badge/eu.wiegandt.nicklas/simpleexcelimexporter.svg)](http://www.javadoc.io/doc/eu.wiegandt.nicklas/simpleexcelimexporter)

## Maven Artifact
    <dependency>
      <groupId>eu.wiegandt.nicklas</groupId>
      <artifactId>simpleexcelimexporter</artifactId>
      <version>1.2</version>
    </dependency>

## How to use

1. Install the framework as libary.
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
