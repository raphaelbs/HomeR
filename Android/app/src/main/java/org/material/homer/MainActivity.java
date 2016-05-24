package org.material.homer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ecv.freetime.FreetimeActivity;
import com.ecv.freetime.services.ServicesManager;
import com.ecv.freetime.webservices.Requester;

import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * Main Activity
 */
public class MainActivity extends FreetimeActivity {

    /**
     * State
     */
    final static public class State {

        final public class Room {
            float use;
            int swt;
        }

        float renewD;
        float batery;
        Room[] rooms = new Room[] {new Room(), new Room(), new Room(), new Room()};
    }

    // Consts
    final public static int CONNECTION_ERROR = 0x1;
    final public static int COMMUNICATION_ERROR = 0x2;
    final public static int COMMUNICATION_STATE = 0x3;

    // Private Variables
    private boolean mConnected = false;
    private Button mConnectButton;
    private Batery mBatery;
    private Produce mProduce;
    private TextView[] mRoomText = new TextView[4];
    private ToggleButton[] mRoomToggle = new ToggleButton[4];
    private ImageView[] mRoomIcon = new ImageView[4];
    DecimalFormat mDecimalFormat = new DecimalFormat("00.00");

    /**
     * On Create
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFreetime(savedInstanceState);
        setContentView(R.layout.activity_main);


        mConnectButton = (Button) findViewById(R.id.connect_button);
        mBatery = (Batery) findViewById(R.id.batery);
        mProduce = (Produce) findViewById(R.id.produce);
        mRoomText[0] = (TextView) findViewById(R.id.room_1_text);
        mRoomText[1] = (TextView) findViewById(R.id.room_2_text);
        mRoomText[2] = (TextView) findViewById(R.id.room_3_text);
        mRoomText[3] = (TextView) findViewById(R.id.room_4_text);
        mRoomToggle[0] = (ToggleButton) findViewById(R.id.room_1_toggle);
        mRoomToggle[1] = (ToggleButton) findViewById(R.id.room_2_toggle);
        mRoomToggle[2] = (ToggleButton) findViewById(R.id.room_3_toggle);
        mRoomToggle[3] = (ToggleButton) findViewById(R.id.room_4_toggle);
        mRoomToggle[0].setToggle(true, false);
        mRoomToggle[1].setToggle(true, false);
        mRoomToggle[2].setToggle(true, false);
        mRoomToggle[3].setToggle(true, false);

        mRoomIcon[0] = (ImageView) findViewById(R.id.room_1_icon);
        mRoomIcon[1] = (ImageView) findViewById(R.id.room_2_icon);
        mRoomIcon[2] = (ImageView) findViewById(R.id.room_3_icon);
        mRoomIcon[3] = (ImageView) findViewById(R.id.room_4_icon);
        refreshIcons();

         final Requester req = new Requester(this, getSharedServicesManager());

        //
        mRoomToggle[0].setListener(new ToggleButtonListener() {

            @Override
            public void onToggle(boolean t) {
                refreshIcons();
                JSONObject obj = new JSONObject();
                try {
                    obj.put("key", "57430b208a82e75c071daae1");
                    obj.put("userId", "0");
                    obj.put("value", t ? 0 : 1);
                } catch (Exception e){}
                req.prepare(Requester.RequestType.POST)
                        .setUrl("http://192.168.10.100:3000/set")
                        .setBody(obj.toString())
                        .setMode(Requester.RequestMode.FUTURE).request();

            }
        });

        mRoomToggle[1].setListener(new ToggleButtonListener() {

            @Override
            public void onToggle(boolean t) {
                refreshIcons();

                JSONObject obj = new JSONObject();
                try {
                    obj.put("key", "57430b208a82e75c071daae1");
                    obj.put("userId", "1");
                    obj.put("value", t ? 0 : 1);
                } catch (Exception e){}
                req.prepare(Requester.RequestType.POST)
                        .setUrl("http://192.168.10.100:3000/set")
                        .setBody(obj.toString())
                        .setMode(Requester.RequestMode.FUTURE).request();

            }
        });

        mRoomToggle[2].setListener(new ToggleButtonListener() {

            @Override
            public void onToggle(boolean t) {
                refreshIcons();

                JSONObject obj = new JSONObject();
                try {
                    obj.put("key", "57430b208a82e75c071daae1");
                    obj.put("userId", "2");
                    obj.put("value", t ? 0 : 1);
                } catch (Exception e){}
                req.prepare(Requester.RequestType.POST)
                        .setUrl("http://192.168.10.100:3000/set")
                        .setBody(obj.toString())
                        .setMode(Requester.RequestMode.FUTURE).request();

            }
        });

        mRoomToggle[3].setListener(new ToggleButtonListener() {

            @Override
            public void onToggle(boolean t) {
                refreshIcons();

                JSONObject obj = new JSONObject();
                try {
                    obj.put("key", "57430b208a82e75c071daae1");
                    obj.put("userId", "3");
                    obj.put("value", t ? 0 : 1);
                } catch (Exception e){}
                req.prepare(Requester.RequestType.POST)
                        .setUrl("http://192.168.10.100:3000/set")
                        .setBody(obj.toString())
                        .setMode(Requester.RequestMode.FUTURE).request();

            }
        });

        mConnectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(mConnected) {
                    getBackgroundServices().removeService("BACK_SERVICE");
                    mConnectButton.setText("Conectar");
                } else {
                    getBackgroundServices().addService(new Service(), "BACK_SERVICE");
                    mConnectButton.setText("Desconectar");
                }
                mConnected = !mConnected;
            }
        });

        //ToggleButton t = (ToggleButton) findViewById(R.id.id_teste1);

    }

    /**
     * Service manager response
     *
     * @param manager
     * @param code
     * @param obj
     */
    @Override
    public void onServicesManagerResponse(ServicesManager manager, int code, Object obj) {
        super.onServicesManagerResponse(manager, code, obj);
        // Connection Error
        if (code == CONNECTION_ERROR) {
            Toast.makeText(getApplicationContext(), "Ocorreu um erro na conexão.", Toast.LENGTH_LONG).show();
            mConnectButton.setText("Conectar");
            mConnected = false;
        } else if (code == COMMUNICATION_ERROR) {
            Toast.makeText(getApplicationContext(), "Ocorreu um erro na comunicação..", Toast.LENGTH_LONG).show();
            mConnectButton.setText("Conectar");
            mConnected = false;
        } else if (code == COMMUNICATION_STATE) {
            State state = (State) obj;
            mBatery.setControl(state.batery);
            mProduce.setControl(state.renewD);
            for(int i=0; i<4; i++) {
                mRoomText[i].setText(mDecimalFormat.format(state.rooms[i].use).replace(',', '.')+"W");
                mRoomToggle[i].setToggle(!(state.rooms[i].swt == 1), true);
            }
        }
    }

    /**
     * Refresh Icons
     */
    public void refreshIcons() {
        for(int i=0; i<4; i++) {
            if(!mRoomToggle[i].getToggle()) {
                mRoomIcon[i].setImageResource(R.drawable.batery);
            } else {
                mRoomIcon[i].setImageResource(R.drawable.pole);
            }
        }
    }
}
