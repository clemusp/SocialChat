package com.optic.socialchat.models;

public class NewPublic {

    private String id;
    private String menssagepublic;
    private String image1;
    private String image2;
    private String idUser;
    private String category;
    private long timestamp;

    public NewPublic(){

    }

    public NewPublic(String id, String menssagepublic, String image1, String image2, String idUser, String category, long timestamp) {

        this.id = id;
        this.menssagepublic = menssagepublic;
        this.image1 = image1;
        this.image2 = image2;
        this.idUser = idUser;
        this.category = category;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMenssagepublic() {
        return menssagepublic;
    }

    public void setMenssagepublic(String menssagepublic) {
        this.menssagepublic = menssagepublic;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
