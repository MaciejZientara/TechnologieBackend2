package com.jpacourse.persistance.dao;

import com.jpacourse.persistence.dao.AddressDao;
import com.jpacourse.persistence.dao.DoctorDao;
import com.jpacourse.persistence.dao.PatientDao;
import com.jpacourse.persistence.entity.AddressEntity;
import com.jpacourse.persistence.entity.PatientEntity;
import com.jpacourse.persistence.entity.DoctorEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.jpacourse.persistence.enums.Gender;
import com.jpacourse.persistence.enums.Specialization;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PatientDaoTest
{
    @Autowired
    private AddressDao addressDao;

    @Autowired
    private PatientDao patientDao;

    @Autowired
    private DoctorDao doctorDao;

    @Transactional
    @Test
    public void testShouldUpdateVisitEntity() {
        // given
        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setAddressLine1("line1");
        addressEntity.setAddressLine2("line2");
        addressEntity.setCity("City1");
        addressEntity.setPostalCode("66-666");
        final AddressEntity addressSaved = addressDao.save(addressEntity);

        PatientEntity patientEntity = new PatientEntity();
        patientEntity.setFirstName("PatFName");
        patientEntity.setLastName("PatLName");
        patientEntity.setTelephoneNumber("666 666 666");
        patientEntity.setEmail("patient@email.com");
        patientEntity.setPatientNumber("PAT001");
        patientEntity.setDateOfBirth(LocalDate.parse("1985-03-25"));
        patientEntity.setGender(Gender.HELICOPTER);
        patientEntity.setAddress(addressSaved);
        final PatientEntity patientSaved = patientDao.save(patientEntity);

        DoctorEntity doctorEntity = new DoctorEntity();
        doctorEntity.setFirstName("DocFName");
        doctorEntity.setLastName("DocLName");
        doctorEntity.setTelephoneNumber("111 111 111");
        doctorEntity.setEmail("doctor@email.com");
        doctorEntity.setDoctorNumber("DOC001");
        doctorEntity.setSpecialization(Specialization.SURGEON);
        doctorEntity.setAddress(addressSaved);
        final DoctorEntity doctorSaved = doctorDao.save(doctorEntity);

        LocalDateTime visitTime = LocalDate.parse("2024-12-30").atTime(10,15,25);
        String visitDesc = "VisitDescription";

        assertThat(patientSaved.getVisits()).isNull();

        // when
        patientDao.addVisit(
                patientSaved.getId(),
                doctorSaved.getId(),
                visitTime,
                visitDesc
        );

        // then
        assertThat(patientSaved.getVisits().size()).isEqualTo(1);

        assertThat(patientSaved.getVisits().get(0)).isEqualTo(patientEntity.getVisits().get(0));

        assertThat(patientSaved.getVisits().get(0).getPatient()).isEqualTo(patientEntity);
        assertThat(patientSaved.getVisits().get(0).getDoctor()).isEqualTo(doctorEntity);

        assertThat(patientSaved.getVisits().get(0).getTime()).isEqualTo(visitTime);
        assertThat(patientSaved.getVisits().get(0).getDescription()).isEqualTo(visitDesc);

    }
}
