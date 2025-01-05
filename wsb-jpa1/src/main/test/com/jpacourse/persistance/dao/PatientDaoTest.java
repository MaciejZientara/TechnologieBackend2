package com.jpacourse.persistance.dao;

import com.jpacourse.persistence.dao.PatientDao;
import com.jpacourse.persistence.entity.PatientEntity;
import com.jpacourse.persistence.entity.VisitEntity;
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
public class PatientDaoTest
{
    @Autowired
    private PatientDao patientDao;

    @Transactional
    @Test
    public void testShouldUpdateVisitEntity() {
        // given

        // use existing data
        long patientId = 1L;
        long doctorId = 1L;
        LocalDateTime visitTime = LocalDate.parse("2024-12-30").atTime(10,15,25);
        String visitDesc = "FakeVisitDescription";

        // assert state before update
        final List<VisitEntity> visitsBeforeUpdate = patientDao.getOne(patientId).getVisits();
        int patientVisitsBefore = visitsBeforeUpdate.size();
        assertThat(visitsBeforeUpdate.stream()
                .noneMatch(visit -> visit.getDescription().equals(visitDesc)))
                .isTrue();

        // when
        patientDao.addVisit(
                patientId,
                doctorId,
                visitTime,
                visitDesc
        );

        // then
        PatientEntity updatedPatient = patientDao.getOne(patientId);

        assertThat(updatedPatient.getVisits().size()).isEqualTo(patientVisitsBefore+1);
        assertThat(patientDao.getOne(patientId)
                .getVisits().stream()
                .filter(visit -> visit.getDescription().equals(visitDesc)).count())
                .isEqualTo(1);

    }

    @Transactional
    @Test
    public void testShouldFindPatientsByLastName() {
        // given
        // use existing data from data.sql
        // Patient Id, FirstName, LastName
        //      1       Adam        Nowak
        //      6      Magdalena    Nowak
        //      2       Ewa         Kowalska
        //      5       Jan         Kowalski

        // when
        final List<PatientEntity> patients1 = patientDao.findByLastName("Nowak"); // should find 2
        final List<PatientEntity> patients2 = patientDao.findByLastName("Kowalska"); // should find 1
        final List<PatientEntity> patients3 = patientDao.findByLastName("Kowalski"); // should find 1
        final List<PatientEntity> patients4 = patientDao.findByLastName("Kowalsk"); // should find 0, only equals, no patterns
        final List<PatientEntity> patients5 = patientDao.findByLastName("Kowalsk*"); // should find 0, only equals, no patterns

        // then
        assertThat(patients1.size()).isEqualTo(2);
        assertThat(patients2.size()).isEqualTo(1);
        assertThat(patients3.size()).isEqualTo(1);
        assertThat(patients4.size()).isEqualTo(0);
        assertThat(patients5.size()).isEqualTo(0);

        assertThat(patients1.get(0).getId()).isEqualTo(1L);
        assertThat(patients1.get(0).getFirstName()).isEqualTo("Adam");
        assertThat(patients1.get(1).getId()).isEqualTo(6L);
        assertThat(patients1.get(1).getFirstName()).isEqualTo("Magdalena");

        assertThat(patients2.get(0).getId()).isEqualTo(2L);
        assertThat(patients2.get(0).getFirstName()).isEqualTo("Ewa");

        assertThat(patients3.get(0).getId()).isEqualTo(5L);
        assertThat(patients3.get(0).getFirstName()).isEqualTo("Jan");
    }

}
