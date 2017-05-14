package com.tudor.weather.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tudor.weather.R;
import com.tudor.weather.db.City;
import com.tudor.weather.db.County;
import com.tudor.weather.db.Province;
import com.tudor.weather.util.ConstUtil;
import com.tudor.weather.util.OkUtil;
import com.tudor.weather.util.Utility;
import com.tudor.weather.util.WeatherAPI;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by LiDongduo on 2017/4/24.
 */

public class ChooseAreaFragment extends Fragment {
    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTY = 2;
    private ImageView mIvChooseArea;
    private TextView mTvChooseAreaTittle;
    private ListView mLvChooseArea;
    private List<String> mList = new ArrayList<>();
    private List<Province> mProvinces;
    private List<City> mCities;
    private List<County> mCounties;
    private Province selectedProvince;
    private City selectedCity;
    private int currentLevel;
    private ArrayAdapter<String> mAdapter;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_choose_area,container,false);
        mIvChooseArea = (ImageView) view.findViewById(R.id.iv_chooseArea);
        mTvChooseAreaTittle = (TextView) view.findViewById(R.id.tv_chooseArea_tittle);
        mLvChooseArea = (ListView) view.findViewById(R.id.lv_chooseArea);
        mAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,mList);
        mLvChooseArea.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLvChooseArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = mProvinces.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = mCities.get(position);
                    queryCounties();
                }
            }
        });
        mIvChooseArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    private void queryCounties() {
        mTvChooseAreaTittle.setText(selectedCity.getCityName());
        mIvChooseArea.setVisibility(View.VISIBLE);
        mCounties = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId()))
                .find(County.class);
        if (mCounties.size() >0) {
            mList.clear();
            for (County county : mCounties) {
                mList.add(county.getCountyName());
            }
            mAdapter.notifyDataSetChanged();
            mLvChooseArea.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = WeatherAPI.HTTP + provinceCode + "/" + cityCode;
            queryForServer(address, ConstUtil.STR_COUNTY);
        }
    }

    private void queryCities() {
        mTvChooseAreaTittle.setText(selectedProvince.getProvinceName());
        mIvChooseArea.setVisibility(View.VISIBLE);
        mCities = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId()))
                .find(City.class);
        if (mCities.size() >0) {
            mList.clear();
            for (City city : mCities) {
                mList.add(city.getCityName());
            }
            mAdapter.notifyDataSetChanged();
            mLvChooseArea.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = WeatherAPI.HTTP + provinceCode;
            queryForServer(address,ConstUtil.STR_CITY);
        }
    }

    private void queryProvinces() {
        mTvChooseAreaTittle.setText("中国");
        mIvChooseArea.setVisibility(View.GONE);
        mProvinces = DataSupport.findAll(Province.class);
        if (mProvinces.size()>0) {
            mList.clear();
            for (Province province : mProvinces) {
                mList.add(province.getProvinceName());
            }
            mAdapter.notifyDataSetChanged();
            mLvChooseArea.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryForServer(WeatherAPI.HTTP,ConstUtil.STR_PROVINCE);
        }
    }

    private void queryForServer(String address, final String type) {
        showProgressDlg();
        OkUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDlg();
                        Toast.makeText(getContext(), ConstUtil.STR_LOAD_FAILED, Toast.LENGTH_SHORT)
                                .show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resText = response.body().string();
                boolean result = false;
                if (type.equals(ConstUtil.STR_PROVINCE)) {
                    result = Utility.handleProvince(resText);
                } else if (type.equals(ConstUtil.STR_COUNTY)) {
                    result = Utility.handleCity(resText,selectedProvince.getId());
                } else if (type.equals(ConstUtil.STR_CITY)) {
                    result = Utility.handleCounty(resText,selectedCity.getId());
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDlg();
                            if (type.equals(ConstUtil.STR_PROVINCE)) {
                                queryProvinces();
                            } else if (type.equals(ConstUtil.STR_COUNTY)) {
                                queryCities();
                            } else if (type.equals(ConstUtil.STR_CITY)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    private void showProgressDlg() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage(ConstUtil.STR_LOADING);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDlg(){
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
