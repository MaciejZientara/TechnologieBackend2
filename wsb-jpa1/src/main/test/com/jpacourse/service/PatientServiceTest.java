package com.jpacourse.service;


import com.jpacourse.dto.PatientTO;
import com.jpacourse.dto.PatientsVisitTO;
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
import com.jpacourse.persistence.enums.TreatmentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Transactional
    @Test
    public void testShouldFindAllPatientVisits() {
        // given
        // use existing data from data.sql
        // Patient 1 has 4 visits as follows (doctor_id, visit_id): (1, 1), (2, 12), (2, 13), (3, 18)
        // Doctor data (id, firstName, lastName):
        // 1, John, Smith
        // 2, Anna, Brown
        // 3, Peter, White

        // when
        List<PatientsVisitTO> visits = patientService.findVisitsOfPatient(1L);
        // then
        assertThat(visits.size()).isEqualTo(4);

        // PatientVisitTO has following fields:
        // time - visit date
        // doctorFirstName
        // doctorLastName
        // type - list of TreatmentType enum

        // Visit with id 1
        assertThat(visits.get(0).getTime()).isEqualTo(LocalDateTime.parse("2024-12-05T09:00:00"));
        assertThat(visits.get(0).getDoctorFirstName()).isEqualTo("John");
        assertThat(visits.get(0).getDoctorLastName()).isEqualTo("Smith");
        // Visit with id 12
        assertThat(visits.get(1).getTime()).isEqualTo(LocalDateTime.parse("2024-12-05T14:30:00"));
        assertThat(visits.get(1).getDoctorFirstName()).isEqualTo("Anna");
        assertThat(visits.get(1).getDoctorLastName()).isEqualTo("Brown");
        // Visit with id 13
        assertThat(visits.get(2).getTime()).isEqualTo(LocalDateTime.parse("2024-12-05T15:00:00"));
        assertThat(visits.get(2).getDoctorFirstName()).isEqualTo("Anna");
        assertThat(visits.get(2).getDoctorLastName()).isEqualTo("Brown");
        // Visit with id 18
        assertThat(visits.get(3).getTime()).isEqualTo(LocalDateTime.parse("2024-12-06T10:00:00"));
        assertThat(visits.get(3).getDoctorFirstName()).isEqualTo("Peter");
        assertThat(visits.get(3).getDoctorLastName()).isEqualTo("White");

    }

}