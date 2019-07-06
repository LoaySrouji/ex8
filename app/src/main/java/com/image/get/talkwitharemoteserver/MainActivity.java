package com.image.get.talkwitharemoteserver;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;


public class MainActivity extends AppCompatActivity {
    public class SetUserPrettyNameRequest { String pretty_name; }
    public class SetUserProfileImageRequest { String image_url; }
    public class TokenResponse { String data; }
    public class User { String pretty_name, image_url; }
    public class UserResponse { User data; }

    public interface MyServer{

        @GET("/users/{user_name}/token")
        Call<TokenResponse> getUserToken(@Path("user_name") String userName);

        @GET("/user")
        Call<UserResponse> getUserResponse(@Header("Authorization") String token);

        @Headers({
                "Content-Type:application/json"
        })
        @POST("/user/edit/")
        Call<UserResponse> postPrettyName(@Body SetUserPrettyNameRequest request,
                                          @Header("Authorization") String token);

        @Headers({
                "Content-Type:application/json"
        })
        @POST("/user/edit/")
        Call<UserResponse> chooseProfileImage(@Body SetUserProfileImageRequest request,
                                              @Header("Authorization") String token);

    }

    EditText editText_userName, editText_prettyName;
    Button button_userName, button_prettyName;

    ImageView imageView;
    TextView textView;

    ProgressDialog progress;
    Spinner spinner;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    String string_userName, string_prettyName, string_imageUrl, string_Tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText_userName = (EditText) findViewById(R.id.edit_trst_UserName);
        editText_userName.setVisibility(View.INVISIBLE);

        editText_prettyName = (EditText) findViewById(R.id.editText_PRN);
        editText_prettyName.setVisibility(View.INVISIBLE);

        button_userName = (Button) findViewById(R.id.button_USRN);
        button_userName.setVisibility(View.INVISIBLE);
        button_userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyServer serverInterface = ServerHolder.getInstance().serverInterface;
                final String new_user = editText_userName.getText().toString();
                if (new_user.equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Enter a valid username and hit the submit button!",
                            Toast.LENGTH_LONG).show();
                } else {
                    Call<TokenResponse> call = serverInterface.getUserToken(new_user);

                    call.enqueue(new Callback<TokenResponse>() {
                        @Override
                        public void onResponse(Call<TokenResponse> call,
                                               Response<TokenResponse> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(getApplicationContext(),
                                        "code: " + String.valueOf(response.code() + ", try again!"),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                dataSet(response, new_user);
                                engine();
                            }
                        }
                        @Override
                        public void onFailure(Call<TokenResponse> call, Throwable t) {
                            showErrorMessage(t);
                        }
                    });
                }
            }
        });

        button_prettyName = (Button) findViewById(R.id.buton_PRN);
        button_prettyName.setVisibility(View.INVISIBLE);
