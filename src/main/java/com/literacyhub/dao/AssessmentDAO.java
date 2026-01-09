package com.literacyhub.dao;

import com.literacyhub.entity.*;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
@Transactional
public class AssessmentDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public List<AssessmentConfig> findAllConfigs() {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM AssessmentConfig", AssessmentConfig.class)
                .getResultList();
    }

    public AssessmentConfig findConfigByType(String type) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM AssessmentConfig WHERE testType = :t", AssessmentConfig.class)
                .setParameter("t", type)
                .uniqueResult();
    }

    public List<AssessmentOption> findOptionsByType(String type) {
        String hql = "FROM AssessmentOption WHERE testType = :type ORDER BY displayOrder ASC";
        return sessionFactory.getCurrentSession().createQuery(hql, AssessmentOption.class)
                .setParameter("type", type)
                .getResultList();
    }

    public void saveConfig(AssessmentConfig config) {
        sessionFactory.getCurrentSession().saveOrUpdate(config);
    }

    public List<AssessmentQuestion> findByType(String type) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM AssessmentQuestion WHERE type = :t ORDER BY id ASC", AssessmentQuestion.class)
                .setParameter("t", type)
                .getResultList();
    }

    public AssessmentQuestion findById(Long id) {
        return sessionFactory.getCurrentSession().get(AssessmentQuestion.class, id);
    }

    public void saveQuestion(AssessmentQuestion question) {
        sessionFactory.getCurrentSession().saveOrUpdate(question);
    }

    public void saveSubmission(UserAssessmentSubmission submission) {
        sessionFactory.getCurrentSession().save(submission);
    }

    public UserAssessmentSubmission findSubmissionById(Long id) {
        String hql = "SELECT s FROM UserAssessmentSubmission s LEFT JOIN FETCH s.breakdown WHERE s.id = :sid";
        return sessionFactory.getCurrentSession()
                .createQuery(hql, UserAssessmentSubmission.class)
                .setParameter("sid", id)
                .uniqueResult();
    }

    public List<UserAssessmentSubmission> findSubmissionsByUser(Long userId) {
        String hql = "FROM UserAssessmentSubmission WHERE userId = :uid ORDER BY submittedAt DESC";
        return sessionFactory.getCurrentSession()
                .createQuery(hql, UserAssessmentSubmission.class)
                .setParameter("uid", userId)
                .getResultList();
    }
}