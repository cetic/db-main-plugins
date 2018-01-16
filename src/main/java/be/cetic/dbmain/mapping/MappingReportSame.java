package be.cetic.dbmain.mapping;

import com.dbmain.jidbm.*;

import javax.swing.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class MappingReportSame {
    private static PrintWriter fw = null;
    private static DBMSchema mappingSchema;
    private static Mapping mapping;

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
                if (sch.getVersion().equals("Mapping")) {
                    mappingSchema = sch;
                }
                sch = pro.getNextProductSchema(sch);
            }
            if (mappingSchema == null) {
                System.out.println("Error: Could not find mapping schema.");
            } else {
                // Create a new CSV file
                JFileChooser fc = new JFileChooser();
                fc.setSelectedFile(new File("mapping_ar.csv"));
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
                System.out.println("Mapping schema: " + mappingSchema.getName());
                mapping = new Mapping(mappingSchema, mappingSchema);
                ArrayList<DBMDataObject> groupedDataObjects = new ArrayList<>();
                Integer groupId = 1;
                DBMEntityType entityType = mappingSchema.getFirstDataObjectEntityType();
                while (entityType != null) {
                    if (!groupedDataObjects.contains(entityType)) {
                        Vector<DBMDataObject> dataObjects = mapping.findMappingDataObject(entityType, mappingSchema);
                        if (dataObjects.size() > 1) {
                            groupedDataObjects.addAll(dataObjects);
                            displayGroup(groupId, dataObjects);
                            groupId++;
                        }
                    }
                    entityType = mappingSchema.getNextDataObjectEntityType(entityType);
                }
                // Close the CSV file
                fw.close();
                System.out.println("DONE!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void displayGroup(Integer groupId, Vector<DBMDataObject> masterDataObjects) {
        ArrayList<DBMAttribute> managedAttributes = new ArrayList<>();

        HashMap<String, ArrayList<DBMAttribute>> mappingHashMap = new HashMap<>();

        int i = 0;
        while (i < masterDataObjects.size() - 1) {
            DBMEntityType firstEntityType = (DBMEntityType) masterDataObjects.get(i);
            DBMAttribute siatt = firstEntityType.getFirstAttribute();
            while (siatt != null) {
                if (!managedAttributes.contains(siatt)) {
                    ArrayList<DBMAttribute> mappingList = new ArrayList<>();
                    for (int k = 0; k < i; k++) {
                        mappingList.add(null);
                    }
                    mappingList.add(siatt);
                    int j = i + 1;
                    while (j < masterDataObjects.size()) {
                        DBMEntityType nextEntityType = (DBMEntityType) masterDataObjects.get(j);
                        DBMAttribute nextsiatt = nextEntityType.getFirstAttribute();
                        boolean found = false;
                        while (nextsiatt != null) {
                            if (!managedAttributes.contains(nextsiatt) && nextsiatt.getName().equals(siatt.getName())) {
                                mappingList.add(nextsiatt);
                                found = true;
                                managedAttributes.add(nextsiatt);
                            }
                            nextsiatt = nextEntityType.getNextAttribute(nextsiatt);
                        }
                        if (!found) {
                            mappingList.add(null);
                        }
                        j++;
                    }
                    mappingHashMap.put(siatt.getName(), mappingList);
                }
                siatt = firstEntityType.getNextAttribute(siatt);
            }
            i++;
        }


        StringBuilder sb = new StringBuilder();
        sb.append("===== ").append(groupId).append(" =====").append(System.getProperty("line.separator"));
        // headers: table names
        for (DBMDataObject datao : masterDataObjects) {
            sb.append(";").append(datao.getName());
        }
        sb.append(System.getProperty("line.separator"));

        // one line by column
        for (Map.Entry<String, ArrayList<DBMAttribute>> entry : mappingHashMap.entrySet()) {
            String key = entry.getKey();
            ArrayList<DBMAttribute> listAtt = entry.getValue();

            sb.append(key);
            for (DBMAttribute att : listAtt) {
                if (att == null) {
                    sb.append(";-");
                } else {
                    DataObject dataObject = new DataObject(att);
                    sb.append(";").append(dataObject.attributeDefinition());
                }
            }
            sb.append(System.getProperty("line.separator"));
        }

        fw.println(sb);
    }

}