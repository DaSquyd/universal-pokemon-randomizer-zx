    
    ldrb    r0, [r0, #TrainerPoke.basicFlags]
    #read_bits(r0, TrainerPoke_BasicFlags.formBit, TrainerPoke_BasicFlags.formSize)
    bx      lr