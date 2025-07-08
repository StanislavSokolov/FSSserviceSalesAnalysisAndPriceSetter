package org.example.model;

import javax.persistence.*;

@Entity
@Table(name = "queuerequests")
public class QueueRequests {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "clientId")
    private int clientId;
    @Column(name = "shop")
    private String shop;
    @Column(name = "method")
    private String method;
    @Column(name = "article")
    private String article;
    @Column(name = "dataToChange")
    private String dataToChange;
    @Column(name = "addAttribute")
    private String addAttribute;

    public String getAddAttribute() {
        return addAttribute;
    }

    public void setAddAttribute(String addAttribute) {
        this.addAttribute = addAttribute;
    }

    public QueueRequests() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getDataToChange() {
        return dataToChange;
    }

    public void setDataToChange(String dataToChange) {
        this.dataToChange = dataToChange;
    }

    public QueueRequests(int clientId, String shop, String method, String article, String dataToChange, String addAttribute) {
        this.clientId = clientId;
        this.shop = shop;
        this.method = method;
        this.article = article;
        this.dataToChange = dataToChange;
        this.addAttribute = addAttribute;
    }
}
