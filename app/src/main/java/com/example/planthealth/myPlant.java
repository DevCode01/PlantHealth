package com.example.planthealth;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.icu.math.BigDecimal;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import org.w3c.dom.Text;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

public class myPlant extends Activity {

    private static final UUID HUMIDITY_SERVICE = UUID.fromString("0000FFE0-0000-1000-8000-00805F9B34FB");
    private static final UUID HUMIDITY_DATA_CHAR = UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB");
    private static final UUID CONFIG_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    BluetoothGatt mConnectedGatt;
    BluetoothDevice bdDevice;
    TextView essai, humeurPlante, myPlant;
    ImageButton boutonRetour;
    ImageView etatPlante;
    String number;
    float x;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_plant);

        essai = findViewById(R.id.humidite);
        humeurPlante = findViewById(R.id.humeurPlante);
        myPlant = findViewById(R.id.deviceNameMyPlant);
        boutonRetour = findViewById(R.id.returnButton);
        etatPlante = findViewById(R.id.imagePlanteEtat);



        Intent intent = getIntent();
        if (intent != null){
            bdDevice= getIntent().getExtras().getParcelable("essaie");
        }

        myPlant.setText(bdDevice.getName());
        mConnectedGatt = bdDevice.connectGatt(getApplicationContext(), false, mGattCallback);

        final Handler handler=new Handler();
        final Runnable updateTask=new Runnable() {
            @Override
            public void run() {

                essai.setText(number + " %.");
                if(x < 35) {
                    humeurPlante.setText("Vite ! Je suis assoiffée :(");
                    etatPlante.setBackgroundResource(R.drawable.plante_fanee_v2);
                    sendNotification();


                } else if (x > 79){
                    humeurPlante.setText("Oups, je crois que j'ai trop bue :/");
                    etatPlante.setBackgroundResource(R.drawable.plante_verte_v2);
                }else {
                    humeurPlante.setText("Je me sens bien ! :)");
                    etatPlante.setBackgroundResource(R.drawable.plante_verte_v2);
                }
                handler.postDelayed(this,1000);
            }
        };

        handler.postDelayed(updateTask,1000);



        boutonRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });





    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    class ListItemClicked implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // TODO Auto-generated method stub
            //bdClass = arrayListBluetoothDevices.get(position);
          //  Log.i("Log", "The device : "+bdDevice.toString());

            /*
             * here below we can do pairing without calling the callthread(), we can directly call the
             * connect(). but for the safer side we must usethe threading object.
             */
            //callThread();
            //connect(bdDevice);
            Boolean isBonded = false;
            try {
                isBonded = createBond(bdDevice);
                if(isBonded)
                {
                    //arrayListpaired.add(bdDevice.getName()+"\n"+bdDevice.getAddress());
                    //adapter.notifyDataSetChanged();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            connect(bdDevice);
           // Log.i("Log", "The bond is created: "+isBonded);
            mConnectedGatt = bdDevice.connectGatt(getApplicationContext(), false, mGattCallback);
            Intent intent = new Intent(getApplicationContext(),myPlant.class);
            intent.putExtra("essaie", bdDevice);
            startActivity(intent);



        }
    }

    /*private void callThread() {
        new Thread(){
            public void run() {
                Boolean isBonded = false;
                try {
                    isBonded = createBond(bdDevice);
                    if(isBonded)
                    {
                        arrayListpaired.add(bdDevice.getName()+"\n"+bdDevice.getAddress());
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }//connect(bdDevice);
                Log.i("Log", "The bond is created: "+isBonded);
            }
        }.start();
    }*/
    private Boolean connect(BluetoothDevice bdDevice) {
        Boolean bool = false;
        try {
          //  Log.i("Log", "service method is called ");
            Class cl = Class.forName("android.bluetooth.BluetoothDevice");
            Class[] par = {};
            Method method = cl.getMethod("createBond", par);
            Object[] args = {};
            bool = (Boolean) method.invoke(bdDevice);//, args);// this invoke creates the detected devices paired.
           // Log.i("Log", "This is: "+bool.booleanValue());
           // Log.i("Log", "devices: "+bdDevice.getName());
        } catch (Exception e) {
           // Log.i("Log", "Inside catch of serviceFromDevice Method");
            e.printStackTrace();
        }
        return bool.booleanValue();
    };


    public boolean removeBond(BluetoothDevice btDevice)
            throws Exception
    {
        Class btClass = Class.forName("android.bluetooth.BluetoothDevice");
        Method removeBondMethod = btClass.getMethod("removeBond");
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }


    public boolean createBond(BluetoothDevice btDevice)
            throws Exception
    {
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }




    class HandleSeacrh extends Handler
    {
        @Override
        public void handleMessage(Message msg) {
            BluetoothGattCharacteristic characteristic;
            switch (msg.what) {
                case MSG_HUMIDITY:
                    characteristic = (BluetoothGattCharacteristic) msg.obj;
                    if (characteristic.getValue() == null) {
                //        Log.w("Humidity error", "Error obtaining humidity value");
                        return;
                    }
                    break;
                case MSG_PROGRESS:
                    break;
                case MSG_DISMISS:
                    break;
                case MSG_CLEAR:
                    break;
            }
        }
    }


    private static final int MSG_HUMIDITY = 101;
    private static final int MSG_PROGRESS = 201;
    private static final int MSG_DISMISS = 202;
    private static final int MSG_CLEAR = 301;



    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {


        private void readNextSensor(BluetoothGatt gatt) {
            BluetoothGattCharacteristic characteristic;
          //  Log.d("Humidity", "Reading humidity");
            characteristic = gatt.getService(HUMIDITY_SERVICE).getCharacteristic(HUMIDITY_DATA_CHAR);
         //   Log.i("Read Next sensors", "All Sensors Enabled");
            gatt.readCharacteristic(characteristic);
        }

        /*
         * Enable notification of changes on the data characteristic for each sensor
         * by writing the ENABLE_NOTIFICATION_VALUE flag to that characteristic's
         * configuration descriptor.
         */
        private void setNotifyNextSensor(BluetoothGatt gatt) {
            BluetoothGattCharacteristic characteristic;
           // Log.d("Humidity", "Set notify humidity");
            characteristic = gatt.getService(HUMIDITY_SERVICE).getCharacteristic(HUMIDITY_DATA_CHAR);
         //   Log.i("Notifi Sensors", "All Sensors Enabled");
            //Enable local notifications
            gatt.setCharacteristicNotification(characteristic, true);
            //Enabled remote notifications
            BluetoothGattDescriptor desc = characteristic.getDescriptor(CONFIG_DESCRIPTOR);
            desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(desc);
        }



        /* OK */

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d("Connection state change", "Connection State Change: "+status+" -> "+connectionState(newState));
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                /*
                 * Once successfully connected, we must next discover all the services on the
                 * device before we can read and write their characteristics.
                 */
                gatt.discoverServices();
            } else if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {
                /*
                 * If at any point we disconnect, send a message to clear the weather values
                 * out of the UI
                 */
            } else if (status != BluetoothGatt.GATT_SUCCESS) {
                /*
                 * If there is a failure at any stage, simply disconnect
                 */
                gatt.disconnect();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d("Discovered", "Services Discovered: "+status);
            /*
             * With services discovered, we are going to reset our state machine and start
             * working through the sensors we need to enable
             */
            //  enableNextSensor(gatt);

            readNextSensor(gatt);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //For each read, pass the data up to the UI thread to update the display
            if (HUMIDITY_DATA_CHAR.equals(characteristic.getUuid())) {

                byte[] test = characteristic.getValue();
                String str = new String(test);
                int lsb = characteristic.getValue()[0] & 0xff;
                //Log.i("INFO :", " " + String.valueOf(gatt.readCharacteristic(characteristic)));
                // gatt.readCharacteristic(characteristic);
               // Log.i("HUMIDITY + UUID", "Presence capteur humidité : " + characteristic.getUuid().toString() + " " + lsb + " test " + str);




            }
            //After reading the initial value, next we enable notifications
            setNotifyNextSensor(gatt);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //After writing the enable flag, next we read the initial value
            readNextSensor(gatt);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            /*
             * After notifications are enabled, all updates from the device on characteristic
             * value changes will be posted here.  Similar to read, we hand these up to the
             * UI thread to update the display.
             */

            byte[] test = characteristic.getValue();
            String str = new String(test);
            int lsb = characteristic.getValue()[0] & 0xff;
            //Log.i("INFO :", " " + String.valueOf(gatt.readCharacteristic(characteristic)));
            // gatt.readCharacteristic(characteristic);
            float jetestencoreuntruc = Float.parseFloat(str);
            float alorsresult = (jetestencoreuntruc / 950) * 100;
            x = 100 - alorsresult;
            BigDecimal result;
            result=round(x,1);
            number = String.valueOf(result);


        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            //Once notifications are enabled, we move to the next sensor and start over with enable

            readNextSensor(gatt);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
          //  Log.d("Remote", "Remote RSSI: "+rssi);
        }

        private String connectionState(int status) {
            switch (status) {
                case BluetoothProfile.STATE_CONNECTED:
                    return "Connected";
                case BluetoothProfile.STATE_DISCONNECTED:
                    return "Disconnected";
                case BluetoothProfile.STATE_CONNECTING:
                    return "Connecting";
                case BluetoothProfile.STATE_DISCONNECTING:
                    return "Disconnecting";
                default:
                    return String.valueOf(status);
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    public void initChannels(Context context) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("default",
                "Channel name",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Channel description");
        notificationManager.createNotificationChannel(channel);
    }

    public void sendNotification() {
        //Get an instance of NotificationManager//
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.plantelogo)
                        .setContentTitle("Plant Health")
                        .setContentText("J'ai soif !");
        // Gets an instance of the NotificationManager service//
        NotificationManager mNotificationManager =  (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // When you issue multiple notifications about the same type of event,
        // it’s best practice for your app to try to update an existing notification
        // with this new information, rather than immediately creating a new notification.
        // If you want to update this notification at a later date, you need to assign it an ID.
        // You can then use this ID whenever you issue a subsequent notification.
        // If the previous notification is still visible, the system will update this existing notification,
        // rather than create a new one. In this example, the notification’s ID is 001//

        mNotificationManager.notify(001, mBuilder.build());
    }
}

