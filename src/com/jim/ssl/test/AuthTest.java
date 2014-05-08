package com.jim.ssl.test;

import java.awt.Desktop;
import java.net.URI;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * A factory for creating WildCardHttpData objects.
 */
public class AuthTest {
	
	private static void log(String msg){
		System.out.println(msg);
	}
	
	public static void main(String[] args)  throws Exception {
		AuthTest myTest = new AuthTest();
		
		if( args.length<3 ){
			showUsage();
		}
		
		String data = myTest.getData( args[0], args[1], args[2]);
		System.out.println( data );
		if( data.indexOf("token_type") > -1 ){
			String token = data.substring( 17, data.indexOf( "token_type") - 3);
			
			int start = data.indexOf("redirect_url" )+ "redirect_url\":\"".length();
			String URL = data.substring( start, data.indexOf( "\"", start) );
			URL="http://localhost:8080/wuprepaid/mtInit.i2c";
			System.out.println( URL + "?authtoken=" + token  );
			if( args.length >= 4 && "open".equalsIgnoreCase(args[3]) ) {
				if(Desktop.isDesktopSupported())
				{
				  Desktop.getDesktop().browse(new URI(URL + "?authtoken=" + token));
				}	
			}
		}
	}
	
	private static void showUsage() {
		System.out.println();
		System.out.println(" ******************************************************************** ");
		System.out.println(" The application expects at least 3 parameters and a 4th optional one ");
		System.out.println("    1) client ID ");
		System.out.println("         This is the client ID that you want to initiate the Authentication Token request ");
		System.out.println("    2) sendmoney | receivemoney ");
		System.out.println("         Send Money launches into the Send MTCN flow ");
		System.out.println("         Receive Money launches into the load MTCN flow ");
		System.out.println("    3) cardnumber ");
		System.out.println("         The card number that you want to complete the operations against ");
		System.out.println("Optional: ");
		System.out.println("    4) open ");
		System.out.println("         Indicates to open the browser upon successful authentication request ");
		
		System.exit(1);
		
	}

	public String getData(String who, String action, String cardNumber ) {
		
		HttpClient myClient = new HttpClient();
		//configureProxy(myClient);
		
		PostMethod method=null;
		String uri ="";
//		uri = "http://10.200.61.148:9080/authengine/oauth/token";
//		uri = "http://localhost:8080/authengine/oauth/token";
		uri = "http://wu.vmlstage.com/authengine/oauth/token";
//		uri = "https://prepaidauth.westernunion.net/authengine/oauth/token";
		
		String u = "jimtest";
		String p = "7ah7sgq2kl2oncjea4jimfm30gu4urb47v20rli5hqvo29klil";
		try {
			method = buildMethodParameters( uri, u, p, action, cardNumber);
			
			log( "URL: ["+ uri +"]");
			long start = System.currentTimeMillis();
			try{
				myClient.executeMethod( method );
			} finally {
				log( "FNIS - dur=[" + (System.currentTimeMillis()-start) /1000.0 + "]" );
			}
			return method.getResponseBodyAsString();
		} catch (Throwable t) {
			log( t.getMessage() );
			throw new RuntimeException (t);
		}  finally{
			if( method != null )
				method.releaseConnection();
		}
	}
	
	private PostMethod buildMethodParameters(String uri, String u, String p, String action, String cardNumber) {
		PostMethod method = new PostMethod( uri );
		method.addParameter("grant_type","client_credentials");
		method.addParameter("client_id", u );
		method.addParameter("client_secret", p );
		method.addParameter("app","wuprepaid");
		method.addParameter("app_version","2");
		
		
		method.addParameter("transaction_type", action );
		method.addParameter("card_information", cardNumber);
		
		method.addParameter("success_callback","http://wu.vmlstage.com/noxworp/harness/success.do");
		method.addParameter("failure_callback","http://wu.vmlstage.com/noxworp/harness/failure.do");
		
		method.addParameter("user_id","anothertestuser");
		
		return method;
	}

	/**
	 * Configure proxy.
	 *
	 * @param myClient the my client
	 */
	private void configureProxy(HttpClient myClient) {
		log( "proxy values [" + System.getProperty( "https.proxyHost") +":"+System.getProperty("https.proxyPort") +"]");

		// Pass in these values as -D options to the JVM.  If they are present, then they will be set on the client
//		if( System.getProperty( "https.proxyHost") != null &&  System.getProperty("https.proxyPort") != null ){
			String pHost = "127.0.0.1";
			int pPort = 8080;
			myClient.getHostConfiguration().setProxy( pHost, pPort );
//		}
	}

}
