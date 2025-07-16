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

    @Override
    public void run() {
        int count = 0;

        super.run();
        while (true) {
            try {
                LocalTime currentTime = LocalTime.now();
                int minuteToCompare = 55;
                int currentMinute = currentTime.getMinute();
                int hourToCompare = 23;
                int currentHour = currentTime.getHour();
                if ((currentMinute == minuteToCompare) && (currentHour == hourToCompare)) {
                    update(count);
                    sleep(100*1000);
                } else {
                    sleep(50*1000);
                }
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
                                                session.save(new QueueRequests(user.getId(), "wb", "prices", p.getNmId(), String.valueOf((int) ((int) p.getPrice() * 0.99)), String.valueOf(p.getDiscount())));
                                            } else if (sum > p.getMaxOrder()) {
                                                text = text + "\n" + "Увеличена стоимость " + p.getSubject() + " " + p.getSupplierArticle();
                                                session.save(new QueueRequests(user.getId(), "wb", "prices", p.getNmId(), String.valueOf((int) ((int) p.getPrice() * 1.01)), String.valueOf(p.getDiscount())));
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
            }
            session.getTransaction().commit();
        } finally {
            sessionFactory.close();
        }
    }
}
