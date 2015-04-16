package cl.tdc.felipe.tdc.webservice;

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

public class SoapRequest {
	
	private static	
	final String URL = "https://pcba.telefonicachile.cl/smartphone/ws/sca.php";
	
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
	
	/*
	 * XML-001: Seleccionar Cliente
	 */
	 
	public static String getCustomer(String phone, String IMEI, String IMSI) throws Exception {
		final String SOAP_ACTION = "urn:Demo#Customer";
	    String response= null;
	    String xml = null;
	    DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	    Date fecha = new Date();

	    HttpClient httpClient = getNewHttpClient();
	    HttpPost httpPost = new HttpPost(URL);

	    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	    envelope.encodingStyle = SoapSerializationEnvelope.ENC;
	    envelope.dotNet = false;		
		envelope.implicitTypes = true;
		
	    String bodyOut = 
	    		"<?xml version=\"1.0\" encoding=\"utf-8\"?><soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Demo\">" +
				"<soapenv:Header/>" +
				"<soapenv:Body>" +
				 "<urn:Customer soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
				  	"<RequestCustomer xsi:type=\"urn:RequestCustomer\">" +
				  	"<Operation xsi:type=\"urn:OperationType\">" +
				    "<OperationCode xsi:type=\"xsd:string\">XML-001</OperationCode>" +
				     "<OperationId xsi:type=\"xsd:string\">"+ formatter.format(fecha).toString() +"</OperationId>" +
				      "<!--Optional:-->" +
				       "<DateTime xsi:type=\"xsd:string\">"+formatter.format(fecha).toString()+"</DateTime>" +
				        "<!--Optional:-->" +
				         "<IdUser xsi:type=\"xsd:string\">1</IdUser>" +
				          "<IMEI xsi:type=\"xsd:string\">"+IMEI+"</IMEI>" +
				           "<IMSI xsi:type=\"xsd:string\">"+IMSI+"</IMSI>" +
				         "</Operation>" +
				         "<Service xsi:type=\"urn:ServiceCustomerIn\">" +
				            "<Customer xsi:type=\"urn:CustomerIn\">" +
				               "<Input xsi:type=\"urn:CustomerInData\">" +
				                  "<Phone xsi:type=\"xsd:string\">" + phone + "</Phone>" +
				               "</Input>" +
				            "</Customer>" +
				         "</Service>" +
				      "</RequestCustomer>" +
				   "</urn:Customer>" +
				"</soapenv:Body>" +
				"</soapenv:Envelope>";
	    xml = bodyOut;
	    StringEntity se = new StringEntity(xml, HTTP.UTF_8);
	    se.setContentType("text/xml");	    
	    httpPost.addHeader(SOAP_ACTION, URL);		
	    
	    httpPost.setEntity(se);
	    HttpResponse httpResponse = httpClient.execute(httpPost);
	    HttpEntity resEntity = httpResponse.getEntity();	    
	    response = EntityUtils.toString(resEntity);
	    return response;
	}

}

