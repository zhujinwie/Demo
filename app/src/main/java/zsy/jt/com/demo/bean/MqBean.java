package zsy.jt.com.demo.bean;

import java.io.Serializable;

/**
 * Created by ZhaoShengYang
 * email 99720140@qq.com
 */

public class MqBean implements Serializable {

    /**
     * mq_image : 头像
     * mq_name : 姓名
     * mq_type : 类型 0表示通过✔️ 1表示不通过✘    2表示账号已过期
     * mq_number : 编号
     * mq_section : 部门
     * mq_message : 信息
     */

    private String mq_image;
    private String mq_name;
    private String mq_type;
    private String mq_number;
    private String mq_section;
    private String mq_message;
    private long date;

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getMq_image() {
        return mq_image;
    }

    public void setMq_image(String mq_image) {
        this.mq_image = mq_image;
    }

    public String getMq_name() {
        return mq_name;
    }

    public void setMq_name(String mq_name) {
        this.mq_name = mq_name;
    }

    public String getMq_type() {
        return mq_type;
    }

    public void setMq_type(String mq_type) {
        this.mq_type = mq_type;
    }

    public String getMq_number() {
        return mq_number;
    }

    public void setMq_number(String mq_number) {
        this.mq_number = mq_number;
    }

    public String getMq_section() {
        return mq_section;
    }

    public void setMq_section(String mq_section) {
        this.mq_section = mq_section;
    }

    public String getMq_message() {
        return mq_message;
    }

    public void setMq_message(String mq_message) {
        this.mq_message = mq_message;
    }

    @Override
    public String toString() {
        return "MqBean{" +
                "mq_image='" + mq_image + '\'' +
                ", mq_name='" + mq_name + '\'' +
                ", mq_type='" + mq_type + '\'' +
                ", mq_number='" + mq_number + '\'' +
                ", mq_section='" + mq_section + '\'' +
                ", mq_message='" + mq_message + '\'' +
                '}';
    }
}
