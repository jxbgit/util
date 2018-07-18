package com.jimlp.util.web.pojo;

/**
 * ip 基本信息类
 *
 * <br>
 * 创建时间 2017年12月21日上午11:46:23
 *
 * @author jxb
 *
 */
public final class IpInfo {
    private String ip;
    private String isp;
    private String country;
    private String area;
    private String province;
    private String city;
    private String county;

    public IpInfo() {
        super();
    }

    /**
     * 获取 ip
     *
     * @return ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * 设置 ip
     * 
     * @param ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * 获取 运行商名称
     *
     * @return 运行商名称
     */
    public String getIsp() {
        return isp;
    }

    /**
     * 设置 运行商名称
     * 
     * @param 运行商名称
     */
    public void setIsp(String isp) {
        this.isp = isp;
    }

    /**
     * 获取 国家名称
     *
     * @return 国家名称
     */
    public String getCountry() {
        return country;
    }

    /**
     * 设置 国家名称
     * 
     * @param 国家名称
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * 获取 地区名称
     *
     * @return 地区名称
     */
    public String getArea() {
        return area;
    }

    /**
     * 设置 地区名称
     * 
     * @param 地区名称
     */
    public void setArea(String area) {
        this.area = area;
    }

    /**
     * 获取 省份名称
     *
     * @return 省份名称
     */
    public String getProvince() {
        return province;
    }

    /**
     * 设置 省份名称
     * 
     * @param 省份名称
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * 获取 城市名称
     *
     * @return 城市名称
     */
    public String getCity() {
        return city;
    }

    /**
     * 设置 城市名称
     * 
     * @param 城市名称
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * 获取 区县名称
     *
     * @return 区县名称
     */
    public String getCounty() {
        return county;
    }

    /**
     * 设置 区县名称
     * 
     * @param 区县名称
     */
    public void setCounty(String county) {
        this.county = county;
    }

    @Override
    public String toString() {
        return "IpInfo [ip=" + ip + ", isp=" + isp + ", country=" + country + ", area=" + area + ", province=" + province + ", city=" + city + ", county=" + county + "]";
    }

}
