package be.cetic.dbmain.mapping;

import com.dbmain.jidbm.DBMDataObject;
import com.dbmain.jidbm.DBMGenericObject;
import com.dbmain.jidbm.DBMSimpleAttribute;

class DataObject {
    private DBMDataObject datao;
    private String dataoType;
    private String attributeType = "-";
    private String mandatory = "-";
    private String parent = "-";

    DataObject(DBMDataObject datao) {
        this.datao = datao;
        dataoType = this.getObjectType();
        if (dataoType.equals("COLUMN")) {
            DBMSimpleAttribute att = (DBMSimpleAttribute) datao;
            DBMDataObject parentDatao = (DBMDataObject) att.getAttributeOwner();
            parent = parentDatao.getName();
            attributeType = findType(att);
            if (att.getMinimumCardinality() == 0) {
                mandatory = "null";
            } else {
                mandatory = "not null";
            }
        }
    }

    private String getObjectType() {
        String dataoType = "";
        switch (datao.getObjectType()) {
            case DBMGenericObject.ENTITY_TYPE:
                dataoType = "TABLE";
                break;
            case DBMGenericObject.SI_ATTRIBUTE:
                dataoType = "COLUMN";
                break;
            case DBMGenericObject.GROUP:
                dataoType = "GROUP";
                break;
        }
        return dataoType;
    }

    /**
     * Find simple attribute type.
     *
     * @param si A simple attribute.
     * @return A string containing the type of simple attribute si.
     */
    private static String findType(DBMSimpleAttribute si) {
        int l, d;
        String physType;

        if (si.getMetaPropertyValue("physType") != null) {
            physType = si.getMetaPropertyValue("physType").toString();
        } else {
            physType = "";
        }
        if (physType == null || physType.compareToIgnoreCase("") == 0) {
            switch (si.getType()) {
                case DBMSimpleAttribute.DATE_ATT:
                    return "date";
                case DBMSimpleAttribute.CHAR_ATT:
                    return "char(" + si.getLength() + ")";
                case DBMSimpleAttribute.VARCHAR_ATT:
                    l = si.getLength();
                    if (l == DBMSimpleAttribute.N_CARD) {
                        return "long varchar";
                    } else {
                        return "varchar(" + l + ")";
                    }
                case DBMSimpleAttribute.NUM_ATT:
                    l = si.getLength();
                    d = si.getDecimalNumber();
                    if (d == 0) {
                        return "numeric(" + l + ")";
                    } else {
                        return "numeric(" + l + "," + d + ")";
                    }
                case DBMSimpleAttribute.FLOAT_ATT:
                    return "float(" + si.getLength() + ")";
                case DBMSimpleAttribute.BOO_ATT:
                    return "char";
            }
        } else {//physType
            if (physType.contains("%l")) {
                physType = physType.replaceAll("%l", String.valueOf(si.getLength()));
            }
            if (physType.contains("%d")) {
                physType = physType.replaceAll("%d", String.valueOf(si.getDecimalNumber()));
            }
            if (physType.contains("%pl")) {
                if (si.getMetaPropertyValue("physLen") != null) {
                    physType = physType.replaceAll("%pl", Integer.toString((Integer) si.getMetaPropertyValue("physLen")));
                } else {
                    physType = physType.replaceAll("%pl", String.valueOf(si.getLength()));
                }
            }
            return physType;
        }

        return "";
    }

    String toCSV() {
        return dataoType + ";" + this.datao.getName() + ";" + this.parent + ";" + attributeType + ";" + mandatory;
    }

    String attributeDefinition() {
        return attributeType + " " + mandatory;
    }
}

