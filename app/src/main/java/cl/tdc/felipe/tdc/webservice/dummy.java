package cl.tdc.felipe.tdc.webservice;

public class dummy {
        /* TODO URL WS */
        public static final String URL_SITES = "http://190.102.143.238/tdc/Service/NeighborSite.php";
        public static final String URL_WIFI = "http://190.102.143.238/tdc/Service/collection_wifi.php";
        public static final String URL_TRACKING = "http://190.102.143.238/tdc/Service/position.php";
        public static final String URL_SEFI = "http://190.102.143.238:8080/sefi-ws/SefiPort";
        public static final String URL_ELEMENT = "http://190.102.143.238/tdc/Service/ElementDamage.php";
        public static final String URL_COMPONENT = "http://190.102.143.238/tdc/Service/ComponentDamage.php";
        public static final String URL_AVERIA = "http://190.102.143.238/tdc/Service/AffectedReport.php";
        public static final String URL_PREVENTIVE = "http://190.102.143.238/tdc/Service/form_prev.php";
        public static final String URL_TYPE = "http://190.102.143.238/tdc/Service/TypeActivity.php";
        public static final String URL_FORM_CLOSE = "http://190.102.143.238/tdc/Service/form_prev2.php";

        public static String getInformation = "<SOAP-ENV:Envelope SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:tns=\"urn:Configurationwsdl\">" +
                "<SOAP-ENV:Body>" +
                "<ns1:requestResponse xmlns:ns1=\"urn:Configurationwsdl\">" +
                "<Response xsi:type=\"tns:Response\">" +
                "<Information xsi:type=\"tns:Information\">" +
                "<Maintenance xsi:type=\"tns:Maintenance\">" +
                "<Date xsi:type=\"xsd:string\">2015-05-27 00:00:00</Date>" +
                "<System xsi:type=\"xsd:string\">Generador de Energia1</System>" +
                "<Latitude xsi:type=\"xsd:string\">-33.3313</Latitude>" +
                "<Longitude xsi:type=\"xsd:string\">-71.001</Longitude>" +
                "<Address xsi:type=\"xsd:string\">Calle</Address>" +
                "<Station xsi:type=\"xsd:string\">station2</Station>" +
                "<Status xsi:type=\"xsd:string\">ASSIGNED</Status>" +
                "<IdMaintenance xsi:type=\"xsd:string\">74</IdMaintenance>" +
                "<Type xsi:type=\"xsd:string\">Preventivo</Type>" +
                "<Activities xsi:type=\"tns:Activities\">" +
                "<Name xsi:type=\"xsd:string\">Limpieza Generador</Name>" +
                "<Description xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<IdActivity xsi:type=\"xsd:string\">1</IdActivity>" +
                "</Activities>" +
                "<Activities xsi:type=\"tns:Activities\">" +
                "<Name xsi:type=\"xsd:string\">Cargar generador</Name>" +
                "<Description xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<IdActivity xsi:type=\"xsd:string\">2</IdActivity>" +
                "</Activities>" +
                "</Maintenance>" +
                "<Maintenance xsi:type=\"tns:Maintenance\">" +
                "<Date xsi:type=\"xsd:string\">2015-05-27 00:00:00</Date>" +
                "<System xsi:type=\"xsd:string\">Generador de Energia2</System>" +
                "<Latitude xsi:type=\"xsd:string\">-33.3213</Latitude>" +
                "<Longitude xsi:type=\"xsd:string\">-70.001</Longitude>" +
                "<Address xsi:type=\"xsd:string\">Calle</Address>" +
                "<Station xsi:type=\"xsd:string\">station1</Station>" +
                "<Status xsi:type=\"xsd:string\">TERMINATED</Status>" +
                "<IdMaintenance xsi:type=\"xsd:string\">74</IdMaintenance>" +
                "<Type xsi:type=\"xsd:string\">Preventivo</Type>" +
                "<Activities xsi:type=\"tns:Activities\">" +
                "<Name xsi:type=\"xsd:string\">Limpieza Generador</Name>" +
                "<Description xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<IdActivity xsi:type=\"xsd:string\">1</IdActivity>" +
                "</Activities>" +
                "<Activities xsi:type=\"tns:Activities\">" +
                "<Name xsi:type=\"xsd:string\">Limpieza Generador</Name>" +
                "<Description xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<IdActivity xsi:type=\"xsd:string\">2</IdActivity>" +
                "</Activities>" +
                "<Activities xsi:type=\"tns:Activities\">" +
                "<Name xsi:type=\"xsd:string\">Cargar generador</Name>" +
                "<Description xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<IdActivity xsi:type=\"xsd:string\">3</IdActivity>" +
                "</Activities>" +
                "<Activities xsi:type=\"tns:Activities\">" +
                "<Name xsi:type=\"xsd:string\">Limpieza Generador</Name>" +
                "<Description xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<IdActivity xsi:type=\"xsd:string\">4</IdActivity>" +
                "</Activities>" +
                "</Maintenance>" +
                "</Information>" +
                "<Code xsi:type=\"xsd:string\">0</Code>" +
                "<Description xsi:type=\"xsd:string\">Datos ingresados exitosamente</Description>" +
                "<Element xsi:type=\"xsd:string\">PLANNING</Element>" +
                "<OperationType xsi:type=\"xsd:string\">GET</OperationType>" +
                "</Response>" +
                "</ns1:requestResponse>" +
                "</SOAP-ENV:Body>" +
                "</SOAP-ENV:Envelope>";

        public static String updateTechnicians = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<S:Body>" +
                "<ns2:updateTechniciansResponse xmlns:ns2=\"http://bean.ws.sefi.com/\">" +
                "<return>" +
                "<returnCode>0</returnCode>" +
                "<returnDescription>OK</returnDescription>" +
                "<responses>" +
                "<entityId>Cuadrilla1</entityId>" +
                "<returnCode>0</returnCode>" +
                "<returnType>OK</returnType>" +
                "<returnDesc>actualizado correctamente. Actualizadas las [ 0] zonas. Actualizadas los [ 0] skills</returnDesc>" +
                "</responses>" +
                "</return>" +
                "</ns2:updateTechniciansResponse>" +
                "</S:Body>" +
                "</S:Envelope>";
        
        public static String updateActivities = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<S:Body>" +
                "<ns2:updateActivitiesResponse xmlns:ns2=\"http://bean.ws.sefi.com/\">" +
                "<return>" +
                "<returnCode>0</returnCode>" +
                "<returnDescription>OK</returnDescription>" +
                "<responses>" +
                "<entityId>ACTIVIDAD6</entityId>" +
                "<returnCode>0</returnCode>" +
                "<returnType>OK</returnType>" +
                "<returnDesc/>" +
                "</responses>" +
                "</return>" +
                "</ns2:updateActivitiesResponse>" +
                "</S:Body>" +
                "</S:Envelope>";
}
