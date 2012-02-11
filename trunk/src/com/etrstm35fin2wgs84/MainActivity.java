package com.etrstm35fin2wgs84;  

import java.util.regex.Pattern;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/*
  ETRSTM35FIN coordinate conversion to WGS84
  Ported from CPAN package Matti Lattu: matti@lattu.biz http://search.cpan.org/~mplattu/Geo-Coordinates-ETRSTM35FIN-0.01/lib/Geo/Coordinates/ETRSTM35FIN.pm 
  Conversion formulas: Python module from Olli Lammi http://olammi.iki.fi/sw/fetch_map/
  
  http://code.google.com/hosting/
  
 License information:
  	ETRSTM35FIN coordinate conversion to WGS84 for Android
    Copyright (C) 2012  Timo Polvinen <timo.polvinen@iki.fi>
 
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 
 */

public class MainActivity extends Activity {
	
	private static String TAG="Etrstm35fintoWgs84";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
         
    }
    
    private void showMessage(String pMessage) {
    	AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
		alertDialog.setMessage(pMessage);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {
		 
		    	  dialog.dismiss();
		 
		    } }); 
		alertDialog.show();
    	
    }
    public void convert(View v) {	
    	
    	try {
    		double etrs_x;
    		double etrs_y;
    		
    		 
    		EditText x=(EditText) findViewById(R.id.x);
    		etrs_x = Double.valueOf(x.getText().toString());
    		
    		double ETRSTM35FIN_min_x=6582464.0358;
    		double ETRSTM35FIN_max_x=7799839.8902;
    		double ETRSTM35FIN_min_y=50199.4814;
    		double ETRSTM35FIN_max_y=761274.6247;
    		
    		// Check the values entered are ok
    		if (etrs_x<ETRSTM35FIN_min_x||etrs_x>ETRSTM35FIN_max_x) { // 
    			showMessage(String.format(getString(R.string.badXValue), ETRSTM35FIN_min_x,ETRSTM35FIN_max_x));
    			return;
    		}
    		
    		EditText y=(EditText) findViewById(R.id.y);
       		etrs_y = Double.valueOf(y.getText().toString());
       		
    		if (etrs_y<ETRSTM35FIN_min_y||etrs_y>ETRSTM35FIN_max_y) {
    			showMessage(String.format(getString(R.string.badXValue), ETRSTM35FIN_min_y,ETRSTM35FIN_max_y));
    			return;
    		}
       		 
 	  		double Ca=6378137.0;
 	  		double Cb=6356752.314245;
 	  		double Cf=1.0 / 298.257223563;
 	  		double Ck0=0.9996;		 
 	  		double Clo0=Math.toRadians(27.0); 	 	  		
 	  		double CE0=500000.0; 	  		
 	  		double Cn=Cf/(2.0-Cf); 	 	  			
 	  		double CA1= Ca / (1.0 + Cn) * (1.0 + (Math.pow(Cn, 2.0)) /    4.0 + (Math.pow(Cn, 4.0)) / 64.0); 
 	  		double Ce= Math.sqrt((2.0 * Cf - Math.pow(Cf,2.0)));
 	  		double Ch1 =  1.0/2.0 * Cn - 2.0/3.0 * ( Math.pow(Cn,2.0)) + 37.0/96.0 * (Math.pow(Cn,3.0)) - 1.0/360.0 * (Math.pow(Cn ,4.0));
 	  		double Ch2 = 1.0/48.0 * ( Math.pow(Cn,2.0)) + 1.0/15.0 * ( Math.pow(Cn,3.0)) - 437.0/1440.0 * ( Math.pow(Cn,4.0));
 	  		double Ch3 = 17.0/480.0 * (Math.pow(Cn,3.0)) - 37.0/840.0 * (Math.pow(Cn,4.0));
 	  		double Ch4 = 4397.0/161280.0 * ( Math.pow(Cn,4.0));

 	  	
 	  		double E = etrs_x / (CA1 * Ck0);
 	  		double nn = (etrs_y - CE0) / (CA1 * Ck0);
 	  		double E1p = Ch1 *  Math.sin(2.0 * E) * Math.cosh(2.0 * nn);
 	  		double E2p = Ch2 * Math.sin(4.0 * E) * Math.cosh(4.0 * nn);
 	  		double E3p = Ch2 * Math.sin(6.0 * E) * Math.cosh(6.0 * nn);
 	  		double E4p = Ch3 * Math.sin(8.0 * E) * Math.cosh(8.0 * nn);
 	  		 
 	  		double nn1p = Ch1 * Math.cos(2.0 * E) * Math.sinh(2.0 * nn);
 	  		double nn2p = Ch2 * Math.cos(4.0 * E) * Math.sinh(4.0 * nn);
 	  		double nn3p = Ch3 * Math.cos(6.0 * E) * Math.sinh(6.0 * nn);
 	  		double nn4p = Ch4 * Math.cos(8.0 * E) * Math.sinh(8.0 * nn);
 	  		
 	  		double Ep = E - E1p - E2p - E3p - E4p;
 	  		
 	  		double nnp = nn - nn1p - nn2p - nn3p - nn4p;
 	  		double be = Math.asin(Math.sin(Ep) / Math.cosh(nnp));
 	  		  	  		  
 	  		double Q = asinh(Math.tan(be));
 	  		double Qp = Q + Ce * atanh(Ce * Math.tanh(Q));
 	  		Qp = Q + Ce * atanh(Ce * Math.tanh(Qp));
 	  		Qp = Q + Ce * atanh(Ce * Math.tanh(Qp));
 	  		Qp = Q + Ce * atanh(Ce * Math.tanh(Qp));
 	  				
 	  		double wgs_la = Math.toDegrees(Math.atan(Math.sinh(Qp)));
 	  		
 	  		double wgs_lo = Math.toDegrees(Clo0 + Math.asin(Math.tanh(nnp) / Math.cos(be)));
 	  		
 	  		
 	  		TextView tvLat=(TextView) findViewById(R.id.txtLat);
 	    	tvLat.setText("N / lat: " + String.valueOf(wgs_la));

 	    	TextView tvLon=(TextView) findViewById(R.id.txtLon);
 	    	tvLon.setText("E / lon: " + String.valueOf(wgs_lo));
 	    	
 	    	TextView tvLink=(TextView) findViewById(R.id.txtLink);
 	    	String link = "www.openstreetmap.org/index.html?lat=" + wgs_la + "&lon=" + wgs_lo + "&zoom=15";
 	    	setAsLink(tvLink, link);
 	    	
 		  }
 		  catch(Exception ex) {
 			  Log.e(TAG, "Received an exception", ex); 			   			 
 		  }
    }
    
  	private static double atanh(double value) {		
  		return Math.log((1 / value + 1) / (1 / value - 1)) / 2;				 
  	}
   
  	private static double asinh(double value) {
  		return Math.log(value + Math.sqrt(value * value + 1));		
  	}   
  	
  	
  	private void setAsLink(TextView view, String url){
        Pattern pattern = Pattern.compile(url);
        Linkify.addLinks(view, pattern, "http://");
        view.setText(Html.fromHtml("<a href='http://"+url+"'>http://"+url+"</a>"));
    }

  	@Override
  	public boolean onCreateOptionsMenu(Menu menu) {
  	    MenuInflater inflater = getMenuInflater();
  	    inflater.inflate(R.menu.menu, menu);
  	    return true;
  	}
  	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		boolean result = true;
	
		switch(item.getItemId())
		{
			case R.id.menu_info:
			{
				about();
				break;
			}
		}
		
		return result; 
	}


	public void about()
    {
    	
    	String versionName="";
        PackageInfo pinfo;
        try
        {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pinfo.versionName;
        }
        catch (NameNotFoundException e)
        {
        	Log.e(TAG, "NameNotFoundException", e); 
        }
      
        
        final SpannableString s = new SpannableString(this.getString(R.string.app_name) + versionName + 
        		this.getString(R.string.about_menu_info));
    	
    	   //added a TextView       
    	   final TextView tx1=new TextView(this);
    	   tx1.setText(s);
    	   tx1.setAutoLinkMask(RESULT_OK);
    	   tx1.setMovementMethod(LinkMovementMethod.getInstance());
    	
    	   Linkify.addLinks(s, Linkify.EMAIL_ADDRESSES);
    	   AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	   builder.setTitle("Tietoja")
    	     .setCancelable(false)
    	     .setPositiveButton("OK", new DialogInterface.OnClickListener() {
    	         public void onClick(DialogInterface dialog, int id) {
    	          }
            })
         
    	        
    	     .setView(tx1)
    	     .show();

        }
    		
}