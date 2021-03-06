package be.cetic.dbmain.mapping;

import com.dbmain.jidbm.*;

import java.util.Vector;

/**
 * Map selected (groups of) objects
 */

public class MapObjectsSame {
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
            DBMSchema masterSchema = null;
            DBMSchema slaveSchema = null;
            while (sch != null) {
                if (sch.getVersion().equals("Mapping")) {
                    masterSchema = sch;
                }
                sch = pro.getNextProductSchema(sch);
            }
            if (masterSchema == null) {
                System.out.println("Error: Could not find mapping schema.");
            } else {
                Vector<DBMGenericObject> selectedObjects = new Vector<>(masterSchema.getSelectedObjects());
                if (selectedObjects.size() > 0) {
                    Object[] MappingOID = new Object[selectedObjects.size()];
                    int i = 0;
                    for (DBMGenericObject selectedObject : selectedObjects) {
                        MappingOID[i] = selectedObject.getObjectIdentifier();
                        i++;
                    }
                    for (DBMGenericObject selectedObject : selectedObjects) {
                        selectedObject.setMetaPropertyListValue("MappingOID", MappingOID);
                        selectedObject.setFlagConstant(DBMGenericObject.MARK1, true);
                    }
                    System.out.println(selectedObjects.size() + " objects mapped.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
