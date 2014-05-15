package it.sii.mywaiter;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.util.Log;
import android.widget.Toast;
import android.widget.TextView;

public class NfcActivity extends Activity {
	
	public static final String MIME_TEXT_PLAIN = "text/plain";
	public static final String TAG = "NfcDemo";
	 
    private TextView mTextView;
    private NfcAdapter mNfcAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nfc_layout);
		mTextView = (TextView) findViewById(R.id.textView_explanation);
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		
		if (mNfcAdapter == null) {
            // controlla che l'nfc sia presente (forse ridondante)
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
     
        if (!mNfcAdapter.isEnabled()) {
        	//controlla che l'nfc sia attivo
            mTextView.setText(R.string.nfc_disabled);
            NfcsDialogFragment dialog = new NfcsDialogFragment();
            FragmentManager fm = getFragmentManager();
            dialog.show(fm, "frag_name");
        } else {
            mTextView.setText(R.string.nfc_active);
        }
        handleIntent(getIntent());
    }
	
	public class NfcsDialogFragment extends DialogFragment {
		//crea il dialogfragment
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage(R.string.nfc_dialog_absent)
	        	   .setTitle(R.string.nfc_dialog_title)
	               .setPositiveButton(R.string.nfc_dialog_enable, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
	                   }
	               })
	               .setNegativeButton(R.string.nfc_dialog_noenable, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   Intent intent = new Intent(NfcActivity.this,
	       						OrdinaActivity.class);
	       					startActivity(intent);
	                   }
	               });
	        return builder.create();
	    }
	}
	
	@Override
    protected void onPause() {
		NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		nfcAdapter.disableForegroundDispatch(this);
        super.onPause();
    }
	
	@Override
	protected void onResume(){
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
		super.onResume();
	}
     
    @Override
    protected void onNewIntent(Intent intent) { 
        handleIntent(intent);
    }
     
    private void handleIntent(Intent intent) {
    	String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
             
            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {
     
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);
                 
            } 
            else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        }
    }
     
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
 
        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);
 
        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};
 
        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }
         
        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }
 
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }
    
    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {
     
        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];
             
            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag. 
                return null;
            }
     
            NdefMessage ndefMessage = ndef.getCachedNdefMessage();
     
            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }
     
            return null;
        }
         
        private String readText(NdefRecord record) throws UnsupportedEncodingException {
            byte[] payload = record.getPayload();
     
            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
     
            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;
             
            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"
             
            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }
         
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                mTextView.setText("Read content: " + result);
                //finish();
            }
        }
    }
}