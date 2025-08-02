#DEFINE EFFECT_CHANGE_STAT_STAGE 0x0E

    push    {r3-r7,lr}
    mov     r5, r1
    mov     r4, r2

    mov     r6, #2
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return

    mov     r0, #3
    bl      Battle::EventVar_GetValue
    lsls    r0, r0, #0x18
    lsrs    r1, r0, #0x18
    mov     r0, r4
    bl      Battle::MainModule_IsAllyMonID
    cmp     r0, #0
    bne     Return

    mov     r0, #VAR_Volume
    mov     r7, #0x20
    bl      Battle::EventVar_GetValue
    cmp     r0, #0
    bge     Return

    mov     r0, r5
    mov     r1, #EFFECT_CHANGE_STAT_STAGE
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r1, r0
    ldr     r2, [r1]
    lsls    r0, r7, #0x12
    orrs    r0, r2
    str     r0, [r1]
    mov     r0, #STSG_Defense
    str     r0, [r1,#4]
    strb    r6, [r1,#0xC]
    strb    r0, [r1,#0xE]
    strb    r0, [r1,#0xF]
    mov     r0, r5
    strb    r4, [r1,#0x10]
    bl      Battle::Handler_PopWork

Return:
    pop     {r3-r7,pc}