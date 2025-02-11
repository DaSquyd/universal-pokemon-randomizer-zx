    
    ldrb    r0, [r0, #TrainerPoke.basicFlags]
    lsl     r0, #(32 - (TrainerPoke_BasicFlags.formBit + TrainerPoke_BasicFlags.formSize))
    lsr     r0, #(32 - TrainerPoke_BasicFlags.formSize)
    bx      lr