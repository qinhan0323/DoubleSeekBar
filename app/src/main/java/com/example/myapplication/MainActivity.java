package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    DoubleSeekBar mDoubleSeekBar;
    TextView tv1,seekBar1_tv,seekBar2_tv,seekBar3_tv;
    SeekBar seekBar1,seekBar2,seekBar3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDoubleSeekBar=(DoubleSeekBar)findViewById(R.id.mDoubleSeekBar);
        tv1=(TextView)findViewById(R.id.tv1);
        seekBar1=(SeekBar)findViewById(R.id.seekBar1);
        seekBar1_tv=(TextView)findViewById(R.id.seekBar1_tv);
        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //数值改变
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBar1_tv.setText(""+progress+"%");
            }
            //开始拖动
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            //停止拖动
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar2=(SeekBar)findViewById(R.id.seekBar2);
        seekBar2_tv=(TextView)findViewById(R.id.seekBar2_tv);
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBar2_tv.setText( ""+progress+"%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar3=(SeekBar)findViewById(R.id.seekBar3);
        seekBar3_tv=(TextView)findViewById(R.id.seekBar3_tv);
        seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBar3_tv.setText( ""+progress+"%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

}
