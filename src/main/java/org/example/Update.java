package org.example;

import org.example.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class Update extends Thread {

    @Override
    public void run() {
        int count = 0;

        super.run();
        while (true) {
            try {
                update(count);
                sleep(60*1000*60*24);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update(int count) {


        SessionFactory sessionFactory = null;
        try {
            sessionFactory = new Configuration().addAnnotatedClass(User.class).
                    addAnnotatedClass(Item.class).
                    addAnnotatedClass(Media.class).
                    addAnnotatedClass(Product.class).
                    addAnnotatedClass(Stock.class).
                    addAnnotatedClass(QueueRequests.class).
                    setProperty("hibernate.driver_class", Settings.getProperties("hibernate.driver_class")).
                    setProperty("hibernate.connection.url", Settings.getProperties("hibernate.connection.url")).
                    setProperty("hibernate.connection.username", Settings.getProperties("hibernate.connection.username")).
                    setProperty("hibernate.connection.password", Settings.getProperties("hibernate.connection.password")).
                    setProperty("hibernate.dialect", Settings.getProperties("hibernate.dialect")).
                    setProperty("hibernate.current_session_context_class", Settings.getProperties("hibernate.current_session_context_class")).
                    setProperty("hibernate.show_sql", Settings.getProperties("hibernate.show_sql")).
                    buildSessionFactory();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Session session = sessionFactory.getCurrentSession();

        try {
            session.beginTransaction();
            List<User> users = session.createQuery("FROM User").getResultList();

            if (count == 0) {
                for (User user : users) {
                    if (user.getNameShopWB() != null) {
                        if (user.getTokenStandartWB() != null) {
                            for (Product p: user.getProducts()) {
                                if (!p.getSupplierArticle().equals("")) {
                                    int sum = 0;
                                    for (Item i: p.getItems()) {
                                        if (i.getCdate().equals(URLRequestResponse.getDateCurrent())) {
                                            sum = sum + 1;
                                        }
                                    }
                                    if (p.getEnControlPrice() == 1) {
                                        if (sum < p.getMinOrder()) {
                                            session.save(new QueueRequests(user.getId(), "wb", "prices", p.getNmId(), String.valueOf((int) ((int) p.getPrice()*0.99)), String.valueOf(p.getDiscount())));
                                        } else if (sum > p.getMaxOrder())  {
                                            session.save(new QueueRequests(user.getId(), "wb", "prices", p.getNmId(), String.valueOf((int) ((int) p.getPrice()*1.01)), String.valueOf(p.getDiscount())));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            session.getTransaction().commit();
        } finally {
            sessionFactory.close();
        }
    }
}
