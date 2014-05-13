package it.sii.mywaiter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class QrActivity extends Activity implements OnClickListener{

	public int SCANNER_REQUEST_CODE = 123;
	 
    TextView tvScanResults;
    Button btnScan;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_layout);
 
        initViews();
    }
 
    private void initViews() {
        tvScanResults = (TextView) findViewById(R.id.qr_textview);
        btnScan = (Button) findViewById(R.id.qr_button);
        btnScan.setOnClickListener(this);
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
 
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
 
        if (requestCode == SCANNER_REQUEST_CODE) {
            // Handle scan intent
            if (resultCode == Activity.RESULT_OK) {
                // Handle successful scan
                String contents = intent.getStringExtra("SCAN_RESULT");
                String formatName = intent.getStringExtra("SCAN_RESULT_FORMAT");
                byte[] rawBytes = intent.getByteArrayExtra("SCAN_RESULT_BYTES");
                int intentOrientation = intent.getIntExtra("SCAN_RESULT_ORIENTATION", Integer.MIN_VALUE);
                Integer orientation = (intentOrientation == Integer.MIN_VALUE) ? null : intentOrientation;
                String errorCorrectionLevel = intent.getStringExtra("SCAN_RESULT_ERROR_CORRECTION_LEVEL");
 
                tvScanResults.setText(contents + "\n\n" + formatName);
 
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Handle cancel
            }
        } else {
            // Handle other intents
        }
 
    }
 
    public void onClick(View v) {
        if (v.getId() == R.id.qr_button) {
            // go to fullscreen scan
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "SCAN_MODE");
            startActivityForResult(intent, SCANNER_REQUEST_CODE);
        }
    }
}


