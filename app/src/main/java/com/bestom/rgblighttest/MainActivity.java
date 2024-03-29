package com.bestom.rgblighttest;

import android.content.Intent;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bestom.rgblighttest.hepler.CommandExec;

import java.io.IOException;

public class MainActivity extends BaseNfcActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    static String RED_LIGHT_OPEN = "echo 200 > /sys/class/leds/red/brightness";
    static String RED_LIGHT_CLOSE = "echo 0 > /sys/class/leds/red/brightness";
    static String RED_LIGHT_BLN_OPEN = "echo 1 > /sys/class/leds/red/blink";
    static String RED_LIGHT_BLN_CLOSE = "echo 0 > /sys/class/leds/red/blink";

    static String BLUE_LIGHT_OPEN = "echo 200 > /sys/class/leds/blue/brightness";
    static String BLUE_LIGHT_CLOSE = "echo 0 > /sys/class/leds/blue/brightness";
    static String BLUE_LIGHT_BLN_OPEN = "echo 1 > /sys/class/leds/blue/blink";
    static String BLUE_LIGHT_BLN_CLOSE = "echo 0 > /sys/class/leds/blue/blink";

    static String GREEN_LIGHT_OPEN = "echo 200 > /sys/class/leds/green/brightness";
    static String GREEN_LIGHT_CLOSE = "echo 0 > /sys/class/leds/green/brightness";
    static String GREEN_LIGHT_BLN_OPEN = "echo 1 > /sys/class/leds/green/blink";
    static String GREEN_LIGHT_BLN_CLOSE = "echo 0 > /sys/class/leds/green/blink";

    private TextView mNfcText;
    private String mTagText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.red_open).setOnClickListener(this);
        findViewById(R.id.red_close).setOnClickListener(this);
        findViewById(R.id.red_bln_open).setOnClickListener(this);
        findViewById(R.id.red_bln_close).setOnClickListener(this);

        findViewById(R.id.blue_open).setOnClickListener(this);
        findViewById(R.id.blue_close).setOnClickListener(this);
        findViewById(R.id.blue_bln_open).setOnClickListener(this);
        findViewById(R.id.blue_bln_close).setOnClickListener(this);

        findViewById(R.id.green_open).setOnClickListener(this);
        findViewById(R.id.green_close).setOnClickListener(this);
        findViewById(R.id.green_bln_open).setOnClickListener(this);
        findViewById(R.id.green_bln_close).setOnClickListener(this);

        mNfcText = findViewById(R.id.tv_nfc_text);

        //获取Tag对象
        Tag detectedTag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (detectedTag!=null){
            //读
            mNfcText.setText(readTag(detectedTag));
            sendOder(GREEN_LIGHT_BLN_OPEN);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // Red light
            case R.id.red_open:
                sendOder(RED_LIGHT_OPEN);
                break;
            case R.id.red_close:
                sendOder(RED_LIGHT_CLOSE);
                break;
            case R.id.red_bln_open:
                sendOder(RED_LIGHT_BLN_OPEN);
                break;
            case R.id.red_bln_close:
                sendOder(RED_LIGHT_BLN_CLOSE);
                break;
            // Blue light
            case R.id.blue_open:
                //sendOder(BLUE_LIGHT_OPEN);
                sendOder(GREEN_LIGHT_OPEN);
                break;
            case R.id.blue_close:
                //sendOder(BLUE_LIGHT_CLOSE);
                sendOder(GREEN_LIGHT_CLOSE);
                break;
            case R.id.blue_bln_open:
                //sendOder(BLUE_LIGHT_BLN_OPEN);
                sendOder(GREEN_LIGHT_BLN_OPEN);
                break;
            case R.id.blue_bln_close:
                //sendOder(BLUE_LIGHT_BLN_CLOSE);
                sendOder(GREEN_LIGHT_BLN_CLOSE);
                break;
            // Green light
            case R.id.green_open:
                //sendOder(GREEN_LIGHT_OPEN);
                sendOder(BLUE_LIGHT_OPEN);
                break;
            case R.id.green_close:
                //sendOder(GREEN_LIGHT_CLOSE);
                sendOder(BLUE_LIGHT_CLOSE);
                break;
            case R.id.green_bln_open:
                //sendOder(GREEN_LIGHT_BLN_OPEN);
                sendOder(BLUE_LIGHT_BLN_OPEN);
                break;
            case R.id.green_bln_close:
                //sendOder(GREEN_LIGHT_BLN_CLOSE);
                sendOder(BLUE_LIGHT_BLN_CLOSE);
                break;
        }
    }

    private void sendOder(String order) {
        CommandExec.execCommand(order, false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        //1.获取Tag对象
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        //读
        mNfcText.setText(readTag(detectedTag));
        sendOder(GREEN_LIGHT_BLN_OPEN);

        //写
//        writeNfcTag(detectedTag);
    }

    private void writeNfcTag(Tag tag) {
        if (tag == null) {
            return;
        }
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{NdefRecord.createUri(Uri.parse("https://www.baidu.com/"))});
        int size = ndefMessage.toByteArray().length;
        Ndef ndef = Ndef.get(tag);
        try {
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    return;
                }
                if (ndef.getMaxSize() < size) {
                    return;
                }
                ndef.writeNdefMessage(ndefMessage);
                Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
            }else {
                NdefFormatable ndefFormatable=NdefFormatable.get(tag);
                if(ndefFormatable!=null){
                    ndefFormatable.connect();
                    ndefFormatable.format(ndefMessage);
                    Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "formating is failed", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }

    }


    public String readTag(Tag tag) {
        MifareClassic mfc = MifareClassic.get(tag);
        for (String tech : tag.getTechList()) {
            System.out.println(tech);
        }
        boolean auth = false;
        //读取TAG

        try {
            String metaInfo = "";
            //Enable I/O operations to the tag from this TagTechnology object.
            mfc.connect();
            int type = mfc.getType();//获取TAG的类型
            int sectorCount = mfc.getSectorCount();//获取TAG中包含的扇区数
            String typeS = "";
            switch (type) {
                case MifareClassic.TYPE_CLASSIC:
                    typeS = "TYPE_CLASSIC";
                    break;
                case MifareClassic.TYPE_PLUS:
                    typeS = "TYPE_PLUS";
                    break;
                case MifareClassic.TYPE_PRO:
                    typeS = "TYPE_PRO";
                    break;
                case MifareClassic.TYPE_UNKNOWN:
                    typeS = "TYPE_UNKNOWN";
                    break;
            }
            metaInfo += "Card Type：" + typeS + "\nSector:：" + sectorCount + "\nBlock: "
                    + mfc.getBlockCount() + "\nStorage Space: " + mfc.getSize() + "B\n";
            return metaInfo;
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            if (mfc != null) {
                try {
                    mfc.close();
                } catch (IOException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG)
                            .show();
                }
            }
        }
        return null;
    }
}
