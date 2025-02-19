    
    ldrh    r0, [r0, #TrainerData.flags]
    #read_bits(r1, r0, #TrainerData_Flags.hasNatureBit, 1)
    #read_bits(r0, #TrainerData_Flags.hasIVsEVsBit, 1)
    orr     r0, r1
    bx      lr