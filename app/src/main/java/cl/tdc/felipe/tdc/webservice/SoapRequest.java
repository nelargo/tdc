package cl.tdc.felipe.tdc.webservice;

import android.telephony.TelephonyManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import java.security.KeyStore;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cl.tdc.felipe.tdc.adapters.Actividad;
import cl.tdc.felipe.tdc.adapters.ComponenteCantidad;

public class SoapRequest {
	/*
	 * Clase Principal de Conexion SSL a WDSL
	 */
	
	private static HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }


    public static String sendForm1(String IMEI, List<ComponenteCantidad> items,int IdMaintenance, String observacion) throws Exception {
        final String SOAP_ACTION = "";//"urn:Configurationwsdl#request";
        String response= null;
        String xml = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = new Date();

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(dummy.URL_PREVENTIVE);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.dotNet = false;
        envelope.implicitTypes = true;

        String bodyOut =
                "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Configurationwsdl\">" +
                        "<soapenv:Header/>" +
                        "<soapenv:Body>" +
                        "<urn:request soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                        "<FormPrev xsi:type=\"urn:FormPrev\">" +
                        "<Request xsi:type=\"urn:Request\">" +
                        "<!--Optional:-->" +
                        "<Form_Detail xsi:type=\"urn:Form_Detail\">" +
                        "<!--Zero or more repetitions:-->";


        for(int i = 0; i<items.size(); i++){
            bodyOut+="<Parameters xsi:type=\"urn:Parameters\">" +
                    "<!--Zero or more repetitions:-->" +
                    "<Parameter xsi:type=\"urn:Parameter\">" +
                    "<Name xsi:type=\"xsd:string\">EQUIPMENTID</Name>" +
                    "<Value xsi:type=\"xsd:string\">1</Value>" +
                    "</Parameter>" +
                    "<Parameter xsi:type=\"urn:Parameter\">" +
                    "<Name xsi:type=\"xsd:string\">TECHNIC</Name>" +
                    "<Value xsi:type=\"xsd:string\">"+IMEI+"</Value>" +
                    "</Parameter>" +
                    "<Parameter xsi:type=\"urn:Parameter\">" +
                    "<Name xsi:type=\"xsd:string\">REFILLID</Name>" +
                    "<Value xsi:type=\"xsd:string\">"+items.get(i).getComponente().getComponentId()+"</Value>" +
                    "</Parameter>" +
                    "<Parameter xsi:type=\"urn:Parameter\">" +
                    "<Name xsi:type=\"xsd:string\">QUANTITY</Name>" +
                    "<Value xsi:type=\"xsd:string\">"+items.get(i).getCantidad()+"</Value>" +
                    "</Parameter>" +
                    "<Parameter xsi:type=\"urn:Parameter\">" +
                    "<Name xsi:type=\"xsd:string\">STOREID</Name>" +
                    "<Value xsi:type=\"xsd:string\">"+items.get(i).getComponente().getStoreId()+"</Value>" +
                    "</Parameter>" +
                    "</Parameters>";
        }

        bodyOut +="</Form_Detail>" +
                "<!--Optional:-->" +
                "<Header xsi:type=\"urn:Header\">" +
                "<Date xsi:type=\"xsd:string\">"+formatter.format(fecha)+"</Date>" +
                "<Platafform xsi:type=\"xsd:string\">MOBILE</Platafform>" +
                "<User xsi:type=\"xsd:string\">"+IMEI+"</User>" +
                "</Header>" +
                "<!--Optional:-->" +
                "<Form_Header xsi:type=\"urn:Form_Header\">" +
                "<MaintenanceId xsi:type=\"xsd:string\">"+IdMaintenance+"</MaintenanceId>" +
                "<Observation xsi:type=\"xsd:string\">"+observacion+"</Observation>" +
                "</Form_Header>" +
                "<OperationType xsi:type=\"xsd:string\">INSERT</OperationType>" +
                "<Element xsi:type=\"xsd:string\">FORM_PREVENTIVE</Element>" +
                "</Request>" +
                "</FormPrev>" +
                "</urn:request>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";

        xml = bodyOut;
        StringEntity se = new StringEntity(xml, HTTP.UTF_8);
        se.setContentType("text/xml");
        httpPost.addHeader(SOAP_ACTION, dummy.URL_PREVENTIVE);

        httpPost.setEntity(se);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity resEntity = httpResponse.getEntity();
        response = EntityUtils.toString(resEntity);
        return response;
    }



    public static String sendPosition(String LONGITUDE, String LATITUDE, String DATE,String IMEI) throws Exception {
        final String SOAP_ACTION = "";//"urn:Configurationwsdl#request";
        String response= null;
        String xml = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = new Date();

        if(DATE.compareTo("")==0)
            DATE = formatter.format(fecha);

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(dummy.URL_TRACKING);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.dotNet = false;
        envelope.implicitTypes = true;

        String bodyOut =
                "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Configurationwsdl\">" +
                "<soapenv:Header/>" +
                "<soapenv:Body>" +
                "<urn:request soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                "<Position xsi:type=\"urn:Position\">" +
                "<Request xsi:type=\"urn:Request\">" +
                "<!--Zero or more repetitions:-->" +
                "<Parameters xsi:type=\"urn:Parameters\">" +
                "<Parameter xsi:type=\"urn:Parameter\">" +
                "<Name xsi:type=\"xsd:string\">LATITUDE</Name>" +
                "<Value xsi:type=\"xsd:string\">"+LATITUDE+"</Value>" +
                "</Parameter>" +
                "<Parameter xsi:type=\"urn:Parameter\">" +
                "<Name xsi:type=\"xsd:string\">LONGITUDE</Name>" +
                "<Value xsi:type=\"xsd:string\">"+LONGITUDE+"</Value>" +
                "</Parameter>" +
                "<Parameter xsi:type=\"urn:Parameter\">" +
                "<Name xsi:type=\"xsd:string\">TECHNIC</Name>" +
                "<Value xsi:type=\"xsd:string\">"+IMEI+"</Value>" +
                "</Parameter>" +
                "</Parameters>" +
                "<Header xsi:type=\"urn:Header\">" +
                "<Date xsi:type=\"xsd:string\">"+DATE+"</Date>" +
                "<Platafform xsi:type=\"xsd:string\">MOBILE</Platafform>" +
                "<User xsi:type=\"xsd:string\">"+IMEI+"</User>" +
                "</Header>" +
                "<OperationType xsi:type=\"xsd:string\">INSERT</OperationType>" +
                "<Element xsi:type=\"xsd:string\">TRACKING</Element>" +
                "</Request>" +
                "</Position>" +
                "</urn:request>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";
        xml = bodyOut;
        StringEntity se = new StringEntity(xml, HTTP.UTF_8);
        se.setContentType("text/xml");
        httpPost.addHeader(SOAP_ACTION, dummy.URL_TRACKING);

        httpPost.setEntity(se);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity resEntity = httpResponse.getEntity();
        response = EntityUtils.toString(resEntity);
        return response;
    }

    public static String sendWifi(String MAC, String SIGNAL, String CHANNEL, String INFO, String LONGITUDE, String LATITUDE, String IMEI) throws Exception {
        final String SOAP_ACTION = "urn:Configurationwsdl#request";
        String response= null;
        String xml = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = new Date();

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(dummy.URL_WIFI);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.dotNet = false;
        envelope.implicitTypes = true;

        String bodyOut =
                "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Configurationwsdl\">" +
                "<soapenv:Header/>" +
                "<soapenv:Body>" +
                "<urn:request soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                "<Wifi xsi:type=\"urn:Wifi\">" +
                "<Request xsi:type=\"urn:Request\">" +
                "<Parameters xsi:type=\"urn:Parameters\">" +
                "<Parameter xsi:type=\"urn:Parameter\">" +
                "<Name xsi:type=\"xsd:string\">MAC</Name>" +
                "<Value xsi:type=\"xsd:string\">"+MAC+"</Value>" +
                "</Parameter>" +
                "<Parameter xsi:type=\"urn:Parameter\">" +
                "<Name xsi:type=\"xsd:string\">LEVELSIGNAL</Name>" +
                "<Value xsi:type=\"xsd:string\">"+SIGNAL+"</Value>" +
                "</Parameter>" +
                "<Parameter xsi:type=\"urn:Parameter\">" +
                "<Name xsi:type=\"xsd:string\">CHANNEL</Name>" +
                "<Value xsi:type=\"xsd:string\">"+CHANNEL+"</Value>" +
                "</Parameter>" +
                "<Parameter xsi:type=\"urn:Parameter\">" +
                "<Name xsi:type=\"xsd:string\">INFORMATION</Name>" +
                "<Value xsi:type=\"xsd:string\">"+INFO+"</Value>" +
                "</Parameter>" +
                "<Parameter xsi:type=\"urn:Parameter\">" +
                "<Name xsi:type=\"xsd:string\">LATITUDE</Name>" +
                "<Value xsi:type=\"xsd:string\">"+LATITUDE+"</Value>" +
                "</Parameter>" +
                "<Parameter xsi:type=\"urn:Parameter\">" +
                "<Name xsi:type=\"xsd:string\">LONGITUDE</Name>" +
                "<Value xsi:type=\"xsd:string\">"+LONGITUDE+"</Value>" +
                "</Parameter>" +
                "</Parameters>" +
                "<Header xsi:type=\"urn:Header\">" +
                "<Date xsi:type=\"xsd:string\">"+formatter.format(fecha)+"</Date>" +
                "<Platafform xsi:type=\"xsd:string\">MOBILE</Platafform>" +
                "<User xsi:type=\"xsd:string\">"+IMEI+"</User>" +
                "</Header>" +
                "<OperationType xsi:type=\"xsd:string\">INSERT</OperationType>" +
                "<Element xsi:type=\"xsd:string\">WIFI</Element>" +
                "</Request>" +
                "</Wifi>" +
                "</urn:request>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";
        xml = bodyOut;
        StringEntity se = new StringEntity(xml, HTTP.UTF_8);
        se.setContentType("text/xml");
        httpPost.addHeader(SOAP_ACTION, dummy.URL_WIFI);

        httpPost.setEntity(se);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity resEntity = httpResponse.getEntity();
        response = EntityUtils.toString(resEntity);
        return response;
    }


    public static String getInformation(String IMEI) throws Exception{
        //final String SOAP_ACTION = "urn:Configurationwsdl#request";
        String response= null;
        String xml = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = new Date();

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://190.102.143.238/tdc/Service/maintenancePlanning.php");

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.dotNet = false;
        envelope.implicitTypes = true;

        xml =
                "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Configurationwsdl\">" +
                        "<soapenv:Header/>" +
                        "<soapenv:Body>" +
                        "<urn:request soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                        "<Planning xsi:type=\"urn:Planning\">" +
                        "<Request xsi:type=\"urn:Request\">" +
                        "<Header xsi:type=\"urn:Header\">" +
                        "<Date xsi:type=\"xsd:string\">"+formatter.format(fecha)+"</Date>" +
                        "<Platafform xsi:type=\"xsd:string\">MOBILE</Platafform>" +
                        "<User xsi:type=\"xsd:string\">"+IMEI+"</User>" +
                        "</Header>" +
                        "<OperationType xsi:type=\"xsd:string\">GET</OperationType>" +
                        "<Element xsi:type=\"xsd:string\">PLANNING</Element>" +
                        "</Request>" +
                        "</Planning>" +
                        "</urn:request>" +
                        "</soapenv:Body>" +
                        "</soapenv:Envelope>";

        StringEntity se = new StringEntity(xml, HTTP.UTF_8);
        se.setContentType("text/xml");
        httpPost.addHeader("", "http://190.102.143.238/tdc/Service/maintenancePlanning.php");

        httpPost.setEntity(se);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity resEntity = httpResponse.getEntity();
        response = EntityUtils.toString(resEntity);
        return response;
    }




    public static String updateActivity(String ACTIVIDAD) throws Exception{
        final String SOAP_ACTION = "urn:Configurationwsdl#request";
        String response= null;
        String xml = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = new Date();

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(dummy.URL_SEFI);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.dotNet = false;
        envelope.implicitTypes = true;

        String bodyOut =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:bean=\"http://bean.ws.sefi.com/\">" +
                "<soapenv:Header/>" +
                "<soapenv:Body>" +
                "<bean:updateActivities>" +
                "<validation>" +
                "<login>soap_user</login>" +
                "<auth_key>51fe85ffda7ffafa4b7543c130f27d4d</auth_key>" +
                "<now>"+formatter.format(fecha)+"</now>" +
                "</validation>" +
                "<activity>" +
                "<action>UPDATE</action>" +
                "<activityId>"+ACTIVIDAD+"</activityId>" +
                "<status>COMPLETE</status>" +
                "</activity>" +
                "</bean:updateActivities>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";
        xml = bodyOut;
        StringEntity se = new StringEntity(xml, HTTP.UTF_8);
        se.setContentType("text/xml");
        httpPost.addHeader(SOAP_ACTION, dummy.URL_SEFI);

        httpPost.setEntity(se);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity resEntity = httpResponse.getEntity();
        response = EntityUtils.toString(resEntity);
        return response;
    }


    public static String typeActivity(String IMEI, int id) throws Exception{
        final String SOAP_ACTION = "urn:Configurationwsdl#request";
        String response= null;
        String xml = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = new Date();

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(dummy.URL_TYPE);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.dotNet = false;
        envelope.implicitTypes = true;

        String bodyOut =
                "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Configurationwsdl\">" +
                        "<soapenv:Header/>" +
                        "<soapenv:Body>" +
                        "<urn:request soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                        "<Mobile xsi:type=\"urn:Mobile\">" +
                        "<DataForm xsi:type=\"urn:Request\">" +
                        "<Parameters xsi:type=\"urn:Parameters\">" +
                        "<Parameter xsi:type=\"urn:Parameter\">" +
                        "<Name xsi:type=\"xsd:string\">IDACTIVITY</Name>" +
                        "<Value xsi:type=\"xsd:string\">"+id+"</Value>" +
                        "</Parameter>" +
                        "</Parameters>" +
                        "<Header xsi:type=\"urn:Header\">" +
                        "<Date xsi:type=\"xsd:string\">"+formatter.format(fecha)+"</Date>" +
                        "<Platafform xsi:type=\"xsd:string\">MOBILE</Platafform>" +
                        "<User xsi:type=\"xsd:string\">"+IMEI+"</User>" +
                        "</Header>" +
                        "<OperationType xsi:type=\"xsd:string\">SELECT</OperationType>" +
                        "<Element xsi:type=\"xsd:string\">DATAFORM</Element>" +
                        "</DataForm>" +
                        "</Mobile>" +
                        "</urn:request>" +
                        "</soapenv:Body>" +
                        "</soapenv:Envelope>";
        xml = bodyOut;
        StringEntity se = new StringEntity(xml, HTTP.UTF_8);
        se.setContentType("text/xml");
        httpPost.addHeader(SOAP_ACTION, dummy.URL_TYPE);

        httpPost.setEntity(se);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity resEntity = httpResponse.getEntity();
        response = EntityUtils.toString(resEntity);
        return response;
    }


    public static String updateTechnician(String IMEI) throws Exception{
        final String SOAP_ACTION = "urn:Configurationwsdl#request";
        String response= null;
        String xml = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = new Date();

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(dummy.URL_SEFI);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.dotNet = false;
        envelope.implicitTypes = true;

        String bodyOut =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:bean=\"http://bean.ws.sefi.com/\">" +
                "<soapenv:Header/>" +
                "<soapenv:Body>" +
                "<bean:updateTechnicians>" +
                "<validation>" +
                "<login>soap_user</login>" +
                "<auth_key>51fe85ffda7ffafa4b7543c130f27d4d</auth_key>" +
                "<now>"+formatter.format(fecha)+"</now>" +
                "</validation>" +
                "<technician>" +
                "<action>UPDATE</action>" +
                "<technicianId>"+IMEI+"</technicianId>" +
                "<status>ACTIVE</status>" +
                "</technician>" +
                "</bean:updateTechnicians>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";
        xml = bodyOut;
        StringEntity se = new StringEntity(xml, HTTP.UTF_8);
        se.setContentType("text/xml");
        httpPost.addHeader(SOAP_ACTION, dummy.URL_SEFI);

        httpPost.setEntity(se);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity resEntity = httpResponse.getEntity();
        response = EntityUtils.toString(resEntity);
        return response;
    }

    public static String getElements(String IMEI) throws Exception{
        final String SOAP_ACTION = "urn:Configurationwsdl#request";
        String response= null;
        String xml = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = new Date();

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(dummy.URL_ELEMENT);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.dotNet = false;
        envelope.implicitTypes = true;

        String bodyOut =
                "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Configurationwsdl\">" +
                        "<soapenv:Header/>" +
                        "<soapenv:Body>" +
                        "<urn:request soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                        "<Mobile xsi:type=\"urn:Mobile\">" +
                        "<Element xsi:type=\"urn:Request\">" +
                        "<!--Optional:-->" +
                        "<Header xsi:type=\"urn:Header\">" +
                        "<Date xsi:type=\"xsd:string\">"+formatter.format(fecha)+"</Date>" +
                        "<Platafform xsi:type=\"xsd:string\">MOBILE</Platafform>" +
                        "<User xsi:type=\"xsd:string\">"+IMEI+"</User>" +
                        "</Header>" +
                        "<OperationType xsi:type=\"xsd:string\">SELECT</OperationType>" +
                        "<Element xsi:type=\"xsd:string\">ELEMENTDAMAGE</Element>" +
                        "</Element>" +
                        "</Mobile>" +
                        "</urn:request>" +
                        "</soapenv:Body>" +
                        "</soapenv:Envelope>";
        xml = bodyOut;
        StringEntity se = new StringEntity(xml, HTTP.UTF_8);
        se.setContentType("text/xml");
        httpPost.addHeader(SOAP_ACTION, dummy.URL_ELEMENT);

        httpPost.setEntity(se);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity resEntity = httpResponse.getEntity();
        response = EntityUtils.toString(resEntity);
        return response;
    }

    public static String getComponents(String IMEI, String Element) throws Exception{
        final String SOAP_ACTION = "urn:Configurationwsdl#request";
        String response= null;
        String xml = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = new Date();

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(dummy.URL_COMPONENT);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.dotNet = false;
        envelope.implicitTypes = true;

        String bodyOut =
                "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Configurationwsdl\">" +
                        "<soapenv:Header/>" +
                        "<soapenv:Body>" +
                        "<urn:request soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                        "<Mobile xsi:type=\"urn:Mobile\">" +
                        "<Component xsi:type=\"urn:Request\">" +
                        "<!--Optional:-->" +
                        "<Header xsi:type=\"urn:Header\">" +
                        "<Date xsi:type=\"xsd:string\">"+formatter.format(fecha)+"</Date>" +
                        "<Platafform xsi:type=\"xsd:string\">MOBILE</Platafform>" +
                        "<User xsi:type=\"xsd:string\">"+IMEI+"</User>" +
                        "</Header>" +
                        "<!--Optional:-->" +
                        "<Parameters xsi:type=\"urn:Parameters\">" +
                        "<!--Zero or more repetitions:-->" +
                        "<Parameter xsi:type=\"urn:Parameter\">" +
                        "<Name xsi:type=\"xsd:string\">COMPONENT</Name>" +
                        "<Value xsi:type=\"xsd:string\">"+Element+"</Value>" +
                        "</Parameter>" +
                        "</Parameters>" +
                        "<OperationType xsi:type=\"xsd:string\">SELECT</OperationType>" +
                        "<Element xsi:type=\"xsd:string\">COMPONENTDAMAGE</Element>" +
                        "</Component>" +
                        "</Mobile>" +
                        "</urn:request>" +
                        "</soapenv:Body>" +
                        "</soapenv:Envelope>";
        xml = bodyOut;
        StringEntity se = new StringEntity(xml, HTTP.UTF_8);
        se.setContentType("text/xml");
        httpPost.addHeader(SOAP_ACTION, dummy.URL_COMPONENT);

        httpPost.setEntity(se);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity resEntity = httpResponse.getEntity();
        response = EntityUtils.toString(resEntity);
        return response;
    }

    public static String sendAveria(String IMEI, String Element, String Component, String Comment, String Image, String Latitude, String Longitude) throws Exception{
        final String SOAP_ACTION = "urn:Configurationwsdl#request";
        String response= null;
        String xml = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = new Date();

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(dummy.URL_AVERIA);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.dotNet = false;
        envelope.implicitTypes = true;

        String bodyOut =
                "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Configurationwsdl\">" +
                        "<soapenv:Header/>" +
                        "<soapenv:Body>" +
                        "<urn:request soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                        "<Mobile xsi:type=\"urn:Mobile\">" +
                        "<Damage xsi:type=\"urn:Request\">" +
                        "<!--Zero or more repetitions:-->" +
                        "<Parameters xsi:type=\"urn:Parameters\">" +
                        "<!--Zero or more repetitions:-->" +
                        "<Parameter xsi:type=\"urn:Parameter\">" +
                        "<Name xsi:type=\"xsd:string\">ELEMENT</Name>" +
                        "<Value xsi:type=\"xsd:string\">"+Element+"</Value>" +
                        "</Parameter>" +
                        "<Parameter xsi:type=\"urn:Parameter\">" +
                        "<Name xsi:type=\"xsd:string\">TYPEDAMAGE</Name>" +
                        "<Value xsi:type=\"xsd:string\">"+Component+"</Value>" +
                        "</Parameter>" +
                        "<Parameter xsi:type=\"urn:Parameter\">" +
                        "<Name xsi:type=\"xsd:string\">IMAGE</Name>" +
                        "<Value xsi:type=\"xsd:string\">"+Image+"</Value>" +
                        "</Parameter>" +
                        " <Parameter xsi:type=\"urn:Parameter\">" +
                        "<Name xsi:type=\"xsd:string\">LATITUDE</Name>" +
                        "<Value xsi:type=\"xsd:string\">"+Latitude+"</Value>" +
                        "</Parameter>" +
                        "<Parameter xsi:type=\"urn:Parameter\">" +
                        "<Name xsi:type=\"xsd:string\">LONGITUDE</Name>" +
                        "<Value xsi:type=\"xsd:string\">"+Longitude+"</Value>" +
                        "</Parameter>" +
                        "<Parameter xsi:type=\"urn:Parameter\">" +
                        "<Name xsi:type=\"xsd:string\">USER</Name>" +
                        "<Value xsi:type=\"xsd:string\">"+IMEI+"</Value>" +
                        "</Parameter>" +
                        "<Parameter xsi:type=\"urn:Parameter\">" +
                        "<Name xsi:type=\"xsd:string\">DATE</Name>" +
                        "<Value xsi:type=\"xsd:string\">"+formatter.format(fecha)+"</Value>" +
                        "</Parameter>" +
                        "<Parameter xsi:type=\"urn:Parameter\">" +
                        "<Name xsi:type=\"xsd:string\">SUMMARY</Name>" +
                        "<Value xsi:type=\"xsd:string\">"+Comment+"</Value>" +
                        "</Parameter>" +
                        "</Parameters>" +
                        "<!--Optional:-->" +
                        "<Header xsi:type=\"urn:Header\">" +
                        "<Date xsi:type=\"xsd:string\">"+formatter.format(fecha)+"</Date>" +
                        "<Platafform xsi:type=\"xsd:string\">MOBILE</Platafform>" +
                        "<User xsi:type=\"xsd:string\">"+IMEI+"</User>" +
                        "</Header>" +
                        "<OperationType xsi:type=\"xsd:string\">INSERT</OperationType>" +
                        "<Element xsi:type=\"xsd:string\">DAMAGE</Element>" +
                        "</Damage>" +
                        "</Mobile>" +
                        "</urn:request>" +
                        "</soapenv:Body>" +
                        "</soapenv:Envelope>";
        xml = bodyOut;
        StringEntity se = new StringEntity(xml, HTTP.UTF_8);
        se.setContentType("text/xml");
        httpPost.addHeader(SOAP_ACTION, dummy.URL_AVERIA);

        httpPost.setEntity(se);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity resEntity = httpResponse.getEntity();
        response = EntityUtils.toString(resEntity);
        return response;
    }



    public static String getLocation(String LONGITUDE, String LATITUDE, String IMEI) throws Exception {
        final String SOAP_ACTION = "urn:Configurationwsdl#request";
        String response= null;
        String xml = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = new Date();

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(dummy.URL_SITES);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.dotNet = false;
        envelope.implicitTypes = true;

        String bodyOut =
                "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Configurationwsdl\">" +
                "<soapenv:Header/>" +
                "<soapenv:Body>" +
                "<urn:request soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                "<Mobile xsi:type=\"urn:Mobile\">" +
                "<Request xsi:type=\"urn:Request\">" +
                "<Parameters xsi:type=\"urn:Parameters\">" +
                "<Parameter xsi:type=\"urn:Parameter\">" +
                "<Name xsi:type=\"xsd:string\">LATITUDE</Name>" +
                "<Value xsi:type=\"xsd:string\">"+
                        LATITUDE+
                "</Value>" +
                "</Parameter>" +
                "<Parameter xsi:type=\"urn:Parameter\">" +
                "<Name xsi:type=\"xsd:string\">LONGITUDE</Name>" +
                "<Value xsi:type=\"xsd:string\">"+
                        LONGITUDE+
                "</Value>" +
                "</Parameter>" +
                "</Parameters>" +
                "<Header xsi:type=\"urn:Header\">" +
                "<Date xsi:type=\"xsd:string\">"+
                        formatter.format(fecha)+
                "</Date>" +
                "<Platafform xsi:type=\"xsd:string\">MOBILE</Platafform>" +
                "<User xsi:type=\"xsd:string\">"+IMEI+"</User>" +
                "</Header>" +
                "<OperationType xsi:type=\"xsd:string\">LOCATION</OperationType>" +
                "<Element xsi:type=\"xsd:string\">MAP</Element>" +
                "</Request>" +
                "</Mobile>" +
                "</urn:request>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";
        xml = bodyOut;
        StringEntity se = new StringEntity(xml, HTTP.UTF_8);
        se.setContentType("text/xml");
        httpPost.addHeader(SOAP_ACTION, dummy.URL_SITES);

        httpPost.setEntity(se);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity resEntity = httpResponse.getEntity();
        response = EntityUtils.toString(resEntity);
        return response;
    }

    public static String FormClosing(String IMEI, int IdMaintenance) throws Exception {
        final String SOAP_ACTION = "";//"urn:Configurationwsdl#request";
        String response= null;
        String xml = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = new Date();
        formatter.format(fecha);

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(dummy.URL_FORM_CLOSE);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.dotNet = false;
        envelope.implicitTypes = true;

        String bodyOut =
                "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Configurationwsdl\">" +
                        "<soapenv:Header/>" +
                        "<soapenv:Body>" +
                        "<urn:request soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                        "<FormPrev xsi:type=\"urn:FormPrev\">" +
                        "<Request xsi:type=\"urn:Request\">" +
                        "<!--Optional:-->" +
                        "<Form_Detail xsi:type=\"urn:Form_Detail\">" +
                        "<!--Zero or more repetitions:-->" +
                        "<Parameters xsi:type=\"urn:Parameters\">" +
                        "<!--Zero or more repetitions:-->" +
                        "<Parameter xsi:type=\"urn:Parameter\">" +
                        "<Name xsi:type=\"xsd:string\">EQUIPOID</Name>" +
                        "<Value xsi:type=\"xsd:string\">1</Value>" +
                        "</Parameter>" +
                        "</Parameters>" +
                        "</Form_Detail>" +
                        "<!--Optional:-->" +
                        "<Header xsi:type=\"urn:Header\">" +
                        "<Date xsi:type=\"xsd:string\">formatter.format(fecha)</Date>" +
                        "<Platafform xsi:type=\"xsd:string\">MOBILE</Platafform>" +
                        "<Imei xsi:type=\"xsd:string\">"+IMEI+"</Imei>" +
                        "</Header>" +
                        "<!--Optional:-->" +
                        "<Form_Header xsi:type=\"urn:Form_Header\">" +
                        "<MaintenanceId xsi:type=\"xsd:string\">"+IdMaintenance+"</MaintenanceId>" +
                        "</Form_Header>" +
                        "<OperationType xsi:type=\"xsd:string\">INSERT</OperationType>" +
                        "<Element xsi:type=\"xsd:string\">FORM_PREVENTIVE</Element>" +
                        "</Request>" +
                        "</FormPrev>" +
                        "</urn:request>" +
                        "</soapenv:Body>" +
                        "</soapenv:Envelope>";
        xml = bodyOut;
        StringEntity se = new StringEntity(xml, HTTP.UTF_8);
        se.setContentType("text/xml");
        httpPost.addHeader(SOAP_ACTION, dummy.URL_FORM_CLOSE);

        httpPost.setEntity(se);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity resEntity = httpResponse.getEntity();
        response = EntityUtils.toString(resEntity);
        return response;
    }

}

