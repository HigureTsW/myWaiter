package it.sii.mywaiter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class OrdinaActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ordina_layout);
		
		Button qr = (Button) findViewById(R.id.ordinabutton1);
		qr.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(OrdinaActivity.this,
						MenuActivity.class);
				startActivity(intent);
			}
		});
		
		Button nfc = (Button) findViewById(R.id.ordinabutton2);
		nfc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(OrdinaActivity.this,
						NfcActivity.class);
				startActivity(intent);
			}
		});
	}

}