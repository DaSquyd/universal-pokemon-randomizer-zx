    
    ldrh    r0, [r0, #TrainerData.flags]
    #read_bits(r0, #TrainerData_Flags.hasNatureBit, 1)
    bx      lr