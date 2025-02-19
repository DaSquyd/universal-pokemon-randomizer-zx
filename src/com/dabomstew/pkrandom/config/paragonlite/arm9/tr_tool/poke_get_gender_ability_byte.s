    
    ldrb    r1, [r0, #TrainerPoke.basicFlags]
    #read_bits(r0, r1, TrainerPoke_BasicFlags.genderBit, 1)
    add     r0, #1 ; when used in this form, male is 1 and female is 2, so we increment from 0,1 to 1,2
    
    #read_bits(r1, TrainerPoke_BasicFlags.abilityBit, TrainerPoke_BasicFlags.abilitySize)
    lsl     r1, #4
    
    orr     r0, r1
    bx      lr