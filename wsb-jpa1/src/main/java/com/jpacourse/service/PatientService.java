package com.jpacourse.service;

import com.jpacourse.dto.PatientTO;

public interface PatientService {
    public PatientTO findById(final Long id);

    public void deleteById(final Long id);
}