//        button_prettyName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String newPrettyName = editText_prettyName.getText().toString();
//                if (newPrettyName.equals("")) {
//                    Toast.makeText(getApplicationContext(),
//                            "Enter a valid pretty name and hit the submit button!",
//                            Toast.LENGTH_LONG).show();
//                }
//                else
//                {
//                    MyServer serverInterface = ServerHolder.getInstance().serverInterface;
//                    SetUserPrettyNameRequest request = new SetUserPrettyNameRequest();
//                    request.pretty_name = newPrettyName;
//                    Call<UserResponse> call = serverInterface.postPrettyName(request, "token " +
//                            string_Tag);
//                    callEnqueueIII(call);
//                }
//            }
//        });

        textView = (TextView) findViewById(R.id.editText_id);
        textView.setVisibility(View.INVISIBLE);

        spinner = (Spinner) findViewById(R.id.spinner_img);
        spinner.setVisibility(View.INVISIBLE);

        imageView = (ImageView) findViewById(R.id.prof_img);
        imageView.setVisibility(View.INVISIBLE);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();

        string_Tag = sp.getString("user_tag", "TAG");
        string_Tag = string_Tag == null ? "" : string_Tag;

        if (string_Tag.equals("TAG")) {
            editText_userName.setVisibility(View.VISIBLE);
            button_userName.setVisibility(View.VISIBLE);
        }

        engine();

    }


    private void showErrorMessage(Throwable t)
    {
        Toast.makeText(getApplicationContext(), "Error Occurred " + t.getMessage(),
                Toast.LENGTH_LONG).show();
    }


    private void callEnqueueII(Call<UserResponse> call)
    {
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),
                            "code: " + String.valueOf(response.code() + ", try again!"),
                            Toast.LENGTH_LONG).show();
                    string_imageUrl = "";
                    editor.putString("image_url", string_imageUrl);
                    editor.apply();
                }
                else
                {
                    editor.putString("image_url", string_imageUrl);
                    editor.apply();
                    imageView.setVisibility(View.VISIBLE);
                    Picasso.get().load("http://hujipostpc2019.pythonanywhere.com" + string_imageUrl).into(imageView);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                showErrorMessage(t);
                string_imageUrl = "";
                editor.putString("image_url", string_imageUrl);
                editor.apply();
            }
        });
    }

    private void titleName()
    {
        String suffix = string_userName + "!";
        textView.setText((string_prettyName == null || string_prettyName.equals("")) ?
                "welcome, " + suffix : "welcome again, " + editText_prettyName.getText().toString());
    }

    private void callEnqueueIII(Call<UserResponse> call)
    {
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),
                            "code: " + String.valueOf(response.code() + ", try again!"),
                            Toast.LENGTH_LONG).show();
                } else {
                    User data = response.body().data;
                    string_prettyName = data.pretty_name;
                    string_imageUrl = data.image_url;
                    editor.putString("editText_prettyName", string_prettyName);
                    editor.putString("image_url", string_imageUrl);
                    editor.apply();
                    string_userName = sp.getString("user_name", "");
                    textView.setVisibility(View.VISIBLE);
                    titleName();
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                showErrorMessage(t);
            }
        });
    }


    private void mySelectListener()
    {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = parent.getItemAtPosition(position).toString();
                string_imageUrl = "/images/" + selection + ".png";
                MyServer serverInterface = ServerHolder.getInstance().serverInterface;
                SetUserProfileImageRequest request = new SetUserProfileImageRequest();
                request.image_url = string_imageUrl;
                Call<UserResponse> call = serverInterface.chooseProfileImage(request, "token "
                        + string_Tag);
                callEnqueueII(call);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { return; }
        });
    }

    private void editImageUrl() {
        if (!string_Tag.equals("TAG"))
        {
            spinner.setVisibility(View.VISIBLE);
            String[] items = new String[]{"crab", "unicorn", "alien", "robot", "octopus", "frog"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
            spinner.setAdapter(adapter);
            mySelectListener();
        }
    }

    private void editPrettyName() {
        if (!string_Tag.equals("TAG")) {
            editText_prettyName.setVisibility(View.VISIBLE);
            button_prettyName.setVisibility(View.VISIBLE);
            myListen_prettyName();
        }
    }

    private void set_progress(Response<UserResponse> response)
    {
        progress.dismiss();
        User data = response.body().data;
        string_prettyName = data.pretty_name;
        string_imageUrl = data.image_url;
        editor.putString("editText_prettyName", string_prettyName);
        editor.putString("image_url", string_imageUrl);
        editor.apply();
        string_userName = sp.getString("user_name", "");
        editText_userName.setVisibility(View.GONE);
        button_userName.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
    }

    private void enqueue4(Call<UserResponse> call)
    {
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (!response.isSuccessful()) {
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(),
                            "code: " + String.valueOf(response.code() + ", try again!"),
                            Toast.LENGTH_LONG).show();
                } else {
                    set_progress(response);

                    titleName();
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                progress.dismiss();
                showErrorMessage(t);
            }
        });
    }

    private void getUserDataFromServer() {
        if (!string_Tag.equals("TAG")) {
            MyServer serverInterface = ServerHolder.getInstance().serverInterface;
            progress = new ProgressDialog(this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false);
            progress.show();
            Call<UserResponse> call = serverInterface.getUserResponse("token " + string_Tag);
            enqueue4(call);
        }
    }

    private void dataSet(Response<TokenResponse> response, String new_user)
    {
        String data = response.body().data;
        string_Tag = data;
        editor.putString("user_tag", data);
        editor.putString("user_name", new_user);
        editor.apply();
        editText_userName.setVisibility(View.GONE);
        button_userName.setVisibility(View.GONE);
    }

    private void engine()
    {
        getUserDataFromServer();
        editPrettyName();
        editImageUrl();
    }
    private void myListen_prettyName()
    {
        button_prettyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPrettyName = editText_prettyName.getText().toString();
                if (newPrettyName.equals("")) {
                    Toast.makeText(getApplicationContext(), "Enter a valid pretty name and hit the submit button!",
                            Toast.LENGTH_LONG).show();
                }
                else
                {
                    MyServer serverInterface = ServerHolder.getInstance().serverInterface;
                    SetUserPrettyNameRequest request = new SetUserPrettyNameRequest();
                    request.pretty_name = newPrettyName;
                    Call<UserResponse> call = serverInterface.postPrettyName(request, "token " + string_Tag);
                    callEnqueueIII(call);
                }
            }
        });
    }

}
