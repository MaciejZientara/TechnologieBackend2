package com.jpacourse.mapper;

import com.jpacourse.dto.PatientsVisitTO;
import com.jpacourse.persistence.entity.MedicalTreatmentEntity;
import com.jpacourse.persistence.entity.VisitEntity;
import com.jpacourse.persistence.enums.TreatmentType;

import java.util.ArrayList;
import java.util.List;

public class PatientsVisitsMapper {
    public static PatientsVisitTO mapToTO(final VisitEntity visitEntity)
    {
        if (visitEntity == null)
        {
            return null;
        }
        final PatientsVisitTO patientsVisitTO = new PatientsVisitTO();
        patientsVisitTO.setTime(visitEntity.getTime());
        patientsVisitTO.setDoctorFirstName(visitEntity.getDoctor().getFirstName());
        patientsVisitTO.setDoctorLastName(visitEntity.getDoctor().getLastName());

        List<TreatmentType> typeList = new ArrayList<>();
        for(MedicalTreatmentEntity mte : visitEntity.getTreatments())
        {
            typeList.add(mte.getType());
        }
        patientsVisitTO.setType(typeList);

        return patientsVisitTO;
    }

    public static List<PatientsVisitTO> mapListToTO(final List<VisitEntity> visits)
    {
        List<PatientsVisitTO> result = new ArrayList<>();
        for(VisitEntity visit : visits)
        {
            result.add(mapToTO(visit));
        }
        return result;
    }
}
