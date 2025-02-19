    
    ldrh    r0, [r0, #TrainerData.flags]
    #read_bits(r0, #TrainerData_Flags.hasItemBit, 1)
    bx      lr