    
    ldrb    r1, [r0, #TrainerPoke.basicFlags]
    lsl     r0, r1, #(32 - (TrainerPoke_BasicFlags.genderBit + TrainerPoke_BasicFlags.genderSize))
    lsr     r0, #(32 - TrainerPoke_BasicFlags.genderSize)
    
    lsl     r1, #(32 - (TrainerPoke_BasicFlags.abilityBit + TrainerPoke_BasicFlags.abilitySize))
    lsr     r1, #(32 - TrainerPoke_BasicFlags.abilitySize - 4)
    
    orr     r0, r1
    bx      lr