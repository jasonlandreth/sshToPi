package com.example.jason.sshtopi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    String one, two, three, four;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=(Button)findViewById(R.id.btn);
        et = (EditText)findViewById(R.id.cmdtxt);
        commandList = (TextView)findViewById(R.id.sshtxt);
        sshDialog();

        findViewById(R.id.btn).setOnClickListener(new StartSSHListener());
    }

    public class StartSSHListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            cmd = et.getText().toString();
            SSHPiTask sshpiTask = new SSHPiTask();
            sshpiTask.execute(cmd);
            Log.i("EXECUTE", String.valueOf(sshpiTask));

        }
    }

    public void sshDialog(){

        LayoutInflater li = LayoutInflater.from(this);
        View view = li.inflate(R.layout.pi_login, null);

        AlertDialog.Builder ssh_dialog = new AlertDialog.Builder(this);

        // set prompts.xml to alertdialog builder
        ssh_dialog.setView(view);
        ssh_dialog.setTitle("SSH Raspberry PI");

        final EditText userTxt = (EditText) view.findViewById(R.id.username);
        final EditText ipTxt = (EditText) view.findViewById(R.id.ip_address);
        final EditText passTxt = (EditText) view.findViewById(R.id.password);
        final EditText portTxt = (EditText) view.findViewById(R.id.port);

        // set dialog message
        AlertDialog.Builder builder = ssh_dialog
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                username = userTxt.getText().toString();
                                ip_address = ipTxt.getText().toString();
                                password = passTxt.getText().toString();

                                try {
                                    port = Integer.parseInt(portTxt.getText().toString());
                                } catch (NumberFormatException e) {
                                    Toast.makeText(getApplicationContext(), "Enter correct port number", Toast.LENGTH_SHORT).show();
                                    sshDialog();
                                }


                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = ssh_dialog.create();

        // show it
        alertDialog.show();
    }


        public class SSHPiTask extends AsyncTask<String, Void, String> {
            boolean check;

            protected String doInBackground(String... paramVarArgs) {
                try {
                    Session localSession = new JSch().getSession(username, ip_address, port);
                    localSession.setPassword(password);
                    Properties localProperties = new Properties();
                    localProperties.put("StrictHostKeyChecking", "no");
                    localSession.setConfig(localProperties);
                    localSession.connect();
                    check = localSession.isConnected();
                    if(!check){
                        Toast.makeText(getApplicationContext(), "Connection was not established  \n Enter information again", Toast.LENGTH_SHORT).show();
                        sshDialog();
                    }

                    ChannelExec localChannelExec = (ChannelExec) localSession.openChannel("exec");
                    localChannelExec.setCommand(cmd);
                    localChannelExec.connect();
                    BufferedReader input = new BufferedReader(new InputStreamReader(localChannelExec.getInputStream()));
                    StringBuilder localStringBuilder = new StringBuilder();
                    for (; ; ) {
                        String str = input.readLine();
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

            protected void onPostExecute(String paramString) {
                commandList.setText(output);
                if(!check){
                    et.setText(" ");

                }
                Toast.makeText(getApplicationContext(), "Connected: " + check, Toast.LENGTH_SHORT).show();
            }

        }

    @Override
    public void onClick(View v) {

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
