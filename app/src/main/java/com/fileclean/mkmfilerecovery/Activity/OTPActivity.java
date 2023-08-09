package com.fileclean.mkmfilerecovery.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.fileclean.mkmfilerecovery.Database.DataOTP.DataOTP;
import com.fileclean.mkmfilerecovery.Database.DataOTP.InfoOTP;
import com.fileclean.mkmfilerecovery.R;

import java.util.ArrayList;
import java.util.List;

public class OTPActivity extends AppCompatActivity {

    private PinView pinView;
    private List<InfoOTP> Listotp;
    private TextView txt_verify;
    private TextView bt_verify;
    private int OTP;
    private int otptg = 0;
    private Window window;

//    private FirebaseAnalytics mFirebaseAnalytics;
//    private AdsUtils adsUtils;
//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpactivity);

        initView();
        getDataOTP();
        Verify();
        ClickVerify();
        setStatusbar();
    }

    private void Verify() {
        pinView.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() >=4) {
                    HideKeyBoard();
                    OTP = Integer.parseInt(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void HideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void getDataOTP() {
        Listotp = DataOTP.getInstance(getApplicationContext()).daoSql().getall();
        if (Listotp.size() == 0) {
            txt_verify.setText(getString(R.string.setotp));
        }else {
            txt_verify.setText(getString(R.string.verify));
        }
    }

    private void initView() {
        pinView = findViewById(R.id.pinview);
        Listotp = new ArrayList<>();
        txt_verify = findViewById(R.id.txt_verify);
        bt_verify = findViewById(R.id.bt_verify);
    }
    private void setStatusbar() {
        window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void ClickVerify() {

        bt_verify.setOnClickListener(view -> {

//            Bundle bundle = new Bundle();
//            mFirebaseAnalytics.logEvent("verify_otp_bt_verify", bundle);

            if (pinView.getText().length() < 4) {
                Toast.makeText(this, getString(R.string.pleaseentercode), Toast.LENGTH_SHORT).show();
            }else {
                if (DataOTP.getInstance(getApplicationContext()).daoSql().getall().size() == 0) {

                    if (otptg == 0) {
                        txt_verify.setText(getString(R.string.confirmotp));
                        otptg = OTP;

                        pinView.setText("");
                    }else

                    if (otptg == OTP) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle(getString(R.string.setotp));
                        builder.setMessage(getString(R.string.sure_otp) + " " + OTP + " ?");

                        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Listotp.add(new InfoOTP(OTP));
                                DataOTP.getInstance(getApplicationContext()).daoSql().insert(new InfoOTP(Listotp.get(0).getOtp()));
                                startActivity(new Intent(getApplicationContext(), FileVaultActivtity.class));
                            }
                        });

                        builder.show();

                    }else {
                        Toast.makeText(this, getString(R.string.otpnotmatch), Toast.LENGTH_SHORT).show();
                        pinView.setText("");
                    }

                }else {
                    if (Listotp.get(0).getOtp() == OTP) {
                        startActivity(new Intent(this, FileVaultActivtity.class));
                    }else {
                        pinView.setText("");
                        Toast.makeText(this, getString(R.string.otpincorrect), Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
    }

}