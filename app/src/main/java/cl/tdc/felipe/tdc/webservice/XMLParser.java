package cl.tdc.felipe.tdc.webservice;

import android.util.Log;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import cl.tdc.felipe.tdc.adapters.Actividades;
import cl.tdc.felipe.tdc.adapters.Maintenance;

public class XMLParser {


    public static ArrayList<String> getElements(String xml) throws ParserConfigurationException,
            SAXException, IOException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));
        Document doc = db.parse(is);

        ArrayList<String> response = new ArrayList<>();

        String Code = getCharacterDataFromElement((Element) doc.getElementsByTagName("Code").item(0));
        String Description = getCharacterDataFromElement((Element) doc.getElementsByTagName("Description").item(0));

        response.add(Code);
        response.add(Description);

        if (Code.compareTo("0") == 0) { //Si la consulta tuvo éxito obtenemos los parametros
            NodeList Parameters = doc.getElementsByTagName("Parameters");
            for (int i = 0; i < Parameters.getLength(); i++) {
                NodeList Parameter = Parameters.item(i).getChildNodes();
                String dato = "";
                for (int j = 0; j < Parameter.getLength(); j++) {
                    String Name = getCharacterDataFromElement((Element) Parameter.item(j).getChildNodes().item(0));
                    String Value = getCharacterDataFromElement((Element) Parameter.item(j).getChildNodes().item(1));
                    dato += Name + ";" + Value;
                    if (j < Parameter.getLength() - 1) {
                        dato += "&";
                    }
                }
                response.add(dato);
            }

        }

        return response;
        //	return cpe.elementAt(1).toString(); // Mostrar elemento 1 del Vector
    }

	/*
	 * Parser Return Code
	 */

    public static ArrayList<String> getReturnCode(String xmlRecords) throws ParserConfigurationException,
            SAXException, IOException, XPathExpressionException {
        ArrayList<String> models = new ArrayList<>();


        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlRecords));

        Document doc = db.parse(is);
        NodeList nodes = doc.getElementsByTagName("return");

        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            NodeList name = element.getChildNodes();

            for (int j = 0; j < name.getLength(); j++) {
                Element line1 = (Element) name.item(j);
                models.add(getCharacterDataFromElement(line1));
            }
        }

        return models;
        //return cpe.elementAt(1).toString(); // Mostrar elemento 1 del Vector
    }

    public static ArrayList<String> getReturnCode1(String xmlRecords) throws ParserConfigurationException,
            SAXException, IOException, XPathExpressionException {
        ArrayList<String> models = new ArrayList<>();


        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlRecords));

        Document doc = db.parse(is);
        NodeList nodes = doc.getElementsByTagName("Response");

        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            NodeList name = element.getChildNodes();

            for (int j = 0; j < name.getLength(); j++) {
                Element line1 = (Element) name.item(j);
                models.add(getCharacterDataFromElement(line1));
            }
        }

        return models;
        //return cpe.elementAt(1).toString(); // Mostrar elemento 1 del Vector
    }

    public static ArrayList<String> getReturnCode2(String xmlRecords) throws ParserConfigurationException,
            SAXException, IOException, XPathExpressionException {
        ArrayList<String> models = new ArrayList<>();

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlRecords));

        Document doc = db.parse(is);
        Element code = (Element) doc.getElementsByTagName("Code").item(0);
        Element desc = (Element) doc.getElementsByTagName("Description").item(0);;

        models.add(getCharacterDataFromElement(code));
        models.add(getCharacterDataFromElement(desc));

        return models;
        //return cpe.elementAt(1).toString(); // Mostrar elemento 1 del Vector
    }


    public static ArrayList<String> getLocations(String xml) throws ParserConfigurationException,
            SAXException, IOException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));
        Document doc = db.parse(is);

        ArrayList<String> response = new ArrayList<>();

        String Code = getCharacterDataFromElement((Element) doc.getElementsByTagName("Code").item(0));
        String Description = getCharacterDataFromElement((Element) doc.getElementsByTagName("Description").item(0));

        response.add(Code);
        response.add(Description);

        if (Code.compareTo("0") == 0) { //Si la consulta tuvo éxito obtenemos los parametros
            NodeList Parameters = doc.getElementsByTagName("Parameters");
            for (int i = 0; i < Parameters.getLength(); i++) {
                NodeList Parameter = Parameters.item(i).getChildNodes();
                String dato = "";
                for (int j = 0; j < Parameter.getLength(); j++) {
                    String Name = getCharacterDataFromElement((Element) Parameter.item(j).getChildNodes().item(0));
                    String Value = getCharacterDataFromElement((Element) Parameter.item(j).getChildNodes().item(1));

                    dato += Name + ";" + Value;
                    if (j != Parameter.getLength() - 1)
                        dato += "&";
                }
                response.add(dato);
            }

        }

        return response;
        //	return cpe.elementAt(1).toString(); // Mostrar elemento 1 del Vector
    }

    public static ArrayList<String> getTypeActivity(String xml) throws ParserConfigurationException,
            SAXException, IOException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));
        Document doc = db.parse(is);

        ArrayList<String> response = new ArrayList<>();
        NodeList Parameters = doc.getElementsByTagName("Response").item(0).getChildNodes();
        NodeList Tipo = Parameters.item(0).getChildNodes();
        NodeList Stores = Parameters.item(1).getChildNodes();
        NodeList Respuesta = Parameters.item(2).getChildNodes();
        String Code = getCharacterDataFromElement((Element) Respuesta.item(0));
        String Description = getCharacterDataFromElement((Element) Respuesta.item(1));

        String Type = getCharacterDataFromElement((Element) Tipo.item(0).getChildNodes().item(1));

        response.add(Code);
        response.add(Description);
        response.add(Type);

        for(int i = 0 ; i < Stores.getLength(); i++){
            String stores = "";
            String header = "";
            String detail = "";

            NodeList Store = Stores.item(i).getChildNodes();
            NodeList Header = Store.item(0).getChildNodes();
            NodeList Detail = Store.item(1).getChildNodes().item(0).getChildNodes();

            for(int j=0; j < Header.getLength();j++){
                String name = getCharacterDataFromElement((Element)Header.item(j).getChildNodes().item(0));
                String value = getCharacterDataFromElement((Element)Header.item(j).getChildNodes().item(1));
                header += value;
                if(j<Header.getLength()-1){
                    header+=";";
                }
            }

            stores+= header +"&";

            for(int j = 0; j< Detail.getLength(); j++){
                String name = getCharacterDataFromElement((Element)Detail.item(j).getChildNodes().item(0));
                String value = getCharacterDataFromElement((Element)Detail.item(j).getChildNodes().item(1));
                detail += value;
                if(j<Detail.getLength()-1)
                    detail += ";";
            }
            stores += detail;
            response.add(stores);
        }


        return response;
        //	return cpe.elementAt(1).toString(); // Mostrar elemento 1 del Vector
    }


    public static ArrayList<Maintenance> getListadoActividades(String xmlRecords) throws ParserConfigurationException,
            SAXException, IOException, XPathExpressionException {
        ArrayList<Maintenance> models = new ArrayList<>();

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlRecords));

        Document doc = db.parse(is);
        NodeList nodes = doc.getElementsByTagName("Maintenance");

        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            String Date = getCharacterDataFromElement((Element)element.getElementsByTagName("Date").item(0));
            String System = getCharacterDataFromElement((Element)element.getElementsByTagName("System").item(0));
            String Latitude = getCharacterDataFromElement((Element) element.getElementsByTagName("Latitude").item(0));
            String Longitude = getCharacterDataFromElement((Element) element.getElementsByTagName("Longitude").item(0));
            String Address = getCharacterDataFromElement((Element) element.getElementsByTagName("Address").item(0));
            String Station = getCharacterDataFromElement((Element)element.getElementsByTagName("Station").item(0));
            String Status = getCharacterDataFromElement((Element)element.getElementsByTagName("Status").item(0));
            String IdMaintenance = getCharacterDataFromElement((Element)element.getElementsByTagName("IdMaintenance").item(0));
            String Type = getCharacterDataFromElement((Element)element.getElementsByTagName("Type").item(0));

            Maintenance maintenance = new Maintenance(Date,System,Latitude,Longitude,Address,Station,Status,IdMaintenance,Type);

            NodeList Activities = element.getElementsByTagName("Activities");
            for(int j = 0; j<Activities.getLength(); j++){
                Element subElement = (Element) Activities.item(j);
                String Name = getCharacterDataFromElement((Element)subElement.getElementsByTagName("Name").item(0));
                String Description = getCharacterDataFromElement((Element)subElement.getElementsByTagName("Description").item(0));
                String IdActivity = getCharacterDataFromElement((Element)subElement.getElementsByTagName("IdActivity").item(0));
                maintenance.addActivity(new Actividades(Name,Description, IdActivity));
            }
            models.add(maintenance);
        }
        return models;
    }


	/*
	 * Generico para todas las consultas
	 */

    public static String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "";
    }

}
