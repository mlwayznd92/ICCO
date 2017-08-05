package vn.monkey.icco.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import vn.monkey.icco.model.RegisterResponse;
import vn.monkey.icco.util.KeyConstant;
import vn.monkey.icco.util.LocaleHelper;
import vn.monkey.icco.util.Util;


/**
 * Created by ANHTH on 02-Jun-16.
 */
public class SignUpActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvTitle;
    private ImageButton ibtBack;
    private EditText edtUsername, edtPassword;
    private Button btnJoin;
    private ProgressBar progressBar;
    private CustomApplication myApplication;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        myApplication = (CustomApplication) getApplication();
        bindView();
        init();

    }

    private void bindView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvTitle = (TextView) toolbar.findViewById(R.id.tvTitle);
        ibtBack = (ImageButton) toolbar.findViewById(R.id.ibtBack);
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnJoin = (Button) findViewById(R.id.btnJoin);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    private void init() {
        tvTitle.setText(getString(R.string.sign_up));
        ibtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtUsername.getText().toString();
                String pass = edtPassword.getText().toString();

                if (validateInput()) {
                    Util.disableTouch(progressBar, getWindow());
                    Call<RegisterResponse> call = myApplication.apiService
                            .register(username, pass, KeyConstant.PHONE_REGISTER);

                    call.enqueue(new Callback<RegisterResponse>() {
                        @Override
                        public void onResponse(
                                Call<RegisterResponse> call, Response<RegisterResponse> response) {
                            Util.enableTouch(progressBar, getWindow());
                            if (response == null || response.body() == null) return;
                            if (response.body().success) {
                                if (response.body().message != null)
                                    Util.showToastMessage(SignUpActivity.this,
                                            response.body().message);
                                finish();
                            } else {
                                if (response.body().message != null)
                                    Util.showToastMessage(SignUpActivity.this,
                                            response.body().message);
                            }
                        }

                        @Override
                        public void onFailure(Call<RegisterResponse> call, Throwable t) {
                            Util.enableTouch(progressBar, getWindow());
                            t.printStackTrace();
                        }
                    });
                }
            }
        });
    }

    /**
     * validate input
     *
     * @return
     */
    private boolean validateInput() {
        String phone = edtUsername.getText().toString();
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
