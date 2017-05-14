package com.tudor.weather.db;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by LiDongduo on 2017/4/24.
 */

public class Province extends DataSupport implements Serializable {

    private int id;
    private String provinceName;
    private int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
