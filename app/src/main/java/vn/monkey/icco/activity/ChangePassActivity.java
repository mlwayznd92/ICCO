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
import vn.monkey.icco.model.RegisterResponse;
import vn.monkey.icco.util.AppConfig;
import vn.monkey.icco.util.KeyConstant;
import vn.monkey.icco.util.LocaleHelper;
import vn.monkey.icco.util.Manager;
import vn.monkey.icco.util.Util;


/**
 * Created by ANHTH on 02-Jun-16.
 */
public class ChangePassActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvTitle;
    private ImageButton ibtBack;
    private EditText edtNewPass, edtOldPass;
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
        setContentView(R.layout.activity_change_pass);
        myApplication = (CustomApplication) getApplication();
        bindView();
        init();

    }

    private void bindView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvTitle = (TextView) toolbar.findViewById(R.id.tvTitle);
        ibtBack = (ImageButton) toolbar.findViewById(R.id.ibtBack);
        edtNewPass = (EditText) findViewById(R.id.edtNewPass);
        edtOldPass = (EditText) findViewById(R.id.edtOldPass);
        btnJoin = (Button) findViewById(R.id.btnJoin);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    private void init() {
        tvTitle.setText(getString(R.string.change_password));
        ibtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPass = edtNewPass.getText().toString();
                String oldPass = edtOldPass.getText().toString();

                if (validateInput()) {
                    Util.disableTouch(progressBar, getWindow());
                    String headerAuthen = String.format(AppConfig.HEADER_VALUE, Manager.getToken());
                    Call<BaseResponse> call =
                            myApplication.apiService.changePassword(headerAuthen, newPass, oldPass);

                    call.enqueue(new Callback<BaseResponse>() {
                        @Override
                        public void onResponse(Call<BaseResponse> call,
                                               Response<BaseResponse> response) {
                            Util.enableTouch(progressBar, getWindow());
                            if (response == null || response.body() == null) return;
                            if (response.body().success) {
                                if (response.body().message != null)
                                    Util.showToastMessage(ChangePassActivity.this,
                                            response.body().message);
                                finish();
                            } else {
                                if (response.body().message != null)
                                    Util.showToastMessage(ChangePassActivity.this,
                                            response.body().message);
                            }
                        }

                        @Override
                        public void onFailure(Call<BaseResponse> call, Throwable t) {
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
        String newPass = edtNewPass.getText().toString();
        String oldPass = edtOldPass.getText().toString();
        String rule = getString(R.string.input_empty);
        if (TextUtils.isEmpty(newPass)) {
            Util.showToastMessage(this, String.format(rule, getString(R.string.new_password)));
            return false;
        } else if (TextUtils.isEmpty(oldPass)) {
            Util.showToastMessage(this, String.format(rule, getString(R.string.old_password)));
            return false;
        }
        return true;
    }
}
