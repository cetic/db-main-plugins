package be.cetic.dbmain.mapping;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import javax.swing.JFileChooser;

import com.dbmain.jidbm.*;

public class MappingReport {
    private static PrintWriter fw = null;
    private static DBMSchema masterSchema;
    private static DBMSchema slaveSchema;

    /**
     * Method called by DB-MAIN to generate a CSV file containing the mapping between "Master" and "Slave" schemas.
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
                // Create a new CSV file
                JFileChooser fc = new JFileChooser();
                fc.setSelectedFile(new File("mapping.csv"));
                int rVal = fc.showSaveDialog(null);
                if (rVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        fw = new PrintWriter(fc.getSelectedFile());
                    } catch (Exception wri) {
                        System.out.println("Error: Impossible to write in the file "
                                + fc.getSelectedFile().getName() + ".");
                        return;
                    }
                } else {
                    return;
                }
                // Mapping
                System.out.println("Master schema: " + masterSchema.getName());
                System.out.println("Slave schema: " + slaveSchema.getName());
                fw.println("MAPPING ID;MASTER TYPE;MASTER NAME;MASTER PARENT;MASTER COLUMN TYPE;MASTER MANDATORY;SLAVE TYPE;SLAVE NAME;SLAVE PARENT;SLAVE COLUMN TYPE;SLAVE MANDATORY");
                Mapping mapping = new Mapping(masterSchema, slaveSchema);
                ArrayList<DBMDataObject> groupedDataObjects = new ArrayList<>();
                Integer groupId = 1;
                // master schema
                DBMDataObject datao = masterSchema.getFirstDataObject();
                while (datao != null) {
                    if (!groupedDataObjects.contains(datao)) {
                        Vector<DBMDataObject> dataObjectsMaster = mapping.findMappingDataObject(datao, masterSchema);
                        Vector<DBMDataObject> dataObjectsSlave = mapping.findMappingDataObject(datao, slaveSchema);
                        if (dataObjectsMaster.size() > 1 || dataObjectsSlave.size() > 1) {
                            // mapping N-N
                            groupedDataObjects.addAll(dataObjectsMaster);
                            groupedDataObjects.addAll(dataObjectsSlave);
                            displayGroup(groupId, dataObjectsMaster, dataObjectsSlave);
                        } else if (dataObjectsMaster.size() > 0 && dataObjectsSlave.size() > 0) {
                            // mapping 1-1
                            DBMDataObject masterDatao = dataObjectsMaster.firstElement();
                            DBMDataObject slaveDatao = dataObjectsSlave.firstElement();
                            groupedDataObjects.add(masterDatao);
                            groupedDataObjects.add(slaveDatao);
                            displayDatao(groupId, masterDatao, slaveDatao);
                        } else {
                            // no mapping
                            displayDatao(groupId, datao, null);
                        }
                        groupId++;
                    }
                    datao = masterSchema.getNextDataObject(datao);
                }
                // slave schema: remaining data objects, not mapped with master schema objects
                datao = slaveSchema.getFirstDataObject();
                while (datao != null) {
                    if (!groupedDataObjects.contains(datao)) {
                        // no mapping
                        displayDatao(groupId, null, datao);
                        groupId++;
                    }
                    datao = slaveSchema.getNextDataObject(datao);
                }
                // Close the CSV file
                fw.close();
                System.out.println("DONE!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void displayGroup(Integer groupId, Vector<DBMDataObject> masterDataObjects, Vector<DBMDataObject> slaveDataObjects) {
        String groupData = "[GROUP];[GROUP];[GROUP];[GROUP];[GROUP]";
        for (DBMDataObject datao : masterDataObjects) {
            DataObject dataObject = new DataObject(datao);
            fw.println(groupId + ";" + dataObject.toCSV() + ";" + groupData);
        }
        for (DBMDataObject datao : slaveDataObjects) {
            DataObject dataObject = new DataObject(datao);
            fw.println(groupId + ";" + groupData + ";" + dataObject.toCSV());
        }
    }

    private static void displayDatao(Integer groupId, DBMDataObject masterDatao, DBMDataObject slaveDatao) {
        String masterData;
        String slaveData;
        String noMappingData = "[NO MAPPING];[NO MAPPING];[NO MAPPING];[NO MAPPING];[NO MAPPING]";
        if (masterDatao != null) {
            DataObject master = new DataObject(masterDatao);
            masterData = master.toCSV();
        } else {
            masterData = noMappingData;
        }
        if (slaveDatao != null) {
            DataObject slave = new DataObject(slaveDatao);
            slaveData = slave.toCSV();
        } else {
            slaveData = noMappingData;
        }
        fw.println(groupId + ";" + masterData + ";" + slaveData);
    }

}