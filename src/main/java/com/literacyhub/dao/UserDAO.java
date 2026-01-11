package com.literacyhub.dao;

import com.literacyhub.entity.Professional;
import com.literacyhub.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class UserDAO {
    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public List<User> findAllUsers() {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery("FROM User", User.class).getResultList();
    }

    @Transactional(readOnly = true)
    public List<Professional> findPendingProfessionals() {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM Professional p WHERE p.status = 'PENDING'";
        return session.createQuery(hql, Professional.class).getResultList();
    }

    @Transactional
    public void processApproval(int userId, String action) {
        Session session = sessionFactory.getCurrentSession();
        User user = session.get(User.class, userId);

        if (user != null) {
            if ("APPROVE".equalsIgnoreCase(action)) {
                user.setStatus("ACTIVE");
                session.update(user);
            } else if ("REJECT".equalsIgnoreCase(action)) {
                session.delete(user);
            }
        }
    }

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
