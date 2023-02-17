package com.thf.junsunshell;

import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.*;
import android.util.Log;
import android.content.Context;
import android.widget.Toast;
import com.thf.junsunshell.databinding.ActivityMainBinding;
import com.itsaky.androidide.logsender.LogSender;
import com.thf.junsunshell.utils.Util;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private ActivityMainBinding binding;
    private Util util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Remove this line if you don't want AndroidIDE to show this app's logs
        LogSender.startLogging(this);
        super.onCreate(savedInstanceState);
        // Inflate and get instance of binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        // set content view to binding's root
        setContentView(binding.getRoot());
        context = getApplicationContext();
        util = new Util(getString(R.string.su));
    }

    public void execCommand(View view) {
        String command = binding.txtCommand.getText().toString();
        List<String> output;
        String strOutput = "";
        try {
            output = util.sudoForResult(command);

            for (String line : output) {
                if ("".equals(strOutput)) {
                    strOutput = line;
                }
                strOutput += "\n" + line;
            }
            binding.txtOutput.setText(strOutput);
        } catch (Util.SuCommandException ex) {
            binding.txtOutput.setText(ex.getMessage());
        }
    }
}
