package com.jpacourse.service;

import com.jpacourse.dto.PatientTO;
import com.jpacourse.persistence.entity.VisitEntity;

import java.util.List;

public interface PatientService {
    public PatientTO findById(final Long id);

    public void deleteById(final Long id);

    public List<VisitEntity> findVisitsOfPatient(final Long id);
}
