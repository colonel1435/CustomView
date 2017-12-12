package com.zero.customview.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.zero.customview.R;
import com.zero.customview.offlinemap.OfflineMapActivity;
import com.zero.customview.offlinemap.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HelloMapActivity extends AppCompatActivity implements LocationSource,
        AMapLocationListener, AMap.OnMapTouchListener, AMap.OnMapLoadedListener{
    private final String TAG = this.getClass().getSimpleName() + "@wumin";
    private final String MAP_DIR = "amapSunland";
    @BindView(R.id.map)
    MapView map;
    @BindView(R.id.bt_offline)
    Button btOffline;

    private Context mContext;
    private AMap aMap;
    private UiSettings mUiSettings;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private boolean isFirstLoc = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_map);
        ButterKnife.bind(this);

        mContext = this;
//        LatLng centerBJPoint= new LatLng(36.07,103.82);
//        AMapOptions mapOptions = new AMapOptions();
//        mapOptions.camera(new CameraPosition(centerBJPoint, 10f, 0, 0));
//        MapView mapView = new MapView(this, mapOptions);
//        mapView.onCreate(savedInstanceState);

        map.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        mlocationClient = new AMapLocationClient( getApplicationContext() );
        mlocationClient.setLocationListener( this );

        if (aMap == null) {
            aMap = map.getMap();
            mUiSettings = aMap.getUiSettings();
        }

        aMap.setLocationSource(this);
        mUiSettings.setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);


        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.interval(2000);
        aMap.setMyLocationStyle(myLocationStyle);

        setMapOption();
    }

    public void setMapOption() {
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode( AMapLocationClientOption.AMapLocationMode.Hight_Accuracy );
        mLocationOption.setNeedAddress( true );
        mLocationOption.setOnceLocation( false );
        mLocationOption.setGpsFirst(true);
        mLocationOption.setMockEnable( false );
        mLocationOption.setInterval( 5000 );
        mlocationClient.setLocationOption( mLocationOption );
        mlocationClient.startLocation();
    }


    /**
     * marker设置
     *
     * @param
     */
    private MarkerOptions getMarkerOptions(AMapLocation amapLocation) {
        MarkerOptions options = new MarkerOptions();
        options.position( new LatLng( amapLocation.getLatitude(), amapLocation.getLongitude() ) );
        StringBuffer buffer = new StringBuffer();
        buffer.append( amapLocation.getCountry() + "" + amapLocation.getProvince() + "" + amapLocation.getCity() + "" + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum() );
        options.title( buffer.toString() );
        options.snippet( "Marker" );
        options.period( 60 );

        return options;

    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        map.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        map.onSaveInstanceState(outState);
    }

    @OnClick(R.id.bt_offline)
    public void onViewClicked() {
        Intent intent = new Intent(HelloMapActivity.this, OfflineMapActivity.class);
        startActivity(intent);
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        Log.d(TAG, "activate: ...");
    }

    @Override
    public void deactivate() {
        Log.d(TAG, "deactivate: ");
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        Log.d(TAG, "onLocationChanged: ");
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                aMapLocation.getLatitude();
                aMapLocation.getLongitude();
                aMapLocation.getAccuracy();
                aMapLocation.getAddress();
                aMapLocation.getCountry();
                aMapLocation.getProvince();
                aMapLocation.getCity();
                aMapLocation.getDistrict();
                aMapLocation.getStreet();
                aMapLocation.getCityCode();
                aMapLocation.getAdCode();
                aMapLocation.getFloor();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(aMapLocation.getTime());
                df.format(date);
                if (isFirstLoc) {
                    aMap.moveCamera( CameraUpdateFactory.newLatLngZoom( new LatLng( aMapLocation.getLatitude(), aMapLocation.getLongitude() ), 17 ) );
                    aMap.moveCamera( CameraUpdateFactory.changeLatLng( new LatLng( aMapLocation.getLatitude(), aMapLocation.getLongitude() ) ) );
                    mListener.onLocationChanged( aMapLocation );
                    aMap.addMarker( getMarkerOptions( aMapLocation ) );
                    isFirstLoc = false;
                }
                Log.d(TAG, "onLocationChanged: " + aMapLocation.toString());
            } else {
                Log.d(TAG, "onLocationChanged: ");
                ToastUtil.showShortToast(mContext, "Location failed! \n" + aMapLocation.getErrorInfo());
            }

        }
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        Log.d(TAG, "onTouch: ");
    }

    @Override
    public void onMapLoaded() {
        Log.d(TAG, "onMapLoaded: ");
        ToastUtil.showShortToast(mContext, "Map loaded finished!");
    }
}
