package com.literacyhub.dao;

import com.literacyhub.entity.WellnessTip;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class WellnessTipDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional(readOnly = true)
    public List<WellnessTip> findAllTips() {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM WellnessTip";
        return session.createQuery(hql, WellnessTip.class).getResultList();
    }

    @Transactional(readOnly = true)
    public Optional<WellnessTip> findById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        WellnessTip tip = session.get(WellnessTip.class, id);
        return Optional.ofNullable(tip);
    }

    @Transactional
    public void save(WellnessTip tip) {
        Session session = sessionFactory.getCurrentSession();
        session.save(tip);
    }

    @Transactional
    public void update(WellnessTip tip) {
        Session session = sessionFactory.getCurrentSession();
        session.update(tip);
    }

    @Transactional
    public void delete(Long id) {
        Session session = sessionFactory.getCurrentSession();
        WellnessTip tip = session.get(WellnessTip.class, id);
        if (tip != null) {
            session.delete(tip);
        }
    }

    /**
     * Get a random wellness tip from the database.
     */
    @Transactional(readOnly = true)
    public WellnessTip getRandomTip() {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM WellnessTip ORDER BY RAND()";
        return session.createQuery(hql, WellnessTip.class)
                .setMaxResults(1)
                .uniqueResult();
    }
}
