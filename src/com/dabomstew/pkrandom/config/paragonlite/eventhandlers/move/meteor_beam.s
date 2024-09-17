#DEFINE VAR_ATTACKING_MON 0x03
#DEFINE EFFECT_MESSAGE 0x04
#DEFINE EFFECT_CHANGE_STAT_STAGE 0x0E
#DEFINE STATSTAGE_SPECIAL_ATTACK 3

    push    {r3-r7, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_ATTACKING_MON
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, r5
    mov     r1, #EFFECT_MESSAGE
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r6, r0
    
    add     r0, r6, #4
    mov     r1, #2 ; file 0x12
    ldr     r2, =1209 ; "[poke] is overflowing with space power!"
    bl      Battle::Handler_StrSetup
    
    add     r0, r6, #4
    mov     r1, r4
    bl      Battle::Handler_AddArg
    
    mov     r0, r5
    mov     r1, r6
    bl      Battle::Handler_PopWork
    
    mov     r0, r5
    mov     r1, #EFFECT_CHANGE_STAT_STAGE
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r1, r0
    mov     r0, #STATSTAGE_SPECIAL_ATTACK
    str     r0, [r1, #0x04] ; rank type
    mov     r0, #1
    strb    r0, [r1, #0x0C] ; rank volume
    strb    r0, [r1, #0x0E] ; move animation
    strb    r0, [r1, #0x0F] ; poke count
    strb    r4, [r1, #0x10] ; poke id
    mov     r0, r5
    bl      Battle::Handler_PopWork
    
Return:
    pop     {r3-r7, lr}