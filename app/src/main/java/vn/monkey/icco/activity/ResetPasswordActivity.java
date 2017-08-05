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
import vn.monkey.icco.R;
import vn.monkey.icco.model.BaseResponse;
import vn.monkey.icco.util.LocaleHelper;
import vn.monkey.icco.util.Util;

/**
 * Created by ANHTH on 29-Jun-16.
 */
public class ResetPasswordActivity extends AppCompatActivity {

    ProgressBar progressBar;
    Toolbar toolbar;
    TextView tvTitle;
    ImageButton ibtBack;
    EditText edtToken;
    Button btnActive;
    String email;
    String token;
    private CustomApplication myApplication;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        myApplication = (CustomApplication) getApplication();
        bindview();
        init();
    }


    private void init() {
        setupToolbar();
        btnActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = edtToken.getText().toString();
                if (TextUtils.isEmpty(token)) {
                    Util.showToastMessage(ResetPasswordActivity.this,
                            String.format(getString(R.string.input_empty),
                                    edtToken.getHint().toString()));
                    return;
                } else if (!Util.isEmailValid(token)) {
                    Util.showToastMessage(ResetPasswordActivity.this,
                            getString(R.string.email_invalid));
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                Call<BaseResponse> call = myApplication.apiService.resetPassword(token);
                call.enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(
                            Call<BaseResponse> call, Response<BaseResponse> response) {
                        progressBar.setVisibility(View.GONE);

                        if (response.body().success) {
                            Util.showToastMessage(ResetPasswordActivity.this,
                                    getString(R.string.password_change_success));
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        //                        Util.showToastMessage(ResetPasswordActivity.this, t.getMessage());
                        //Log.d("Retrofit-Log onFailure", t.getMessage());
                    }
                });
            }
        });
    }

    private void setupToolbar() {
        tvTitle.setText(getString(R.string.active_account));
        ibtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void bindview() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvTitle = (TextView) toolbar.findViewById(R.id.tvTitle);
        ibtBack = (ImageButton) toolbar.findViewById(R.id.ibtBack);
        edtToken = (EditText) findViewById(R.id.edtEmail);
        btnActive = (Button) findViewById(R.id.btnActive);
    }


}
