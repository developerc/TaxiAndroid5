package com.taxiandroid.ru.taxiandr;



/**
 * Created by saperov on 13.11.15.
 */
public class Taximeter {
    private int _id;
    private String mDtim;
    private String mPstart;
    private String mSumma;
    private String mEtim;
    private String mDgor;
    private String mDprigor;

    // Пустой констуктор
    public Taximeter() {

    }

    // Конструктор с параметрами
    public Taximeter(int id, String dtim, String pstart, String summa, String etim, String dgor, String dprigor) {
        this._id = id;
        this.mDtim = dtim;
        this.mPstart = pstart;
        this.mSumma = summa;
        this.mEtim = etim;
        this.mDgor = dgor;
        this.mDprigor = dprigor;
    }

    // Конструктор с параметрами
    public Taximeter(String dtim, String pstart, String summa, String etim, String dgor, String dprigor) {
        this.mDtim = dtim;
        this.mPstart = pstart;
        this.mSumma = summa;
        this.mEtim = etim;
        this.mDgor = dgor;
        this.mDprigor = dprigor;
    }

    // Создание геттеров-сеттеров

    public int getID() {
        return this._id;
    }

    public void setID(int id) {
        this._id = id;
    }

    public String getDtim() {
        return this.mDtim;
    }

    public void setDtim(String dtim) {
        this.mDtim = dtim;
    }

    public String getPstart() {
        return this.mPstart;
    }

    public void setPstart(String pstart) {
        this.mPstart = pstart;
    }

    public String getSumma() {
        return this.mSumma;
    }

    public void setSumma(String summa) {
        this.mSumma = summa;
    }

    public String getEtim() {
        return this.mEtim;
    }

    public void setEtim(String etim) {
        this.mEtim = etim;
    }

    public String getmDgor() {
        return this.mDgor;
    }

    public void setmDgor(String dgor) {
        this.mDgor = dgor;
    }

    public String getmDprigor() {
        return this.mDprigor;
    }

    public void setmDprigor(String dprigor) {
        this.mDprigor = dprigor;
    }
    @Override
    public String toString() {
        return this._id + " , " + this.mDtim + " , " + this.mPstart + " , " + this.mSumma + " , "+this.mDgor + " , "+this.mDprigor +" , "+ this.mEtim;
    }
}
