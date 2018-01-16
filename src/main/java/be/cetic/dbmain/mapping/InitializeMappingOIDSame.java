package be.cetic.dbmain.mapping;

import com.dbmain.jidbm.*;

public class InitializeMappingOIDSame {
    private static DBMSchema masterSchema;
    private static DBMSchema slaveSchema;

    /**
     * Method called by DB-MAIN to initialize MappingOID meta-property of data objects with their object identifier.
     * Only the data objects in schemas with "Master" or "Slave" version are modified.
     */
    public static void runDBM() {
        try {
            new DBMConsole();
            // Create the DBMLibrary instance
            DBMLibrary lib = new DBMLibrary();
            // Get the current project
            long sysid = lib.getCurrentProject();
            DBMProject pro = new DBMProject(sysid);
            // Get the schemas
            DBMSchema sch = pro.getFirstProductSchema();
            while (sch != null) {
                if (sch.getVersion().equals("Mapping")) {
                    initMappingOID(sch);
                }
                sch = pro.getNextProductSchema(sch);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initMappingOID(DBMSchema schema) {
        System.out.print("Initializing MappingOID in schema " + schema.getName() + "/" + schema.getVersion() + "...");
        DBMEntityType ent = schema.getFirstDataObjectEntityType();
        while (ent != null) {
            Mapping.initializeMapping(ent);
            ent.setFlagConstant(DBMGenericObject.MARK1, false);
            DBMGroup gr = ent.getFirstGroup();
            while (gr != null) {
                Mapping.initializeMapping(gr);
                gr.setFlagConstant(DBMGenericObject.MARK1, false);
                gr = ent.getNextGroup(gr);
            }
            ent = schema.getNextDataObjectEntityType(ent);
        }
        DBMSimpleAttribute att = schema.getFirstDataObjectSimpleAttribute();
        while (att != null) {
            Mapping.initializeMapping(att);
            att.setFlagConstant(DBMGenericObject.MARK1, false);
            att = schema.getNextDataObjectSimpleAttribute(att);
        }
        System.out.println(" done.");
    }
}

