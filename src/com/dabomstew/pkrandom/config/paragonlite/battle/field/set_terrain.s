    mov     r2, r1
    mov     r1, r0
    ldr     r0, =BATTLE_FIELD_WORK
    ldr     r3, =(Battle::Field_SetTerrainCore+1)
    bx      r3
    