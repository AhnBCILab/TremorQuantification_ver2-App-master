package com.ahnbcilab.tremorquantification.functions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ahnbcilab.tremorquantification.data.Complex;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


public class main {

	private static fft ft;
	private static LineTaskAnalyze lineTaskAnalyze;
	private static FillNull fn ;
	private static fitting fg;
	private static double[] Result;
	private static final int srate = 50;
	private static int count = 0;
	static String Clinic_ID;

	private static FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
	//private static DatabaseReference firebaseSprial = firebaseDatabase.getReference("Spiral RowData");
	public static int spiral_count;
	private static String sspiral_count;
	static File file;
	static String m;
	static Context ctx;


	public static double[] main(String args, Context context, String id, String data_path) throws IOException {
		ctx = context;
		Clinic_ID = id;
		// TODO Auto-generated method stub
		double[] resultx = new double[4];      double[] resulty = new double[4];// result of fft
		double[][] fitting;//result of fitting
		double[][] fildata;// filled null point data




		/* read data - skip*/
		List<Double> orgX = new ArrayList<Double>();
		List<Double> orgY = new ArrayList<Double>();
		List<Double> time = new ArrayList<Double>();
		String csvFile = args;
		BufferedReader br = null;
		String line = "";
		file = new File(ctx.getFilesDir(), Clinic_ID + "SpiralRow_num.txt");
		//writeToFile("0", ctx);

		try {


			br = new BufferedReader(new FileReader(csvFile));
			br.readLine();
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",+");
				orgX.add(Double.parseDouble(data[0]));
				if(sspiral_count == null)
					sspiral_count = "00";
				orgY.add(Double.parseDouble(data[1]));
				time.add(Double.parseDouble(data[2]));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


		/* data setting - must do */
		int n = time.size();
		Complex[] x = new Complex[n];      Complex[] y = new Complex[n];

		//pre-processing

		fn = new FillNull();
		fildata = new double[2][n];
		fildata[0] = fn.FillNull(orgX,n);
		fildata[1] = fn.FillNull(orgY,n);


		for (int i = 0; i < n; i++) {
			x[i] = new Complex(fildata[0][i], 0);
			y[i] = new Complex(fildata[1][i], 0);
		}

		//separate data
		int start = 1;
		List<Integer> slice = new ArrayList<Integer>();
		Dataslice ds = new Dataslice();
		slice = ds.Dataslice(n);
		int m = slice.size();
		int totalL = 0;

		fitting = new double[m][2];
		fg = new fitting();

		//make position listarray to array
		double[] x_position = new double[n];
		double[] y_position = new double[n];
		double[] time_array = new double[n];
		int size = 0;
		for(double temp: orgX)
		{
			x_position[size++] = temp;
			if(temp == n)
				break;
		}
		size = 0;
		for(double temp: orgY)
		{
			y_position[size++] = temp;
			if(temp == n)
				break;
		}
		size = 0;
		for(double temp: time)
		{
			time_array[size++] = temp;
			if(temp == n)
				break;
		}

		Result=new double[5];
		Result = fg.fitting(x_position, y_position,time_array, n, true, data_path, Clinic_ID);

		for(int i = 0;i<5;i++){
			Result[i] = Math.round(Result[i]*1000)/1000.0;
			Log.d("test2","result:" +i+" "+ Result[i] );
		}


		//Result [0]: TM , [1]: TF , [2]:time , [3]: ED , [4]:velocity
		return Result;




	}

	// write File
	private static void writeToFile(String data, Context context) {
		try {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(Clinic_ID + "SpiralRow_num.txt", Context.MODE_PRIVATE));
			outputStreamWriter.write(data);
			outputStreamWriter.close();
		}
		catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}



	// read File
	private static String readFromFile(Context context) {
		String ret = "";
		try {
			InputStream inputStream = context.openFileInput(Clinic_ID + "SpiralRow_num.txt");
			if ( inputStream != null ) {
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();

				while ( (receiveString = bufferedReader.readLine()) != null ) {
					stringBuilder.append(receiveString);
				}

				inputStream.close();
				ret = stringBuilder.toString();
			}
		}
		catch (FileNotFoundException e) {
			Log.e("login activity", "File not found: " + e.toString());
		} catch (IOException e) {
			Log.e("login activity", "Can not read file: " + e.toString());
		}
		return ret;
	}

}