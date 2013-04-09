package ru.biovamp.circlelayoutexample;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.app.Activity;

public class MainActivity extends Activity {

	private boolean mPieMode = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/*
		 * All code below is NOT required. I've added it just for demonstration
		 * of different layout modes
		 */
		
		final View pie = findViewById(R.id.pie);
		final View normal = findViewById(R.id.normal);
		
		final Button switchBtn = (Button) findViewById(R.id.switchBtn);
		switchBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mPieMode) {
					pie.setVisibility(View.GONE);
					normal.setVisibility(View.VISIBLE);
					
					switchBtn.setText(R.string.pie);
					
					mPieMode = false;
				} else {
					pie.setVisibility(View.VISIBLE);
					normal.setVisibility(View.GONE);
					
					switchBtn.setText(R.string.normal);
					
					mPieMode = true;
				}
			}
		});
	}

}
