package com.literacyhub.dao;

import com.literacyhub.entity.Resource;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class ResourceDAO {
    @Autowired
    private SessionFactory sessionFactory;

    @Transactional(readOnly = true)
    public Resource findById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(Resource.class, id);
    }

    @Transactional(readOnly = true)
    public List<Resource> search(String query, String category) {
        Session session = sessionFactory.getCurrentSession();

        StringBuilder hql = new StringBuilder("FROM Resource r WHERE 1=1");
        if (category != null && !category.trim().isEmpty()) {
            hql.append(" AND r.category = :category");
        }
        if (query != null && !query.trim().isEmpty()) {
            hql.append(" AND (lower(r.title) LIKE :kw OR lower(r.description) LIKE :kw)");
        }
        hql.append(" ORDER BY r.createdAt DESC");

        var q = session.createQuery(hql.toString(), Resource.class);
        if (category != null && !category.trim().isEmpty()) {
            q.setParameter("category", category);
        }
        if (query != null && !query.trim().isEmpty()) {
            q.setParameter("kw", "%" + query.toLowerCase() + "%");
        }
        return q.getResultList();
    }

    @Transactional(readOnly = true)
    public List<String> findDistinctCategories() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(
                        "SELECT DISTINCT r.category FROM Resource r WHERE r.category IS NOT NULL ORDER BY LOWER(r.category)",
                        String.class)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Resource> findAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("FROM Resource r ORDER BY r.createdAt DESC", Resource.class).getResultList();
    }

    @Transactional(readOnly = true)
    public List<Resource> findByOwner(String uploadedBy) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("FROM Resource r WHERE r.uploadedBy = :owner ORDER BY r.createdAt DESC", Resource.class)
                .setParameter("owner", uploadedBy)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Resource> searchByOwner(String query, String category, String uploadedBy) {
        Session session = sessionFactory.getCurrentSession();

        StringBuilder hql = new StringBuilder("FROM Resource r WHERE r.uploadedBy = :owner");
        if (category != null && !category.trim().isEmpty()) {
            hql.append(" AND r.category = :category");
        }
        if (query != null && !query.trim().isEmpty()) {
            hql.append(" AND (lower(r.title) LIKE :kw OR lower(r.description) LIKE :kw)");
        }
        hql.append(" ORDER BY r.createdAt DESC");

        var q = session.createQuery(hql.toString(), Resource.class);
        q.setParameter("owner", uploadedBy);
        if (category != null && !category.trim().isEmpty()) {
            q.setParameter("category", category);
        }
        if (query != null && !query.trim().isEmpty()) {
            q.setParameter("kw", "%" + query.toLowerCase() + "%");
        }
        return q.getResultList();
    }

    @Transactional
    public void save(Resource resource) {
        Session session = sessionFactory.getCurrentSession();
        session.save(resource);
    }

    @Transactional
    public void update(Resource resource) {
        Session session = sessionFactory.getCurrentSession();
        session.update(resource);
    }

    @Transactional
    public void delete(Long id) {
        Session session = sessionFactory.getCurrentSession();
        Resource r = session.get(Resource.class, id);
        if (r != null) {
            session.delete(r);
        }
    }
}
