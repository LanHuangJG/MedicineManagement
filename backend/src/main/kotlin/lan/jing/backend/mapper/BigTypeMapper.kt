package lan.jing.backend.mapper

import com.mybatisflex.core.BaseMapper
import lan.jing.backend.entity.MedicineBigType
import lan.jing.backend.entity.MedicineBigTypeWithMedicines
import lan.jing.backend.entity.MedicineBigTypeWithoutMedicines

interface BigTypeMapper : BaseMapper<MedicineBigType>
interface MedicineBigTypeWithoutMedicinesMapper : BaseMapper<MedicineBigTypeWithoutMedicines>
interface MedicineBigTypeWithMedicinesMapper : BaseMapper<MedicineBigTypeWithMedicines>