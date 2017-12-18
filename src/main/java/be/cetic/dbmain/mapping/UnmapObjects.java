package be.cetic.dbmain.mapping;

import com.dbmain.jidbm.*;

import java.io.IOException;
import java.util.Vector;

/**
 * Unmap selected objects by reinitializing their MappingOID metaproperty with their object identifier
 */

public class UnmapObjects {
    public static void runDBM() {
        try {
            Vector<DBMGenericObject> selectedObjects = new Vector<>();
            new DBMConsole();
            // Create the DBMLibrary instance
            DBMLibrary lib = new DBMLibrary();
            // Get the current project
            long sysid = lib.getCurrentProject();
            DBMProject pro = new DBMProject(sysid);
            // Get the schemas
            DBMSchema sch = pro.getFirstProductSchema();
            DBMSchema masterSchema = null;
            DBMSchema slaveSchema = null;
            while (sch != null) {
                if (sch.getVersion().equals("Master")) {
                    masterSchema = sch;
                } else if (sch.getVersion().equals("Slave")) {
                    slaveSchema = sch;
                }
                sch = pro.getNextProductSchema(sch);
            }
            if (masterSchema == null || slaveSchema == null) {
                System.out.println("Error: Could not find master and/or slave schema.");
            } else {
                selectedObjects.addAll(masterSchema.getSelectedObjects());
                selectedObjects.addAll(slaveSchema.getSelectedObjects());
                if (selectedObjects.size() > 0) {
                    for (DBMGenericObject selectedObject : selectedObjects) {
                        selectedObject.setMetaPropertyListValue("MappingOID", new Object[]{selectedObject.getObjectIdentifier()});
                        selectedObject.setFlagConstant(DBMGenericObject.MARK1, false);
                    }
                }
            }
       } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
