package com.inw24.multiwebview;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.inw24.multiwebview.utils.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Full Screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Hide Toolabr
        getSupportActionBar().hide();

        //Get all information
        getAllInformation();
    }


    //==========================================================================//
    public void getAllInformation() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Config.GET_ALL_INFORMATION_URL + "?api_key="+ Config.API_KEY + "&content_id="+ Config.WEBSITE_ID, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        //Set local variable and use it in whole application
                        ((AppController) SplashActivity.this.getApplication()).setContent_id(jsonObject.getString("content_id"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_user_id(jsonObject.getString("content_user_id"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_title(jsonObject.getString("content_title"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_sub_title(jsonObject.getString("content_sub_title"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_description(jsonObject.getString("content_description"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_property1(jsonObject.getString("content_property1"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_property2(jsonObject.getString("content_property2"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_orientation(jsonObject.getString("content_orientation"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_primary_color(jsonObject.getString("content_primary_color"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_dark_color(jsonObject.getString("content_dark_color"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_accent_color(jsonObject.getString("content_accent_color"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_type_id(jsonObject.getString("content_type_id"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_access(jsonObject.getString("content_access"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_category_id(jsonObject.getString("content_category_id"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_user_role_id(jsonObject.getString("content_user_role_id"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_image(jsonObject.getString("content_image"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_url(jsonObject.getString("content_url"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_url1(jsonObject.getString("content_url1"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_url1_text(jsonObject.getString("content_url1_text"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_url2(jsonObject.getString("content_url2"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_url2_text(jsonObject.getString("content_url2_text"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_url3(jsonObject.getString("content_url3"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_url3_text(jsonObject.getString("content_url3_text"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_url4(jsonObject.getString("content_url4"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_url4_text(jsonObject.getString("content_url4_text"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_url5(jsonObject.getString("content_url5"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_url5_text(jsonObject.getString("content_url5_text"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_email(jsonObject.getString("content_email"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_viewed(jsonObject.getString("content_viewed"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_featured(jsonObject.getString("content_featured"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_special(jsonObject.getString("content_special"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_publish_date(jsonObject.getString("content_publish_date"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_rtl(jsonObject.getString("content_rtl"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_fullscreen(jsonObject.getString("content_fullscreen"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_toolbar(jsonObject.getString("content_toolbar"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_banner_ads(jsonObject.getString("content_banner_ads"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_interstitial_ads(jsonObject.getString("content_interstitial_ads"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_admob_app_id(jsonObject.getString("content_admob_app_id"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_admob_banner_unit_id(jsonObject.getString("content_admob_banner_unit_id"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_admob_interstitial_unit_id(jsonObject.getString("content_admob_interstitial_unit_id"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_menu(jsonObject.getString("content_menu"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_loader(jsonObject.getString("content_loader"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_pull_to_refresh(jsonObject.getString("content_pull_to_refresh"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_status(jsonObject.getString("content_status"));
                        ((AppController) SplashActivity.this.getApplication()).setSettingVersionCode(jsonObject.getString("setting_version_code"));
                        ((AppController) SplashActivity.this.getApplication()).setSettingAndroidMaintenance(jsonObject.getString("setting_android_maintenance"));
                        ((AppController) SplashActivity.this.getApplication()).setSettingTextMaintenance(jsonObject.getString("setting_text_maintenance"));
                        ((AppController) SplashActivity.this.getApplication()).setContent_onesignal_app_id(jsonObject.getString("content_onesignal_rest_api_key"));

                        //Set tv_splash --> Website Title
                        TextView tv_splash = (TextView)findViewById(R.id.tv_splash);
                        tv_splash.setText(((AppController) SplashActivity.this.getApplication()).getContent_title());

                        // Set portrait or landscape ==>  1: It does not matter | 2: portrait | 3: landscape
                        if(((AppController) SplashActivity.this.getApplication()).getContent_orientation().equals("2"))
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        else if(((AppController) SplashActivity.this.getApplication()).getContent_orientation().equals("3"))
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


                        String content_status = ((AppController) SplashActivity.this.getApplication()).getContent_status();
                        if(content_status.equals("1"))
                        {
                            //Go to MainActivity
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            },600);

                        }else{
                            Toast.makeText(SplashActivity.this, getResources().getString(R.string.your_application_has_been_disabled), Toast.LENGTH_LONG).show();
                            finish();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("BlueDev Volley Error: ", error + "");
                //Toast.makeText(SplashActivity.this, R.string.txt_error+" "+error, Toast.LENGTH_SHORT).show();
                // No Internet Connection
                AlertDialog.Builder builderCheckUpdate = new AlertDialog.Builder(SplashActivity.this);
                builderCheckUpdate.setTitle(getResources().getString(R.string.txt_whoops));
                builderCheckUpdate.setMessage(getResources().getString(R.string.txt_no_network_connection_found));
                builderCheckUpdate.setCancelable(false);

                builderCheckUpdate.setPositiveButton(
                        getResources().getString(R.string.txt_try_again),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                                startActivity(getIntent());
                            }
                        });

                builderCheckUpdate.setNegativeButton(
                        getResources().getString(R.string.txt_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                finish();
                            }
                        });

                AlertDialog alert1CheckUpdate = builderCheckUpdate.create();
                alert1CheckUpdate.show();
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);
    }
}
