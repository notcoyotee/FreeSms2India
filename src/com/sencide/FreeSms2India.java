package com.sencide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sencide.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FreeSms2India extends Activity {

	private final String NAMESPACE = "http://www.webserviceX.NET/";
	private final String URL = "http://www.webservicex.net/SendSMS.asmx";
	private final String METHOD_NAME = "SendSMSToIndia";

	String Response_MobileNumber = "";
	String Response_EmailAddress = "";
	String Response_Message = "";
	String Response_Provider = "";
	String Response_State = "";
	String Response_Status = "";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		final TextView tv = (TextView) this.findViewById(R.id.textViewResult);

		final ProgressBar normalProgressBar;
		normalProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		normalProgressBar.setVisibility(8);

		final Button StrtButton = (Button) findViewById(R.id.button1);
		StrtButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
				EditText MobileNumber_txtbox = (EditText) findViewById(R.id.editText1);
				String MobileNumber = MobileNumber_txtbox.getText().toString();

				EditText email_txtbox = (EditText) findViewById(R.id.editText2);
				String FromEmailAddress = email_txtbox.getText().toString();

				EditText Message_txtbox = (EditText) findViewById(R.id.editText3);
				String Message = Message_txtbox.getText().toString();

				Message = Message.replaceAll(" ", "");

				PropertyInfo MobileNumberProp = new PropertyInfo();
				MobileNumberProp.setName("MobileNumber");
				MobileNumberProp.setValue(MobileNumber);
				MobileNumberProp.setType(String.class);
				request.addProperty(MobileNumberProp);

				PropertyInfo fromEmailProp = new PropertyInfo();
				fromEmailProp.setName("FromEmailAddress");
				fromEmailProp.setValue(FromEmailAddress);
				fromEmailProp.setType(String.class);
				request.addProperty(fromEmailProp);

				PropertyInfo MessageProp = new PropertyInfo();
				MessageProp.setName("Message");
				MessageProp.setValue(Message);
				MessageProp.setType(String.class);
				request.addProperty(MessageProp);

				request.addProperty(MobileNumber, MobileNumberProp);
				request.addProperty(Message, MessageProp);
				request.addProperty(FromEmailAddress, fromEmailProp);

				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.dotNet = true;
				envelope.setOutputSoapObject(request);
				new HttpTransportSE(URL);

				// 0 - VISIBLE; 4 - INVISIBLE; 8 - GONE
				normalProgressBar.setVisibility(0);

				InputStream content = null;
				HttpGet httpGet = new HttpGet(
						"http://www.webservicex.net/SendSMS.asmx/SendSMSToIndia?MobileNumber="
								+ MobileNumber + "&FromEmailAddress="
								+ FromEmailAddress + "&Message=" + Message);
				HttpClient httpclient = new DefaultHttpClient();
				// Execute HTTP Get Request
				HttpResponse response;
				try {
					response = httpclient.execute(httpGet);
					content = response.getEntity().getContent();

				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				BufferedReader rd = new BufferedReader(new InputStreamReader(
						content), 4096);
				String line;
				StringBuilder sb = new StringBuilder();
				try {
					while ((line = rd.readLine()) != null) {
						sb.append(line);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					rd.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String contentOfMyInputStream = sb.toString();
				tv.setText(contentOfMyInputStream);
				Log.d("GBC", "response :" + contentOfMyInputStream);

				Document doc = XMLfunctions
						.XMLfromString(contentOfMyInputStream);

				int numResults = XMLfunctions.numResults(doc);

				if ((numResults <= 0)) {
					Log.d("GBC", "Num Results :" + numResults);
					// finish();
				}

				NodeList nodes = doc.getElementsByTagName("SMSResult");

				for (int i = 0; i < nodes.getLength(); i++) {
					Element e = (Element) nodes.item(i);
					Response_MobileNumber = XMLfunctions.getValue(e,
							"MobileNumber");
					Response_EmailAddress = XMLfunctions.getValue(e,
							"FromEmailAddress");
					Response_Provider = XMLfunctions.getValue(e, "Provider");
					Response_State = XMLfunctions.getValue(e, "State");
					Response_Status = XMLfunctions.getValue(e, "Status");
					Log.d("GBC", "FromEmailAddress :" + Response_EmailAddress);
					Log.d("GBC", "MobileNumber :" + Response_MobileNumber);
					Log.d("GBC", "Provider :" + Response_Provider);
					Log.d("GBC", "State :" + Response_State);
					Log.d("GBC", "Status :" + Response_Status);
					if (Response_Provider == "Not Covered") {
						tv.setText("This is not a supported Network");
					} else {
						tv.setText("Sms sent to " + Response_Provider
								+ " number " + Response_MobileNumber);
					}
				}
				normalProgressBar.setVisibility(8);

			}
		});

	}
}