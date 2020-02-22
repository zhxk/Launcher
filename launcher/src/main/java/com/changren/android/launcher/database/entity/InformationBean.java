package com.changren.android.launcher.database.entity;

import java.util.List;

public class InformationBean extends DataSource {

    /**
     * total : 60
     * per_page : 1
     * current_page : 3
     * last_page : 60
     * data : [{"id":12,"title":"测试1","img":"http://cradmin.zouke.com/static/upload/7cdcab03954d5a07/3f1a52aa0839b9cc.jpg"}]
     */
    private int total;
    private String per_page;
    private int current_page;
    private int last_page;
    private List<DataBean> data;

    public InformationBean() {
        setItem_type("C");
    }

    public InformationBean(int total, String per_page, int current_page, int last_page, List<DataBean> data) {
        this.total = total;
        this.per_page = per_page;
        this.current_page = current_page;
        this.last_page = last_page;
        this.data = data;

        setItem_type("C");
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getPer_page() {
        return per_page;
    }

    public void setPer_page(String per_page) {
        this.per_page = per_page;
    }

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public int getLast_page() {
        return last_page;
    }

    public void setLast_page(int last_page) {
        this.last_page = last_page;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "InformationBean{" +
                "total=" + total +
                ", per_page='" + per_page + '\'' +
                ", current_page=" + current_page +
                ", last_page=" + last_page +
                ", data=" + data.size() +
                '}';
    }

    @Override
    public void clear() {
        data = null;
    }

    public static class DataBean {
        /**
         * id : 12
         * title : 测试1
         * img : http://cradmin.zouke.com/static/upload/7cdcab03954d5a07/3f1a52aa0839b9cc.jpg
         */

        private int id;
        private String title;
        private String img;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }
    }
}
