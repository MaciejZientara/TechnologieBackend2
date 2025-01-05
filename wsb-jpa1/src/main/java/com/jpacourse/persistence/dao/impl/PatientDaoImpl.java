package com.jpacourse.persistence.dao.impl;


import com.jpacourse.persistence.dao.DoctorDao;
import com.jpacourse.persistence.dao.PatientDao;
import com.jpacourse.persistence.entity.DoctorEntity;
import com.jpacourse.persistence.entity.PatientEntity;
import com.jpacourse.persistence.entity.VisitEntity;
import com.jpacourse.persistence.enums.Gender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PatientDaoImpl  extends AbstractDao<PatientEntity, Long> implements PatientDao {

    @Autowired
    private DoctorDao doctorDao;

    @Override
    public void addVisit(long patientId, long doctorId, LocalDateTime time, String description)
    {
        PatientEntity patient = getOne(patientId);

        VisitEntity newVisit = new VisitEntity();
        newVisit.setDescription(description);
        newVisit.setTime(time);
        newVisit.setDoctor(doctorDao.getOne(doctorId));
        newVisit.setPatient(patient);

        List<VisitEntity> result = patient.getVisits();
        if(result==null){
            result = new ArrayList<>();
        }
        result.add(newVisit);
        patient.setVisits(result);
        update(patient);
    }

    @Override
    public List<PatientEntity> findByLastName(String lastName){
        return entityManager.createQuery("select pat from PatientEntity pat " +
                "where pat.lastName = :lastName",
                PatientEntity.class).setParameter("lastName",lastName).getResultList();
    }

    @Override
    public List<PatientEntity> findWithMoreVisits(long visitCount){ // visitCount w treści zadania X
        return entityManager.createQuery("select pat from PatientEntity pat " +
                "join pat.visits visit " +
                "group by pat " +
                "having count(visit) >= :visCount",
                PatientEntity.class).setParameter("visCount",visitCount).getResultList();
    }

    @Override
    public List<PatientEntity> findByGender(Gender g) {
        return entityManager.createQuery("select pat from PatientEntity pat " +
                "where pat.gender = :gender",
                PatientEntity.class).setParameter("gender",g).getResultList();
    }

}
