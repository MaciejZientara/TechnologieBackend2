package com.jpacourse.persistence.dao;

import com.jpacourse.persistence.entity.PatientEntity;
import com.jpacourse.persistence.enums.Gender;

import java.time.LocalDateTime;
import java.util.List;

public interface PatientDao extends Dao<PatientEntity, Long> {
    void addVisit(long patientId, long doctorId, LocalDateTime time, String description);

    List<PatientEntity> findByLastName(String lastName);

    List<PatientEntity> findWithMoreVisits(long visitCount);

    List<PatientEntity> findByGender(Gender g);
}
