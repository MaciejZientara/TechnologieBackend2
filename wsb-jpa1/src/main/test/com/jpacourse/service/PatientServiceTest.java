package com.jpacourse.service;


import com.jpacourse.dto.PatientTO;
import com.jpacourse.persistence.dao.AddressDao;
import com.jpacourse.persistence.dao.DoctorDao;
import com.jpacourse.persistence.dao.PatientDao;
import com.jpacourse.persistence.dao.VisitDao;
import com.jpacourse.persistence.entity.AddressEntity;
import com.jpacourse.persistence.entity.DoctorEntity;
import com.jpacourse.persistence.entity.PatientEntity;
import com.jpacourse.persistence.entity.VisitEntity;
import com.jpacourse.persistence.enums.Gender;
import com.jpacourse.persistence.enums.Specialization;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PatientServiceTest
{
    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientDao patientDao;

    @Autowired
    private DoctorDao doctorDao;

    @Autowired
    private VisitDao visitDao;

    @Transactional
    @Test
    public void testShouldFindPatientByIdOrReturnNull() {
        // given
        // use existing data from data.sql
        // when
        PatientTO patientTO    = patientService.findById(1L);
        PatientTO shouldBeNull = patientService.findById(-1L);
        // then
        assertThat(patientTO).isNotNull();
        assertThat(patientTO.getId()).isEqualTo(1L);
        assertThat(patientTO.getFirstName()).isEqualTo("Adam");
        assertThat(patientTO.getLastName()).isEqualTo("Nowak");
        assertThat(patientTO.getTelephoneNumber()).isEqualTo("+48123456781");
        assertThat(patientTO.getEmail()).isEqualTo("adam.nowak@example.com");
        assertThat(patientTO.getPatientNumber()).isEqualTo("PAT1001");
        assertThat(patientTO.getDateOfBirth()).isEqualTo(LocalDate.parse("1985-03-25"));
        assertThat(patientTO.getGender()).isEqualTo(Gender.MALE);

        assertThat(shouldBeNull).isNull();
    }

    @Transactional
    @Test
    public void testShouldDeleteVisitsNotDoctorsWhenDeletePatient() {
        // given
        // use existing data from data.sql
        // Patient 1 has 4 visits as follows (doctor_id, visit_id): (1, 1), (2, 12), (2, 13), (3, 18)

        // assert state before update
        final List<VisitEntity> visitsBeforeUpdate = patientDao.getOne(1L).getVisits();
        assertThat(visitsBeforeUpdate.size()).isEqualTo(4);
        int allVisitsCountBefore = visitDao.findAll().size();

        // when
        patientService.deleteById(1L);

        // then
        assertThat(patientDao.findOne(1L)).isNull(); // patient deleted
        assertThat(doctorDao.findOne(1L)).isNotNull(); // doctors not deleted
        assertThat(doctorDao.findOne(2L)).isNotNull(); // doctors not deleted
        assertThat(doctorDao.findOne(3L)).isNotNull(); // doctors not deleted

        assertThat(visitDao.findAll().size()).isEqualTo(allVisitsCountBefore - 4); // remaining visits unchanged

        assertThat(visitDao.findOne(1L)).isNull(); // all visits of deleted patient deleted
        assertThat(visitDao.findOne(12L)).isNull(); // all visits of deleted patient deleted
        assertThat(visitDao.findOne(13L)).isNull(); // all visits of deleted patient deleted
        assertThat(visitDao.findOne(18L)).isNull(); // all visits of deleted patient deleted
    }

}