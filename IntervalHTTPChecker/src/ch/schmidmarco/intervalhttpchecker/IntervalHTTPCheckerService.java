package ch.schmidmarco.intervalhttpchecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class IntervalHTTPCheckerService extends Service
{
	public static final String LOG_TAG = "IntervalHTTPCheckerService";
	private NotificationManager nm_;
	private final IBinder binder_ = new LocalBinder();
	private String last_refresh_id_ = "empty";
	private Handler handler_ = new Handler();
	private Runnable runnable_ = new Runnable()
	{
		public void run() 
		{
			UpdateNewsStatus();

	        SharedPreferences settings = getSharedPreferences(IntervalHTTPChecker.PREFS_NAME, 0);
	        Integer value = settings.getInt("timer_setting", 10000);
			handler_.postDelayed(this, value);
		}
	};
	
	public class LocalBinder extends Binder
	{
		IntervalHTTPCheckerService getService()
		{
			return IntervalHTTPCheckerService.this;
		}
	}
	
	@Override
	public IBinder onBind(Intent arg0)
	{
		return binder_;
	}
	
	@Override
	public void onCreate()
	{
		nm_ = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		
		Toast.makeText(this, "Started service", Toast.LENGTH_SHORT).show();
		runnable_.run();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.i(LOG_TAG, "Received start id " + startId + ": " + intent);
		return START_STICKY;
	}
	
	@Override
	public void onDestroy()
	{
		Toast.makeText(this, "Stopped service", Toast.LENGTH_SHORT).show();
	}
	
	private void UpdateNewsStatus ()
	{
		String http_data = CheckHTTPUrl();
		if (http_data != "empty")
		{
			if (!http_data.equals(last_refresh_id_))
			{
				ShowNotification(getText(R.string.service_notification_text_new_message));
				last_refresh_id_ = http_data;
			}
		}
	}
	
	private void ShowNotification(CharSequence message)
	{
		CharSequence title = getText(R.string.service_notification_title);
		Notification notification = new Notification(R.drawable.icon, message, System.currentTimeMillis());
		SharedPreferences settings = getSharedPreferences(IntervalHTTPChecker.PREFS_NAME, 0);
		String url = settings.getString("notification_url", "http://google.com");
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		PendingIntent contentIntent = PendingIntent.getActivity(this,0,i,0);
		
		notification.setLatestEventInfo(this, title, message, contentIntent);		
		nm_.notify(R.string.service_notification_title, notification);
	}
	
	private String CheckHTTPUrl()
	{
		HttpClient httpclient = new DefaultHttpClient();
		SharedPreferences settings = getSharedPreferences(IntervalHTTPChecker.PREFS_NAME, 0);
		String req_url = settings.getString("request_url", "empty");
		HttpPost httppost = new HttpPost(req_url);
		
		if (req_url == "empty")
			return "empty";
		
		try
		{
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			//TODO implement post requests in GUI
			nameValuePairs.add(new BasicNameValuePair("nothing1", "1"));
			nameValuePairs.add(new BasicNameValuePair("nothing2", "1"));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			return GetHttpReceivedMessage(response);
		}
		catch (ClientProtocolException e)
		{
			// TODO Auto-generated catch block
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
		}  
		return "empty";
	}
	
	private static String GetHttpReceivedMessage(HttpResponse response)
	{
		String result = "";
		try
		{
		    InputStream in = response.getEntity().getContent();
		    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		    StringBuilder str = new StringBuilder();
		    String line = null;
		    while((line = reader.readLine()) != null){
		        str.append(line + "\n");
		    }
		    in.close();
		    result = str.toString();
		}
		catch(Exception ex)
		{
		    result = "empty";
		}
		return result;
	}
}
