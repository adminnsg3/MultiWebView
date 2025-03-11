package com.inw24.multiwebview.utils;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class AppController extends Application
{
	public static final String TAG = AppController.class.getSimpleName();
	private RequestQueue mRequestQueue;
	private static AppController mInstance;

	private String content_id;
	private String content_user_id;
	private String content_title;
	private String content_sub_title;
	private String content_description;
	private String content_property1;
	private String content_property2;
	private String content_orientation;
	private String content_primary_color;
	private String content_dark_color;
	private String content_accent_color;
	private String content_type_id;
	private String content_access;
	private String content_category_id;
	private String content_user_role_id;
	private String content_image;
	private String content_url;
	private String content_url1;
	private String content_url1_text;
	private String content_url2;
	private String content_url2_text;
	private String content_url3;
	private String content_url3_text;
	private String content_url4;
	private String content_url4_text;
	private String content_url5;
	private String content_url5_text;
	private String content_email;
	private String content_viewed;
	private String content_featured;
	private String content_special;
	private String content_publish_date;
	private String content_rtl;
	private String content_fullscreen;
	private String content_toolbar;
	private String content_banner_ads;
	private String content_interstitial_ads;
	private String content_admob_app_id;
	private String content_admob_banner_unit_id;
	private String content_admob_interstitial_unit_id;
	private String content_menu;
	private String content_loader;
	private String content_pull_to_refresh;
	private String content_status;
	private String settingVersionCode;
	private String settingAndroidMaintenance;
	private String settingTextMaintenance;
    private String content_onesignal_app_id;

    public String getContent_onesignal_app_id() {
        return content_onesignal_app_id;
    }

    public void setContent_onesignal_app_id(String content_onesignal_app_id) {
        this.content_onesignal_app_id = content_onesignal_app_id;
    }

    public String getContent_loader() {
		return content_loader;
	}

	public void setContent_loader(String content_loader) {
		this.content_loader = content_loader;
	}

	public String getContent_pull_to_refresh() {
		return content_pull_to_refresh;
	}

	public void setContent_pull_to_refresh(String content_pull_to_refresh) {
		this.content_pull_to_refresh = content_pull_to_refresh;
	}

	public String getContent_fullscreen() {
		return content_fullscreen;
	}

	public void setContent_fullscreen(String content_fullscreen) {
		this.content_fullscreen = content_fullscreen;
	}

	public String getContent_toolbar() {
		return content_toolbar;
	}

	public void setContent_toolbar(String content_toolbar) {
		this.content_toolbar = content_toolbar;
	}

	public String getSettingVersionCode() {
		return settingVersionCode;
	}

	public void setSettingVersionCode(String settingVersionCode) {
		this.settingVersionCode = settingVersionCode;
	}

	public String getSettingAndroidMaintenance() {
		return settingAndroidMaintenance;
	}

	public void setSettingAndroidMaintenance(String settingAndroidMaintenance) {
		this.settingAndroidMaintenance = settingAndroidMaintenance;
	}

	public String getSettingTextMaintenance() {
		return settingTextMaintenance;
	}

	public void setSettingTextMaintenance(String settingTextMaintenance) {
		this.settingTextMaintenance = settingTextMaintenance;
	}

	public String getContent_admob_app_id() {
		return content_admob_app_id;
	}

	public void setContent_admob_app_id(String content_admob_app_id) {
		this.content_admob_app_id = content_admob_app_id;
	}

	public String getContent_admob_banner_unit_id() {
		return content_admob_banner_unit_id;
	}

	public void setContent_admob_banner_unit_id(String content_admob_banner_unit_id) {
		this.content_admob_banner_unit_id = content_admob_banner_unit_id;
	}

	public String getContent_admob_interstitial_unit_id() {
		return content_admob_interstitial_unit_id;
	}

	public void setContent_admob_interstitial_unit_id(String content_admob_interstitial_unit_id) {
		this.content_admob_interstitial_unit_id = content_admob_interstitial_unit_id;
	}

	public String getContent_banner_ads() {
		return content_banner_ads;
	}

	public void setContent_banner_ads(String content_banner_ads) {
		this.content_banner_ads = content_banner_ads;
	}

	public String getContent_interstitial_ads() {
		return content_interstitial_ads;
	}

	public void setContent_interstitial_ads(String content_interstitial_ads) {
		this.content_interstitial_ads = content_interstitial_ads;
	}

	public String getContent_url1_text() {
		return content_url1_text;
	}

	public void setContent_url1_text(String content_url1_text) {
		this.content_url1_text = content_url1_text;
	}

	public String getContent_url2_text() {
		return content_url2_text;
	}

	public void setContent_url2_text(String content_url2_text) {
		this.content_url2_text = content_url2_text;
	}

	public String getContent_url3_text() {
		return content_url3_text;
	}

	public void setContent_url3_text(String content_url3_text) {
		this.content_url3_text = content_url3_text;
	}

	public String getContent_url4_text() {
		return content_url4_text;
	}

	public void setContent_url4_text(String content_url4_text) {
		this.content_url4_text = content_url4_text;
	}

	public String getContent_url5_text() {
		return content_url5_text;
	}

	public void setContent_url5_text(String content_url5_text) {
		this.content_url5_text = content_url5_text;
	}

	public String getContent_id() {
		return content_id;
	}

	public void setContent_id(String content_id) {
		this.content_id = content_id;
	}

	public String getContent_user_id() {
		return content_user_id;
	}

	public void setContent_user_id(String content_user_id) {
		this.content_user_id = content_user_id;
	}

	public String getContent_title() {
		return content_title;
	}

	public void setContent_title(String content_title) {
		this.content_title = content_title;
	}

	public String getContent_sub_title() {
		return content_sub_title;
	}

	public void setContent_sub_title(String content_sub_title) {
		this.content_sub_title = content_sub_title;
	}

	public String getContent_description() {
		return content_description;
	}

	public void setContent_description(String content_description) {
		this.content_description = content_description;
	}

	public String getContent_property1() {
		return content_property1;
	}

	public void setContent_property1(String content_property1) {
		this.content_property1 = content_property1;
	}

	public String getContent_property2() {
		return content_property2;
	}

	public void setContent_property2(String content_property2) {
		this.content_property2 = content_property2;
	}

	public String getContent_orientation() {
		return content_orientation;
	}

	public void setContent_orientation(String content_orientation) {
		this.content_orientation = content_orientation;
	}

	public String getContent_primary_color() {
		return content_primary_color;
	}

	public void setContent_primary_color(String content_primary_color) {
		this.content_primary_color = content_primary_color;
	}

	public String getContent_dark_color() {
		return content_dark_color;
	}

	public void setContent_dark_color(String content_dark_color) {
		this.content_dark_color = content_dark_color;
	}

	public String getContent_accent_color() {
		return content_accent_color;
	}

	public void setContent_accent_color(String content_accent_color) {
		this.content_accent_color = content_accent_color;
	}


	public String getContent_type_id() {
		return content_type_id;
	}

	public void setContent_type_id(String content_type_id) {
		this.content_type_id = content_type_id;
	}

	public String getContent_access() {
		return content_access;
	}

	public void setContent_access(String content_access) {
		this.content_access = content_access;
	}

	public String getContent_category_id() {
		return content_category_id;
	}

	public void setContent_category_id(String content_category_id) {
		this.content_category_id = content_category_id;
	}

	public String getContent_user_role_id() {
		return content_user_role_id;
	}

	public void setContent_user_role_id(String content_user_role_id) {
		this.content_user_role_id = content_user_role_id;
	}

	public String getContent_image() {
		return content_image;
	}

	public void setContent_image(String content_image) {
		this.content_image = content_image;
	}

	public String getContent_url() {
		return content_url;
	}

	public void setContent_url(String content_url) {
		this.content_url = content_url;
	}

	public String getContent_url1() {
		return content_url1;
	}

	public void setContent_url1(String content_url1) {
		this.content_url1 = content_url1;
	}

	public String getContent_url2() {
		return content_url2;
	}

	public void setContent_url2(String content_url2) {
		this.content_url2 = content_url2;
	}

	public String getContent_url3() {
		return content_url3;
	}

	public void setContent_url3(String content_url3) {
		this.content_url3 = content_url3;
	}

	public String getContent_url4() {
		return content_url4;
	}

	public void setContent_url4(String content_url4) {
		this.content_url4 = content_url4;
	}

	public String getContent_url5() {
		return content_url5;
	}

	public void setContent_url5(String content_url5) {
		this.content_url5 = content_url5;
	}

	public String getContent_email() {
		return content_email;
	}

	public void setContent_email(String content_email) {
		this.content_email = content_email;
	}

	public String getContent_viewed() {
		return content_viewed;
	}

	public void setContent_viewed(String content_viewed) {
		this.content_viewed = content_viewed;
	}

	public String getContent_featured() {
		return content_featured;
	}

	public void setContent_featured(String content_featured) {
		this.content_featured = content_featured;
	}

	public String getContent_special() {
		return content_special;
	}

	public void setContent_special(String content_special) {
		this.content_special = content_special;
	}

	public String getContent_publish_date() {
		return content_publish_date;
	}

	public void setContent_publish_date(String content_publish_date) {
		this.content_publish_date = content_publish_date;
	}

	public String getContent_rtl() {
		return content_rtl;
	}

	public void setContent_rtl(String content_rtl) {
		this.content_rtl = content_rtl;
	}

	public String getContent_menu() {
		return content_menu;
	}

	public void setContent_menu(String content_menu) {
		this.content_menu = content_menu;
	}

	public String getContent_status() {
		return content_status;
	}

	public void setContent_status(String content_status) {
		this.content_status = content_status;
	}

	//Set and Get local variable
	// To Set
	//((AppController) this.getApplication()).setSomeVariable("foo");
	//((AppController) MainActivity.this.getApplication()).setUserId(jsonObject.getString("user_id"));

	// To Get
	//String s = ((AppController) this.getApplication()).getSomeVariable();

	@Override
	public void onCreate()
	{
		super.onCreate();
		mInstance = this;
		//For Custom Font

	}

	public static synchronized AppController getInstance()
	{
		return mInstance;
	}

	public RequestQueue getRequestQueue()
	{
		if (mRequestQueue == null)
		{
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag)
	{
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req)
	{
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag)
	{
		if (mRequestQueue != null)
		{
			mRequestQueue.cancelAll(tag);
		}
	}

	public void cancelPendingRequests()
	{
		if (mRequestQueue != null)
		{
			mRequestQueue.cancelAll(TAG);
		}
	}
}