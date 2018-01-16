# DB-MAIN mapping plugins

This project contains DB-MAIN plugins used to manage the mapping between schemas or between tables inside a schema. 

## Prerequisites

Install [DB-MAIN](http://www.rever.eu/en/content/db-main-homepage) with integrated JRE.

To launch a plugin in DB-MAIN:
1. Open a DB-MAIN project.
2. Go to File > Execute plug-in...
3. Find and execute the desired plug-in (a .class file).

## Description of the plugins 

### Mapping between two schemas

This plugins are meant to be launched on a DB-MAIN project containing at least two schemas, one with the "Master" version and the other one with the "Slave" version.
They essentially use the predefined "MappingOID" metaproperty of the objects (entity types (or tables) and attributes (or columns)).

#### InitializeMappingOID

Initialize the MappingOID metaproperty of the objects of master and slave schemas with their object identifier. 

#### MapObjects

Map selected (groups of) objects in master and slave schemas.

#### UnmapObjects

Unmap selected objects in master and slave schemas by reinitializing the MappingOID property with their object identifier.

#### MappingReport

Generates a CSV file containing the mapping report between master and slave schemas. The report has 11 columns: 
* MAPPING ID: all objects (tables and columns) with the same MAPPING ID are mapped together.
* MASTER (or SLAVE) TYPE: "TABLE" or "COLUMN"
* MASTER (or SLAVE) NAME: name of the table or the column
* MASTER (or SLAVE) PARENT: if slave object is a column, the name of the parent table
* MASTER (or SLAVE) COLUMN TYPE: if slave object is a column, its type and length
* MASTER (or SLAVE) MANDATORY: "null" or "not null"

Special values:
* GROUP indicates a N-N mapping : masters and slaves form a group identified by MAPPING ID.
* NO MAPPING indicates that the master or the slave has no mapped object in the slave or master schema.

### Mapping inside one schema

This plugins are meant to be launched on a DB-MAIN project containing at least one schema with the "Mapping" version.
They essentially use the predefined "MappingOID" metaproperty of the tables.

#### InitializeMappingOIDSame

Initialize the MappingOID metaproperty of objects in the mapping schema with their object identifier. 

#### MapObjectsSame

Map selected tables in mapping schema.

#### MappingReportSame

Generates a CSV file containing the mapping report between tables of mapping schema. The file is divided in sections. Each section contains one mapping report:
* line 1, columns 2 to N: mapped table names (only one if the table doesn't map)
* lines 2 to N: union of columns of the mapped tables
    * column 1: name of the column
    * column 2 to N: type of the column in the corresponding table (cf. line 1)

### DataObject

THIS IS NOT A PLUGIN. Class used by the other plugins of the package. 

## Compiled classes

For the user's convenience, we provide the compiled classes in directory /target/production. To build the source code, one needs the library jidbm.jar that is provided with DB-MAIN. See more in JIDBM reference manual provided with DB-MAIN.

_Troubleshooting: if the plugins don't launch, put them in the same directory as the DB-MAIN project (.lun file)._  

## Authors

* **Anne-France Brogneaux** - *Initial work* - [afbrogneaux](https://github.com/afbrogneaux)

See also the list of [contributors](https://github.com/cetic/db-main-plugins/graphs/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](https://github.com/cetic/db-main-plugins/blob/master/LICENSE) file for details.
