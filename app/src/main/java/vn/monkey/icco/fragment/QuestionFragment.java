package vn.monkey.icco.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.monkey.icco.R;
import vn.monkey.icco.activity.CustomApplication;
import vn.monkey.icco.model.QuestionResponse;
import vn.monkey.icco.util.AppConfig;
import vn.monkey.icco.util.KeyConstant;
import vn.monkey.icco.util.LocaleHelper;
import vn.monkey.icco.util.Manager;
import vn.monkey.icco.util.Util;

/**
 * Created by ANHTH on 03-Jun-16.
 */

public class QuestionFragment extends Fragment {

    private static final int SELECT_PICTURE = 1000;
    private Context context;
    private CustomApplication myApplication;
    private View mView;
    private Button upQuestion;
    private ProgressBar progressBar;
    private ImageView imageView;
    private Bitmap bitmap;
    private EditText edtQuestion;
    private Toolbar toolbar;
    private DrawerLayout drawer;

    @Override
    public void onAttach(Context context) {
        super.onAttach(LocaleHelper.onAttach(context));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_question, container, false);
        context = mView.getContext();
        myApplication = (CustomApplication) context.getApplicationContext();
        bindView();
        init();
        return mView;
    }

    /**
     *
     */
    private void bindView() {
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        imageView = (ImageView) mView.findViewById(R.id.image_question);
        upQuestion = (Button) mView.findViewById(R.id.btnUpQuestion);
        edtQuestion = (EditText) mView.findViewById(R.id.edtQuestion);
        progressBar = (ProgressBar) mView.findViewById(R.id.progress_bar);
        drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
    }

    /**
     *
     */
    private void init() {
        setHasOptionsMenu(true);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View focus = getActivity().getCurrentFocus();
                if (focus != null) {
                    Util.hiddenKeyboard(focus, mView.getContext());
                }
                FragmentManager fm = getFragmentManager();
                fm.popBackStack();
                toolbar.setNavigationIcon(R.drawable.menu_grey_36x36);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawer.openDrawer(Gravity.LEFT);
                    }
                });
            }
        });

        // choose image
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent();
                pickIntent.setType("image/*");
                pickIntent.setAction(Intent.ACTION_GET_CONTENT);
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String pickTitle =
                        getString(R.string.select_or_take_picture); // Or get from strings.xml
                Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});
                startActivityForResult(chooserIntent, SELECT_PICTURE);
            }
        });

        // up question
        upQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateInput()) return;
                Util.disableTouch(progressBar, getActivity().getWindow());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String image = "";
                        String question = edtQuestion.getText().toString();
                        try {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            byte[] b = baos.toByteArray();
                            image = Base64.encodeToString(b, Base64.DEFAULT);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        upQuestion(question, image);
                    }
                }).start();
            }
        });
    }

    ;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }

            try {
                if (data.getData() != null) {
                    InputStream inputStream =
                            getActivity().getContentResolver().openInputStream(data.getData());
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true);
                    imageView.setImageBitmap(bitmap);
                    return;
                }

                Bundle extras = data.getExtras();
                bitmap = (Bitmap) extras.get("data");
                bitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true);
                imageView.setImageBitmap(bitmap);
            } catch (Exception ex) {
                Log.e(KeyConstant.APP_CODE, "", ex);
            }
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }


    /**
     *
     */
    private void upQuestion(String question, String image) {
        String headerAuthen = String.format(AppConfig.HEADER_VALUE, Manager.USER.getToken());
        Call<QuestionResponse> call = myApplication.apiService.ask(headerAuthen, question, image);
        call.enqueue(new Callback<QuestionResponse>() {
            @Override
            public void onResponse(Call<QuestionResponse> call,
                                   Response<QuestionResponse> response) {
                Util.enableTouch(progressBar, getActivity().getWindow());
                if (response == null || response.body() == null) return;
                if (response.body().success) {
                    Manager.QAS = null;
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.flContainer, new QuestionAnswerFragment()).commit();
                    Util.showToastMessage(myApplication, response.body().message);
                } else {
                    Util.showToastMessage(myApplication, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<QuestionResponse> call, Throwable t) {
                Util.enableTouch(progressBar, getActivity().getWindow());
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
        String question = edtQuestion.getText().toString();
        if (TextUtils.isEmpty(question)) {
            Util.showToastMessage(myApplication, getString(R.string.require_question));
            return false;
        }
        return true;
    }

}
