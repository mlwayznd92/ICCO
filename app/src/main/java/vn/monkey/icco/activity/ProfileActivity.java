package vn.monkey.icco.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.monkey.icco.R;
import vn.monkey.icco.model.ChangeInfoResponse;
import vn.monkey.icco.model.User;
import vn.monkey.icco.model.UserResponse;
import vn.monkey.icco.util.AppConfig;
import vn.monkey.icco.util.KeyConstant;
import vn.monkey.icco.util.LocaleHelper;
import vn.monkey.icco.util.Manager;
import vn.monkey.icco.util.Util;

public class ProfileActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1002;
    //    private static final int LOGIN_FOR_PROFILE = 1003;
    private Toolbar toolbar;
    private TextView tvTitle;
    private ImageButton ibtBack;
    private CustomApplication myApplication;
    private RadioGroup radioSexGroup;
    private Button btnUpdate;
    private EditText tvFullName, tvAddress;
    private ProgressBar progressBar;
    private CircleImageView ivAvatar;
    private Bitmap bitmap;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        myApplication = (CustomApplication) getApplication();
        bindView();
        init();
    }

    /**
     *
     */
    public void bindView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvTitle = (TextView) toolbar.findViewById(R.id.tvTitle);
        ibtBack = (ImageButton) toolbar.findViewById(R.id.ibtBack);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        radioSexGroup = (RadioGroup) findViewById(R.id.radioSex);
        tvFullName = (EditText) findViewById(R.id.edtFullName);
        tvAddress = (EditText) findViewById(R.id.edtAddress);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_update_profile);
        ivAvatar = (CircleImageView) findViewById(R.id.ivAvatar);
    }

    /**
     *
     */
    public void init() {
        loadUserInfo();
        setSupportActionBar(toolbar);
        tvTitle.setText(getString(R.string.profile));
        ibtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Util.disableTouch(progressBar, getWindow());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int sex = 1;
                        String fullName = String.valueOf(tvFullName.getText());
                        String address = String.valueOf(tvAddress.getText());

                        // get selected radio button from radioGroup
                        int selectedId = radioSexGroup.getCheckedRadioButtonId();
                        if (selectedId == R.id.radioFemale) {
                            sex = 2;
                        }

                        String image = "";
                        try {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] b = baos.toByteArray();
                            image = Base64.encodeToString(b, Base64.DEFAULT);
                        } catch (Exception ex) {
                            Log.e(KeyConstant.APP_CODE, "", ex);
                        }
                        updateProfile(fullName, address, sex, image);
                    }
                }).start();
            }

        });

        // choose image
        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent();
                pickIntent.setType("image/*");
                pickIntent.setAction(Intent.ACTION_GET_CONTENT);
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String pickTitle = getString(R.string.select_or_take_picture);
                Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});
                startActivityForResult(chooserIntent, SELECT_PICTURE);
            }
        });
    }

    /**
     * @param fullName
     * @param address
     * @param sex
     */
    public void updateProfile(final String fullName, String address, int sex, String image) {
        String headerAuthen =
                String.format(AppConfig.HEADER_VALUE, Util.getUser(myApplication).getToken());
        Call<ChangeInfoResponse> call =
                myApplication.apiService.changeInfo(headerAuthen, fullName, address, sex, image);
        call.enqueue(new Callback<ChangeInfoResponse>() {
            @Override
            public void onResponse(Call<ChangeInfoResponse> call,
                                   Response<ChangeInfoResponse> response) {
                Util.enableTouch(progressBar, getWindow());
                if (response == null || response.body() == null) return;
                if (response.body().success) {
                    Manager.USER.setFullName(fullName);
                    Manager.cacheUser(myApplication, Manager.USER);
                    Manager.loadUserInformation(myApplication);
                    setResult(RESULT_OK);
                }
                if (!TextUtils.isEmpty(response.body().message))
                    Util.showToastMessage(myApplication, response.body().message);
                finish();
            }

            @Override
            public void onFailure(Call<ChangeInfoResponse> call, Throwable t) {
                Util.enableTouch(progressBar, getWindow());
                t.printStackTrace();
            }
        });
    }


    /**
     * load user info
     */
    private void loadUserInfo() {
        Util.disableTouch(progressBar, getWindow());
        String headerAuthen = String.format(AppConfig.HEADER_VALUE, Manager.USER.getToken());
        Call<UserResponse> call =
                myApplication.apiService.getUserInfo(headerAuthen, KeyConstant.PHONE_REGISTER);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response == null || response.body() == null) {
                    Log.e(KeyConstant.APP_CODE, "load user response null!");
                    finish();
                    return;
                }

                if (response.body().success) {
                    UserResponse userResponse = response.body();
                    User user = new User();
                    user.setFullName(userResponse.data.full_name);
                    user.setSex(userResponse.data.sex);
                    user.setAvatarUrl(userResponse.data.avatar);
                    user.setAddress(userResponse.data.address);
                    setViewUserInfo(user);
                } else {
                    if (response.body().message != null)
                        Util.showToastMessage(myApplication, response.body().message);
                    if (response.body().statusCode == KeyConstant.ERROR_CODE_AUTHEN) {
                        Manager.logout(myApplication);
                        Intent intent = new Intent(ProfileActivity.this, TermActivity.class);
                        intent.setFlags(
                                Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        //  startActivityForResult(intent, LOGIN_FOR_PROFILE);
                    }
                }
                Util.enableTouch(progressBar, getWindow());
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Util.enableTouch(progressBar, getWindow());
                t.printStackTrace();
            }
        });
    }

    /**
     * @param user
     */
    private void setViewUserInfo(User user) {
        // initial full name
        if (user.getFullName() != null) {
            tvFullName.setText(user.getFullName());
        }

        // initial full name
        if (user.getAddress() != null) {
            tvAddress.setText(user.getAddress());
        }

        if (!TextUtils.isEmpty(user.getAvatarUrl()))
            Picasso.with(myApplication).load(user.getAvatarUrl())
                    .error(R.mipmap.no_user_profile_image)
                    .placeholder(R.mipmap.no_user_profile_image).into(ivAvatar);

        // initial full name
        if (user.getSex() != null) {
            if (user.getSex() == 1) {
                RadioButton radioButton = (RadioButton) findViewById(R.id.radioMale);
                radioButton.setChecked(true);
            } else {
                RadioButton radioButton = (RadioButton) findViewById(R.id.radioFemale);
                radioButton.setChecked(true);
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            try {
                if (data.getData() != null) {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    ivAvatar.setImageBitmap(bitmap);
                    return;
                }

                Bundle extras = data.getExtras();
                bitmap = (Bitmap) extras.get("data");
                ivAvatar.setImageBitmap(bitmap);
            } catch (Exception ex) {
                Log.e(KeyConstant.APP_CODE, "", ex);
            }
        }
        //        else if (requestCode == LOGIN_FOR_PROFILE) {
        //            if (resultCode != Activity.RESULT_OK) {
        //                finish();
        //                return;
        //            }
        //            loadUserInfo();
        //        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_change_pass:
//                startActivity(new Intent(ProfileActivity.this, ChangePassActivity.class));
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_profile, menu);
//        return true;
//    }

}
