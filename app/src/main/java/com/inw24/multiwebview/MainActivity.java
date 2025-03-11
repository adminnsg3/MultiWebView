package com.inw24.multiwebview;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import com.inw24.multiwebview.utils.AppController;
import com.onesignal.OneSignal;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public boolean doubleBackToExitPressedOnce = false;
    public WebViewFragment webViewFragment = new WebViewFragment();
    String contentId;
    String primaryColor;
    String darkColor;
    String accentColor;
    String contentUrl;
    String theURL;
    String admobAppId;
    String admobBannerUnitId;
    String admobInterstitialUnitId;
    Toolbar toolbar;
    NavigationView navigationView;
    String tag;
    private boolean first_fragment = false;
    TextView tvNavTitle;
    TextView tvNavSubTitle;
    LinearLayout navLinearLayout;
    private InterstitialAd mInterstitialAd;
    String settingVersionCode;
    int appVersionCode = 1;

    private static final int REQUEST_PERMISSIONS = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(((AppController) this.getApplication()).getContent_title());
        toolbar.setSubtitle(((AppController) this.getApplication()).getContent_sub_title());
        setSupportActionBar(toolbar);

        // Get Variables
        contentId = ((AppController) this.getApplication()).getContent_id();
        primaryColor = ((AppController) this.getApplication()).getContent_primary_color();
        darkColor = ((AppController) this.getApplication()).getContent_dark_color();
        accentColor = ((AppController) this.getApplication()).getContent_accent_color();
        contentUrl = ((AppController) this.getApplication()).getContent_url();
        admobAppId = ((AppController) this.getApplication()).getContent_admob_app_id();
        admobBannerUnitId = ((AppController) this.getApplication()).getContent_admob_banner_unit_id();
        admobInterstitialUnitId = ((AppController) this.getApplication()).getContent_admob_interstitial_unit_id();

        // Full Screen
        if(((AppController) this.getApplication()).getContent_fullscreen().equals("1"))
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Hide Toolbar
        if(((AppController) this.getApplication()).getContent_toolbar().equals("0"))
            getSupportActionBar().hide();

        // Total View + 1 //
        totalView();

        // Check App Version
        versionCheck();

        // Check Maintenance Status
        maintenanceCheck();

        // AdMob Initialization
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(Config.ONESIGNAL_APP_ID);

        // List of permissions to request
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
        };

        // List to hold permissions that are not granted
        List<String> permissionsToRequest = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        // Request all necessary permissions at once if any are not granted
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS);
        }


        // Banner Ad
        final View adContainer = findViewById(R.id.adMobView);
        final AdView mAdView = new AdView(MainActivity.this);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(admobBannerUnitId);
        ((RelativeLayout)adContainer).addView(mAdView);
        if(((AppController) this.getApplication()).getContent_banner_ads().equals("1"))
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    FrameLayout frmContainer = (FrameLayout) findViewById(R.id.frmContainer);
                    frmContainer.setPadding(0,0,0,149);
                    adContainer.setVisibility(View.VISIBLE);
                    AdRequest adRequest = new AdRequest.Builder().build();
                    mAdView.loadAd(adRequest);
                }
            },8000); // A time that show Banner Ads. eg: 8 seconds
        }

        // Interstitial Ad
        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, admobInterstitialUnitId, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("TAG", "onInterstitialAdLoaded");
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("TAG", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });


        // Set portrait or landscape ==>  1: It does not matter | 2: portrait | 3: landscape
        if(((AppController) this.getApplication()).getContent_orientation().equals("2"))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else if(((AppController) this.getApplication()).getContent_orientation().equals("3"))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Set to RTL or LTR
        if(((AppController) MainActivity.this.getApplication()).getContent_rtl().equals("1"))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } else {
                Log.d("MyTag", "Working in Normal Mode, RTL Mode is Disabled");
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Enable Navigation Menu
        if(((AppController) MainActivity.this.getApplication()).getContent_menu().equals("1"))
        {
            navigationView.setNavigationItemSelectedListener(this);

            // Get menu from navigationView
            Menu menu = navigationView.getMenu();
            MenuItem nav_custom_1 = menu.findItem(R.id.nav_custom_1); // find MenuItem you want to change
            nav_custom_1.setTitle(((AppController) this.getApplication()).getContent_url1_text()); // set new title to the MenuItem
            if(((AppController) this.getApplication()).getContent_url1_text().equals(""))
                nav_custom_1.setVisible(false);

            MenuItem nav_custom_2 = menu.findItem(R.id.nav_custom_2);
            nav_custom_2.setTitle(((AppController) this.getApplication()).getContent_url2_text());
            if(((AppController) this.getApplication()).getContent_url2_text().equals(""))
                nav_custom_2.setVisible(false);

            MenuItem nav_custom_3 = menu.findItem(R.id.nav_custom_3);
            nav_custom_3.setTitle(((AppController) this.getApplication()).getContent_url3_text());
            if(((AppController) this.getApplication()).getContent_url3_text().equals(""))
                nav_custom_3.setVisible(false);

            MenuItem nav_custom_4 = menu.findItem(R.id.nav_custom_4);
            nav_custom_4.setTitle(((AppController) this.getApplication()).getContent_url4_text());
            if(((AppController) this.getApplication()).getContent_url4_text().equals(""))
                nav_custom_4.setVisible(false);

            MenuItem nav_custom_5 = menu.findItem(R.id.nav_custom_5);
            nav_custom_5.setTitle(((AppController) this.getApplication()).getContent_url5_text());
            if(((AppController) this.getApplication()).getContent_url5_text().equals(""))
                nav_custom_5.setVisible(false);

            //Set Navigation Menu title and sub title and background color
            View header = navigationView.inflateHeaderView(R.layout.nav_header_main);
            tvNavTitle = (TextView) header.findViewById(R.id.navTitle);
            tvNavTitle.setText(((AppController) this.getApplication()).getContent_title());
            tvNavSubTitle = (TextView) header.findViewById(R.id.navSubTitle);
            tvNavSubTitle.setText(((AppController) this.getApplication()).getContent_email());
            navLinearLayout = (LinearLayout) header.findViewById(R.id.navLinearLayout);
            navLinearLayout.setBackgroundColor(Color.parseColor(primaryColor));
        }else{
            //Disable Navigation Menu
            toggle.setDrawerIndicatorEnabled(false);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

        // Application Color
        if (Build.VERSION.SDK_INT >= 21) {
           int intPrimaryColor = Color.parseColor(primaryColor);
           int intDarkPrimaryColor = Color.parseColor(darkColor);

           //set for action bar
           float[] hsv = new float[3];
           Color.colorToHSV(intPrimaryColor,hsv);
           hsv[2] *= 0.8f; // value component
           intPrimaryColor = Color.HSVToColor(hsv);
           getWindow().setNavigationBarColor(intPrimaryColor); //Navigation Bar
           getWindow().setStatusBarColor(intDarkPrimaryColor); //Status Bar
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(primaryColor))); // Toolbar Method 3

        //Floating Button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Load First Fragment
        Bundle bundle = new Bundle();
        theURL = ((AppController) this.getApplication()).getContent_url();
        bundle.putString("theURL", theURL);
        bundle.putString("theTitle", getString(R.string.app_name));
        webViewFragment.setArguments(bundle);
        first_fragment = true;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frmContainer, webViewFragment, "WEBVIEW_FRAGMENT")
                .detach(webViewFragment)
                .attach(webViewFragment)
                .addToBackStack(null)
                .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        tag = null;
        first_fragment = false;

        if (id == R.id.nav_home) {
            toolbar.setSubtitle(((AppController) this.getApplication()).getContent_sub_title());
            Bundle bundle = new Bundle();
            theURL = ((AppController) this.getApplication()).getContent_url();
            bundle.putString("theURL", theURL);
            bundle.putString("theTitle", ((AppController) this.getApplication()).getContent_title());
            toolbar.setTitle(((AppController) this.getApplication()).getContent_title());
            toolbar.setSubtitle(((AppController) this.getApplication()).getContent_sub_title());
            webViewFragment.setArguments(bundle);
            tag = "WebViewFragment";
            first_fragment = true;
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                    .replace(R.id.frmContainer, webViewFragment, "WEBVIEW_FRAGMENT")
                    .detach(webViewFragment)
                    .attach(webViewFragment)
                    .commit();

            // Show Interstitial Ad
            if(((AppController) this.getApplication()).getContent_interstitial_ads().equals("1"))
            {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                    Log.d("MyTag", "The interstitial ad show successfully.");
                } else {
                    Log.d("MyTag", "The interstitial ad wasn't ready yet.");
                }
            }

        } else if (id == R.id.nav_custom_1) {
            toolbar.setSubtitle("");
            Bundle bundle = new Bundle();
            theURL = ((AppController) this.getApplication()).getContent_url1();
            bundle.putString("theURL", theURL);
            bundle.putString("theTitle", ((AppController) this.getApplication()).getContent_url1_text());
            toolbar.setTitle(((AppController) this.getApplication()).getContent_title());
            toolbar.setSubtitle(((AppController) this.getApplication()).getContent_url1_text());
            webViewFragment.setArguments(bundle);
            tag = "WebViewFragment";
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                    .replace(R.id.frmContainer, webViewFragment, "WEBVIEW_FRAGMENT")
                    .detach(webViewFragment)
                    .attach(webViewFragment)
                    .commit();


        } else if (id == R.id.nav_custom_2) {
            toolbar.setSubtitle("");
            Bundle bundle = new Bundle();
            theURL = ((AppController) this.getApplication()).getContent_url2();
            bundle.putString("theURL", theURL);
            bundle.putString("theTitle", ((AppController) this.getApplication()).getContent_url2_text());
            toolbar.setTitle(((AppController) this.getApplication()).getContent_title());
            toolbar.setSubtitle(((AppController) this.getApplication()).getContent_url2_text());
            webViewFragment.setArguments(bundle);
            tag = "WebViewFragment";
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                    .replace(R.id.frmContainer, webViewFragment, "WEBVIEW_FRAGMENT")
                    .detach(webViewFragment)
                    .attach(webViewFragment)
                    .commit();

        } else if (id == R.id.nav_custom_3) {
            toolbar.setSubtitle("");
            Bundle bundle = new Bundle();
            theURL = ((AppController) this.getApplication()).getContent_url3();
            bundle.putString("theURL", theURL);
            bundle.putString("theTitle", ((AppController) this.getApplication()).getContent_url3_text());
            toolbar.setTitle(((AppController) this.getApplication()).getContent_title());
            toolbar.setSubtitle(((AppController) this.getApplication()).getContent_url3_text());
            webViewFragment.setArguments(bundle);
            tag = "WebViewFragment";
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                    .replace(R.id.frmContainer, webViewFragment, "WEBVIEW_FRAGMENT")
                    .detach(webViewFragment)
                    .attach(webViewFragment)
                    .commit();

        } else if (id == R.id.nav_custom_4) {
            toolbar.setSubtitle("");
            Bundle bundle = new Bundle();
            theURL = ((AppController) this.getApplication()).getContent_url4();
            bundle.putString("theURL", theURL);
            bundle.putString("theTitle", ((AppController) this.getApplication()).getContent_url4_text());
            toolbar.setTitle(((AppController) this.getApplication()).getContent_title());
            toolbar.setSubtitle(((AppController) this.getApplication()).getContent_url4_text());
            webViewFragment.setArguments(bundle);
            tag = "WebViewFragment";
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                    .replace(R.id.frmContainer, webViewFragment, "WEBVIEW_FRAGMENT")
                    .detach(webViewFragment)
                    .attach(webViewFragment)
                    .commit();

        } else if (id == R.id.nav_custom_5) {
            toolbar.setSubtitle("");
            Bundle bundle = new Bundle();
            theURL = ((AppController) this.getApplication()).getContent_url5();
            bundle.putString("theURL", theURL);
            bundle.putString("theTitle", ((AppController) this.getApplication()).getContent_url5_text());
            toolbar.setTitle(((AppController) this.getApplication()).getContent_title());
            toolbar.setSubtitle(((AppController) this.getApplication()).getContent_url5_text());
            webViewFragment.setArguments(bundle);
            tag = "WebViewFragment";
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                    .replace(R.id.frmContainer, webViewFragment, "WEBVIEW_FRAGMENT")
                    .detach(webViewFragment)
                    .attach(webViewFragment)
                    .commit();

        } else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = getString(R.string.txt_share);
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.app_name);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));

        } else if (id == R.id.nav_exit) {
            finishAffinity(); // Close all activities
            System.exit(0);  // Closing files, releasing resources
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //==========================================================================//
    public void SetItemChecked(int position) {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(position).setChecked(true);
    }

    //==========================================================================//
    private void totalView(){
        Response.Listener<String> listener = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                // Total itemView + 1
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(getApplicationContext(), "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        StringRequest requestView = new StringRequest(Request.Method.POST, Config.TOTAL_CONTENT_VIEWED_URL + "?api_key=" + Config.API_KEY,listener,errorListener)
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String,String> params = new HashMap<>();
                params.put("content_id",contentId);
                return params;
            }
        };
        requestView.setRetryPolicy(new DefaultRetryPolicy(10000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(requestView);
    }


    //==========================================================================//
    public void versionCheck() {
        // Get versionCode from admin dashboard
        settingVersionCode = ((AppController) MainActivity.this.getApplication()).getSettingVersionCode();

        // Get versionCode from local app
        PackageManager manager = this.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
            appVersionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (appVersionCode < Integer.parseInt(settingVersionCode))
        {
            // Your app is old
            AlertDialog.Builder builderCheckUpdate = new AlertDialog.Builder(MainActivity.this);
            builderCheckUpdate.setTitle(getResources().getString(R.string.txt_check_update_title));
            builderCheckUpdate.setMessage(getResources().getString(R.string.txt_check_update_msg));
            builderCheckUpdate.setCancelable(false);

            builderCheckUpdate.setPositiveButton(
                    getResources().getString(R.string.txt_get_update),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.txt_update_url)));
                            startActivity(browserIntent);
                        }
                    });

            builderCheckUpdate.setNegativeButton(
                    getResources().getString(R.string.txt_later),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert1CheckUpdate = builderCheckUpdate.create();
            alert1CheckUpdate.show();
        }
    }


    //==========================================================================//
    public void maintenanceCheck() {
        // Get versionCode from local variable
        String settingMaintenance = ((AppController) this.getApplication()).getSettingAndroidMaintenance();
        String settingMaintenanceMessage = ((AppController) this.getApplication()).getSettingTextMaintenance();
        if (settingMaintenance.equals("1"))
        {
            // Maintenance mode is enable
            AlertDialog.Builder builderCheckUpdate = new AlertDialog.Builder(MainActivity.this);
            builderCheckUpdate.setTitle(getResources().getString(R.string.txt_maintenance_title));
            builderCheckUpdate.setMessage(settingMaintenanceMessage);
            builderCheckUpdate.setCancelable(false);

            builderCheckUpdate.setPositiveButton(
                    getResources().getString(R.string.txt_ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finishAffinity(); // Close all activities
                            System.exit(0);  // Closing files, releasing resources
                        }
                    });

            AlertDialog alert1CheckUpdate = builderCheckUpdate.create();
            alert1CheckUpdate.show();
        }
    }


    //==========================================================================//
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, handle accordingly
                    Log.d("MyTag", "Permission granted, handle accordingly: "+permissions[i]);
                } else {
                    // Permission denied, handle accordingly
                    Log.d("MyTag", "Permission denied, handle accordingly: "+permissions[i]);
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
