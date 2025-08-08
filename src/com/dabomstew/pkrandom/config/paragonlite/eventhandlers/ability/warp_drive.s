    push    {r4-r6, lr}
    mov     r5, r1 ; r5 := current speed
    mov     r4, r2

    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return

    mov     r0, r5
    bl      Battle::GetTerrain // TODO
    cmp     r0, #TERRAIN_Psychic
    bne     Return

    mov     r0, VAR_Ratio
    mov     r6, #2
    lsl     r1, r6, #0xC
    bl      Battle::EventVar_MulValue

Return:
    pop     {r4-r6, pc}