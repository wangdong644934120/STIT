package com.st.p2018.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;
import com.st.p2018.util.MyTextToSpeech;

public class PassActivity extends Activity {

    private EditText edkkongling;
    private Button btnOK;
    private Button btnCancle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        initView();
    }
    private void initView(){
        edkkongling=(EditText)findViewById(R.id.kouling);
        btnOK=(Button)findViewById(R.id.passok);
        btnOK.setOnClickListener(new onClickListener());
        btnCancle=(Button)findViewById(R.id.passcancle);
        btnCancle.setOnClickListener(new onClickListener());
    }
    /**
     * 单击事件监听
     *
     * @author dinghaoyang
     *
     */
    public class onClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.isEnabled() == false)
                return;
            switch (v.getId()) {

                case R.id.passok:
                    btnOK.setPressed(true);
                    check();
                    btnOK.setPressed(false);
                    break;
                case R.id.passcancle:
                    btnCancle.setPressed(true);
                    PassActivity.this.finish();
                    btnCancle.setPressed(false);
                    break;

                default:
                    break;
            }
        }

    }

    private void check(){
        if(edkkongling.getText().toString().trim().equals("")){
            MyTextToSpeech.getInstance().speak("请输入口令");
            Toast.makeText(PassActivity.this, "请输入口令", Toast.LENGTH_SHORT).show();
            return;
        }
        if(edkkongling.getText().toString().trim().equals("888888")){
            Message message  = Message.obtain(Cache.myHandle);
            Bundle bund = new Bundle();
            bund.putString("ui","cd");
            message.setData(bund);
            Cache.myHandle.sendMessage(message);
            InputMethodManager imm = (InputMethodManager) this
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(PassActivity.this.getCurrentFocus().getWindowToken(),0);
            }
            PassActivity.this.finish();
        }else{
            MyTextToSpeech.getInstance().speak("口令错误");
            Toast.makeText(PassActivity.this, "口令错误", Toast.LENGTH_SHORT).show();
        }
    }

}
