package com.mobi.stealth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FileUploadTestingActivity extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button btn = (Button) findViewById(R.id.upload);
        
        btn.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		Intent serv = new Intent(this, UploadService.class);
		startService(serv);
		
	}
}