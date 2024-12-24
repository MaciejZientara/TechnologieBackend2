package com.jpacourse.persistence.dao.impl;


import com.jpacourse.persistence.dao.PatientDao;
import com.jpacourse.persistence.entity.DoctorEntity;
import com.jpacourse.persistence.entity.PatientEntity;
import com.jpacourse.persistence.entity.VisitEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class PatientDaoImpl  extends AbstractDao<PatientEntity, Long> implements PatientDao {

    AbstractDao<DoctorEntity,Long> doctorDao;

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
        result.add(newVisit);
        patient.setVisits(result);
    }
}
