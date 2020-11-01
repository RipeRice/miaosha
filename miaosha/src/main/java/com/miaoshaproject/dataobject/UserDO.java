package com.miaoshaproject.dataobject;

public class UserDO {

    private Integer id;


    private String name;


    private Integer age;


    private Byte gender;


    private String telphone;


    private String registerMode;


    private String thirdPartId;


    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }


    public Integer getAge() {
        return age;
    }


    public void setAge(Integer age) {
        this.age = age;
    }


    public Byte getGender() {
        return gender;
    }


    public void setGender(Byte gender) {
        this.gender = gender ;
    }


    public String getTelphone() {
        return telphone;
    }


    public void setTelphone(String telphone) {
        this.telphone = telphone == null ? null : telphone.trim();
    }


    public String getRegisterMode() {
        return registerMode;
    }


    public void setRegisterMode(String registerMode) {
        this.registerMode = registerMode == null ? null : registerMode.trim();
    }


    public String getThirdPartId() {
        return thirdPartId;
    }


    public void setThirdPartId(String thirdPartId) {
        this.thirdPartId = thirdPartId == null ? null : thirdPartId.trim();
    }
}