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
}
