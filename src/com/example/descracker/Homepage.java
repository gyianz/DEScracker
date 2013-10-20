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
		//new buildDict().execute();
		
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
	
	public static int integerfrmbinary(String str){
	    double j=0;
	    for(int i=0;i<str.length();i++){
	        if(str.charAt(i)== '1'){
	        	j=j+ Math.pow(2,str.length()-1-i);
	        }
	    }
	    return (int) j;
	}
	
	
	private float buildDict(String param) throws IOException{
		
		float percentageOfCorrectness = DictionaryCheckHash.dictCheck(param, Dictionary);
	
		return percentageOfCorrectness;
		
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
			 
			 try {
				byte[] cipher64 = ciphertext.getBytes("UTF-8");
				ciphertext = Base64.encodeToString(cipher64, Base64.DEFAULT);
				byte[] key64 = key.getBytes("UTF-8");
				key = Base64.encodeToString(key64, Base64.DEFAULT);
						
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			 
			 
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
			float bestPercentage = 0;
			String returnValue = null;
			try {
				publishProgress("Building Dictionary");
				
				if(finishedDict == false){
					BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("dictionary-english")));
					String check;
					
					while((check = br.readLine()) != null){
						dialog.setProgress((int) (Math.round(counter*100/fileLines)));
						Dictionary.add(check);
						counter++;
						//TESTING
					}
					
					br.close();
					finishedDict = true;
				}
				
				// decode the ciphertext and key
				byte[] decodedKey = Base64.decode(key, Base64.DEFAULT);
				byte[] decodedCiphertext = Base64.decode(ciphertext, Base64.DEFAULT);
				
			
				// convert the key into binary for padding
				String bin = null;
				
				for(byte b : decodedKey){
					
					if(bin == null){
						bin = Integer.toBinaryString(b);
					}else{
						bin = bin.concat(Integer.toBinaryString(b));
					}
				}
				
				// get the total times of padding
				int paddingCount = 64 - bin.length();
				
				// Check how many times to pad and generate a string to pass into string format, eg: %16s
				String tempString = "%";
				tempString = tempString.concat(String.valueOf(paddingCount));
				tempString = tempString.concat("s");
				
				int init = 0;
				
				publishProgress("Decrypting");
				System.out.println(Math.pow(2, paddingCount));
				
				for(int i = 0; i < ((int)Math.pow(2, paddingCount)); i++){
					
					dialog.setProgress( i*100/(int) Math.pow(2,paddingCount));
					
					String paddingCell = String.format(tempString, Integer.toBinaryString(init)).replace(' ', '0');
					
					// Concat the padding cell to generate a 64 bits key
					String finalKey = bin.concat(paddingCell);
					 assert finalKey.length() % 4 == 0;
				        String[] finalArray = new String[finalKey.length()/4];
				        for (int index = 0; index < finalArray.length; index++)
				            finalArray[index] = finalKey.substring(index*4,index*4 + 4);
					String hexaKey ="";
					for(String fa : finalArray){
						hexaKey.concat(Integer.toHexString(integerfrmbinary(fa)));
					}
					
					System.out.println(hexaKey);
					//finalKey = Double.toHexString(pleaseWork);
					
					byte[] finalKeyByte = DES.parseBytes(finalKey);
					
					// Run the decrypt and check with dictionary
					String temp = runDecrypt(decodedCiphertext, finalKeyByte);
					
					float tempPercent = buildDict(temp);
					
					//get the best percentage of matching and assign the highest correct rate text into return value
					if(tempPercent > bestPercentage){
						bestPercentage = tempPercent;
						returnValue = temp;
					}
					
					// counter of padding
					init = init + 1;
				}
				
				percentage = "The percentage matches with english words: ";
				percentage = percentage.concat(String.valueOf(bestPercentage));
				
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
	
		public String runDecrypt(byte[] cipherByte, byte[] keyByte) throws IOException{	
			
			String returnValue = null;
			try{
				
				//byte[] cipherByte = DES.parseBytes(ciphertext);
				//String decKey = DES.convertStringToHex(key);
				 
				//byte[] keyByte = DES.parseBytes(decKey);
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
	


