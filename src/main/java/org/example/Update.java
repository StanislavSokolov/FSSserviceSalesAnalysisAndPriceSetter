package org.example;

import org.example.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Update extends Thread {

    private int MINUTE_TO_PERFORM_ANALYSIS = 30;
    private int HOUR_TO_PERFORM_ANALYSIS = 23;

    private int MINUTE_TO_REDUCE_DISCOUNT = 0;
    private int HOUR_TO_REDUCE_DISCOUNT = 4;

    private int MINUTE_TO_INCREASE_DISCOUNT = 0;
    private int HOUR_TO_INCREASE_DISCOUNT = 8;

    @Override
    public void run() {
        int count = 0;

        super.run();
        while (true) {
            try {
                LocalTime currentTime = LocalTime.now();
                int currentMinute = currentTime.getMinute();
                int currentHour = currentTime.getHour();
                if ((currentMinute == MINUTE_TO_PERFORM_ANALYSIS) && (currentHour == HOUR_TO_PERFORM_ANALYSIS)) {
                    update(0);
                } else if ((currentMinute == MINUTE_TO_REDUCE_DISCOUNT ) && (currentHour == HOUR_TO_REDUCE_DISCOUNT)) {
                    update(1);
                } else if ((currentMinute == MINUTE_TO_INCREASE_DISCOUNT ) && (currentHour == HOUR_TO_INCREASE_DISCOUNT)) {
                    update(2);
                }
                sleep(50*1000);
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
                    String text = "";
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
                                        int stocks = 0;
                                        for (Stock s: p.getStocks()) {
                                            stocks = stocks + s.getQuantity();
                                        }
                                        if (stocks > p.getMinOrder()) {
                                            if (sum < p.getMinOrder()) {
                                                text = text + "\n" + "Уменьшена стоимость " + p.getSubject() + " " + p.getSupplierArticle();
                                                session.save(new QueueRequests(user.getId(), "wb", "prices", p.getNmId(), String.valueOf((int) ((int) p.getPrice() * 0.98)), String.valueOf(p.getDiscount())));
                                            } else if (sum > p.getMaxOrder()) {
                                                text = text + "\n" + "Увеличена стоимость " + p.getSubject() + " " + p.getSupplierArticle();
                                                if (p.getPrice() * 0.03 < 15) {
                                                    session.save(new QueueRequests(user.getId(), "wb", "prices", p.getNmId(), String.valueOf((int) ((int) p.getPrice() + 15)), String.valueOf(p.getDiscount())));
                                                } else {
                                                    session.save(new QueueRequests(user.getId(), "wb", "prices", p.getNmId(), String.valueOf((int) ((int) p.getPrice() * 1.03)), String.valueOf(p.getDiscount())));
                                                }
                                            } else {
                                                text = text + "\n" + "Стоимость не изменилась " + p.getSubject() + " " + p.getSupplierArticle();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    System.out.println(text);
                }
            } else if (count == 1) {
                for (User user : users) {
                    String text = "";
                    if (user.getNameShopWB() != null) {
                        if (user.getTokenStandartWB() != null) {
                            for (Product p: user.getProducts()) {
                                if (!p.getSupplierArticle().equals("")) {
                                    if (p.getEnChangeDiscount() == 1) {
                                        if (p.getDiscount() > 22) {
                                            session.save(new QueueRequests(user.getId(), "wb", "updateDiscounts", p.getNmId(), String.valueOf(p.getDiscount() - 23), String.valueOf(p.getPrice())));
                                            p.setStatusChangeDiscount(1);
                                            session.save(p);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    System.out.println(text);
                }
            } else if (count == 2) {
            for (User user : users) {
                String text = "";
                if (user.getNameShopWB() != null) {
                    if (user.getTokenStandartWB() != null) {
                        for (Product p: user.getProducts()) {
                            if (!p.getSupplierArticle().equals("")) {
                                if (p.getEnChangeDiscount() == 1) {
                                    if (p.getStatusChangeDiscount() == 1) {
                                        session.save(new QueueRequests(user.getId(), "wb", "updateDiscounts", p.getNmId(), String.valueOf(p.getDiscount() + 23), String.valueOf(p.getPrice())));
                                        p.setStatusChangeDiscount(0);
                                        session.save(p);
                                    }
                                }
                            }
                        }
                    }
                }
                System.out.println(text);
            }
        }
            session.getTransaction().commit();
        } finally {
            sessionFactory.close();
        }
    }
}
