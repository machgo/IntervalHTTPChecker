package ch.schmidmarco.intervalhttpchecker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class IntervalHTTPChecker extends Activity implements OnClickListener
{
	public static final String LOG_TAG = "IntervalHTTPChecker";
	public static final String PREFS_NAME = "IntervalHTTPChecker";
	
	private Button buttonStart, buttonStop, buttonSaveSettings;
	private EditText editTextTimerSetting, editTextRequestUrl, editTextNotificationUrl;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
    	editTextRequestUrl = (EditText)findViewById(R.id.EditTextRequestUrl);
    	editTextNotificationUrl = (EditText)findViewById(R.id.EditTextNotificationUrl);
    	editTextTimerSetting = (EditText)findViewById(R.id.EditTextTimerSetting);
    		
		buttonStart = (Button) findViewById(R.id.ButtonStart);
		buttonStop = (Button) findViewById(R.id.ButtonStop);
		buttonSaveSettings = (Button) findViewById(R.id.ButtonSaveSettings);
		buttonStart.setOnClickListener(this);
		buttonStop.setOnClickListener(this);
	    buttonSaveSettings.setOnClickListener(this);
		
	    LoadSettings();	
	}
	
	private void SaveSettings()
	{    	  	
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("timer_setting", new Integer(editTextTimerSetting.getText().toString()));
        editor.putString("request_url", editTextRequestUrl.getText().toString());
        editor.putString("notification_url", editTextNotificationUrl.getText().toString());
        
        // Commit the edits!
        editor.commit(); 
	}
	
    private void LoadSettings()
    {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        
        Integer value = settings.getInt("timer_setting", 10000);
        editTextTimerSetting.setText(value.toString());
        editTextRequestUrl.setText(settings.getString("request_url", "http://url/file.php"));
        editTextNotificationUrl.setText(settings.getString("notification_url", "http://url/file.php"));
    }
    
    public void onClick(View src)
    {
    	switch (src.getId())
		{
		case R.id.ButtonStart:
			Log.d(LOG_TAG, "onClick: ButtonStart");
			SaveSettings();
			startService(new Intent(this, IntervalHTTPCheckerService.class));
			break;
			
		case R.id.ButtonStop:
			Log.d(LOG_TAG, "onClick: ButtonStop");
			stopService(new Intent(this, IntervalHTTPCheckerService.class));
			break;
			
		case R.id.ButtonSaveSettings:
			Log.d(LOG_TAG, "onClick: ButtonSaveSettings");
			SaveSettings();
			break;

		default:
			break;
		}
    }
    
    
}