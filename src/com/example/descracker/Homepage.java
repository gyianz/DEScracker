package com.example.descracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.JsonReader;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Homepage extends Activity implements OnClickListener {
	//public static TextView v;
	
	private static HashSet<String> Dictionary = new HashSet<String>();
	private static ProgressDialog dialog;
	private static double fileLines = 234937;
	private static double counter = 1;
	private static boolean finishedDict = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homepage);

		Button decrypt = (Button) findViewById(R.id.decrypt);
		Button getText = (Button) findViewById(R.id.cipherGet);
		decrypt.setOnClickListener(this);
		getText.setOnClickListener(this);
		new buildDict().execute();
		
	}
		
	
	public void onClick (View v){

		switch (v.getId()){
		
		case R.id.decrypt:
			new decrypt().execute();
			break;
		
		case R.id.cipherGet:
			try {
				getText(v);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
			
		default:
			break;
		}
	}
	
	
	private class buildDict extends AsyncTask<String,String,String>{

		@Override
		protected String doInBackground(String... arg0) {
			try{
			BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("dictionary-english")));
			String check;
			
			publishProgress("Building dictionary");
			

			while((check = br.readLine()) != null){
				//System.out.println((counter/fileLines)*100);
				//System.out.println(counter+"*****"+(Math.round(counter/fileLines)*100));
				Dictionary.add(check);
				counter++;
			}
			
				br.close();
			
		}catch(Exception e){
			// Do something here
		}
			finishedDict = true;
			return null;
		}
		
		protected void onProgressUpdate(String... progress){
			dialog.setMessage(progress[0]);
		}
		
	}
	
	// first read file then extract information out of json and then decrypts it
	public String[] readJsonStream() throws IOException {
		AssetManager assetManager = getAssets(); // gets access to the assets
															// folder
		InputStream input = assetManager.open("json_block56"); // reads the json file from assets folder
				
		Reader test = new InputStreamReader(input);
		JsonReader reader = new JsonReader(test);
	
		@SuppressWarnings("unused")
		String found = "";
		String[] jsoninfo;
				
		try {
			//extract information
			jsoninfo = readMessage(reader);
				
		}finally {
			reader.close();
		}
			return jsoninfo;
		}
		
		//extracts the JSON information and put it in an string array
	public String[] readMessage(JsonReader reader) throws IOException {
		String[] result = new String[3];
		@SuppressWarnings("unused")
		String reply = "";
		String type = "";
		String key = "";
		String ciphertext = "";
		reader.beginObject();
			
		while (reader.hasNext()) {
			String name = reader.nextName();
	
			if (name.equals("reply")) {
				reader.beginObject();
				while (reader.hasNext()) {
					String name2 = reader.nextName();
					
					if (name2.equals("type")) {
						type = reader.nextString();
						
					} else if (name2.equals("key")) {
						key = reader.nextString();
						
					} else if (name2.equals("ciphertext")) {							
						ciphertext = reader.nextString();
						
					} else {
						reader.skipValue();
					}
				}
				reader.endObject();
			} else {
				reader.skipValue();
			}
		}
		
		reader.endObject();
		result[0] = type;
		result[1] = key;
		result[2] = ciphertext;
	
		return result;
	}
	
	public void getText(View v) throws IOException{
		String[] jsonObj = readJsonStream();
		String key = jsonObj[1];
		String ciphertext = jsonObj[2];
		
		TextView keyResult = (TextView)findViewById(R.id.key);
		keyResult.setText(key);
		
		TextView cipherResult = (TextView)findViewById(R.id.cipherText);
		cipherResult.setText(ciphertext);
		
		
	}
	
	
	private String buildDict(String param) throws IOException{
		
			
		String finalResult = null;
		float percentageOfCorrectness = DictionaryCheckHash.dictCheck(param, Dictionary);
				
		String temp = "The percentage matches with english words: ";
		finalResult = temp.concat(String.valueOf(percentageOfCorrectness));	


		return finalResult;
		
	}

	
	public void paddingBit(View v) throws UnsupportedEncodingException{
		TextView editText = (TextView) findViewById(R.id.key);
		String key = editText.getText().toString();
		byte[] bytekey = key.getBytes();
		byte[] decodedKey = Base64.decode(bytekey, Base64.DEFAULT);
		String binary = null;
		
		for(int i = 0; i < decodedKey.length; i++){
			String temp = Integer.toBinaryString( (int) decodedKey[i]);
			if(binary == null){
				binary = temp;
				
			}else{
				binary = binary.concat(temp);
				
			}
		}
		int init = 0;
		
		for(int i = 0; i < ((int)Math.pow(2, 16) ); i++){
		
			String mystr = String.format("%16s", Integer.toBinaryString(init)).replace(' ', '0');
			init = init + 1;
			String test = binary.concat(mystr);
		}
	
	}
	
	
	
	private class decrypt extends AsyncTask<String, String, String>{
		
		TextView editText = (TextView) findViewById(R.id.cipherText);
		TextView editText2 = (TextView) findViewById(R.id.key);
		//private ProgressDialog pDialog;
		String ciphertext;
		String key;
		String percentage = null;
		
		
	protected void onPreExecute(){
		
		 ciphertext = editText.getText().toString();
		 key = editText2.getText().toString();
		 dialog = new ProgressDialog(Homepage.this);            
         dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
         dialog.setMax(100);
         dialog.setMessage("Decrypting. Please Wait...");
         dialog.setCancelable(true);
         dialog.show();              
	}
		/*	pDialog = new ProgressDialog(Homepage.this);
			pDialog.setTitle("Loading.");
    		pDialog.setMessage("Please Wait...");
    		
    	
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
          */  
    
	
		
	@Override
	protected String doInBackground(String... params) {
		
		String returnValue = null;
		try {
			
			String temp = runDecrypt();
			while(DES.progressTick != -99 && DES.progressTotal != -99 ){
				dialog.setProgress((int) Math.round(DES.progressTick *100 / DES.progressTotal));
			}
			
			returnValue = temp;
			
			publishProgress("Building dictionary");
			
			while(finishedDict == false){
				dialog.setProgress((int) (Math.round(counter*100/fileLines)));
			}
			
		    percentage = buildDict(returnValue);

		    
			publishProgress("Checking validity");
			while(DictionaryCheckHash.progressTick != -99 && DictionaryCheckHash.progressTotal != -99){
				
				dialog.setProgress((int) Math.floor(DictionaryCheckHash.progressTick / DictionaryCheckHash.progressTotal *100));
			}

			dialog.setProgress(100);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return returnValue;
	}
	
	@Override
	   protected void onProgressUpdate(String... progress) {
		 //setProgress(progress[0]);
		 dialog.setMessage(progress[0]);
	   }
	@Override
	protected void onPostExecute(String finResult){
	//	pDialog.setMessage("Done");
	//	pDialog.dismiss();
		dialog.setMessage("Done");
		dialog.dismiss();
		
		TextView FinalResult = (TextView)findViewById(R.id.decryptText);
		FinalResult.setMovementMethod(new ScrollingMovementMethod());
		FinalResult.setText(finResult);
		
		TextView resultView = (TextView) findViewById(R.id.Percentage);
		resultView.setText(percentage);
		
	}
	
	public String runDecrypt() throws IOException{	
		
		String returnValue = null;
		try{
			
			byte[] cipherByte = DES.parseBytes(ciphertext);
			String decKey = DES.convertStringToHex(key);
			 
			byte[] keyByte = DES.parseBytes(decKey);
			String decryptResult = DES.hex(DES.decryptCBC(cipherByte, keyByte));
			 
			String temp = decryptResult.replace(" ", "");
			String finalDecrypt = temp.substring(0, (temp.length() - DES.getConcatCount()));
			String finResult  = DES.convertHexToString(finalDecrypt);
			 
			
			returnValue = finResult;
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return returnValue;
		
	}
	
	
	
	
	}
}
	


