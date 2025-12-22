package com.literacyhub.dao;

import com.literacyhub.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserDAO {
    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public User login(String email, String password) {
        Session session = sessionFactory.getCurrentSession();

        String hql = "FROM User u WHERE u.email = :email AND u.password = :password";

        return session.createQuery(hql, User.class)
                .setParameter("email", email)
                .setParameter("password", password)
                .uniqueResult();
    }

    @Transactional
    public void save(User user) {
        Session session = sessionFactory.getCurrentSession();
        session.save(user);
    }
}
