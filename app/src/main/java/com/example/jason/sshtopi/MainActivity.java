package com.example.jason.sshtopi;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;


public class MainActivity extends Activity implements View.OnClickListener {

    String cmd;
    TextView commandList;
    EditText et;
    String output;
    Button button;
    String username;
    String password;
    String ip_address;
    int port;
    String host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=(Button)findViewById(R.id.btn);
        et = (EditText)findViewById(R.id.cmdtxt);
        commandList = (TextView)findViewById(R.id.sshtxt);
        button=(Button)findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View paramAnonymousView)
            {
                cmd = et.getText().toString();
                sshconnection localsshconnection = new sshconnection();
                localsshconnection.execute(cmd);
                Log.i("EXECUTE", String.valueOf(localsshconnection));
            }
        });
        username ="pi";
        password ="raspberry";
        ip_address = "162.203.185.64";
        port = 22;
    }


    @Override
    public void onClick(View v) {

    }


        public class sshconnection extends AsyncTask<String, Void, String> {
            boolean check;
            BufferedReader input;
            String re;

            public sshconnection() {
            }


            protected String doInBackground(String... paramVarArgs) {
                try {
                    Session localSession = new JSch().getSession(username, ip_address, port);
                    localSession.setPassword(password);
                    Properties localProperties = new Properties();
                    localProperties.put("StrictHostKeyChecking", "no");
                    localSession.setConfig(localProperties);
                    localSession.connect();
                    check = localSession.isConnected();
                    ChannelExec localChannelExec = (ChannelExec) localSession.openChannel("exec");
                    localChannelExec.setCommand(cmd);
                    localChannelExec.connect();
                    BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localChannelExec.getInputStream()));
                    StringBuilder localStringBuilder = new StringBuilder();
                    for (; ; ) {
                        String str = localBufferedReader.readLine();
                        if (str == null) {
                            output = localStringBuilder.toString();
                            localChannelExec.disconnect();
                            break;
                        }
                        localStringBuilder.append(str);
                        localStringBuilder.append('\n');
                    }
                    return null;
                } catch (Exception localException) {
                    System.out.println(localException.getMessage());
                }

                return output;
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);

                commandList.setText(output);

            }

            protected void onPostExecute(String paramString) {
                //commandList.setText(output);
                Toast.makeText(getApplicationContext(), "Connected: " + check, Toast.LENGTH_SHORT).show();
            }

        }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
