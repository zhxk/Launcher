package com.changren.android.launcher.database.entity;


import java.util.List;

public class PlanTodayBean extends DataSource {
    private List<PlanBean> datas;

    public PlanTodayBean() {
        setItem_type("P");
    }

    public PlanTodayBean(List<PlanBean> datas) {
        this.datas = datas;

        setItem_type("P");
    }

    public List<PlanBean> getDatas() {
        return datas;
    }

    public void setDatas(List<PlanBean> datas) {
        this.datas = datas;
    }

    @Override
    public void clear() {
        datas = null;
    }

    /**
     * type : 3
     * time : 07:00
     * clocked : 1
     * clock_time : 2018-09-25 14:09:42
     * take_type : 1
     * take_number : 1
     * drug_name : 重组人胰岛素注射液
     * product_name : 优泌林R
     * norm : 笔芯30ml/300单位/支；
     */

    public static class PlanBean{
        private int type;
        private String time;
        private int clocked;
        private String clock_time;
        private int take_type;
        private int take_number;
        private String drug_name;
        private String product_name;
        private String norm;
        private boolean flag;

        public boolean isFlag() {
            return flag;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getClocked() {
            return clocked;
        }

        public void setClocked(int clocked) {
            this.clocked = clocked;
        }

        public String getClock_time() {
            return clock_time;
        }

        public void setClock_time(String clock_time) {
            this.clock_time = clock_time;
        }

        public int getTake_type() {
            return take_type;
        }

        public void setTake_type(int take_type) {
            this.take_type = take_type;
        }

        public int getTake_number() {
            return take_number;
        }

        public void setTake_number(int take_number) {
            this.take_number = take_number;
        }

        public String getDrug_name() {
            return drug_name;
        }

        public void setDrug_name(String drug_name) {
            this.drug_name = drug_name;
        }

        public String getProduct_name() {
            return product_name;
        }

        public void setProduct_name(String product_name) {
            this.product_name = product_name;
        }

        public String getNorm() {
            return norm;
        }

        public void setNorm(String norm) {
            this.norm = norm;
        }

        @Override
        public String toString() {
            return "PlanTodayBean{" +
                    "type=" + type +
                    ", time='" + time + '\'' +
                    ", clocked=" + clocked +
                    ", clock_time='" + clock_time + '\'' +
                    ", take_type=" + take_type +
                    ", take_number=" + take_number +
                    ", drug_name='" + drug_name + '\'' +
                    ", product_name='" + product_name + '\'' +
                    ", norm='" + norm + '\'' +
                    ", flag=" + flag +
                    '}';
        }
    }
}
