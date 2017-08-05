package vn.monkey.icco.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import vn.monkey.icco.R;
import vn.monkey.icco.model.BaseResponse;
import vn.monkey.icco.model.LoginResponse;
import vn.monkey.icco.model.User;
import vn.monkey.icco.util.LocaleHelper;
import vn.monkey.icco.util.Manager;
import vn.monkey.icco.util.Util;

/**
 * Created by ANHTH on 02-Jun-16.
 */
public class SignInActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvTitle, tvForgotPass;
    private EditText edtPhoneNum, edtPassword;
    private ImageButton ibtBack;
    private Button btnJoin, btnSignUp;
    private ProgressBar progressBar;
    private CustomApplication myApplication;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        myApplication = (CustomApplication) getApplication();
        bindview();
        init();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void bindview() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvTitle = (TextView) toolbar.findViewById(R.id.tvTitle);
        ibtBack = (ImageButton) toolbar.findViewById(R.id.ibtBack);
        tvForgotPass = (TextView) findViewById(R.id.tvForgotPassword);
        edtPhoneNum = (EditText) findViewById(R.id.edtPhoneNum);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnJoin = (Button) findViewById(R.id.btnSignIn);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    private void init() {
        edtPhoneNum.setInputType(
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        tvForgotPass.setPaintFlags(tvForgotPass.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInput()) {
                    resetPasswordTask();
                }
            }
        });

        tvTitle.setText(getString(R.string.sign_in));
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    loginTask();
                }
            }
        });

        ibtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        });
    }

    /**
     * login task
     */
    private void loginTask() {
        String phone = edtPhoneNum.getText().toString();
        String pass = edtPassword.getText().toString();
        Util.disableTouch(progressBar, getWindow());
        Call<LoginResponse> call = myApplication.apiService.login(phone, pass);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Util.enableTouch(progressBar, getWindow());
                if (response == null || response.body() == null) return;
                if (response.body().success) {
                    User user = new User();
                    user.setUsername(response.body().data.username);
                    user.setToken(response.body().data.token);
                    user.setAvatarUrl(response.body().data.avatar);
                    user.setFullName(response.body().data.full_name);
                    Manager.USER = user;
                    Manager.cacheUser(myApplication, user);
                    if (!TextUtils.isEmpty(response.body().message)) {
                        Util.showToastMessage(myApplication, response.body().message);
                    }
                    startActivity(new Intent(SignInActivity.this, WelcomeActivity.class));
                    // setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    Manager.clearCacheUser(myApplication);
                    Manager.USER = null;
                    if (!TextUtils.isEmpty(response.body().message)) {
                        Util.showToastMessage(myApplication, response.body().message);
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Util.enableTouch(progressBar, getWindow());
                t.printStackTrace();
            }
        });
    }

    /**
     * reset password
     */
    private void resetPasswordTask() {
        String phone = edtPhoneNum.getText().toString();
        String pass = edtPassword.getText().toString();
        Util.disableTouch(progressBar, getWindow());
        Call<BaseResponse> call = myApplication.apiService.resetPassword(phone, pass);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                Util.enableTouch(progressBar, getWindow());
                if (response == null || response.body() == null) return;
                if (response.body().message != null)
                    Util.showToastMessage(myApplication, response.body().message);

            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Util.enableTouch(progressBar, getWindow());
                t.printStackTrace();
            }
        });
    }

    /**
     * validate input
     *
     * @return
     */
    private boolean validateInput() {
        String phone = edtPhoneNum.getText().toString();
        String pass = edtPassword.getText().toString();
        String rule = getString(R.string.input_empty);

        if (TextUtils.isEmpty(phone)) {
            Util.showToastMessage(this, String.format(rule, getString(R.string.phone_number)));
            return false;
        } else if (TextUtils.isEmpty(pass)) {
            Util.showToastMessage(this, String.format(rule, getString(R.string.password)));
            return false;
        }
        return true;
    }
}
