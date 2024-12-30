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

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PatientServiceTest
{
    @Autowired
    private PatientService patientService;

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private PatientDao patientDao;

    @Autowired
    private DoctorDao doctorDao;

    @Autowired
    private VisitDao visitDao;

    private AddressEntity getAddress(long id) {
        if (addressDao.findOne(id) == null){
            AddressEntity addressEntity = new AddressEntity();
            addressEntity.setAddressLine1(String.format("Line1_%d", id));
            addressEntity.setAddressLine2(String.format("Line2_%d", id));
            addressEntity.setCity(String.format("City%d", id));
            addressEntity.setPostalCode(String.format("66-66%d", id));
            return addressDao.save(addressEntity);
        }
        else {
            return addressDao.findOne(id);
        }
    }

    private PatientEntity addNewPatient(long id) { // id can be 0-9
        PatientEntity patientEntity = new PatientEntity();
        patientEntity.setFirstName(String.format("Pat%dFName", id));
        patientEntity.setLastName(String.format("Pat%dLName", id));
        patientEntity.setTelephoneNumber(String.format("666 6%d6 666", id));
        patientEntity.setEmail(String.format("patient%d@email.com", id));
        patientEntity.setPatientNumber(String.format("PAT00%d", id));
        patientEntity.setDateOfBirth(LocalDate.parse(String.format("198%d-03-25", id)));
        patientEntity.setGender(id%2==0 ? Gender.MALE : Gender.FEMALE);
        patientEntity.setAddress(getAddress(id));
        return patientDao.save(patientEntity);
    }

    private DoctorEntity addNewDoctor(long id) { // id can be 0-9
        DoctorEntity doctorEntity = new DoctorEntity();
        doctorEntity.setFirstName(String.format("Doc%dFName", id));
        doctorEntity.setLastName(String.format("Doc%dLName", id));
        doctorEntity.setTelephoneNumber(String.format("111 1%d1 111", id));
        doctorEntity.setEmail(String.format("doctor%d@email.com", id));
        doctorEntity.setDoctorNumber(String.format("DOC00%d", id));
        doctorEntity.setSpecialization(id%2==0 ? Specialization.SURGEON : Specialization.OCULIST);
        doctorEntity.setAddress(getAddress(id));
        return doctorDao.save(doctorEntity);
    }

    private VisitEntity addNewVisit(long id, PatientEntity patientEntity, DoctorEntity doctorEntity) {
        VisitEntity newVisit = new VisitEntity();
        newVisit.setPatient(patientEntity);
        newVisit.setDoctor(doctorEntity);
        newVisit.setDescription(String.format("testVisit%d", id));
        newVisit.setTime(LocalDate.parse(String.format("2024-12-2%d", id)).atTime(10,11,12));

        return visitDao.save(newVisit);
    }

    @Transactional
    @Test
    public void testShouldFindPatientByIdOrReturnNull() {
        // given
        PatientEntity patientEntity = addNewPatient(0);
        // when
        PatientTO patientTO    = patientService.findById(patientEntity.getId());
        PatientTO shouldBeNull = patientService.findById(-1L);
        // then
        assertThat(patientTO).isNotNull();
        assertThat(patientTO.getId()).isEqualTo(patientEntity.getId());
        assertThat(patientTO.getFirstName()).isEqualTo(patientEntity.getFirstName());
        assertThat(patientTO.getLastName()).isEqualTo(patientEntity.getLastName());
        assertThat(patientTO.getTelephoneNumber()).isEqualTo(patientEntity.getTelephoneNumber());
        assertThat(patientTO.getEmail()).isEqualTo(patientEntity.getEmail());
        assertThat(patientTO.getPatientNumber()).isEqualTo(patientEntity.getPatientNumber());
        assertThat(patientTO.getDateOfBirth()).isEqualTo(patientEntity.getDateOfBirth());
        assertThat(patientTO.getGender()).isEqualTo(patientEntity.getGender());

        assertThat(shouldBeNull).isNull();
    }

    @Transactional
    @Test
    public void testShouldDeleteVisitsNotDoctorsWhenDeletePatient() {
        // given
        PatientEntity patientEntity1 = addNewPatient(1);
        PatientEntity patientEntity2 = addNewPatient(2);
        long deleteId = patientEntity2.getId();

        DoctorEntity doctorEntity = addNewDoctor(3);

        VisitEntity visitToStay    = addNewVisit(1, patientEntity1, doctorEntity);
        VisitEntity visitToDelete1 = addNewVisit(2, patientEntity2, doctorEntity);
        VisitEntity visitToDelete2 = addNewVisit(3, patientEntity2, doctorEntity);

        // when
        patientService.deleteById(deleteId);

        // then
        assertThat(patientDao.findOne(deleteId)).isNull(); // patient deleted
        assertThat(doctorDao.findOne(doctorEntity.getId())).isNotNull(); // doctor not deleted

        assertThat(visitDao.findOne(visitToStay.getId())).isNotNull(); // remaining visits unchanged
        
        // ERROR on asserts below, why?
        assertThat(visitDao.findOne(visitToDelete1.getId())).isNull(); // all visits of deleted patient deleted
        assertThat(visitDao.findOne(visitToDelete2.getId())).isNull(); // all visits of deleted patient deleted
    }

}